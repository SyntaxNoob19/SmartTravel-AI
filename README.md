# SmartTravel

SmartTravel is an AI-powered full-stack travel planning application focused on Indian destinations. The platform helps users generate personalized itineraries, explore destination details, estimate travel budgets, view live weather insights, and save trips in one place.

The project combines a responsive frontend with a Spring Boot backend and MySQL database to create a complete travel planning experience without relying on multiple disconnected platforms.

---

## Project Goals

* Simplify travel planning through a single unified platform
* Generate personalized itineraries using AI-assisted planning
* Provide contextual travel information including budgets, maps, weather, and destination insights
* Build a beginner-friendly full-stack project demonstrating frontend, backend, database, and AI integration

---

## Features

* AI-powered itinerary generation
* User authentication (register & login)
* Personalized saved trips
* Budget estimation
* Integrated maps
* Real-time weather insights
* Destination overviews
* Responsive UI
* MySQL persistence
* Basic RAG-inspired itinerary generation using contextual travel datasets

---

## Tech Stack

### Frontend

* HTML
* CSS
* Vanilla JavaScript

### Backend

* Spring Boot
* Java 17
* Maven

### Database

* MySQL 8+

### AI Integration

* OpenRouter API
* Basic RAG-inspired itinerary generation using synthetic travel datasets

---

## Project Structure

```text
SmartTravel/
├── Backend/
├── Frontend/
├── docs/
│   ├── diagrams/
│   ├── API_REFERENCE.md
│   ├── ARCHITECTURE.md
│   ├── FLOW_DIAGRAMS.md
│   └── PROJECT_SETUP.md
├── .env.example
├── .gitignore
├── import_csv.py
├── README.md
└── datasets/
```

Detailed documentation and architecture explanations are available inside the `docs/` folder.

---

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd SmartTravel
```

### 2. Configure Environment Variables

Update backend configuration or create environment variables:

```env
OPENROUTER_API_KEY=your_api_key
MYSQL_DATABASE=smarttravel
MYSQL_USERNAME=root
MYSQL_PASSWORD=your_password
```

### 3. Start MySQL

Create the required database:

```sql
CREATE DATABASE smarttravel;
```

### 4. Run the Backend

```powershell
cd Backend

Get-NetTCPConnection -LocalPort 9090 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }

.\mvnw.cmd clean spring-boot:run
```

### 5. Open the Application

```text
http://localhost:9090/
```

---

## Documentation

Additional project documentation is available in the `docs/` directory:

* [Project Setup](docs/PROJECT_SETUP.md)
* [Architecture](docs/ARCHITECTURE.md)
* [API Reference](docs/API_REFERENCE.md)
* [Flow Diagrams](docs/FLOW_DIAGRAMS.md)
* [Frontend Improvements](docs/FRONTEND_IMPROVEMENTS.md) - Latest UI/UX enhancements

---

## Recent Frontend Improvements (June 2025)

### Budget Display Enhancements
- **Prominent daily rate banner** at the top of budget section (36px font)
- **Total budget moved to metadata strip** - visible without scrolling
- **Simplified progress bars** - removed visual clutter, focus on amounts
- **Exact category breakdown** - Hotel 40%, Food 18%, Transport 20%, Activities 22%

### Day-by-Day Itinerary Improvements
- **All places expanded by default** - no collapse/expand needed
- **Complete place details** visible - description, tips, safety advice, duration
- **Sequential numbering** - clear 1. 2. 3. ordering for activities
- **Responsive grid layout** - clean presentation on all devices

### Code Quality Improvements
- **Removed raw JSON data exposure** - "View Full Itinerary Data" button removed
- **Cleaner frontend UI** - technical details hidden from end users
- **Mobile responsive design** - seamless experience on all screen sizes

**For detailed information, see [Frontend Improvements Documentation](docs/FRONTEND_IMPROVEMENTS.md)**

---

## Architecture Overview

SmartTravel follows a simple full-stack architecture:

```text
Frontend (HTML/CSS/JavaScript)
            ↓
Spring Boot Backend
            ↓
MySQL Database
            ↓
OpenRouter AI Integration
```

The backend retrieves contextual travel information from synthetic travel datasets and enriches itinerary generation before interacting with the AI layer.

---

## Demo

Screenshots and walkthrough videos will be added soon.

---

## Future Improvements

* Hotel and transport recommendations
* Multi-user trip collaboration
* Cloud deployment support
* Enhanced AI recommendation system
* Live pricing integrations
* Personalized recommendation engine
