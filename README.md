[![YouTube Channel](https://img.shields.io/badge/YouTube-KSVTech-red?logo=youtube)](https://www.youtube.com/@KsvTech)

# Installing Kafka on Windows (Article With Snapshots)

**Title:** Installing Kafka on Windows  
**Author:** KAKARA SHASI VADHAN

---

## Step 1: Download Kafka zip file
Please go to the website https://kafka.apache.org/downloads ,and click on latest version “Binary download”. At the time of creating this tutorial, the latest version is 2.5.0.

![Kafka downloads page](https://miro.medium.com/v2/resize:fit:487/1*mY7nG1aIRWRr6Gpfx7gEMw.png)

It will relink to apache.org website. Download the mirror site (first link in the website).

![Mirror link](https://miro.medium.com/v2/resize:fit:287/1*LI6wNspoP6m-Q2JFMgVskw.png)

Unzip the downloaded file (2 times) and paste the unzipped folder in `C:/` drive (preferable). Rename the folder to `kafka`. (Hint:- There should not be any space in the path).

![Kafka folder layout note](https://miro.medium.com/v2/resize:fit:482/1*SM1eJIFP6ZnPcXLCHQb35g.png)

- `C:\kafka\bin\windows` contains all files required to start Kafka and ZooKeeper on Windows.  
- `C:\kafka\config` contains all configuration files for Kafka, ZooKeeper and consists of log files. All the configurations are set up by default values; you can change them based on your requirements.

---

## Step 2: Edit configuration files
Open the file `C:\kafka\config\server.properties` in any text editor. Search for `log.dirs`. Change the default value to:

![server.properties change](https://miro.medium.com/v2/resize:fit:700/1*xTKjRz3WLbNTNZd-a3zEHQ.png)

Open the file `C:\kafka\config\zookeeper.properties` in any text editor. Search for `dataDir`. Change the default value to:

![zookeeper.properties change](https://miro.medium.com/v2/resize:fit:700/1*_TekYLX9vTEhl624mcHFpA.png)

---

## Step 3: Install Java
Go to https://www.oracle.com/java/technologies/javase-jdk14-downloads.html and download latest version of JDK Installer.

![Oracle JDK page](https://miro.medium.com/v2/resize:fit:574/1*1J6VREK6tL9i0qY9dkgOkw.png)

Java Installation is very straight forward. Please install it under `C:\Java\jdk-14.0.1\`. (Hint:- There should not be any space in the path). Add the folder location `C:\Java\jdk-14.0.1\bin` to PATH variable.

Now, check for the Java Installation by running the following command in cmd.

![java -version](https://miro.medium.com/v2/resize:fit:544/1*ee8eqY7rnXH0IERZVHKx1Q.png)

---

## Step 4: Start ZooKeeper
Open a command prompt in `C:\kafka` and run the following command:

.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

![zookeeper start cmd](https://miro.medium.com/v2/resize:fit:498/1*NamUTslnU3suk9nmMq--ow.png)

---

## Step 5: Start Kafka server
Open another command prompt in `C:\kafka` and run the following command:

.\bin\windows\kafka-server-start.bat .\config\server.properties

![kafka server start cmd](https://miro.medium.com/v2/resize:fit:458/1*2aE08xqb6-o3v891QlSWQA.png)

You will end up with following window stating Kafka server started (with your version number).

![broker started](https://miro.medium.com/v2/resize:fit:539/1*uu65Oqu-h6hOZpIT1TeZ4g.png)

---

## Step 6: Checking Kafka setup
Create a `TestTopic` by using following command (in a new cmd):

![create topic](https://miro.medium.com/v2/resize:fit:576/1*U4GAwAWoCSoN0KLzgbdW5A.png)

Start a Kafka producer by using the following command to send messages to `TestTopic`:

![producer start](https://miro.medium.com/v2/resize:fit:552/1*pnvP7J1OAOqXI8zod0Txyw.png)

After starting the Kafka Producer, you can start sending messages to `TestTopic` as follows:

![producer sending](https://miro.medium.com/v2/resize:fit:576/1*GDjbjRol8PCBvdnjqxatmw.png)

Now start another cmd and start a consumer with following command:

![consumer start](https://miro.medium.com/v2/resize:fit:576/1*OgXV8I-KjZsNhFFSPZV3RA.png)

You can see all messages which you send from Kafka producer (as follows).

![consumer output](https://miro.medium.com/v2/resize:fit:576/1*jrou8zbhiHy-xU1p1NaPuA.png)

If you see those messages, then your Kafka is installed successfully and running successfully.

---

## Produce with Postman <img src="https://www.vectorlogo.zone/logos/getpostman/getpostman-icon.svg" alt="Postman" width="22" height="22" /> and see it in your Spring Boot consumer

If you’re using the Spring Boot microservices from this repo:

- **producer-service** (HTTP on **8081**) publishes to Kafka topic **`ticket.created.v1`**
- **consumer-service** (runs on **8082**) listens to **`ticket.created.v1`** and logs messages in its IntelliJ **Run** console

**1) Start services**
1. Ensure Kafka is running (from Step 5 or via Docker Compose).
2. Run **consumer-service** (8082) in IntelliJ.
3. Run **producer-service** (8081) in IntelliJ.

**2) Send a message with Postman**
- **Method:** `POST`  
- **URL:** `http://localhost:8081/api/tickets`  
- **Headers:** `Content-Type: application/json`  
- **Body (raw JSON):**
```json
{
  "customerId": "cust-101",
  "title": "New ticket from web",
  "priority": 3
}
```
**3) Expected response (HTTP 202):**

```json

{
  "topic": "ticket.created.v1",
  "eventId": "....",
  "correlationId": "...."
}
```
**4) See it on the consumer – Open the consumer-service Run console in IntelliJ; you should see:**

[consumer] id=<uuid> corr=<id> key=cust-101 p=3 title=New ticket from web
