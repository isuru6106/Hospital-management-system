# Hospital Management System

A secure RESTful Hospital Management System developed using **Spring Boot**, **Spring Security 6**, **JWT Authentication**, and **MySQL**. The system provides API endpoints for user authentication, patient management, doctor management, appointment booking, and medical records.

## Features

- User registration and login
- JWT-based authentication
- Role-based access control
- Patient management
- Doctor management
- Appointment booking and management
- Medical record management
- Secure RESTful API endpoints
- Password encryption using Spring Security

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Backend programming |
| Spring Boot 3.5.0 | Backend framework |
| Spring Security 6 | Authentication and authorization |
| JWT | Token-based authentication |
| Spring Data JPA | Database operations |
| MySQL | Database |
| Maven | Dependency management |
| Postman | API testing |
| Hibernate | ORM |

## User Roles

The system supports role-based access control with the following roles:

- **ADMIN** – Manage patients and doctors.
- **DOCTOR** – Access doctor-related appointments and medical records.
- **PATIENT** – Book appointments and access patient-related services.
- **NURSE** – Defined as a supported user role.
- **RECEPTIONIST** – Defined as a supported user role.

## Project Structure

```text
hospital-manager/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/hospital_manager/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── .gitignore
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/isuru6106/Hospital-management-system.git
```

### 2. Navigate to the Project Directory

```bash
cd Hospital-management-system
```

### 3. Configure the MySQL Database

Start MySQL using XAMPP or another MySQL server.

Create a database:

```sql
CREATE DATABASE hospital_management;
```

Update the database configuration in:

`src/main/resources/application.properties`

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_management
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8082
```

Adjust the database port and credentials according to your local environment.

### 4. Configure the JWT Secret

Set the `JWT_SECRET` environment variable before starting the application.

PowerShell:

```powershell
$env:JWT_SECRET="YOUR_SECURE_JWT_SECRET"
```

Use a sufficiently long, secure secret key. Do not commit your actual JWT secret to GitHub.

### 5. Run the Application

Using Maven Wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, if Maven is installed:

```bash
mvn spring-boot:run
```

The application will run at:

```text
http://localhost:8082
```

## REST API Endpoints

All endpoints use the base URL:

`http://localhost:8082`

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/admin/patients` | ADMIN |
| POST | `/api/admin/doctors` | ADMIN |
| GET | `/api/doctors/appointments` | DOCTOR / ADMIN |
| POST | `/api/patients/appointments` | PATIENT |
| POST | `/api/medical-records` | DOCTOR |
| GET | `/api/medical-records/patient/{patientId}` | DOCTOR / ADMIN |

## Authentication

The system uses JSON Web Tokens (JWT) to secure API requests.

### Login

Send a POST request to:

```text
/api/auth/login
```

Example request:

```json
{
  "username": "your_username",
  "password": "your_password"
}
```

After successful authentication, the API returns a JWT token.

### Access Protected Endpoints

Include the token in the request header:

```text
Authorization: Bearer YOUR_JWT_TOKEN
```

The server validates the token and checks the user's assigned role before granting access.

## API Testing

Use **Postman** to test the REST API endpoints.

Testing includes:

- User registration
- User authentication
- JWT token generation
- JWT token validation
- Role-based authorization
- Protected API access
- Patient and doctor management
- Appointment management

## Running Tests

Execute the following command:

```bash
mvn test
```

## GitHub Repository

[Hospital Management System – GitHub](https://github.com/isuru6106/Hospital-management-system)

## Author

Developed as an academic project for the Full-Stack Software Development module at the University of Kelaniya.

---

**Note:** This project is developed for educational purposes. Configure your own database credentials and JWT secret before running it.
