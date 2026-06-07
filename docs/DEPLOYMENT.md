# Deployment & Setup Specification

This document details the configuration steps, environment variables, database initialization, seeding pathways, and execution commands required to deploy and run the SmartTravel application.

---

## 1. Prerequisites

Verify that the target hosting server or local workspace meets the following software requirements:

*   **Operating System:** Windows, macOS, or Linux
*   **Java Development Kit (JDK):** Version 17 or higher
*   **MySQL Database Server:** Version 8.0 or higher
*   **Python Engine:** Version 3.8 or higher (only required if using the Python DB loader script)

---

## 2. Environment Configurations

1.  Create a `.env` configuration file in the repository root directory (use `.env.example` as a template):
    ```bash
    cp .env.example .env
    ```
2.  Set the environment properties to map your database credentials and API keys:
    ```properties
    # Database Settings
    DB_URL=jdbc:mysql://localhost:3306/smart_travel
    DB_USERNAME=root
    DB_PASSWORD=your_mysql_password

    # CORS Settings
    APP_CORS_ALLOWED_ORIGINS=http://localhost:5500,http://127.0.0.1:5500,http://localhost:9090

    # OpenRouter API Integration
    OPENROUTER_API_KEY=your_openrouter_api_key
    PLANNER_AI_MODEL=openai/gpt-4o-mini
    ```

---

## 3. Database Initialization & Ingestion

1.  Start the MySQL server instance.
2.  Connect using a database client and create the schema:
    ```sql
    CREATE DATABASE smart_travel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
3.  Ingest the pre-configured tourist destinations dataset using one of the following methods:

    ### Method A: Maven Exec Wrapper (Java-Native)
    This method compiles and runs the Java data loader utility included in the backend source code.
    ```bash
    cd Backend
    ./mvnw.cmd exec:java -Dexec.mainClass="com.riya.smarttravel.util.CsvImportTool"
    ```

    ### Method B: Python Ingestion Script
    This method executes the helper database script via Python.
    ```bash
    # Install the required MySQL client package
    pip install mysql-connector-python

    # Run the importer script
    python scripts/import_csv.py
    ```

---

## 4. Bootstrapping the Backend Server

1.  Navigate into the `Backend/` directory:
    ```bash
    cd Backend
    ```
2.  Compile, run unit tests, and launch the Spring Boot Tomcat container:
    ```bash
    ./mvnw.cmd clean spring-boot:run
    ```
    The application will bind to local port `9090`.

---

## 5. Hosting the Frontend Client

The frontend consists of static client files. The application offers two deployment architectures for serving the static files:

### Unified Hosting (Recommended)
The Spring Boot server is configured to serve the static frontend directories directly from the root path `http://localhost:9090/`.
*   Verify that the `app.frontend.path` property inside `application.properties` points to the correct location (defaulting to the local directory reference `file:///d:/travel-planner/Frontend/`).
*   Open `http://localhost:9090/` to navigate the system interface.

### Decoupled Hosting (Developer Mode)
The static client can be hosted separately on a lightweight HTTP web server (e.g. Nginx, Python http.server, or VS Code Live Server):
1.  Launch the client server from the `Frontend/` directory:
    ```bash
    cd Frontend
    python -m http.server 5500
    ```
2.  Open `http://localhost:5500/index.html` in your web browser.
3.  Ensure the origin `http://localhost:5500` is listed in the `APP_CORS_ALLOWED_ORIGINS` environment property.
