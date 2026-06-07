# Project Setup

## Prerequisites
- **Java**: JDK 17 or newer
- **Maven**: Included via the Maven wrapper (`mvnw`)
- **MySQL**: Version 8+ running locally (or accessible remotely)
- **Git** (optional) for source control

## Environment Variables
Create a `.env` file in the project root (copy from `.env.example`). Required keys:
```
DB_URL=jdbc:mysql://localhost:3306/smart_travel
DB_USERNAME=your_mysql_user
DB_PASSWORD=your_mysql_password
APP_CORS_ALLOWED_ORIGINS=*
OPENAI_API_KEY=your_openai_key   # for AI itinerary generation
MAPS_API_KEY=your_maps_key       # for map integration
```
The Spring Boot application reads these values via `application.properties` placeholders.

## Database Setup
1. Start MySQL server.
2. Create the database:
```sql
CREATE DATABASE smart_travel;
```
3. Ensure the user defined above has privileges on this database.
4. Hibernate will auto‑create the required tables on first run (development mode).

## Backend Startup
```powershell
cd d:\travel-planner\Backend
# Using the Maven wrapper
./mvnw.cmd spring-boot:run
```
The API will be available at `http://localhost:9090/`.

### Useful Backend Commands
- Compile without tests: `./mvnw.cmd -q -DskipTests compile`
- Run tests: `./mvnw.cmd test`
- Clean build: `./mvnw.cmd clean`

## Frontend Startup
The frontend consists of static files. Serve them with any simple HTTP server, e.g., Python:
```powershell
cd d:\travel-planner\Frontend\smart-travel-planner
python -m http.server 5500
```
Then open `http://localhost:5500/index.html` in a browser.

## Troubleshooting
- **Backend not starting**: Verify Java 17 is installed and `JAVA_HOME` is set.
- **Database connection errors**: Check `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and that MySQL is running.
- **CORS issues**: Ensure `APP_CORS_ALLOWED_ORIGINS` includes the frontend origin (e.g., `http://localhost:5500`).
- **Frontend cannot reach API**: Confirm the backend is running on port `9090` and that the API base URL in the frontend (`js/api.js`) matches `http://localhost:9090/api`.
