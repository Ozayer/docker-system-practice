Dockerized System Information Service: 
This project consists of two microservices that gather system information and provide it through a unified HTTP endpoint. The services are containerized using Docker and managed with Docker Compose.

### Service1
A Java-based HTTP server that exposes an endpoint (`/info`) to display system information and fetch data from Service2.

### Service2
A Python Flask-based service that returns system information such as IP address, running processes, disk space, and uptime.

## Project Overview
The system information service is composed of:

- **Service1 (Java)**: Fetches and returns its own system data (IP, processes, disk space, uptime) and aggregates data from Service2.
- **Service2 (Python)**: Returns its own system data (IP, processes, disk space, uptime).

The two services run inside Docker containers and communicate with each other through HTTP requests. Service1 depends on Service2 to fetch and display combined information.

## Table of Contents
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [How to Use](#how-to-use)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Generate Status Reports](#generate-status-reports)
- [Additional Information](#additional-information)

## Technologies
- Java 11 for Service1.
- Python 3.9 and Flask for Service2.
- Docker and Docker Compose for containerization.
- Maven for Java project management and building.

## Project Structure
```plaintext
docker-compose-project/
│
├── service1/
│   ├── src/                       # Source code for Service1 (Java)
│   │   └── Main.java
│   ├── Dockerfile                 # Dockerfile for Service1
│   ├── pom.xml                    # Maven configuration for Service1
│
├── service2/
│   ├── app.py                     # Flask app for Service2
│   ├── Dockerfile                 # Dockerfile for Service2
│
├── Reports/
│   └── docker-status.txt          # Status report for Docker
│
├── docker-compose.yaml             # Docker Compose configuration for both services
├── llm.txt                         # LLM related information
├── findings.txt                    # Findings and observations
└── README.md                       # Project documentation
```

## Prerequisites
Make sure you have the following installed on your machine:

- [Docker](https://docs.docker.com/get-docker/): Install Docker
- [Docker Compose](https://docs.docker.com/compose/install/): Install Docker Compose
- [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html): Install Java
- [Maven](https://maven.apache.org/install.html): Install Maven (optional if building locally)

## Getting Started

### Step 1: Clone the repository
Clone this repository to your local machine:
```bash
git clone https://github.com/Ozayer/docker-system-practice.git
cd docker-compose-project
```

### Step 2: Build and Run with Docker Compose
Use Docker Compose to build and run both services:
```bash
docker-compose up --build
```
This command will:
- Build both Service1 and Service2 using their respective Dockerfiles.
- Start both services, making them accessible on the following ports:
    - Service1 on port 8199
    - Service2 on port 5000

### Step 3: Access the Services
Once the services are running, you can access Service1’s `/` endpoint:
```bash
curl http://localhost:8199
```
This will return a JSON response combining the system information from both Service1 and Service2.

## How to Use
Start the services using Docker Compose:
```bash
docker-compose up --build
```
Open a browser or use `curl` to access the combined system information from Service1:
```bash
curl http://localhost:8199
```
You can also access Service2 directly if needed:
```bash
curl http://localhost:5000/info
```

## Testing

### Manual Testing
You can manually test the services by hitting the `/` endpoints and verifying the returned JSON response.

**Service1:**
```bash
curl http://localhost:8199
```
Expected JSON output: Combined system info from both Service1 and Service2.

**Service2:**
```bash
curl http://localhost:5000/info
```
Expected JSON output: System info from Service2.

### Automated Testing (Optional)
You can automate testing by using a tool like Postman or by writing a simple script that hits the `/info` endpoint and verifies the response.

## Troubleshooting

### Common Issues

**Service1 returns an error or no response:**
- Make sure Service2 is up and running. Check the logs of Service2 to ensure it is responding on port 5000:
    ```bash
    docker-compose logs service2
    ```

**Network issues between services:**
- Ensure that both services are connected to the same Docker network (as defined in `docker-compose.yaml`).

**Port conflicts:**
- If port 8199 or 5000 is already in use on your machine, update the `docker-compose.yaml` file to map to different ports.

### Logs
To view logs from the services, you can use the following Docker commands:
```bash
docker-compose logs service1
docker-compose logs service2
```
These logs can help you debug any runtime errors or connectivity issues between the services.

## Generate Status Reports
After running the services, generate a status report for Docker:
```bash
docker container ls > Reports/docker-status.txt
docker network ls >> Reports/docker-status.txt
```
`docker-status.txt` includes the status of both services and the Docker network.

## Additional Information
- **findings.txt**: Contains findings and observations related to the project.
- **llm.txt**: Includes information related to the LLM (Language Model) used in the project.

## Conclusion
We now have a fully functional, Dockerized system information service composed of two microservices. We can run both services locally using Docker Compose, retrieve combined system information through a single HTTP endpoint, and easily extend or modify the project as needed.