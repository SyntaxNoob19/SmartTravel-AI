# Deployment & Setup Guide - SmartTravel

This document provides step-by-step instructions to configure, seed, build, and deploy the SmartTravel full-stack application.

---

## 1. Prerequisites
Ensure you have the following environments configured on your system:
- **Operating System:** Windows, macOS, or Linux.
- **Java Development Kit (JDK):** Version 17 or newer.
- **Python:** Version 3.8 or newer (required for CSV utility imports).
- **MySQL Database Server:** Version 8.0 or newer.

---

## 2. Environment Configurations

1. Copy the `.env.example` file at the root directory to create a `.env` file:
   ```bash
   cp .env.example .env
   ```
2. Populate the required environment keys:
   ```properties
   DB_URL=jdbc:mysql://localhost:3306/smart_travel
   DB_USERNAME=root
   DB_PASSWORD=root
   APP_CORS_ALLOWED_ORIGINS=https://smarttravel-ai.onrender.com:5500,http://127.0.0.1:5500
   OPENROUTER_API_KEY=your_openrouter_api_key
   PLANNER_AI_MODEL=openai/gpt-4o-mini
   ```

---

## 3. Database Initialization & Seeding

1. Start your local MySQL database server.
2. Open your MySQL client and create the schema:
   ```sql
   CREATE DATABASE smart_travel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. To seed the database with the pre-compiled Indian travel spots dataset, you have two options:

   **Option A: Seeding via Maven Exec Wrapper (Recommended)**
   ```powershell
   cd Backend
   # Run the custom CsvImportTool java class to automatically parse and load
   .\mvnw.cmd exec:java -Dexec.mainClass="com.riya.smarttravel.util.CsvImportTool"
   ```

   **Option B: Seeding via Python Script**
   ```powershell
   # Install MySQL connector dependency
   pip install mysql-connector-python
   # Run the import script
   python scripts/import_csv.py
   ```

---

## 4. Launching the Backend App

1. Navigate to the `Backend/` directory:
   ```powershell
   cd Backend
   ```
2. (Optional Windows Quick Step) To kill any processes currently holding port 9090 and startup the server:
   ```powershell
   Get-NetTCPConnection -LocalPort 9090 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
   ```
3. Compile and bootstrap the application:
   ```powershell
   .\mvnw.cmd clean spring-boot:run
   ```
   The backend server starts and serves requests on port `9090`.

---

## 5. Serving and Deploying the Frontend

There are two primary methods to serve/deploy the static frontend:

### Option A: Monolithic Serving via Spring Boot (Render)
Since the frontend consists of pure HTML/CSS/JS, it can be served directly by the Spring Boot JAR at the root URL `https://smarttravel-ai.onrender.com/` due to the dynamic `WebConfig` mapping. The backend automatically forwards the root path to serve `index.html` from the static directory specified by `app.frontend.path`.

### Option B: Decoupled Deployment (Netlify)
You can deploy the `Frontend/` directory independently to static hosting services like Netlify (e.g., `https://smart-travel-planner-ai.netlify.app`).
For this decoupled architecture:
1. The frontend configuration (`Frontend/js/config.js`) dynamically detects the production hostname and routes API requests to the Render backend at `https://smarttravel-ai.onrender.com/api`.
2. Ensure the backend CORS configuration `app.cors.allowed-origins` (or the `APP_CORS_ALLOWED_ORIGINS` environment variable on Render) includes `https://smart-travel-planner-ai.netlify.app` to authorize requests and cross-origin cookies.

### Local Development / Decoupled Testing
If you are running the frontend locally via an external HTTP server (e.g., VS Code Live Server):
1. Serve the `Frontend/` folder:
   ```powershell
   cd Frontend
   python -m http.server 5500
   ```
2. Open `http://localhost:5500/` or `http://127.0.0.1:5500/` in your browser.
3. Ensure the backend CORS allowed origins includes your development port (e.g., `http://localhost:5500`).

