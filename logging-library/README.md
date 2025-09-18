# Async Logger LLD (Low-Level Design)

## Overview
This document describes the low-level design for an asynchronous logging subsystem implemented inside the logging-library module. The design prioritizes high-throughput, low-latency logging for producers (application threads) by decoupling log production from I/O using a bounded BlockingQueue and background worker threads driven by an ExecutorService.

## Goals
- Non-blocking fast path for producers under normal load.
- Backpressure under extreme load via bounded queue (producers block when full).
- Ordered, line-safe writes to file output.
- Pluggable formatters and appenders.
- Simple chain-of-responsibility routing based on log levels.
- Graceful shutdown that flushes pending messages.

## Key Components
1) Logger (enum singleton)
   - Location: `com.abhij33t.lld.Logger`
   - Role: Public API for application code. Builds a handler chain from LogHandlerConfig and routes LogMessage objects through it.
   - Methods: `trace`/`debug`/`info`/`warn`/`error`/`fatal` → `log(level, message)`.

2) LogLevel (enum)
   - Location: `com.abhij33t.lld.enums.LogLevel`
   - Values: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL`.

3) LogMessage (immutable data class)
   - Location: `com.abhij33t.lld.model.LogMessage`
   - Fields: `level` (LogLevel), `message` (String), `timestamp` (long).
   - Lombok: `@AllArgsConstructor`, `@Getter` for concise boilerplate.

4) Formatter (strategy)
   - Interface: `com.abhij33t.lld.formatter.LogFormatter`
   - Default impl: `PlainTextFormatter` → `"yyyy-MM-dd HH:mm:ss [LEVEL] - message"`

5) Appenders (outputs)
   - Interface: `com.abhij33t.lld.appender.LogAppender` with `append(LogMessage)`.
   - ConsoleAppender: immediate console prints (not shown here but present in code).
   - FileAppender: synchronous file writes using a synchronized append (baseline).
   - AsyncFileAppender: asynchronous file appender with queue + worker pool (primary focus).

6) Handlers (chain-of-responsibility)
   - Base: `com.abhij33t.lld.handler.LogHandler`
     - Holds subscribed appenders (observers) and optional next handler in chain.
     - `handle(msg)`: if `canHandle(level)` → `notifyObservers(msg)` else delegate to next.
   - Concrete level handlers: `TraceHandler`, `DebugHandler`, `InfoHandler`, `WarnHandler`, `ErrorHandler`, `FatalHandler`.

7) LogHandlerConfig (wiring)
   - Location: `com.abhij33t.lld.config.LogHandlerConfig`
   - Responsibility: Build the handler chain in desired order and allow runtime registration of appenders per level.

## AsyncFileAppender LLD
- Location: `com.abhij33t.lld.appender.AsyncFileAppender`
- Core idea: Producers enqueue messages; background workers dequeue and write to a single `BufferedWriter`.

### Internal structure
- `formatter`: `LogFormatter` → formats messages to a string line.
- `writer`: `BufferedWriter` → opened in append mode; single instance per appender.
- `queue`: `LinkedBlockingQueue<LogMessage>` with fixed capacity (bounded).
- `workers`: `ExecutorService` created via `Executors.newFixedThreadPool(workerCount, ...)`.
- `POISON`: Special sentinel `LogMessage` indicating shutdown.
- `open`: `volatile boolean` guarding acceptance of new messages.

### Append path (producer threads)
1. Logger builds `LogMessage(level, message, timestamp)` and calls `handlerChain.handle(message)`.
2. The appropriate `LogHandler` invokes `notifyObservers(message)` for subscribed appenders.
3. `AsyncFileAppender.append(message)` executes `queue.put(message)`:
   - Fast in normal case.
   - Blocks if the queue is full, providing backpressure.

### Worker path (background threads)
1. Each worker repeatedly executes `queue.take()`.
2. If the item is not `POISON`, format and write a line:
   - `String line = formatter.format(msg)`
   - `synchronized(writer) { writer.write(line); writer.newLine(); }`
   - The synchronized block guarantees line-level atomicity when multiple workers are present.
3. On `POISON`, the worker exits.

### Shutdown sequence
`close()`:
1) Set `open = false`.
2) Enqueue one `POISON` per worker to ensure all workers exit.
3) Shutdown the `ExecutorService` and await termination.
4) Flush and close the writer.

### Backpressure and reliability
- Bounded queue: When full, producers block on `queue.put(...)`. This prevents unbounded memory growth and signals overload.
- If non-blocking behavior is desired, replace `put(...)` with `offer(..., timeout)` and drop or escalate when timed out.
- I/O exceptions inside workers are caught and can be routed to stderr or a fallback appender (extend as needed).

### Thread-safety
- Logger is an enum singleton; safe and simple global access.
- LogHandler uses `CopyOnWriteArrayList` for appenders to allow concurrent subscriptions.
- AsyncFileAppender:
  - `queue` is thread-safe.
  - `writer` writes are protected by `synchronized(writer)` to maintain line integrity.
  - `open` is volatile to provide fast-visible state.

### Configuration knobs
- `capacity` (int): queue size. Higher → fewer producer stalls, more memory use.
- `workerCount` (int): number of worker threads. With a single file writer, 1–4 typically suffices. Multiple workers can help parallelize formatting and scheduling, though file writes remain serialized by the writer lock.
- `formatter`: swap to JSON formatter or custom layout by implementing `LogFormatter`.
- `fileName`: output destination; created if absent and opened in append mode.

### Sequence (text-based UML)
```
Producer Thread → Logger → HandlerChain → TraceHandler → AsyncFileAppender.append()
AsyncFileAppender.append() → BlockingQueue.put()
Worker Thread → BlockingQueue.take() → formatter.format() → synchronized(writer).write()
close() → enqueue POISON × workerCount → workers.shutdown()/await → writer.flush()/close
```

## Usage example
See `Main.java` for an end-to-end test using a cached thread pool to generate load:

```java
ExecutorService pool = Executors.newCachedThreadPool();
AsyncFileAppender asyncFileAppender = new AsyncFileAppender(new PlainTextFormatter(), "trace-logs.txt", 1000, 4);
LogHandlerConfig.addAppenderForLevel(LogLevel.TRACE, asyncFileAppender);
IntStream.range(0, 5000).forEach(i -> pool.submit(() -> Logger.INSTANCE.trace("trace event : "+i)));
pool.shutdown();
pool.awaitTermination(30, TimeUnit.SECONDS);
asyncFileAppender.close();
```

## Why async vs synchronized file writes?
- Synchronous FileAppender synchronizes `append()`, forcing all producer threads to serialize on I/O. This limits throughput and increases tail latency.
- Async design eliminates contention on producers during normal operation and localizes contention to worker threads.
- Bounded queue provides stable memory usage and built-in flow control.

## Limitations and extensions
- Single writer lock means actual disk writes are serialized; multiple workers help mainly with scheduling/formatting.
- Consider batching and periodic flush for higher throughput (e.g., flush every N lines or M milliseconds).
- Add metrics (queue size, dropped logs, I/O errors).
- Add rotation (time or size-based) and retention policies.
- Consider a Disruptor or MPSC ring buffer for ultra-low latency scenarios.

## Build & run
- Requires Java 17.
- Maven build; Lombok is included as a dependency.
- Run Main to produce logs:

```bash
mvn -q -f logging-library/pom.xml clean package
java -cp logging-library/target/classes com.abhij33t.lld.Main
```

## Testing tips
- Reduce capacity (e.g., 3) and increase producer count to observe backpressure.
- Use `workerCount=1` for easier reasoning about ordering; increase to 2–4 to test concurrency.
- Verify that `trace-logs.txt` grows as expected and that `asyncFileAppender.close()` flushes remaining messages.
