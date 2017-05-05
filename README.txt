-----------------------
  System Requirements
-----------------------
- At least a 900x900 resolution screen (smaller can be used but will be unpleasant to navigate)
- JRE Version 1.7.1 or higher

----------------
   How to run
----------------

*Mac*

Client:
1. Download the Github repository and extract it to the folder of your choice
2. Navigate to the Client folder
3. Run the command javac -cp ".:guava-19.0.jar" ClueLess.java
4. Launch with java -cp ".:guava-19.0.jar" ClueLess

Server:
(Note: Only needs to be run for hosting, not connections)
1. Download the Github repository and extract it to the folder of your choice
2. Navigate to the Server folder
3. Run the command javac ClueLessServer.java
4. Launch with java ClueLess Server (note: port 5555 must be allowed)

*PC*

Client:
1. Download the Github repository and extract it to the folder of your choice
2. Navigate to the Client folder
3. Run the command javac -cp ".;guava-19.0.jar" ClueLess.java
4. Launch with java -cp ".;guava-19.0.jar" ClueLess

Server:
(Note: Only needs to be run for hosting, not connections)
1. Download the Github repository and extract it to the folder of your choice
2. Navigate to the Server folder
3. Run the command javac ClueLessServer.java
4. Launch with java ClueLess Server (note: port 5555 must be allowed)