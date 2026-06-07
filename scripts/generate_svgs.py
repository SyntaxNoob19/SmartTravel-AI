import os
from pathlib import Path

SVG_DIR = Path("docs/diagrams/svg")
SVG_DIR.mkdir(parents=True, exist_ok=True)

# Define diagram contents
diagrams = {}

# 1. Folder Structure SVG
diagrams["folder_structure.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 500" width="800" height="500">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#f8fafc" rx="12"/>
  
  <!-- Title -->
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Repository Structure</text>
  
  <!-- Root -->
  <rect x="300" y="70" width="200" height="40" rx="6" fill="#0f172a" stroke="#1e293b" stroke-width="1.5"/>
  <text x="400" y="95" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#ffffff" text-anchor="middle">SmartTravel (Root)</text>
  
  <!-- Branches -->
  <rect x="50" y="180" width="120" height="40" rx="6" fill="#1e293b" stroke="#334155" stroke-width="1.5"/>
  <text x="110" y="205" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#ffffff" text-anchor="middle">Backend/</text>
  
  <rect x="200" y="180" width="120" height="40" rx="6" fill="#1e293b" stroke="#334155" stroke-width="1.5"/>
  <text x="260" y="205" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#ffffff" text-anchor="middle">Frontend/</text>
  
  <rect x="350" y="180" width="120" height="40" rx="6" fill="#1e293b" stroke="#334155" stroke-width="1.5"/>
  <text x="410" y="205" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#ffffff" text-anchor="middle">docs/</text>
  
  <rect x="500" y="180" width="120" height="40" rx="6" fill="#1e293b" stroke="#334155" stroke-width="1.5"/>
  <text x="560" y="205" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#ffffff" text-anchor="middle">datasets/</text>
  
  <rect x="650" y="180" width="120" height="40" rx="6" fill="#1e293b" stroke="#334155" stroke-width="1.5"/>
  <text x="710" y="205" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#ffffff" text-anchor="middle">scripts/</text>
  
  <!-- Connectors -->
  <path d="M 400 110 L 400 145 M 110 145 L 710 145" stroke="#94a3b8" stroke-width="2" fill="none"/>
  <path d="M 110 145 L 110 180" stroke="#94a3b8" stroke-width="2" fill="none" marker-end="url(#arrow)"/>
  <path d="M 260 145 L 260 180" stroke="#94a3b8" stroke-width="2" fill="none" marker-end="url(#arrow)"/>
  <path d="M 410 145 L 410 180" stroke="#94a3b8" stroke-width="2" fill="none" marker-end="url(#arrow)"/>
  <path d="M 560 145 L 560 180" stroke="#94a3b8" stroke-width="2" fill="none" marker-end="url(#arrow)"/>
  <path d="M 710 145 L 710 180" stroke="#94a3b8" stroke-width="2" fill="none" marker-end="url(#arrow)"/>

  <!-- Sub docs -->
  <path d="M 410 220 L 410 280" stroke="#94a3b8" stroke-width="2" fill="none" marker-end="url(#arrow)"/>
  <rect x="340" y="280" width="140" height="80" rx="6" fill="#cbd5e1" stroke="#94a3b8" stroke-width="1.5"/>
  <text x="410" y="305" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">docs/diagrams/</text>
  <text x="410" y="325" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">docs/mermaid/</text>
  <text x="410" y="345" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">docs/diagrams/svg/</text>
</svg>"""

# 2. System Architecture SVG
diagrams["system_architecture.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 550" width="800" height="550">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#f1f5f9" rx="12"/>
  
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - 3-Tier System Architecture</text>
  
  <!-- Presentation Layer -->
  <rect x="50" y="80" width="700" height="90" rx="8" fill="#ffffff" stroke="#cbd5e1" stroke-width="1.5"/>
  <text x="70" y="110" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#2d8a83">1. Presentation Layer (Frontend)</text>
  <rect x="250" y="115" width="300" height="40" rx="6" fill="#e8f4f3" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="400" y="140" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#1f635e" text-anchor="middle">Vanilla HTML / CSS / JS Client Views</text>
  
  <!-- Application Layer -->
  <rect x="50" y="220" width="700" height="150" rx="8" fill="#ffffff" stroke="#cbd5e1" stroke-width="1.5"/>
  <text x="70" y="250" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#2d8a83">2. Application Layer (Spring Boot Backend)</text>
  
  <rect x="90" y="280" width="160" height="60" rx="6" fill="#f8fafc" stroke="#64748b" stroke-width="1.5"/>
  <text x="170" y="305" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">Web Controllers</text>
  <text x="170" y="325" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">REST Mappings / MVC</text>
  
  <rect x="300" y="280" width="200" height="60" rx="6" fill="#f8fafc" stroke="#64748b" stroke-width="1.5"/>
  <text x="400" y="305" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">Business Services</text>
  <text x="400" y="325" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">Planner, Auth, SavedTrip</text>
  
  <rect x="550" y="280" width="160" height="60" rx="6" fill="#f8fafc" stroke="#64748b" stroke-width="1.5"/>
  <text x="630" y="305" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">JPA Query Repos</text>
  <text x="630" y="325" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">Data Access layer</text>

  <!-- Data Layer -->
  <rect x="50" y="420" width="700" height="90" rx="8" fill="#ffffff" stroke="#cbd5e1" stroke-width="1.5"/>
  <text x="70" y="450" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#2d8a83">3. Data &amp; External Services Layer</text>
  
  <rect x="150" y="455" width="200" height="40" rx="6" fill="#f1f5f9" stroke="#475569" stroke-width="1.5"/>
  <text x="250" y="480" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0f172a" text-anchor="middle">MySQL DB Schema</text>
  
  <rect x="450" y="455" width="200" height="40" rx="6" fill="#f1f5f9" stroke="#475569" stroke-width="1.5"/>
  <text x="550" y="480" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0f172a" text-anchor="middle">OpenRouter AI API</text>
  
  <!-- Direct Connectors -->
  <path d="M 400 170 L 400 220" stroke="#2d8a83" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 250 370 L 250 420" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 550 370 L 550 420" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>
</svg>"""

# 3. Frontend Architecture SVG
diagrams["frontend_architecture.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 500" width="800" height="500">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#f8fafc" rx="12"/>
  
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Frontend Core Components</text>
  
  <!-- Views -->
  <rect x="50" y="80" width="180" height="380" rx="8" fill="#e2e8f0" stroke="#94a3b8" stroke-width="1.5"/>
  <text x="140" y="110" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#0f172a" text-anchor="middle">1. Views (pages/)</text>
  
  <rect x="70" y="140" width="140" height="30" rx="4" fill="#ffffff" stroke="#94a3b8" stroke-width="1"/>
  <text x="140" y="160" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">index.html (Home)</text>
  
  <rect x="70" y="190" width="140" height="30" rx="4" fill="#ffffff" stroke="#94a3b8" stroke-width="1"/>
  <text x="140" y="210" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">planner.html (Wizard)</text>
  
  <rect x="70" y="240" width="140" height="30" rx="4" fill="#ffffff" stroke="#94a3b8" stroke-width="1"/>
  <text x="140" y="260" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">itinerary.html (Plan)</text>
  
  <rect x="70" y="290" width="140" height="30" rx="4" fill="#ffffff" stroke="#94a3b8" stroke-width="1"/>
  <text x="140" y="310" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">profile.html (Dashboard)</text>
  
  <rect x="70" y="340" width="140" height="30" rx="4" fill="#ffffff" stroke="#94a3b8" stroke-width="1"/>
  <text x="140" y="360" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">group.html (Expenses)</text>

  <!-- JS Modules -->
  <rect x="300" y="80" width="450" height="380" rx="8" fill="#e0f2fe" stroke="#38bdf8" stroke-width="1.5"/>
  <text x="525" y="110" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#0369a1" text-anchor="middle">2. Modular JS (js/*)</text>
  
  <rect x="330" y="140" width="180" height="60" rx="6" fill="#ffffff" stroke="#0284c7" stroke-width="1"/>
  <text x="420" y="165" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">auth.js</text>
  <text x="420" y="185" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">Login/Register/Logout</text>
  
  <rect x="540" y="140" width="180" height="60" rx="6" fill="#ffffff" stroke="#0284c7" stroke-width="1"/>
  <text x="630" y="165" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">planner.js</text>
  <text x="630" y="185" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">Wizard validation flow</text>
  
  <rect x="330" y="230" width="180" height="60" rx="6" fill="#ffffff" stroke="#0284c7" stroke-width="1"/>
  <text x="420" y="255" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">itinerary.js</text>
  <text x="420" y="275" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">Render Plan &amp; Map View</text>
  
  <rect x="540" y="230" width="180" height="60" rx="6" fill="#ffffff" stroke="#0284c7" stroke-width="1"/>
  <text x="630" y="255" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">navbar.js</text>
  <text x="630" y="275" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">Dynamic components loader</text>
  
  <!-- Base Client API -->
  <rect x="435" y="340" width="180" height="60" rx="6" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="525" y="365" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#1f635e" text-anchor="middle">api.js</text>
  <text x="525" y="385" font-family="'Inter', sans-serif" font-size="10" fill="#115e59" text-anchor="middle">Fetch Wrapper &amp; Base URL</text>

  <!-- Connectors -->
  <path d="M 230 240 L 300 240" stroke="#64748b" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 420 200 L 420 340" stroke="#0284c7" stroke-width="1.5" fill="none"/>
  <path d="M 630 200 L 630 310 L 525 310 L 525 340" stroke="#0284c7" stroke-width="1.5" fill="none"/>
  <path d="M 420 290 L 420 310 L 525 310 L 525 340" stroke="#0284c7" stroke-width="1.5" fill="none"/>
</svg>"""

# 4. Backend Architecture SVG
diagrams["backend_architecture.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 500" width="800" height="500">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#fafafa" rx="12"/>
  
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Spring Boot Backend Layers</text>
  
  <!-- REST endpoints -->
  <rect x="50" y="80" width="700" height="60" rx="6" fill="#e0f2fe" stroke="#0284c7" stroke-width="1.5"/>
  <text x="400" y="115" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#0369a1" text-anchor="middle">REST API Controllers (/api/auth, /api/planner, /api/trips, /api/budget)</text>
  
  <!-- Services -->
  <rect x="50" y="200" width="450" height="120" rx="8" fill="#f0fdf4" stroke="#22c55e" stroke-width="1.5"/>
  <text x="275" y="230" font-family="'Inter', sans-serif" font-size="13" font-weight="bold" fill="#15803d" text-anchor="middle">Business Services Layer</text>
  
  <rect x="80" y="250" width="160" height="50" rx="4" fill="#ffffff" stroke="#16a34a" stroke-width="1"/>
  <text x="160" y="280" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">PlannerService (RAG/Clustering)</text>
  
  <rect x="280" y="250" width="180" height="50" rx="4" fill="#ffffff" stroke="#16a34a" stroke-width="1"/>
  <text x="370" y="280" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">PlannerAiService (OpenRouter client)</text>
  
  <!-- Security -->
  <rect x="540" y="200" width="210" height="120" rx="8" fill="#fff1f2" stroke="#f43f5e" stroke-width="1.5"/>
  <text x="645" y="230" font-family="'Inter', sans-serif" font-size="13" font-weight="bold" fill="#be123c" text-anchor="middle">Session Security Filter</text>
  
  <rect x="560" y="250" width="170" height="50" rx="4" fill="#ffffff" stroke="#e11d48" stroke-width="1"/>
  <text x="645" y="280" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">SessionAuthFilter</text>

  <!-- Repos & DB -->
  <rect x="50" y="380" width="700" height="60" rx="6" fill="#f3e8ff" stroke="#a855f7" stroke-width="1.5"/>
  <text x="400" y="415" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#7e22ce" text-anchor="middle">JPA Repositories (Place, User, SavedTrip) &amp; MySQL DB</text>

  <!-- Connectors -->
  <path d="M 275 140 L 275 200" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 645 140 L 645 200" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 275 320 L 275 380" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 645 320 L 645 380" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>
</svg>"""

# 5. Dependency SVG
diagrams["dependency.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 400" width="800" height="400">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#f8fafc" rx="12"/>
  
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - POM XML Library Dependencies</text>
  
  <!-- Root pom -->
  <rect x="300" y="70" width="200" height="50" rx="6" fill="#0f172a" stroke="#1e293b" stroke-width="1.5"/>
  <text x="400" y="100" font-family="'Inter', sans-serif" font-size="14" font-weight="bold" fill="#ffffff" text-anchor="middle">pom.xml (Maven Core)</text>
  
  <!-- Dependencies -->
  <rect x="50" y="200" width="150" height="40" rx="6" fill="#e0f2fe" stroke="#0284c7" stroke-width="1"/>
  <text x="125" y="225" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0369a1" text-anchor="middle">spring-boot-web</text>
  
  <rect x="230" y="200" width="150" height="40" rx="6" fill="#e0f2fe" stroke="#0284c7" stroke-width="1"/>
  <text x="305" y="225" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0369a1" text-anchor="middle">spring-data-jpa</text>
  
  <rect x="410" y="200" width="150" height="40" rx="6" fill="#e0f2fe" stroke="#0284c7" stroke-width="1"/>
  <text x="485" y="225" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0369a1" text-anchor="middle">mysql-connector-j</text>
  
  <rect x="590" y="200" width="150" height="40" rx="6" fill="#e0f2fe" stroke="#0284c7" stroke-width="1"/>
  <text x="665" y="225" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0369a1" text-anchor="middle">commons-csv (v1.14.1)</text>
  
  <rect x="140" y="280" width="150" height="40" rx="6" fill="#e0f2fe" stroke="#0284c7" stroke-width="1"/>
  <text x="215" y="305" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0369a1" text-anchor="middle">okhttp (v4.11.0)</text>
  
  <rect x="320" y="280" width="150" height="40" rx="6" fill="#e0f2fe" stroke="#0284c7" stroke-width="1"/>
  <text x="395" y="305" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0369a1" text-anchor="middle">lombok (v1.18.34)</text>
  
  <rect x="500" y="280" width="150" height="40" rx="6" fill="#e0f2fe" stroke="#0284c7" stroke-width="1"/>
  <text x="575" y="305" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0369a1" text-anchor="middle">spring-security-crypto</text>

  <!-- Connectors -->
  <path d="M 400 120 L 400 160 M 125 160 L 665 160" stroke="#94a3b8" stroke-width="1.5" fill="none"/>
  <path d="M 125 160 L 125 200" stroke="#94a3b8" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 305 160 L 305 200" stroke="#94a3b8" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 485 160 L 485 200" stroke="#94a3b8" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 665 160 L 665 200" stroke="#94a3b8" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  
  <path d="M 400 160 L 400 250 M 215 250 L 575 250" stroke="#94a3b8" stroke-width="1.5" fill="none"/>
  <path d="M 215 250 L 215 280" stroke="#94a3b8" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 395 250 L 395 280" stroke="#94a3b8" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 575 250 L 575 280" stroke="#94a3b8" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
</svg>"""

# 6. Navigation Flow SVG
diagrams["navigation_flow.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="800" height="450">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#fafafa" rx="12"/>
  
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Frontend Navigation &amp; Pages Flow</text>
  
  <!-- Pages -->
  <rect x="50" y="80" width="160" height="40" rx="6" fill="#2d8a83" stroke="#1f635e" stroke-width="1.5"/>
  <text x="130" y="105" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#ffffff" text-anchor="middle">index.html (Home)</text>
  
  <rect x="300" y="80" width="180" height="40" rx="6" fill="#e8f4f3" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="390" y="105" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#1f635e" text-anchor="middle">planner.html (Wizard Form)</text>
  
  <rect x="570" y="80" width="180" height="40" rx="6" fill="#e8f4f3" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="660" y="105" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#1f635e" text-anchor="middle">itinerary.html (Plan view)</text>
  
  <!-- Auth Split -->
  <polygon points="660,180 720,210 660,240 600,210" fill="#cbd5e1" stroke="#94a3b8" stroke-width="1.5"/>
  <text x="660" y="215" font-family="'Inter', sans-serif" font-size="10" font-weight="bold" fill="#0f172a" text-anchor="middle">Logged In?</text>
  
  <rect x="300" y="190" width="180" height="40" rx="6" fill="#ffe4e6" stroke="#f43f5e" stroke-width="1.5"/>
  <text x="390" y="215" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#be123c" text-anchor="middle">login.html (Auth screen)</text>
  
  <rect x="570" y="320" width="180" height="40" rx="6" fill="#e0f2fe" stroke="#0369a1" stroke-width="1.5"/>
  <text x="660" y="345" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0369a1" text-anchor="middle">profile.html (Saved Trips)</text>
  
  <rect x="300" y="320" width="180" height="40" rx="6" fill="#e0f2fe" stroke="#0369a1" stroke-width="1.5"/>
  <text x="390" y="345" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0369a1" text-anchor="middle">group.html (Shared Budget)</text>
  
  <!-- Flows -->
  <path d="M 210 100 L 300 100" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 480 100 L 570 100" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 660 120 L 660 180" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  
  <path d="M 600 210 L 480 210" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <text x="540" y="200" font-family="'Inter', sans-serif" font-size="10" fill="#be123c" text-anchor="middle">No</text>
  
  <path d="M 660 240 L 660 320" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <text x="670" y="275" font-family="'Inter', sans-serif" font-size="10" fill="#15803d">Yes</text>
  
  <path d="M 570 340 L 480 340" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
</svg>"""

# 7. API Flow SVG
diagrams["api_flow.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="800" height="450">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#f8fafc" rx="12"/>
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="18" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - API Generation Sequence</text>
  
  <!-- Lifelines -->
  <line x1="150" y1="80" x2="150" y2="400" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  <line x1="350" y1="80" x2="350" y2="400" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  <line x1="550" y1="80" x2="550" y2="400" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  
  <rect x="100" y="50" width="100" height="30" rx="4" fill="#0f172a" stroke="#1e293b"/>
  <text x="150" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">Client View</text>
  
  <rect x="300" y="50" width="100" height="30" rx="4" fill="#2d8a83" stroke="#1f635e"/>
  <text x="350" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">Spring Backend</text>
  
  <rect x="500" y="50" width="100" height="30" rx="4" fill="#64748b" stroke="#475569"/>
  <text x="550" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">OpenRouter Cloud</text>

  <!-- Steps -->
  <g>
    <path d="M 150 120 L 350 120" stroke="#0f172a" stroke-width="1.5" marker-end="url(#arrow)"/>
    <text x="250" y="115" font-family="'Inter', sans-serif" font-size="10" font-weight="bold" text-anchor="middle">1. POST /api/planner/generate</text>
  </g>
  
  <g>
    <path d="M 350 160 A 40 40 0 0 1 350 200" stroke="#2d8a83" stroke-width="1.5" fill="none" marker-end="url(#arrow)"/>
    <text x="400" y="185" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e">2. Query &amp; Load CSV Places</text>
  </g>
  
  <g>
    <path d="M 350 240 L 550 240" stroke="#0f172a" stroke-width="1.5" marker-end="url(#arrow)"/>
    <text x="450" y="235" font-family="'Inter', sans-serif" font-size="10" font-weight="bold" text-anchor="middle">3. POST /chat/completions (Prompt context)</text>
  </g>
  
  <g>
    <path d="M 550 290 L 350 290" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
    <text x="450" y="285" font-family="'Inter', sans-serif" font-size="10" text-anchor="middle">4. Return LLM structured JSON response</text>
  </g>
  
  <g>
    <path d="M 350 340 L 150 340" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
    <text x="250" y="335" font-family="'Inter', sans-serif" font-size="10" text-anchor="middle">5. Return 200 OK (Itinerary JSON)</text>
  </g>
</svg>"""

# 8. Authentication Flow SVG
diagrams["authentication_flow.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="800" height="450">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#fafafa" rx="12"/>
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="18" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Session Authentication Flow</text>
  
  <!-- Lifelines -->
  <line x1="150" y1="80" x2="150" y2="400" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  <line x1="350" y1="80" x2="350" y2="400" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  <line x1="550" y1="80" x2="550" y2="400" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  
  <rect x="100" y="50" width="100" height="30" rx="4" fill="#0f172a" stroke="#1e293b"/>
  <text x="150" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">Browser client</text>
  
  <rect x="300" y="50" width="100" height="30" rx="4" fill="#2d8a83" stroke="#1f635e"/>
  <text x="350" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">Spring Boot API</text>
  
  <rect x="500" y="50" width="100" height="30" rx="4" fill="#64748b" stroke="#475569"/>
  <text x="550" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">MySQL Database</text>

  <!-- Steps -->
  <g>
    <path d="M 150 120 L 350 120" stroke="#0f172a" stroke-width="1.5" marker-end="url(#arrow)"/>
    <text x="250" y="115" font-family="'Inter', sans-serif" font-size="10" font-weight="bold" text-anchor="middle">1. POST /api/auth/login (email, password)</text>
  </g>
  
  <g>
    <path d="M 350 160 L 550 160" stroke="#0f172a" stroke-width="1.5" marker-end="url(#arrow)"/>
    <text x="450" y="155" font-family="'Inter', sans-serif" font-size="10" text-anchor="middle">2. Select UserAccount by email</text>
  </g>
  
  <g>
    <path d="M 550 200 L 350 200" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
    <text x="450" y="195" font-family="'Inter', sans-serif" font-size="10" text-anchor="middle">3. Return user entity + bcrypt hash</text>
  </g>
  
  <g>
    <path d="M 350 240 A 40 40 0 0 1 350 280" stroke="#2d8a83" stroke-width="1.5" fill="none" marker-end="url(#arrow)"/>
    <text x="400" y="265" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e">4. Verify bcrypt password matches</text>
  </g>
  
  <g>
    <path d="M 350 320 L 150 320" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
    <text x="250" y="315" font-family="'Inter', sans-serif" font-size="10" font-weight="bold" fill="#1f635e" text-anchor="middle">5. Return 200 OK + session ID Cookie</text>
  </g>
</svg>"""

# 9. Use Case SVG
diagrams["use_case.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 500" width="800" height="500">
  <rect width="100%" height="100%" fill="#f8fafc" rx="12"/>
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Use Case Diagram</text>
  
  <!-- Actor -->
  <g transform="translate(100, 200)">
    <circle cx="0" cy="-40" r="15" fill="none" stroke="#0f172a" stroke-width="2.5"/>
    <line x1="0" y1="-25" x2="0" y2="15" stroke="#0f172a" stroke-width="2.5"/>
    <line x1="-20" y1="-10" x2="20" y2="-10" stroke="#0f172a" stroke-width="2.5"/>
    <line x1="0" y1="15" x2="-15" y2="45" stroke="#0f172a" stroke-width="2.5"/>
    <line x1="0" y1="15" x2="15" y2="45" stroke="#0f172a" stroke-width="2.5"/>
    <text x="0" y="70" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0f172a" text-anchor="middle">Traveler</text>
  </g>

  <!-- Boundary -->
  <rect x="250" y="60" width="450" height="400" rx="8" fill="#ffffff" stroke="#94a3b8" stroke-width="1.5"/>
  <text x="270" y="85" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#475569">SmartTravel System</text>
  
  <!-- Use Cases -->
  <rect x="290" y="110" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="370" y="129" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">Register Account</text>
  
  <rect x="490" y="110" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="570" y="129" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">Login Session</text>
  
  <rect x="290" y="180" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="370" y="199" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">Generate AI Itinerary</text>
  
  <rect x="490" y="180" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="570" y="199" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">Customize Parameters</text>
  
  <rect x="290" y="250" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="370" y="269" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">View Weather &amp; Images</text>
  
  <rect x="490" y="250" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="570" y="269" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">View Maps Coordinates</text>
  
  <rect x="290" y="320" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="370" y="339" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">Save Itinerary to Profile</text>
  
  <rect x="490" y="320" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="570" y="339" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">Manage Saved Trips</text>
  
  <rect x="390" y="390" width="160" height="30" rx="15" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1"/>
  <text x="470" y="409" font-family="'Inter', sans-serif" font-size="10" fill="#1f635e" text-anchor="middle">Split Companion Expenses</text>

  <!-- Links -->
  <line x1="140" y1="210" x2="290" y2="125" stroke="#94a3b8" stroke-width="1"/>
  <line x1="140" y1="210" x2="490" y2="125" stroke="#94a3b8" stroke-width="1"/>
  <line x1="140" y1="210" x2="290" y2="195" stroke="#94a3b8" stroke-width="1"/>
  <line x1="140" y1="210" x2="490" y2="195" stroke="#94a3b8" stroke-width="1"/>
  <line x1="140" y1="210" x2="290" y2="265" stroke="#94a3b8" stroke-width="1"/>
  <line x1="140" y1="210" x2="490" y2="265" stroke="#94a3b8" stroke-width="1"/>
  <line x1="140" y1="210" x2="290" y2="335" stroke="#94a3b8" stroke-width="1"/>
  <line x1="140" y1="210" x2="490" y2="335" stroke="#94a3b8" stroke-width="1"/>
  <line x1="140" y1="210" x2="390" y2="405" stroke="#94a3b8" stroke-width="1"/>
</svg>"""

# 10. Activity SVG
diagrams["activity.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 550" width="800" height="550">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#fafafa" rx="12"/>
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="18" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Itinerary Lifecycle Activity View</text>
  
  <circle cx="400" cy="70" r="10" fill="#000000"/>
  
  <rect x="280" y="110" width="240" height="40" rx="8" fill="#e8f4f3" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="400" y="135" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#1f635e" text-anchor="middle">1. User Inputs Destinations &amp; Preferences</text>
  
  <rect x="280" y="180" width="240" height="40" rx="8" fill="#e8f4f3" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="400" y="205" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#1f635e" text-anchor="middle">2. DB matches local CSV context rows (RAG)</text>
  
  <rect x="280" y="250" width="240" height="40" rx="8" fill="#e8f4f3" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="400" y="275" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#1f635e" text-anchor="middle">3. Issues request template to OpenRouter</text>
  
  <polygon points="400,310 440,335 400,360 360,335" fill="#cbd5e1" stroke="#94a3b8" stroke-width="1.5"/>
  <text x="400" y="339" font-family="'Inter', sans-serif" font-size="9" font-weight="bold" fill="#0f172a" text-anchor="middle">AI works?</text>
  
  <rect x="100" y="380" width="200" height="40" rx="8" fill="#fef08a" stroke="#ca8a04" stroke-width="1.5"/>
  <text x="200" y="405" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#854d0e" text-anchor="middle">Rule-Based Local Fallback</text>
  
  <rect x="500" y="380" width="200" height="40" rx="8" fill="#bbf7d0" stroke="#16a34a" stroke-width="1.5"/>
  <text x="600" y="405" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#166534" text-anchor="middle">Renders LLM structured Plan</text>
  
  <circle cx="400" cy="490" r="10" fill="none" stroke="#000000" stroke-width="2"/>
  <circle cx="400" cy="490" r="6" fill="#000000"/>

  <!-- Lines -->
  <path d="M 400 80 L 400 110" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)"/>
  <path d="M 400 150 L 400 180" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)"/>
  <path d="M 400 220 L 400 250" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)"/>
  <path d="M 400 290 L 400 310" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)"/>
  
  <path d="M 360 335 L 200 335 L 200 380" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <text x="240" y="325" font-family="'Inter', sans-serif" font-size="10" fill="#be123c" text-anchor="middle">No</text>
  
  <path d="M 440 335 L 600 335 L 600 380" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <text x="560" y="325" font-family="'Inter', sans-serif" font-size="10" fill="#15803d" text-anchor="middle">Yes</text>
  
  <path d="M 200 420 L 200 450 L 400 450 L 400 480" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 600 420 L 600 450 L 400 450 L 400 480" stroke="#475569" stroke-width="1.5" fill="none"/>
</svg>"""

# 11. Class Diagram SVG
diagrams["class_diagram.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 650" width="800" height="650">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#f8fafc" rx="12"/>
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Core JPA Entity Class Model</text>
  
  <!-- Class UserAccount -->
  <rect x="50" y="80" width="200" height="130" rx="6" fill="#ffffff" stroke="#475569" stroke-width="1.5"/>
  <rect x="50" y="80" width="200" height="30" rx="6" fill="#e2e8f0" stroke="#475569" stroke-width="1.5"/>
  <text x="150" y="100" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0f172a" text-anchor="middle">UserAccount</text>
  <text x="60" y="130" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ Long id</text>
  <text x="60" y="155" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ String email</text>
  <text x="60" y="180" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ String passwordHash</text>
  
  <!-- Class SavedTrip -->
  <rect x="50" y="270" width="200" height="150" rx="6" fill="#ffffff" stroke="#475569" stroke-width="1.5"/>
  <rect x="50" y="270" width="200" height="30" rx="6" fill="#e2e8f0" stroke="#475569" stroke-width="1.5"/>
  <text x="150" y="290" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0f172a" text-anchor="middle">SavedTrip</text>
  <text x="60" y="320" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ Long id</text>
  <text x="60" y="340" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ String tripName</text>
  <text x="60" y="360" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ String destination</text>
  <text x="60" y="380" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ String plannerResponseJson</text>

  <!-- Class BudgetPlan -->
  <rect x="300" y="80" width="200" height="150" rx="6" fill="#ffffff" stroke="#475569" stroke-width="1.5"/>
  <rect x="300" y="80" width="200" height="30" rx="6" fill="#e2e8f0" stroke="#475569" stroke-width="1.5"/>
  <text x="400" y="100" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0f172a" text-anchor="middle">BudgetPlan</text>
  <text x="310" y="130" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ Long id</text>
  <text x="310" y="150" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ Double totalAmount</text>
  <text x="310" y="170" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ List&lt;String&gt; memberNames</text>
  <text x="310" y="190" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ List&lt;Expense&gt; expenses</text>

  <!-- Class Expense -->
  <rect x="550" y="80" width="200" height="130" rx="6" fill="#ffffff" stroke="#475569" stroke-width="1.5"/>
  <rect x="550" y="80" width="200" height="30" rx="6" fill="#e2e8f0" stroke="#475569" stroke-width="1.5"/>
  <text x="650" y="100" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#0f172a" text-anchor="middle">Expense (Embeddable)</text>
  <text x="560" y="130" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ String name (paidBy)</text>
  <text x="560" y="155" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ Double amount</text>
  <text x="560" y="180" font-family="'Inter', sans-serif" font-size="10" fill="#334155">+ String description</text>

  <!-- Services -->
  <rect x="300" y="270" width="450" height="150" rx="6" fill="#f0fdfa" stroke="#2d8a83" stroke-width="1.5"/>
  <rect x="300" y="270" width="450" height="30" rx="6" fill="#ccfbf1" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="525" y="290" font-family="'Inter', sans-serif" font-size="12" font-weight="bold" fill="#1f635e" text-anchor="middle">PlannerService &amp; PlannerAiService Components</text>
  <text x="310" y="320" font-family="'Inter', sans-serif" font-size="11" fill="#0f172a">PlannerService.generate(PlannerRequest) : PlannerResponseDto</text>
  <text x="310" y="345" font-family="'Inter', sans-serif" font-size="11" fill="#0f172a">PlannerAiService.generateFallbackItinerary(...) : AiFallbackResult</text>
  <text x="310" y="370" font-family="'Inter', sans-serif" font-size="11" fill="#0f172a">PlannerAiService.enhanceItinerary(...) : Optional&lt;AiEnhancementDto&gt;</text>

  <!-- Links -->
  <path d="M 150 210 L 150 270" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 250 145 L 300 145" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" fill="none"/>
  <path d="M 500 145 L 550 145" stroke="#475569" stroke-width="1.5" fill="none"/>
</svg>"""

# 12. Component SVG
diagrams["component.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="800" height="450">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#fafafa" rx="12"/>
  
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="18" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Logical Components Diagram</text>
  
  <!-- Frontend -->
  <rect x="50" y="80" width="300" height="320" rx="8" fill="#ffffff" stroke="#94a3b8" stroke-width="1.5"/>
  <text x="70" y="110" font-family="'Inter', sans-serif" font-size="13" font-weight="bold" fill="#334155">Frontend Application Views</text>
  
  <rect x="80" y="140" width="240" height="60" rx="4" fill="#f8fafc" stroke="#cbd5e1" stroke-width="1"/>
  <text x="200" y="165" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">HTML/CSS Page layouts</text>
  <text x="200" y="185" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">index.html, pages/*, design-tokens.css</text>
  
  <rect x="80" y="240" width="240" height="60" rx="4" fill="#f8fafc" stroke="#cbd5e1" stroke-width="1"/>
  <text x="200" y="265" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#0f172a" text-anchor="middle">JS Page Controllers</text>
  <text x="200" y="285" font-family="'Inter', sans-serif" font-size="10" fill="#475569" text-anchor="middle">planner.js, itinerary.js, auth.js</text>

  <!-- Backend -->
  <rect x="450" y="80" width="300" height="320" rx="8" fill="#ffffff" stroke="#cbd5e1" stroke-width="1.5"/>
  <text x="470" y="110" font-family="'Inter', sans-serif" font-size="13" font-weight="bold" fill="#334155">Spring Boot Services</text>
  
  <rect x="480" y="140" width="240" height="60" rx="4" fill="#e8f4f3" stroke="#2d8a83" stroke-width="1"/>
  <text x="600" y="165" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#1f635e" text-anchor="middle">REST API Endpoints</text>
  <text x="600" y="185" font-family="'Inter', sans-serif" font-size="10" fill="#115e59" text-anchor="middle">AuthController, PlannerController</text>
  
  <rect x="480" y="240" width="240" height="60" rx="4" fill="#e8f4f3" stroke="#2d8a83" stroke-width="1"/>
  <text x="600" y="265" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#1f635e" text-anchor="middle">Core Services Layer</text>
  <text x="600" y="285" font-family="'Inter', sans-serif" font-size="10" fill="#115e59" text-anchor="middle">PlannerService, SavedTripService</text>

  <!-- Connectors -->
  <path d="M 320 270 L 480 270" stroke="#2d8a83" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 320 170 L 480 170" stroke="#64748b" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
</svg>"""

# 13. Deployment SVG
diagrams["deployment.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="800" height="450">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#f8fafc" rx="12"/>
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="20" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Deployment Model &amp; Interfaces</text>
  
  <!-- Nodes -->
  <rect x="50" y="90" width="200" height="100" rx="8" fill="#ffffff" stroke="#94a3b8" stroke-width="1.5"/>
  <text x="150" y="120" font-family="'Inter', sans-serif" font-size="13" font-weight="bold" fill="#0f172a" text-anchor="middle">User Client Machine</text>
  <rect x="70" y="135" width="160" height="40" rx="4" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="150" y="160" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">Browser (Chrome/Firefox)</text>
  
  <rect x="300" y="90" width="220" height="180" rx="8" fill="#ffffff" stroke="#2d8a83" stroke-width="1.5"/>
  <text x="410" y="120" font-family="'Inter', sans-serif" font-size="13" font-weight="bold" fill="#1f635e" text-anchor="middle">JVM Runtime Host (JRE 17)</text>
  <rect x="320" y="135" width="180" height="50" rx="4" fill="#e8f4f3" stroke="#2d8a83"/>
  <text x="410" y="165" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#1f635e" text-anchor="middle">Spring Boot JAR App</text>
  
  <rect x="320" y="200" width="180" height="50" rx="4" fill="#f8fafc" stroke="#cbd5e1"/>
  <text x="410" y="230" font-family="'Inter', sans-serif" font-size="11" fill="#334155" text-anchor="middle">india_travel_dataset.csv</text>
  
  <rect x="560" y="90" width="200" height="100" rx="8" fill="#ffffff" stroke="#a855f7" stroke-width="1.5"/>
  <text x="660" y="120" font-family="'Inter', sans-serif" font-size="13" font-weight="bold" fill="#7e22ce" text-anchor="middle">MySQL Database</text>
  <rect x="580" y="135" width="160" height="40" rx="4" fill="#faf5ff" stroke="#d8b4fe"/>
  <text x="660" y="160" font-family="'Inter', sans-serif" font-size="11" fill="#7e22ce" text-anchor="middle">MySQL DBMS 8.0+ (Port 3306)</text>
  
  <rect x="430" y="320" width="200" height="80" rx="8" fill="#f8fafc" stroke="#64748b" stroke-width="1.5"/>
  <text x="530" y="350" font-family="'Inter', sans-serif" font-size="13" font-weight="bold" fill="#0f172a" text-anchor="middle">OpenRouter Cloud</text>
  <text x="530" y="375" font-family="'Inter', sans-serif" font-size="11" fill="#475569" text-anchor="middle">LLM API Router (HTTPS)</text>

  <!-- Connectors -->
  <path d="M 250 140 L 300 140" stroke="#2d8a83" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 500 140 L 560 140" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)"/>
  <path d="M 410 270 L 410 320" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)"/>
</svg>"""

# 14. Sequence SVG
diagrams["sequence.svg"] = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 550" width="800" height="550">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 2 L 10 5 L 0 8 z" fill="#475569"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="#fafafa" rx="12"/>
  <text x="400" y="35" font-family="'Inter', sans-serif" font-size="18" font-weight="bold" fill="#0f172a" text-anchor="middle">SmartTravel - Itinerary Generation Sequence View</text>
  
  <!-- Lifelines -->
  <line x1="100" y1="80" x2="100" y2="500" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  <line x1="240" y1="80" x2="240" y2="500" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  <line x1="380" y1="80" x2="380" y2="500" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  <line x1="520" y1="80" x2="520" y2="500" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  <line x1="680" y1="80" x2="680" y2="500" stroke="#94a3b8" stroke-width="1" stroke-dasharray="4"/>
  
  <rect x="60" y="50" width="80" height="30" rx="4" fill="#0f172a" stroke="#1e293b"/>
  <text x="100" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">Client JS</text>
  
  <rect x="190" y="50" width="100" height="30" rx="4" fill="#2d8a83" stroke="#1f635e"/>
  <text x="240" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">RestController</text>
  
  <rect x="330" y="50" width="100" height="30" rx="4" fill="#2d8a83" stroke="#1f635e"/>
  <text x="380" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">PlannerService</text>
  
  <rect x="470" y="50" width="100" height="30" rx="4" fill="#64748b" stroke="#475569"/>
  <text x="520" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">PlaceRepo</text>
  
  <rect x="630" y="50" width="100" height="30" rx="4" fill="#64748b" stroke="#475569"/>
  <text x="680" y="70" font-family="'Inter', sans-serif" font-size="11" font-weight="bold" fill="#ffffff" text-anchor="middle">PlannerAiService</text>

  <!-- Execution lines -->
  <path d="M 100 120 L 240 120" stroke="#0f172a" stroke-width="1.5" marker-end="url(#arrow)"/>
  <text x="170" y="115" font-family="'Inter', sans-serif" font-size="9" text-anchor="middle">1. POST generate</text>
  
  <path d="M 240 160 L 380 160" stroke="#0f172a" stroke-width="1.5" marker-end="url(#arrow)"/>
  <text x="310" y="155" font-family="'Inter', sans-serif" font-size="9" text-anchor="middle">2. Invoke generate</text>
  
  <path d="M 380 200 L 520 200" stroke="#0f172a" stroke-width="1.5" marker-end="url(#arrow)"/>
  <text x="450" y="195" font-family="'Inter', sans-serif" font-size="9" text-anchor="middle">3. smartFilter(city)</text>
  
  <path d="M 520 240 L 380 240" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
  <text x="450" y="235" font-family="'Inter', sans-serif" font-size="9" text-anchor="middle">4. Return Place rows</text>
  
  <path d="M 380 280 A 30 30 0 0 1 380 310" stroke="#2d8a83" stroke-width="1.5" fill="none" marker-end="url(#arrow)"/>
  <text x="420" y="300" font-family="'Inter', sans-serif" font-size="9" fill="#1f635e">5. Expand sparse candidates</text>
  
  <path d="M 380 340 L 680 340" stroke="#0f172a" stroke-width="1.5" marker-end="url(#arrow)"/>
  <text x="530" y="335" font-family="'Inter', sans-serif" font-size="9" text-anchor="middle">6. generateFallbackItinerary(context)</text>
  
  <path d="M 680 380 L 380 380" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
  <text x="530" y="375" font-family="'Inter', sans-serif" font-size="9" text-anchor="middle">7. Return RAG generated JSON</text>
  
  <path d="M 380 430 L 240 430" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
  <text x="310" y="425" font-family="'Inter', sans-serif" font-size="9" text-anchor="middle">8. Return PlannerResponseDto</text>
  
  <path d="M 240 470 L 100 470" stroke="#475569" stroke-width="1.5" marker-end="url(#arrow)" stroke-dasharray="3"/>
  <text x="170" y="465" font-family="'Inter', sans-serif" font-size="9" text-anchor="middle">9. Render trip plan</text>
</svg>"""

# Write all SVGs to file
for filename, content in diagrams.items():
    target_path = SVG_DIR / filename
    target_path.write_text(content, encoding="utf-8")
    print(f"Generated {target_path}")

print("All 14 SVG vector diagrams generated successfully!")
