# ✈ Trip Planner

A Java Swing desktop application for planning and managing travel itineraries.
Built as a university OOP project demonstrating **Encapsulation**, **Inheritance**, and **Polymorphism**.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| JDK         | 17 or later |
| Maven       | 3.8 or later |

---

## Project Structure

```
TripPlanner/
├── pom.xml                             ← Maven config (FlatLaf + GSON)
├── README.md
├── run.bat                             ← Run script (Windows)
└── src/
    └── main/
        └── java/
            └── tripplanner/
                ├── TripManager.java        ← Manages the item list
                ├── model/
                │   ├── ItineraryItem.java  ← Abstract base class (Encapsulation)
                │   ├── Flight.java         ← Extends ItineraryItem (Inheritance)
                │   ├── Hotel.java          ← Extends ItineraryItem (Inheritance)
                │   └── Activity.java       ← Extends ItineraryItem (Inheritance)
                ├── storage/
                │   ├── TripStorage.java    ← CSV Import / Export
                │   └── ReminderService.java← Swing Timer reminders
                └── ui/
                    ├── MainDashboard.java  ← Main window (JFrame)
                    └── InputForm.java      ← Add-item dialog (JDialog)
```

---

## How to Build & Run

### Option 1 — Maven (recommended)

```bash
# 1. Download dependencies & compile
mvn compile

# 2. Run directly with Maven
mvn exec:java -Dexec.mainClass="tripplanner.ui.MainDashboard"
```

### Option 2 — Build a runnable JAR

```bash
# Creates target/TripPlanner-runnable.jar (all dependencies included)
mvn package

# Run the JAR
java -jar target/TripPlanner-runnable.jar
```

### Option 3 — Windows batch script

Double-click `run.bat` (requires the JAR to be built first with `mvn package`).

---

## Features

| Feature | Description |
|---------|-------------|
| **Add Items** | Add Flights, Hotels, and Activities via a dialog |
| **Delete Items** | Select a row and click "Delete" |
| **Export CSV** | Save all trips to a `.csv` file |
| **Import CSV** | Load trips from a previously saved `.csv` file |
| **Dark / Light Theme** | Toggle between FlatLaf Dark and Light themes |
| **Trip Reminders** | Automatic popup for trips happening today or within 2 days |

---

## OOP Concepts Demonstrated

### 1. Encapsulation
All fields in `ItineraryItem` are `private`. Access is only through public `getTitle()`,
`getDate()`, `getCost()`, and their corresponding setters.

```java
// ItineraryItem.java
private String title;
private String date;
private double cost;

public String getTitle() { return title; }
public void   setTitle(String title) { this.title = title; }
```

### 2. Inheritance
`Flight`, `Hotel`, and `Activity` all extend `ItineraryItem`, gaining its common fields
while adding their own specific ones.

```java
// Flight.java
public class Flight extends ItineraryItem {
    private String airline;
    private String flightNumber;

    public Flight(String title, String date, double cost,
                  String airline, String flightNumber) {
        super(title, date, cost);   // call parent constructor
        this.airline      = airline;
        this.flightNumber = flightNumber;
    }
}
```

### 3. Polymorphism
`MainDashboard` stores everything as `ItineraryItem` and calls `getDisplayInfo()`
without knowing the concrete type at compile time — Java resolves it at runtime.

```java
// MainDashboard.java — same method call, different output per type
ItineraryItem item = form.getCreatedItem();
tripManager.addItem(item);          // stored as ItineraryItem
item.getDisplayInfo();              // calls Flight/Hotel/Activity override
```

---

## CSV Format

```
type,title,date,cost,extra1,extra2
Flight,Cairo-London,2024-06-15,850.00,EgyptAir,MS-777
Hotel,Hilton Cairo,2024-06-16,200.00,Cairo,3
Activity,Pyramids Tour,2024-06-17,50.00,Giza,
```

---

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| [FlatLaf](https://github.com/JFormDesigner/FlatLaf) | 3.4.1 | Modern Dark/Light Swing theme |
| [GSON](https://github.com/google/gson) | 2.10.1 | JSON support (available for future use) |
