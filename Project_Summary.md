# Trip Planner Project Summary

## Objective
The objective of this project is to build a "Trip Planner" Windows Desktop Application using Java Swing. It provides a user-friendly graphical interface to manage a travel itinerary, allowing users to add Flights, Hotels, and Activities. The project serves as a practical demonstration of core Object-Oriented Programming (OOP) principles, specifically Encapsulation, Inheritance, and Polymorphism.

## Technical Explanation of OOP Concepts

### 1. Encapsulation
Encapsulation is the bundling of data and the methods that operate on that data, while restricting direct access to some of an object's components.
*   **Implementation:** In the `ItineraryItem.java` base class, fields such as `title`, `date`, and `cost` are declared as `private`. This ensures that they cannot be directly modified or accessed from outside the class. Instead, access is mediated through `public` getter and setter methods (e.g., `getTitle()`, `setCost()`). The child classes (`Flight`, `Hotel`, `Activity`) follow this same pattern for their specific fields.

### 2. Inheritance
Inheritance is a mechanism where a new class derives properties and behaviors (methods) from an existing class.
*   **Implementation:** We created an abstract base class `ItineraryItem`. The three child classes—`Flight`, `Hotel`, and `Activity`—`extend` the `ItineraryItem` class. This means they inherit the common properties (`title`, `date`, `cost`) and behaviors without needing to rewrite them. Each child class then adds its own specific properties (e.g., `Flight` adds `airline` and `flightNumber`).

### 3. Polymorphism
Polymorphism allows objects of different classes to be treated as objects of a common superclass, and methods to behave differently based on the actual object type at runtime.
*   **Implementation:** The `ItineraryItem` class defines an abstract method `public abstract String getDisplayInfo()`. Each child class overrides this method to return a specifically formatted string containing its unique details. In the GUI (`MainDashboard.java`), when a new item is added to the `TripManager`, we iterate over an `ArrayList<ItineraryItem>` and call `getDisplayInfo()` on each item. Thanks to dynamic method dispatch (polymorphism), Java automatically calls the appropriate overridden method in the `Flight`, `Hotel`, or `Activity` class, rather than a generic base method.

## How to Run the Application

### Prerequisites
*   Java Development Kit (JDK) installed (Version 8 or higher).
*   Visual Studio Code with the Java Extension Pack installed, or any other Java IDE (like Eclipse or IntelliJ IDEA).

### Running in NetBeans
1.  Open NetBeans.
2.  Choose `File` > `Open Project...`.
3.  Select the project folder. NetBeans will open it as a Maven Java project.
4.  Wait for dependencies to download.
5.  Click **Run Project**. The included `nbactions.xml` points NetBeans to the main class: `tripplanner.ui.MainDashboard`.

Alternatively, run it from the terminal with Maven:
```bash
mvn compile
mvn exec:java
```
