Low-Level Design (LLD) Repository

Welcome to the Low-Level Design repository. This repo hosts modular LLD implementations. Each module contains its own documentation and examples.

Table of Contents
- logging-library – Async Logger LLD
  - Overview and design: logging-library/README.md
- parking-lot – Parking Lot LLD
  - Overview and design: parking-lot/readme.md

Quick Start
- Java 17+ required
- Build a module (example: logging-library):
  - mvn -q -f logging-library/pom.xml clean package
- Run the example main class:
  - java -cp logging-library/target/classes com.abhij33t.lld.Main

Repository Layout
- logging-library/  – Java logging library demonstrating async logging with a BlockingQueue and Executor-based workers
  - README.md       – Detailed LLD, architecture, and usage
- parking-lot/     – Parking Lot LLD with strategy-based pricing and payments
  - README.md       – Design, architecture, and usage

Notes
- Each module maintains its own README with deeper details.
- See module documentation for configuration and extension points.