# Email Notification Microservice

This microservice is designed to handle asynchronous email sending. It receives tasks 
via a Message Broker (RabbitMQ) from the Main API Service (e.g., Movie Service), logs 
all attempts into an ElasticSearch database, and delivers emails using an SMTP server.

The service includes a Retry Mechanism: a background scheduler runs every 5 minutes to
resend emails that failed due to server unavailability or network issues.

## Tech Stack

- Java 25 (Spring Boot)
- RabbitMQ (Message Broker)
- ElasticSearch (Logs Database)
- Kibana (Visualization)
- Docker & Docker Compose

## Prerequisites

Ensure you have the following installed:

- Docker Desktop
- Java JDK (17 or higher)
- Maven

## Installation & Repositories
This system consists of two separate parts. You need to clone both repositories to your 
local machine.

1. Clone the Email Service (Current Repository):

```
git clone https://github.com/1vanytska/email-service.git
```

2. Clone the Main API Service (The source of events):

```
git clone https://github.com/1vanytska/spring-boot-rest-api.git
```

## Configuration (.env)
Before running the application, create a file named .env in the root directory (next to 
docker-compose.yml). This file must contain your SMTP server credentials.

Example content for .env:

``` 
SMTP_HOST=sandbox.smtp.mailtrap.io 
SMTP_PORT=2525 
SMTP_USER=your_mailtrap_user 
SMTP_PASSWORD=your_mailtrap_password
```

## Build and Run

To start the entire system (Infrastructure + Email Service + Main API), follow these steps:

1. Build the JAR file for the Email Service:

```
./mvnw clean package
```

2. Build Main API as well using the same command.

3. Start all services using Docker Compose:

```
docker-compose up --build
```

This command will launch:

- RabbitMQ
- ElasticSearch
- Kibana
- Email Service
- Main API Service
- Service Endpoints

Once running, the services are accessible at:

- Main API: http://localhost:8082
- Email Service: http://localhost:8080 (internal port)
- RabbitMQ Dashboard: http://localhost:15672 (guest / guest)
- Kibana: http://localhost:5601
- ElasticSearch: http://localhost:9200

## How to Test

To test the full flow, you must first create a Director, and then create a Movie linked to that Director.

1. Create a Director (Required first!)

If you try to create a movie with a non-existent directorId, the API will return an error. Create the director first.

```
POST 
URL: http://localhost:8082/directors 
Body (JSON): 
{ 
    "name": "Christopher Nolan" 
}
```
Note the ID returned in the response (usually 1 for the first record).

2. Create a Movie (Triggers Email)

Now create a movie using the Director's ID. This action sends a message to RabbitMQ.

Method: 

```
POST 
URL: http://localhost:8082/movies 
Body (JSON): 
{ 
    "title": "Interstellar", 
    "year": 2014, 
    "genre": "Sci-Fi", 
    "directorId": 1 
}
```

(Make sure the Director with ID 1 exists, or create one first via POST http://localhost:8082/directors).

## Viewing Logs in Kibana

To see the email delivery status:

1. Open http://localhost:5601
2. Go to Management -> Stack Management -> Kibana -> Index Patterns.
3. Create a new Index Pattern with the name: email_logs*
4. Select lastAttemptTime as the timestamp field.
5. Go to Analytics -> Discover to view the logs (Status: SENT or FAILED).

## Retry Logic

- If an email fails to send, it is saved with status FAILED and an error message.
- A scheduled job runs every 5 minutes to retry failed emails.
- If the retry count exceeds 10 attempts, the status changes to CANCELLED.