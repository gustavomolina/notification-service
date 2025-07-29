# Notification Service

A system for sending notifications to users based on their subscriptions and preferred channels.

## Features

- Send messages in different categories (Sports, Finance, Movies)
- Deliver notifications through multiple channels (Email, SMS, Push Notification)
- Users receive notifications only for categories they're subscribed to
- Users receive notifications only through their preferred channels
- View notification logs with detailed information

## Technology Stack

- **Backend**: Java 24 with Spring Boot
- **Frontend**: Angular 17 with Bootstrap
- **Database**: PostgreSQL
- **Containerization**: Docker

## Architecture

The application follows a microservice architecture with:

- **Strategy Pattern** for notification delivery through different channels
- **Repository Pattern** for data access
- **DTO Pattern** for API communication
- **Dependency Injection** for loose coupling

## Prerequisites

- Docker and Docker Compose installed on your machine
- Ports 80 (frontend) and 8080 (backend) available on your host

## Running the Application

The entire application can be run with a single command using Docker Compose:

```bash
docker-compose up -d
```

This command will:
1. Build the backend service (Java Spring Boot)
2. Build the frontend service (Angular)
3. Set up the PostgreSQL database with initial data
4. Start all services and establish networking between them

## Accessing the Application

Once the containers are running, you can access the application at:

- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080/api

## Initial Data

The system comes pre-populated with:

- 5 users with different subscription preferences and notification channels
- Sample messages for each category

## API Endpoints

### Messages

- `POST /api/messages` - Create a new message
- `GET /api/messages` - Get all messages
- `GET /api/messages/{id}` - Get a message by ID

### Notifications

- `GET /api/notifications` - Get all notifications (paginated)
- `GET /api/notifications/stats` - Get notification statistics
- `GET /api/notifications/status/{sent}` - Get notifications by sent status

## Stopping the Application

To stop the application, run:

```bash
docker-compose down
```

To stop the application and remove all data (including the database volume), run:

```bash
docker-compose down -v
```

## Development

### Backend Development

To run the backend service locally for development:

```bash
cd notification-service
./mvnw spring-boot:run
```

### Frontend Development

To run the frontend service locally for development:

```bash
cd notification-frontend
npm install
npm start
```

## Testing

The application includes unit tests for services, controllers, and repositories. To run the tests:

```bash
cd notification-service
./mvnw test
```