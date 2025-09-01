# Room Reservation System - Auth Service

A comprehensive room reservation management system built with Spring Boot, featuring JWT authentication, role-based access control, and a complete booking workflow with notifications.

## 🚀 Features

### Core Functionality
- **User Authentication & Authorization** - JWT-based security with role management (User, Moderator, Admin)
- **Room Management** - Create, update, and manage meeting rooms with equipment tracking
- **Reservation System** - Book rooms with time slot validation and conflict prevention
- **Equipment Management** - Track and manage room equipment and resources
- **Notification System** - Real-time notifications for reservation status updates
- **Dashboard Analytics** - Comprehensive statistics and utilization reports

### Advanced Features
- **Time Slot Validation** - Prevents double-booking and validates availability
- **Status Management** - Complete reservation lifecycle (Pending → Confirmed/Cancelled)
- **Multi-role Support** - Different access levels for users, moderators, and administrators
- **Real-time Notifications** - Automatic notifications for reservation updates
- **Utilization Reports** - Room usage analytics and performance metrics

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.2
- **Security**: Spring Security with JWT
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **CI/CD**: Jenkins Pipeline
- **Testing**: JUnit 5

### Project Structure
```
src/
├── main/java/com/reservation_system/authService/
│   ├── Services/              # Business logic layer
│   ├── controllers/           # REST API endpoints
│   ├── models/               # JPA entities
│   ├── repository/           # Data access layer
│   ├── security/             # JWT & security configuration
│   ├── payload/              # Request/Response DTOs
│   └── config/               # Application configuration
├── main/resources/
│   └── application.properties # Application configuration
└── test/                     # Unit and integration tests
```

## 📊 Database Schema

### Core Entities
- **Users** - User accounts with authentication credentials
- **Roles** - Role-based access control (USER, MODERATOR, ADMIN)
- **Rooms** - Meeting rooms with capacity and type information
- **Equipment** - Room equipment and resources
- **Reservations** - Booking records with time slots and status
- **Notifications** - System notifications for users

### Key Relationships
- Users ↔ Roles (Many-to-Many)
- Users → Reservations (One-to-Many)
- Rooms → Equipment (One-to-Many)
- Rooms → Reservations (One-to-Many)
- Reservations → Notifications (One-to-Many)

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose (optional)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/ZakariaRek/room-reservation-system-auth-service
   cd room-reservation-system-auth-service
   ```

2. **Database Setup**
   ```bash
   # Start MySQL using Docker Compose
   docker-compose up mysql -d
   
   # Or configure your local MySQL
   # Create database: testdb_spring
   ```

3. **Configure Application**
   ```properties
   # src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/testdb_spring
   spring.datasource.username=root
   spring.datasource.password=root
   ```

4. **Build and Run**
   ```bash
   # Using Maven
   ./mvnw clean compile
   ./mvnw spring-boot:run
   
   # Or using Docker
   docker-compose up
   ```

5. **Access the Application**
   - API Base URL: `http://localhost:8083/api`
   - Health Check: `http://localhost:8083/test/booking/status`

## 🔐 Authentication & Authorization

### JWT Authentication Flow
1. **Register**: `POST /api/auth/signup`
2. **Login**: `POST /api/auth/signin`
3. **Access Protected Routes**: Include `Bearer {jwt_token}` in Authorization header

### User Roles
- **USER**: Basic reservation capabilities
- **MODERATOR**: Advanced management features
- **ADMIN**: Full system administration

### Sample API Calls

#### Register a new user
```bash
curl -X POST http://localhost:8083/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "role": ["user"]
  }'
```

#### Login
```bash
curl -X POST http://localhost:8083/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

## 📋 API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/signin` - User login

### User Management
- `GET /api/user` - Get all users
- `GET /api/user/{id}` - Get user by ID
- `PUT /api/user/{id}` - Update user
- `DELETE /api/user/{id}` - Delete user

### Room Management
- `GET /api/rooms` - Get all rooms
- `POST /api/rooms` - Create new room
- `GET /api/rooms/{id}` - Get room details
- `PUT /api/rooms/{id}` - Update room
- `DELETE /api/rooms/{id}` - Delete room

### Reservation Management
- `GET /api/reservations` - Get all reservations
- `POST /api/reservations` - Create new reservation
- `GET /api/reservations/user/{userId}` - Get user reservations
- `PATCH /api/reservations/{id}/status` - Update reservation status
- `GET /api/reservations/available-slots` - Check available time slots

### Dashboard & Analytics
- `GET /api/dashboard/stats` - Get system statistics
- `GET /api/dashboard/room-utilization` - Room utilization reports
- `GET /api/dashboard/reservations-by-status` - Status breakdown
- `GET /api/dashboard/reservations-by-day` - Daily usage patterns

### Notifications
- `GET /api/notifications` - Get all notifications
- `GET /api/notifications/receiver/{userId}` - Get user notifications
- `PATCH /api/notifications/{id}/status` - Update notification status

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/testdb_spring
spring.datasource.username=root
spring.datasource.password=root

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration
bezkoder.app.jwtSecret=your-secret-key
bezkoder.app.jwtExpirationMs=86400000

# Server Configuration
server.port=8083
```

### Docker Configuration
```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: testdb_spring
    ports:
      - "3306:3306"
  
  auth-service:
    build: .
    ports:
      - "8083:8083"
    depends_on:
      - mysql
```

## 🧪 Testing

### Run Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=SpringBootSecurityJwtApplicationTests

# Run with coverage
./mvnw jacoco:report
```

### Test Configuration
Tests use separate MySQL database configuration for isolation.

## 🚀 CI/CD Pipeline

### Jenkins Pipeline Features
- **Automated Testing**: Unit and integration tests
- **Docker Integration**: Containerized builds and deployments
- **Security Scanning**: Trivy vulnerability scanning
- **Email Notifications**: Build status notifications
- **Multi-stage Pipeline**: Build → Test → Security Scan → Deploy

### Pipeline Stages
1. **Checkout**: Source code retrieval
2. **MySQL Setup**: Test database initialization
3. **Maven Build**: Compilation and packaging
4. **Docker Build**: Container image creation
5. **Security Scan**: Vulnerability assessment
6. **Notifications**: Status updates via email

## 📈 Monitoring & Analytics

### Dashboard Metrics
- **System Overview**: Total rooms, users, reservations
- **Reservation Status**: Pending, confirmed, cancelled counts
- **Room Utilization**: Usage percentages and patterns
- **Daily Patterns**: Reservation trends by day of week
- **Recent Activity**: Latest reservation updates

### Business Intelligence
- Peak usage time analysis
- Room popularity metrics
- User engagement statistics
- Cancellation rate tracking

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Spring Boot best practices
- Write comprehensive tests
- Document API changes
- Use consistent code formatting
- Update README for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Related Projects

- **Frontend Application**: Angular-based user interface
- **Notification Service**: Real-time notification system
- **Reporting Service**: Advanced analytics and reporting

## 📞 Support

For support, email zakariarekhla@gmail.com or create an issue in the repository.

## 🙏 Acknowledgments

- Spring Boot Community
- JWT.io for authentication standards
- Docker for containerization
- Jenkins for CI/CD automation
