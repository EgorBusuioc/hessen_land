# 🏛️ HessenLand – a multifunctional service for the residents of the federal state of Hessen.

This project was inspired by various sources with the goal of creating an online platform for managing, updating, and monitoring personal data of Hessen residents.
It provides digital access to a wide range of public services — such as online civil signatures, vaccine certificate requests, registration of movable and immovable property, and much more.

## 🧱 Architecture

This project follows a **true microservice architecture**, where each service is an independently deployed and scalable application.  
Services communicate through **Apache Kafka** and are registered via **Eureka Discovery**.  
**Spring Cloud Gateway** handles routing and acts as the main entry point.

Each service is responsible for a well-defined domain:

- `security-service`: user authentication, registration, and token management
- `user-service`: management of citizen and registrar profiles
- `notification-service`: email dispatch and notification queuing
- `eureka-service`: service discovery and registry
- `gateway-service`: request routing and JWT token verification

> This application is being developed with the goal of ensuring high scalability and fault tolerance.

## 🛠️ Technologies

| Category        | Technologies                                                                    |
|-----------------|--------------------------------------------------------------------------------|
| Backend         | Java 21, Spring Boot 3, Spring Security, Spring Cloud, Spring Eureka, Spring Mail |
| Message Brocker | Apache Kafka                                                                   |
| Databases       | PostgreSQL (Multiple instances)                                                |
| Authentication  | JWT, BCrypt                                                                    |
| Validation      | Jakarta Validation (JSR-380)                                                   |
| DevOps          | Docker, Docker Compose                                                         |
| Other           | Lombok, ModelMapper, MapStruct                                                 |

## 🗺️ Services
### 🔐 Security and Component Isolation

This project is designed with a focus on isolated and secure architecture, where each microservice has a strictly defined responsibility and no direct external access.

Users **do not have direct access** to any microservice. All traffic goes through the **Spring Cloud Gateway**, which holds the configuration for all endpoints and defines access control rules.  
If an endpoint requires authentication, the **security module inside the Gateway** checks for the presence and validity of the JWT token in the request headers. This ensures centralized access control.

---

#### 🧾 Registration Process

Isolation is especially visible during the registration process. When a user submits their registration data, it looks something like this:

```json
{
  "email": "test@gmail.com",
  "password": "TestPassword123",
  "firstName": "TestName",
  "lastName": "TestSurname",
  "identificationNumber": "12345678901",
  "birthDate": "1999-01-01",
  "gender": "MALE"
}
```
However, only two fields — **email** and **password** — are stored in the security-service.
The remaining data is published to Kafka and consumed by the user-service, where it is processed, encrypted using AES, and stored in a separate database.

#### 📧 Account Confirmation and Password Recovery
After registration, an account activation link is sent to the user's email. Until the account is activated, it is considered inactive and cannot be used for authentication.

Password recovery via email is also implemented.
It’s important to note that the security-service communicates with the notification-service exclusively through Kafka. The notification-service does not expose any public HTTP endpoints, preventing any external interference with its operations.

