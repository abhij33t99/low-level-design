Order Notification System Design Question

Objective:

Develop a real-time order notification system for a modern e-commerce platform. The system should alert various users (customers, sellers, logistics, etc.) about significant events in an order's lifecycle, such as order placement, shipping, and delivery. The system must be designed to be extensible so that more users can be accommodated easily in the future.

Requirements:

Handling Event:

Implement a system that handles three primary order event types:

Order Placed: Triggered when a customer successfully creates an order.

Order Shipped: Triggered when the seller ships the package.

Order Delivered: Triggered when the logistics partner confirms delivery.

Messaging Stakeholders:

Notify different types of subscribers (stakeholders) for specific events:

Customer: All three events (Order Placed, Order Shipped, Order Delivered)

Seller: Only when an order is placed

Logistics (Delivery Partner): Only when an order is shipped

Only stakeholders linked to a particular order should be notified

Notification Channels:

Support multiple notification channels (configurable per user/event):

Email Notification

SMS Notification

App Push Notification

Functional Requirements:

Subscribe to Event: Stakeholders can subscribe for notifications for specific event types and preferred channels.

Manage Subscriptions:

Unsubscribe from Event

Add/Remove Notification Channels

Bonus Requirements:

Event Replay: Ability to replay notification events for a particular order Id, event type, and stakeholder, using their latest channel preferences.

Concurrency: Handle concurrent add/remove subscriptions from multiple users.

Asynchronous: Send notifications in a non-blocking way.

Notification Format:

Notifications should include:

Timestamp

Order ID

Event Type

Stakeholder ID

Channel (Email/App/SMS)

Notification Message

Sample Format:

text

[Timestamp][NOTIFICATION][Order ID][Event Type][Stakeholder ID][Channel] - <Message>

Sample Notification:

text

[18-09-2025T00:00:01][NOTIFICATION][ORDER-001][ORDER_DELIVERED][CUSTOMER-1][SMS] - "Your package with ID - ORDER-001 has been delivered"

Sample Subscription Change Message:

text

[18-09-2025T00:00:01][SUBSCRIPTION][SUB-001] - "Customer with ID CUST-1 unsubscribed from event ORDER_DELIVERED"

Or (when channels change):

text

[18-09-2025T00:00:01][SUBSCRIPTION][SUB-001] - "Customer with ID CUST-1 updated channel preference to - [EMAIL, SMS]"

Data should use in-memory data structures (e.g., map of event types → list of subscribers). No database integration required. Demonstrate code via a driver/class, unit tests, or API.

Examples:

Customer subscribes for Order Placed event with SMS as preferred channel

Seller subscribes for Order Placed event with Email as preferred channel

Delivery agent subscribes for Order Shipped event with App as preferred channel

System triggers the appropriate event and sends notifications using users’ preferences

Subscription changes (add/remove channels, unsubscribe) generate corresponding system messages


---

PlantUML Class Diagram

The following PlantUML diagram documents the notification-system classes, enums, services, factories, and strategies. You can render it using any PlantUML-compatible tool.

File: diagrams/notification-system.puml

Render tips:
- VS Code: Install the “PlantUML” extension and open the .puml file to preview.
- CLI/Docker: plantuml diagrams/notification-system.puml
- Online: Copy the file contents into any PlantUML online renderer.
