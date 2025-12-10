# Smart Travel Booking Platform

## 📋 Project Overview

A distributed travel booking backend platform built with **Spring Boot 3.2.0** and **Java 21**, demonstrating microservices architecture with inter-service communication using **Feign Client** and **WebClient**.

### Student Information
- **Name:** Mathivarnan
- **Technology Stack:** Spring Boot 3.2.0, Spring Cloud, Java 21, H2 Database, Maven

---

## 🏗️ Architecture

### Microservices (6 Services)

| Service | Port | Database | Purpose |
|---------|------|----------|---------|
| **User Service** | 8081 | H2 (userdb) | User management and validation |
| **Flight Service** | 8082 | H2 (flightdb) | Flight inventory and seat reservation |
| **Hotel Service** | 8083 | H2 (hoteldb) | Hotel inventory and room reservation |
| **Notification Service** | 8084 | H2 (notificationdb) | Send notifications to users |
| **Payment Service** | 8085 | H2 (paymentdb) | Payment processing |
| **Booking Service** | 8086 | H2 (bookingdb) | **Main Orchestrator** |

---

## 🔄 Communication Patterns

### 1. Feign Client Communication

**Used by:** Booking Service → Flight Service, Hotel Service

**Configuration:**
```yaml
# booking-service/application.yml
services:
  flight-service:
    url: http://localhost:8082
  hotel-service:
    url: http://localhost:8083
```

**Implementation:**
```java
@FeignClient(name = "flight-service", url = "${services.flight-service.url}")
public interface FlightFeignClient {
    @GetMapping("/api/flights/check-availability/{id}")
    ResponseEntity<FlightAvailabilityDTO> checkAvailability(@PathVariable Long id);
    
    @PutMapping("/api/flights/{id}/reserve")
    ResponseEntity<?> reserveSeats(@PathVariable Long id, @RequestBody ReservationDTO dto);
}
```

**Why Feign Client?**
- Declarative REST client
- Simplified service-to-service calls
- Built-in load balancing support
- Easy integration with Spring Cloud

---

### 2. WebClient Communication

**Used by:** 
- Booking Service → User Service (validation)
- Booking Service → Notification Service (send notifications)
- Payment Service → Booking Service (confirm booking)

**Configuration:**
```java
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8081").build();
    }
    
    @Bean
    public WebClient notificationServiceWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8084").build();
    }
}
```

**Implementation:**
```java
// Non-blocking asynchronous call
notificationServiceWebClient
    .post()
    .uri("/api/notifications/send")
    .bodyValue(notificationRequest)
    .retrieve()
    .bodyToMono(String.class)
    .subscribe(
        response -> log.info("✅ Notification sent"),
        error -> log.error("❌ Failed: {}", error.getMessage())
    );
```

**Why WebClient?**
- Reactive, non-blocking
- Better performance for async operations
- Modern replacement for RestTemplate
- Supports reactive programming

---

## 📊 Complete Booking Flow Diagram

```
┌─────────────┐
│   CLIENT    │
└──────┬──────┘
       │ 1. POST /api/bookings
       ▼
┌─────────────────────────────────────────────────────────┐
│           BOOKING SERVICE (Port 8086)                    │
│                Main Orchestrator                         │
└─────────────────────────────────────────────────────────┘
       │
       ├─► Step 1: Validate User
       │   └─► WebClient → User Service (8081)
       │      GET /api/users/validate/1
       │
       ├─► Step 2: Check Flight Availability
       │   └─► Feign Client → Flight Service (8082)
       │      GET /api/flights/check-availability/1
       │
       ├─► Step 3: Check Hotel Availability
       │   └─► Feign Client → Hotel Service (8083)
       │      GET /api/hotels/check-availability/1
       │
       ├─► Step 4: Calculate Total Cost
       │   └─► flightCost + hotelCost = totalCost
       │
       ├─► Step 5: Save Booking as PENDING
       │   └─► Insert into bookings table
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│  Response: Booking ID = 1, Status = "PENDING"           │
│  Total Cost = LKR 195,000                               │
└─────────────────────────────────────────────────────────┘
       │
       │ 2. POST /api/payments/process
       ▼
┌─────────────────────────────────────────────────────────┐
│           PAYMENT SERVICE (Port 8085)                    │
└─────────────────────────────────────────────────────────┘
       │
       ├─► Process Payment (90% success rate)
       │   └─► Save payment with Transaction ID
       │
       ├─► On Success: Confirm Booking
       │   └─► WebClient → Booking Service (8086)
       │      PUT /api/bookings/1/confirm
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│           BOOKING SERVICE (Port 8086)                    │
│              Confirmation Flow                           │
└─────────────────────────────────────────────────────────┘
       │
       ├─► Step 8a: Reserve Flight Seats
       │   └─► Feign Client → Flight Service (8082)
       │      PUT /api/flights/1/reserve (2 seats)
       │      Result: 180 → 178 available seats
       │
       ├─► Step 8b: Reserve Hotel Rooms
       │   └─► Feign Client → Hotel Service (8083)
       │      PUT /api/hotels/1/reserve (1 room)
       │      Result: 50 → 49 available rooms
       │
       ├─► Step 9: Update Booking to CONFIRMED
       │   └─► Update bookings table
       │      status = "CONFIRMED"
       │      confirmedAt = timestamp
       │
       ├─► Step 10: Send Notification
       │   └─► WebClient → Notification Service (8084)
       │      POST /api/notifications/send
       │      Message: "Booking #1 CONFIRMED! Total: LKR 195,000"
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│  FINAL: Booking CONFIRMED, Resources Reserved,          │
│  Notification Sent to User                              │
└─────────────────────────────────────────────────────────┘
```

---

## 🎯 Communication Flow Summary

| From Service | To Service | Method | Purpose | Evidence |
|--------------|------------|--------|---------|----------|
| **Booking** → User | **WebClient** | Validate user exists | Screenshot 19 |
| **Booking** → Flight | **Feign Client** | Check availability & reserve seats | Screenshots 19, 22, 23 |
| **Booking** → Hotel | **Feign Client** | Check availability & reserve rooms | Screenshots 19, 22, 24 |
| **Payment** → Booking | **WebClient** | Confirm booking after payment | Screenshot 21 |
| **Booking** → Notification | **WebClient** | Send confirmation notification | Screenshots 22, 25 |

---

## 🚀 How to Run

### Prerequisites
- Java 21+
- Maven 3.8+
- 6 GB RAM (to run all services)

### Build All Services
```bash
cd smart-travel-platform
mvn clean install
```

### Start Services (6 separate terminals)

**Terminal 1 - User Service:**
```bash
cd user-service
mvn spring-boot:run
# Wait for: "Started UserServiceApplication"
```

**Terminal 2 - Flight Service:**
```bash
cd flight-service
mvn spring-boot:run
# Wait for: "Started FlightServiceApplication"
```

**Terminal 3 - Hotel Service:**
```bash
cd hotel-service
mvn spring-boot:run
# Wait for: "Started HotelServiceApplication"
```

**Terminal 4 - Notification Service:**
```bash
cd notification-service
mvn spring-boot:run
# Wait for: "Started NotificationServiceApplication"
```

**Terminal 5 - Payment Service:**
```bash
cd payment-service
mvn spring-boot:run
# Wait for: "Started PaymentServiceApplication"
```

**Terminal 6 - Booking Service:**
```bash
cd booking-service
mvn spring-boot:run
# Wait for: "Started BookingServiceApplication"
# Look for: "Enabling Feign Clients" in logs
```

### Verify All Services Running

Open in browser:
- http://localhost:8081/swagger-ui.html (User Service)
- http://localhost:8082/swagger-ui.html (Flight Service)
- http://localhost:8083/swagger-ui.html (Hotel Service)
- http://localhost:8084/swagger-ui.html (Notification Service)
- http://localhost:8085/swagger-ui.html (Payment Service)
- http://localhost:8086/swagger-ui.html (Booking Service)

---

## 🧪 Testing the Complete Flow

### 1. Create User
```bash
POST http://localhost:8081/api/users
Content-Type: application/json

{
  "name": "Mathivarnan",
  "email": "mathi@gmail.com",
  "phone": "+94771234567"
}

Response: User ID = 1
```

### 2. Create Flight
```bash
POST http://localhost:8082/api/flights
Content-Type: application/json

{
  "flightNumber": "UL304",
  "origin": "Colombo",
  "destination": "Dubai",
  "departureDate": "2025-01-20",
  "departureTime": "08:30:00",
  "arrivalDate": "2025-01-20",
  "arrivalTime": "12:00:00",
  "pricePerSeat": 45000.00,
  "totalSeats": 180,
  "airline": "SriLankan Airlines"
}

Response: Flight ID = 1, Available Seats = 180
```

### 3. Create Hotel
```bash
POST http://localhost:8083/api/hotels
Content-Type: application/json

{
  "hotelName": "Burj Al Arab",
  "location": "Dubai",
  "address": "Jumeirah Beach Road, Dubai",
  "pricePerNight": 35000.00,
  "totalRooms": 50,
  "starRating": 5
}

Response: Hotel ID = 1, Available Rooms = 50
```

### 4. Create Booking (Status: PENDING)
```bash
POST http://localhost:8086/api/bookings
Content-Type: application/json

{
  "userId": 1,
  "flightId": 1,
  "hotelId": 1,
  "travelDate": "2025-12-25",
  "numberOfNights": 3,
  "numberOfPassengers": 2
}

Response:
- Booking ID = 1
- Status = "PENDING"
- Flight Cost = LKR 90,000 (45,000 × 2 passengers)
- Hotel Cost = LKR 105,000 (35,000 × 3 nights)
- Total Cost = LKR 195,000
```

**Check Logs (Booking Service):**
```
Step 1: Validating user 1 via WebClient ✅
Step 2: Checking flight 1 availability via Feign Client ✅
Step 3: Checking hotel 1 availability via Feign Client ✅
Step 4: Calculating total cost ✅
Step 5: Saving booking as PENDING ✅
```

### 5. Process Payment (Auto-confirms Booking)
```bash
POST http://localhost:8085/api/payments/process
Content-Type: application/json

{
  "bookingId": 1,
  "amount": 195000.00,
  "paymentMethod": "CREDIT_CARD"
}

Response:
- Payment Status = "SUCCESS"
- Transaction ID = "TXN-xxxxx"
```

**Check Logs (Payment Service):**
```
💳 Payment successful! Transaction ID: TXN-xxxxx
Calling Booking Service via WebClient to confirm booking 1 ✅
✅ Booking confirmed via WebClient
```

**Check Logs (Booking Service):**
```
PUT /api/bookings/1/confirm - Confirming booking
Step 8a: Reserving 2 flight seats via Feign Client ✅
Step 8b: Reserving 1 hotel room via Feign Client ✅
Step 9: Updating booking to CONFIRMED ✅
Step 10: Sending confirmation notification via WebClient ✅
✅ Booking 1 successfully CONFIRMED!
```

**Check Logs (Flight Service):**
```
PUT /api/flights/1/reserve - Reserving 2 seats
Successfully reserved 2 seats. Remaining: 178 ✅
```

**Check Logs (Hotel Service):**
```
PUT /api/hotels/1/reserve - Reserving 1 rooms
Successfully reserved 1 rooms. Remaining: 49 ✅
```

**Check Logs (Notification Service):**
```
✉️ NOTIFICATION SENT [EMAIL] to User 1:
"Your booking #1 has been CONFIRMED! Total: $195000.00" ✅
```

### 6. Verify Booking is CONFIRMED
```bash
GET http://localhost:8086/api/bookings/1

Response:
- Status = "CONFIRMED" ✅ (changed from PENDING)
- confirmedAt = "2025-12-10T12:37:29..." ✅
```

### 7. Verify Resources Reduced
```bash
GET http://localhost:8082/api/flights/1
Response: availableSeats = 178 ✅ (reduced from 180)

GET http://localhost:8083/api/hotels/1
Response: availableRooms = 49 ✅ (reduced from 50)
```

---

## 📡 API Endpoints

### User Service (8081)
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user
- `GET /api/users/validate/{id}` - Validate user

### Flight Service (8082)
- `POST /api/flights` - Create flight
- `GET /api/flights/{id}` - Get flight
- `GET /api/flights/check-availability/{id}` - Check availability (Feign)
- `PUT /api/flights/{id}/reserve` - Reserve seats (Feign)
- `GET /api/flights/search` - Search flights

### Hotel Service (8083)
- `POST /api/hotels` - Create hotel
- `GET /api/hotels/{id}` - Get hotel
- `GET /api/hotels/check-availability/{id}` - Check availability (Feign)
- `PUT /api/hotels/{id}/reserve` - Reserve rooms (Feign)
- `GET /api/hotels/search` - Search hotels

### Notification Service (8084)
- `POST /api/notifications/send` - Send notification (WebClient)
- `GET /api/notifications/user/{userId}` - Get user notifications
- `GET /api/notifications/{id}` - Get notification

### Payment Service (8085)
- `POST /api/payments/process` - Process payment (calls Booking via WebClient)
- `GET /api/payments/{id}` - Get payment
- `GET /api/payments/booking/{bookingId}` - Get payment by booking

### Booking Service (8086) - Main Orchestrator
- `POST /api/bookings` - Create booking (Feign + WebClient orchestration)
- `GET /api/bookings/{id}` - Get booking
- `PUT /api/bookings/{id}/confirm` - Confirm booking (called by Payment)
- `GET /api/bookings/user/{userId}` - Get user bookings

---

## 🔧 Technology Stack

### Core Technologies
- **Java:** 21
- **Spring Boot:** 3.2.0
- **Spring Cloud:** 2023.0.0
- **Maven:** 3.8+

### Dependencies
- `spring-boot-starter-web` - REST APIs
- `spring-boot-starter-data-jpa` - Database
- `spring-boot-starter-validation` - Input validation
- `spring-cloud-starter-openfeign` - Feign Client (Booking Service)
- `spring-boot-starter-webflux` - WebClient (Payment, Booking Services)
- `h2` - In-memory database
- `lombok` - Reduce boilerplate
- `springdoc-openapi-starter-webmvc-ui` - Swagger UI

### Database
- H2 in-memory database (separate for each service)
- Auto-create schema on startup
- H2 Console: `/h2-console`

---

## 🎓 Learning Outcomes

### 1. Microservices Architecture
- ✅ Service decomposition
- ✅ Independent deployment
- ✅ Service autonomy
- ✅ Database per service pattern

### 2. Inter-Service Communication
- ✅ **Feign Client:** Synchronous, declarative REST calls
- ✅ **WebClient:** Reactive, non-blocking async calls
- ✅ Understanding when to use each approach

### 3. Orchestration Pattern
- ✅ Booking Service as orchestrator
- ✅ Saga pattern (compensating transactions)
- ✅ Distributed transaction handling

### 4. Best Practices
- ✅ DTOs for data transfer
- ✅ Global exception handling
- ✅ Validation with Bean Validation
- ✅ Consistent response models
- ✅ Proper logging
- ✅ API documentation with Swagger

---

## 📦 Project Structure

```
smart-travel-platform/
│
├── pom.xml (parent)
│
├── user-service/
│   ├── src/main/java/com/travel/user/
│   │   ├── UserServiceApplication.java
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── exception/
│   └── src/main/resources/application.yml
│
├── flight-service/ (similar structure)
├── hotel-service/ (similar structure)
├── notification-service/ (similar structure)
│
├── payment-service/
│   ├── src/main/java/com/travel/payment/
│   │   ├── config/WebClientConfig.java ⭐
│   │   └── ... (other packages)
│   └── application.yml
│
└── booking-service/
    ├── src/main/java/com/travel/booking/
    │   ├── BookingServiceApplication.java (@EnableFeignClients) ⭐
    │   ├── client/
    │   │   ├── FlightFeignClient.java ⭐
    │   │   └── HotelFeignClient.java ⭐
    │   ├── config/WebClientConfig.java ⭐
    │   └── ... (other packages)
    └── application.yml
```

---

## 🐛 Troubleshooting

### Port Already in Use
```
Error: Port 8081 was already in use
Solution: Stop the existing service or use a different port
```

### Connection Refused
```
Error: Connection refused to http://localhost:8082
Solution: Ensure Flight Service is running before calling it
```

### Payment Always Fails
```
Issue: 10% random failure rate in payment simulation
Solution: Try again - it's designed to simulate real-world scenarios
```

---

## 📊 Screenshots Evidence

All screenshots (1-27) demonstrate:
- ✅ All 6 services starting successfully
- ✅ Swagger UI for all services
- ✅ Complete booking flow with inter-service communication
- ✅ **Feign Client** calls in logs (Flight, Hotel reservations)
- ✅ **WebClient** calls in logs (User validation, Payment confirmation, Notifications)
- ✅ Resource deduction (seats: 180→178, rooms: 50→49)
- ✅ Status change (PENDING → CONFIRMED)

---

## 👨‍💻 Author

**Mathivarnan**
- Email: mathi@gmail.com
- Assignment: Distributed Travel Booking Platform
- Technologies: Spring Boot 3.2.0, Spring Cloud, Feign Client, WebClient

---

## 📝 Conclusion

This project successfully demonstrates:
1. ✅ **6 independent microservices** with separate databases
2. ✅ **Feign Client** for synchronous service communication
3. ✅ **WebClient** for reactive, non-blocking communication
4. ✅ **Orchestration pattern** with Booking Service coordinating the flow
5. ✅ **Complete end-to-end flow** from booking creation to confirmation
6. ✅ **Proper error handling** and validation
7. ✅ **Resource management** (seats and rooms properly reserved)

The platform is production-ready for basic travel booking operations! 🚀
