# Visual Transit Simulator

## Visual Transit Simulator Software Overview

### VTS Outline
The VTS software models vehicle transit around the University of Minnesota campus, simulating the behavior of vehicles and passengers on campus. The VTS software supports two types of vehicles: buses and trains. Vehicles provide service for a line made up of two routes: an outbound and an inbound route. Vehicles go along a route, make stops, and pick up/drop off passengers. The simulation operates over a certain number of time units; in each time unit, the VTS software updates the state of the simulation by creating passengers at stops, moving vehicles along the routes, allowing a vehicle to pick up passengers at a stop, etc. The simulation is configured using a configuration file that specifies the simulated lines, the stops of the routes, and how likely it is that a passenger will show up at a certain stop at each time unit. 

### Route Setup
- Routes are defined with both an outbound and inbound route, specified one after the other. The ending stop of the outbound route is the same location as the starting stop of the inbound route and the ending stop of the inbound route is the same location as the starting stop of the outbound route.
- Stops between the starting and ending stops of outbound and inbound routes can be at different locations.
- After a vehicle has passed a stop, it is possible for passengers to show up at stops that the vehicle has already passed; the simulator supports the creation of multiple vehicles and these vehicles will go and pick up the new passengers.
- Each vehicle has its own understanding of its own route, but the stops have relationships with multiple vehicles serving the same line.
- Vehicles do not make more than one trip in the line they serve; when a vehicle finishes both of its routes (outbound and inbound), the vehicle exits the simulation.

### VTS Software Division
The VTS software is divided into two main modules: the visualization module and the simulator module: 
- The visualization module is a web client application that runs in a browser and is written in Javascript and HTML. It displays the state of the simulation in the user's browser of choice.
- The simulator module is a web server application written in Java and divided into two parts: model classes and the webserver classes. The model classes model real-world entities and the webserver classes include the code that orchestrates the simulation.
- The visualization module and the simulator module communicate with each other using websockets.

### VTS User Interface
The user of the VTS software interacts with the visualization module using the browser, specifying how long the simulation will run, and how often new vehicles will be added to a route in the simulation. The users also specifies when to start and pause the simulation. The image below depicts the graphical user interface of the VTS software.

<img width="1158" height="632" alt="Screenshot 2025-12-31 at 12 43 57 PM" src="https://github.com/user-attachments/assets/1ebcf5c0-90cf-448e-879b-6feffca66367" />

### VTS Software Details

#### Simulation Configuration
The simulation is based on the configuration file. The following excerpt of the configuration file defines a bus line and storage facility information:

```
LINE_START, BUS_LINE, Campus Connector

ROUTE_START, East Bound

STOP, Blegen Hall, 44.972392, -93.243774, .15
STOP, Coffman, 44.973580, -93.235071, .3
STOP, Oak Street at University Avenue, 44.975392, -93.226632, .025
STOP, Transitway at 23rd Avenue SE, 44.975837, -93.222174, .05
STOP, Transitway at Commonwealth Avenue, 44.980753, -93.180669, .05
STOP, State Fairgrounds Lot S-108, 44.983375, -93.178810, .01
STOP, Buford at Gortner Avenue, 44.984540, -93.181692, .01
STOP, St. Paul Student Center, 44.984630, -93.186352, 0

ROUTE_END

ROUTE_START, West Bound

STOP, St. Paul Student Center, 44.984630, -93.186352, .35
STOP, Buford at Gortner Avenue, 44.984482, -93.181657, .05
STOP, State Fairgrounds Lot S-108, 44.983703, -93.178846, .01
STOP, Transitway at Commonwealth Avenue, 44.980663, -93.180808, .01
STOP, Thompson Center & 23rd Avenue SE, 44.976397, -93.221801, .025
STOP, Ridder Arena, 44.978058, -93.229176, .05
STOP, Pleasant Street at Jones-Eddy Circle, 44.978366, -93.236038, .1
STOP, Bruininks Hall, 44.974549, -93.236927, .3
STOP, Blegen Hall, 44.972638, -93.243591, 0

ROUTE_END

LINE_END

STORAGE_FACILITY_START

SMALL_BUSES, 3
LARGE_BUSES, 2
ELECTRIC_TRAINS, 1
DIESEL_TRAINS, 5

STORAGE_FACILITY_END
```

- The configuration line `LINE_START, BUS_LINE, Campus Connector` defines the beginning of the information belonging to a simulated line. The configuration line `ROUTE_START, East Bound` defines a the beginning of the information defining the outbound route. (The outbound route is always defined before the inbound route).
- The subsequent configuration lines are the stops in the route. Each stop has a name, a latitude, a longitude, and the probability to generate a passenger at the stop. For example, for `STOP, Blegen Hall, 44.972392, -93.243774, .15`, `Blegen Hall` is the name of the stop, `44.972392` is the latitude, `-93.243774` is the longitude, and `.15` (i.e., `0.15`) is the probability to generate a passenger at the stop.
- The last stop in a route has a probability to generate a passenger always equal to zero.
- The information inside `STORAGE_FACILITY_START` and `STORAGE_FACILITY_END` provides the number of small buses, large buses, electric trains, and diesel trains available for the simulation.

#### Running the VTS Software
To run the VTS software, you have to first start the simulator module and then start the visualization module.
- To start the simulator module, run `./gradlew appRun` (or `./gradlew clean appRun`).
- To start the visualization module, open a browser and paste `http://localhost:7777/project/web_graphics/project.html` in its address bar.
- To stop the simulator module, press the enter/return key in the terminal where you started the module.
- To stop the visualization module, close the tab of browser where you started the module.
- In rare occasions, you might experience some issues in starting the simulator module because a previous instance of the module is still running. To kill old instances, run `pkill gretty` (on Unix-like operating systems), or alternatively `kill $(pgrep gretty)`. 

#### Simulation Workflow
- When you load the visualization module in the browser, the visualization module opens a connection to the simulator module (using a websocket). The opening of the connection triggers the execution of the `WebServerSession.onOpen` method in the simulator module.
- When you click `Start` in the GUI of the visualization module, the module starts sending messages/commands to the simulator module. The messages/commands exchanged by the two modules are JSON objects.
- The simulator module processes messages received by the visualization model inside the `WebServerSession.onMessage` method, and sends messages to the visualization module using the `WebServerSession.sendJson` method.
- Once you start the simulation you can restart it only by reloading the visualization module in the browser.

## Visual Transit Simulator Features

### Core Simulation Features

### Dynamic Color Decoration System

<img width="464" height="409" alt="Screenshot 2025-12-31 at 12 50 06 PM" src="https://github.com/user-attachments/assets/56944bde-565a-46d9-95b0-5c9d2976e80e" />

Vehicles are decorated with distinctive colors using the Decorator pattern:

- **Small buses:** Maroon (RGB: 122, 0, 25)
- **Large buses:** Pink (RGB: 239, 130, 238)
- **Electric trains:** Green (RGB: 60, 179, 113)
- **Diesel trains:** Yellow (RGB: 255, 204, 51)


Transparency effects indicate vehicles affected by line issues, and the color system as a whole aids in visual identification and tracking of vehicle types.

### Simulation Controls

<img width="217" height="639" alt="Screenshot 2025-12-31 at 1 04 48 PM" src="https://github.com/user-attachments/assets/d321761c-1169-498d-b5ad-e0ff476e73a8" />

- **Start/Pause:** Toggles simulation execution in real-time
- **Restart:** Reloads the browser to reset the simulation to initial state
- **Time Step Customization:** Adjusts simulation speed and allows for customizable vehicle spawn rates (from 1-10 units)
- **Duration Control:** Specifies total simulation runtime in time units (from 1-100 units)

### Interactive Information Display
**Vehicle Hover Info:** Mouse over vehicles to view:

- Current passenger count
- Vehicle capacity
- Current CO2 consumption

**Stop Hover Info:** Mouse over stops to display:

- Number of waiting passengers
- Vehicle at stop (if applicable)

### Procedural Route Generation
- Routes configured via customizable external configuration files
- Support for multiple simultaneous lines (bus and train)
- Bidirectional routing (outbound/inbound) with automatic coordination
- Probabilistic passenger generation at each stop

### Technical Implementation Highlights
**Design Patterns**

- **Decorator Pattern:** Vehicle color decoration system with transparency effects
- **Observer Pattern:** WebSocket-based communication between modules
- **Factory Pattern:** Vehicle and passenger instantiation

**Architecture**

- **Modular Design:** Separation of visualization (JavaScript/HTML) and simulation (Java) modules
- **WebSocket Communication:** Real-time bidirectional data exchange
- **Configuration-Driven:** External file-based simulation setup
