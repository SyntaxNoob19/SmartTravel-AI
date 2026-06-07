# SmartTravel Documentation

Welcome to the SmartTravel documentation hub. Here you'll find comprehensive guides, architecture details, and quick references for understanding and working with the project.

## 📚 Documentation Structure

### Quick Start
- **[Quick Reference Guide](QUICK_REFERENCE.md)** ⭐ START HERE
  - Function locations and purposes
  - Common CSS classes and DOM IDs
  - Data flow diagrams
  - Troubleshooting tips
  - Code style guidelines

### Feature Documentation
- **[Frontend Improvements](FRONTEND_IMPROVEMENTS.md)** - Latest UI/UX enhancements (June 2025)
  - Budget display redesign
  - Day-by-day itinerary expansion
  - Removed technical data exposure
  - Mobile responsive design
  - Complete implementation details

- **[Project Setup](PROJECT_SETUP.md)** - Installation & configuration
  - Environment setup
  - Database configuration
  - Running the application
  - Deployment instructions

- **[Architecture Overview](ARCHITECTURE.md)** - System design & structure
  - Frontend folder structure
  - Backend folder structure
  - Package explanations
  - API communication flow
  - Database relationships

- **[API Reference](API_REFERENCE.md)** - Complete API documentation
  - All endpoints
  - Request/response formats
  - Error handling
  - Authentication details

- **[Flow Diagrams](FLOW_DIAGRAMS.md)** - Visual system workflows
  - User journey diagrams
  - Data flow diagrams
  - Component relationships

### Release Information
- **[Changelog](CHANGELOG.md)** - Version history & release notes
  - Recent changes (June 2025)
  - Previous releases
  - Breaking changes
  - Known issues

---

## 🎯 Quick Navigation by Role

### For Developers
1. Start with [Quick Reference Guide](QUICK_REFERENCE.md)
2. Review [Architecture Overview](ARCHITECTURE.md)
3. Check [Frontend Improvements](FRONTEND_IMPROVEMENTS.md) for latest changes
4. Use [API Reference](API_REFERENCE.md) for backend details

### For Designers/UX
1. Read [Frontend Improvements](FRONTEND_IMPROVEMENTS.md)
2. Review [Flow Diagrams](FLOW_DIAGRAMS.md)
3. Check [Quick Reference Guide](QUICK_REFERENCE.md) for CSS classes

### For DevOps/Deployment
1. Follow [Project Setup](PROJECT_SETUP.md)
2. Review [Architecture Overview](ARCHITECTURE.md)
3. Check [API Reference](API_REFERENCE.md) for endpoint details

### For Project Managers
1. Read [Changelog](CHANGELOG.md)
2. Review [Frontend Improvements](FRONTEND_IMPROVEMENTS.md) for feature overview
3. Check [Architecture Overview](ARCHITECTURE.md) for technical context

---

## 📋 Recent Changes (June 2025)

### Frontend Improvements
✅ **Budget Display Enhancements**
- Daily rate now prominently displayed (36px font, teal banner)
- Total budget moved to metadata strip (visible without scrolling)
- Simplified progress bars (removed visual clutter)
- Mobile responsive design included

✅ **Day-by-Day Itinerary Improvements**
- All places now expanded by default (no collapse needed)
- Grid layout with full place details
- Sequential numbering for activities
- Complete information visible immediately

✅ **Code Quality**
- Removed raw JSON data exposure ("View Full Itinerary Data" button removed)
- Cleaner, more professional frontend
- No breaking changes to existing functionality

**See [Frontend Improvements](FRONTEND_IMPROVEMENTS.md) for complete details**

---

## 📁 Document Overview

| Document | Purpose | Audience | Length |
|----------|---------|----------|--------|
| QUICK_REFERENCE.md | Fast lookup guide | All developers | 5 min read |
| FRONTEND_IMPROVEMENTS.md | Feature details | Frontend team | 10 min read |
| ARCHITECTURE.md | System design | All developers | 5 min read |
| API_REFERENCE.md | Endpoint details | Backend developers | 10 min read |
| PROJECT_SETUP.md | Installation guide | DevOps/Deployment | 10 min read |
| FLOW_DIAGRAMS.md | Visual workflows | UX/Designers | 5 min read |
| CHANGELOG.md | Version history | Project managers | 5 min read |

---

## 🔍 Search by Topic

### Budget Calculation
- [Frontend Improvements - Budget Calculation Section](FRONTEND_IMPROVEMENTS.md#4-budget-calculation--display-flow)
- [Quick Reference - Budget Calculation Example](QUICK_REFERENCE.md#budget-calculation-example)
- [API Reference - Budget endpoints](API_REFERENCE.md)

### API Integration
- [API Reference](API_REFERENCE.md)
- [Architecture - API Communication](ARCHITECTURE.md#api-communication)
- [Quick Reference - API Endpoints](QUICK_REFERENCE.md#api-endpoints)

### Frontend Structure
- [Architecture - Frontend Structure](ARCHITECTURE.md#frontend-structure)
- [Quick Reference - Frontend Structure](QUICK_REFERENCE.md#frontend-structure--key-files)
- [Frontend Improvements - Files Modified](FRONTEND_IMPROVEMENTS.md#6-key-files-modified)

### Database Design
- [Architecture - Database Relationships](ARCHITECTURE.md#database-relationships)
- [API Reference - Database schema](API_REFERENCE.md)

### Deployment
- [Project Setup - Deployment](PROJECT_SETUP.md)
- [Architecture - Overview](ARCHITECTURE.md)

### Troubleshooting
- [Quick Reference - Troubleshooting](QUICK_REFERENCE.md#troubleshooting)
- [Project Setup - Common Issues](PROJECT_SETUP.md)

---

## 🚀 Getting Started

### For New Contributors
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SmartTravel
   ```

2. **Read the Quick Reference**
   - [Quick Reference Guide](QUICK_REFERENCE.md)

3. **Set up your environment**
   - [Project Setup Guide](PROJECT_SETUP.md)

4. **Understand the architecture**
   - [Architecture Overview](ARCHITECTURE.md)

5. **Review recent changes**
   - [Frontend Improvements (June 2025)](FRONTEND_IMPROVEMENTS.md)

6. **Start developing!**
   - Use [API Reference](API_REFERENCE.md) for backend details
   - Use [Quick Reference](QUICK_REFERENCE.md) for quick lookups

---

## 📞 Support & Questions

### Common Questions

**Q: How do I add a new feature?**
A: See [Architecture Overview](ARCHITECTURE.md) and [Quick Reference Guide](QUICK_REFERENCE.md)

**Q: How do I run the application?**
A: See [Project Setup Guide](PROJECT_SETUP.md)

**Q: How do I call the API?**
A: See [API Reference](API_REFERENCE.md)

**Q: What changed in the latest update?**
A: See [Changelog](CHANGELOG.md) and [Frontend Improvements](FRONTEND_IMPROVEMENTS.md)

**Q: How is the budget calculated?**
A: See [Frontend Improvements - Budget Calculation](FRONTEND_IMPROVEMENTS.md#4-budget-calculation--display-flow)

**Q: How is the itinerary generated?**
A: See [Flow Diagrams](FLOW_DIAGRAMS.md) and [Architecture Overview](ARCHITECTURE.md)

---

## 📊 Document Statistics

```
Total Documentation: 7 files
Total Size: ~50 KB
Total Content: ~8,500 lines
Coverage Areas: 
  - Architecture: 3 docs
  - Setup & Deployment: 2 docs
  - Features & Changes: 2 docs
  - Quick Reference: 1 doc
```

---

## 🔗 External Resources

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **OpenRouter API**: https://openrouter.ai/docs
- **MDN Web Docs**: https://developer.mozilla.org/

---

## 📝 Contributing

When contributing to SmartTravel:
1. Update relevant documentation files
2. Add entry to [Changelog](CHANGELOG.md)
3. Follow code style guidelines in [Quick Reference](QUICK_REFERENCE.md)
4. Include screenshots for UI changes in [Frontend Improvements](FRONTEND_IMPROVEMENTS.md)

---

## 📄 License

Documentation is part of the SmartTravel project. See project root for license details.

---

## 🔄 Version Information

- **Latest Update**: June 6, 2025
- **Status**: Active Development
- **Maintained By**: SmartTravel Team

---

**Last Updated**: June 6, 2025  
**Next Review**: Q3 2025  
**Contact**: See project repository for contact information
