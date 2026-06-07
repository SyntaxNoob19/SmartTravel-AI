# 📚 SmartTravel Documentation Hub

Welcome to the SmartTravel documentation hub. This directory contains comprehensive guides, system specifications, database schemas, and architectural details to help developers, deployment engineers, and designers understand the codebase.

---

## 📂 Documentation Directory

### 🚀 Setup & Deployment
*   📘 **[Detailed Setup & Deployment Guide](DEPLOYMENT_GUIDE.md):** Step-by-step instructions to configure environment keys, initialize/seed MySQL schema, compile/build the backend server, and map static resources.
*   ⭐ **[Quick Developer Reference Guide](QUICK_REFERENCE.md):** Fast lookup handbook detailing common function locations, DOM selectors, element IDs, troubleshooting advice, and coding standards.

### 📐 Software Engineering Specifications
*   📋 **[Software Requirements Specification (SRS)](SRS.md):** Functional and non-functional requirements, user personas, system boundaries, and active use cases.
*   💾 **[Database Design Schema](DATABASE_DESIGN.md):** JPA Entity configurations, relational database schemas, and companion budgeting layouts.
*   🌐 **[REST API Reference Documentation](API_DOCUMENTATION.md):** Detailed endpoint specifications, request payloads, response structures, and session cookie validation filters.

### 🏗️ Architecture Diagrams & Layouts
*   📐 **[System Architecture Overview](ARCHITECTURE.md):** Architectural design patterns, data flows, and base directories structure.
*   ⚙️ **[Backend Service Layers](BACKEND_ARCHITECTURE.md):** Service dependency injection, database RAG processing pipeline, and exception mappings.
*   ✨ **[Frontend Design System](FRONTEND_ARCHITECTURE.md):** HTML views, CSS layout structures, HSL design tokens, and modular Javascript files.

### 🧹 Project Health & Audits
*   🧹 **[Codebase Cleanup & Auditor Report](CLEANUP_REPORT.md):** Comprehensive summary of duplicate file purges, dead-code deletions, and structural audits.
*   ⚠️ **[Technical Debt & Backlog](TECHNICAL_DEBT_REPORT.md):** Backlog of identified performance bottlenecks, caching extensions, and future features.
*   📈 **[Frontend UI/UX Improvements](FRONTEND_IMPROVEMENTS.md):** Detailed analysis of recent layout updates, dynamic rate banners, and mobile responsiveness.
*   📝 **[Version Changelog](CHANGELOG.md):** Historic releases, feature updates, and patch versions.

---

## 🎨 Visual Workflows & System Diagrams

All source Mermaid diagrams and vector graphics are placed inside:
*   📁 **[Mermaid Raw Sources](mermaid/):** Diagram files in `.mmd` format.
*   📁 **[Rendered Vector SVGs](diagrams/svg/):** Scalable, vector-styled `.svg` diagrams ready for viewing or embedding:
    *   🧗 [User Itinerary Lifecycle Activity](diagrams/svg/activity.svg)
    *   💬 [AI RAG Generation Sequence](diagrams/svg/api_flow.svg)
    *   🔐 [Session Authentication Sequence](diagrams/svg/authentication_flow.svg)
    *   📐 [Core JPA Entity Class Model](diagrams/svg/class_diagram.svg)
    *   📁 [Repository Folder Structure](diagrams/svg/folder_structure.svg)
    *   🌐 [3-Tier System Architecture](diagrams/svg/system_architecture.svg)
    *   🧗 [Traveler Use Case Diagram](diagrams/svg/use_case.svg)

---

## 🏁 Quick Navigation by Role

| Target Profile | Recommended Starting Path |
|---|---|
| **Contributors / Backend Devs** | Read [Quick Developer Reference](QUICK_REFERENCE.md) ➔ Review [API Reference](API_DOCUMENTATION.md) ➔ Read [Backend Service Layout](BACKEND_ARCHITECTURE.md) |
| **UX Designers / Front-End Devs** | Inspect [Frontend Design System](FRONTEND_ARCHITECTURE.md) ➔ Review [Frontend UI/UX Improvements](FRONTEND_IMPROVEMENTS.md) |
| **DevOps / SysAdmins** | Follow [Setup & Deployment Guide](DEPLOYMENT_GUIDE.md) ➔ Review [System Architecture](ARCHITECTURE.md) |
| **Project Managers / QA** | Read [SRS Document](SRS.md) ➔ Review [Changelog](CHANGELOG.md) |
