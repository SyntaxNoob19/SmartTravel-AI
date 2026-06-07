# Changelog

All notable changes to SmartTravel are documented in this file.

## [June 7, 2026] - Architecture Cleanup, Parameterization & Documentation

### Added
- **Parameterizable Asset Routing:** Configured `app.frontend.path` to allow environment-independent runtime static assets mounting, solving path portability vulnerabilities.
- **Portability-Optimized DB Seeding:** Added datasets candidate paths resolution logic in `CsvImportTool.java` and `import_csv.py` to allow dataset seeds imports in any runtime context.
- **Master CSS Tokens:** Established `design-tokens.css` as the master design tokens stylesheet and pre-linked it in all views.
- **SVG Vector Diagrams:** Rendered vector SVG UML and flow diagrams inside `docs/diagrams/svg/` for high-quality, lightweight GitHub rendering.
- **Full Portfolio Documentation:** Generated structured markdown specifications inside `docs/` (SRS, database design, backend and frontend architectures, technical debt reports, and deployment guides).

### Changed
- **CORS Profiles Hardening:** Restrained allowed origin patterns from wildcard `*` to specific client paths matching `app.cors.allowed-origins`.
- **Database Seeding Paths:** Relocated initial seed database file `india_travel_dataset_cleaned_v2.csv` to `datasets/` and utility loader scripts to `scripts/`.

### Removed
- **Unused Controller:** Deleted unreferenced `LandingController.java` to clean up servlet mapping handlers.
- **Obsolete Packaging:** Purged empty package subdirectories from `cleanup/`.
- **Unreferenced JS & CSS:** Purged dead scripts `trip-card.js` and styling sheets `trip-dashboard.css` from the client code.
- **Scratch Files Purge:** Purged raw test logs and notes (`planner.json`, `request.json`, `test-output.txt`, `quick-commands.txt`) from the backend workspace root.

### Fixed
- **Mockito Test Suite Failures:** Resolved `UnnecessaryStubbingException` errors across multiple test files. Corrected Mockito matchers for null city values.

### Version History Summary

| Version | Date | Type | Summary |
|---------|------|------|---------|
| 1.1.0   | Jun 7, 2026 | Cleanup | Architecture Reorganization, CORS Hardening, and Portfolio Documentation |
| 1.0.3   | Jun 6, 2025 | Enhancement | Frontend UX improvements - budget display & itinerary rendering |

## [June 6, 2025] - Frontend UX/UI Improvements

### Added
- **Budget Display Enhancements**
  - New prominent daily rate banner at top of budget section (36px font)
  - Teal gradient background (#e8f4f3 to #f0f9f8) with 2px border
  - Daily rate moved from tiny text to large, eye-catching display
  - New CSS class: `.trip-budget-daily-rate-banner`
  - New CSS class: `.trip-budget-daily-rate-label`
  - New CSS class: `.trip-budget-daily-rate-value`

- **Metadata Strip Improvements**
  - Total budget moved to metadata strip (visible without scrolling)
  - Now displays: Duration | Travelers | **Estimated Total**
  - Total displayed at 18px bold, dark teal color (#1b7e71)
  - Users see all key information immediately

- **Expanded Itinerary Display**
  - All places now displayed expanded by default
  - Removed "Also X other place(s) planned for this day (collapsed)" message
  - Grid layout for day activities with 16px gap between items
  - Each place shows: name, time slot, description, category, rating, duration, tips, safety advice

### Changed
- **Budget Rendering**
  - Restructured `renderBudgetPanel()` in `Frontend/js/itinerary.js`
  - Moved `trip-budget-basis` to top
  - Reorganized metadata strip layout
  - Simplified category breakdown display

- **Day-by-Day Itinerary**
  - Changed from single "top place" rendering to grid of all places
  - Updated `renderPlannerDay()` in `Frontend/js/itinerary.js` (lines 730-760)
  - Replaced conditional rendering with map-based grid layout

- **Progress Bar Display**
  - Simplified budget-breakdown-track: `display: none;`
  - Removed visual clutter while keeping percentage information
  - Reduced CSS complexity

### Removed
- **View Full Itinerary Data Button**
  - Removed button that exposed backend JSON
  - Removed `#planner-show-data` button element (~line 870)
  - Removed `#planner-raw-data` pre element (~line 875)
  - Removed event listener for toggling raw data (previously lines 944-965)
  - Cleaner, more professional UI without technical data exposure

### Fixed
- **Mobile Responsiveness**
  - Added media query for devices <520px width
  - Daily rate font reduced to 28px on mobile (from 36px)
  - Metadata strip changed to single column on mobile
  - Proper gap and padding adjustments for small screens

### Files Modified
- `Frontend/js/itinerary.js`
  - `renderBudgetPanel()` function (lines 1228-1280)
  - Day rendering logic (lines 730-760)
  - Removed event listener code (previously 944-965)

- `Frontend/css/itinerary.css`
  - Added `.trip-budget-daily-rate-banner` (lines 1372-1395)
  - Added `.trip-budget-daily-rate-label` (lines 1378-1382)
  - Added `.trip-budget-daily-rate-value` (lines 1384-1390)
  - Modified `.budget-breakdown-track` to `display: none;` (line 1700)
  - Updated mobile media query (lines 1767-1771)

### Documentation
- Created `docs/FRONTEND_IMPROVEMENTS.md` - comprehensive documentation of all UI/UX changes
- Updated `README.md` with Recent Frontend Improvements section
- Created `docs/CHANGELOG.md` (this file)

### Browser Compatibility
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+
- Mobile browsers (iOS Safari 14+, Chrome Mobile 90+)

### Performance Impact
- Minimal (CSS/layout only, no JavaScript calculations added)
- Removed button event listener slightly reduces code size
- No new dependencies added

### Breaking Changes
- None - all changes are backward compatible
- Existing trip data formats unchanged
- API responses unchanged
- Database schema unchanged

### Known Issues
- None reported

### Testing
- ✅ Manual testing on Chrome, Firefox, Safari
- ✅ Mobile responsive testing on devices and DevTools
- ✅ No console errors or warnings
- ✅ All existing features still functional

---

## [Previous Releases]

### [Spec Documentation - May 2025]
- Created comprehensive bugfix spec for AI itinerary fallback
- Designed budget estimation fix with deterministic variance
- Documented requirements, design, and implementation tasks

### [AI Itinerary Fallback - May 2025]
- Fixed @Value defaults in PlannerAiService
- Added @PostConstruct warning when API key missing
- Introduced AiFallbackStatus enum
- Updated PlannerService error handling
- Frontend error handling improvements

### [Project Initialization]
- Full-stack Java/Spring Boot + HTML/CSS/JavaScript
- MySQL database setup
- Basic user authentication
- Trip planning form and display
- Budget calculation system
- AI integration with OpenRouter

---

## Version History Summary

| Version | Date | Type | Summary |
|---------|------|------|---------|
| 1.0.3 | Jun 6, 2025 | Enhancement | Frontend UX improvements - budget display & itinerary rendering |
| 1.0.2 | May 2025 | Bugfix | AI fallback implementation & budget estimation fixes |
| 1.0.1 | May 2025 | Feature | Initial feature spec & design documentation |
| 1.0.0 | Early 2025 | Initial | Project initialization & core functionality |

---

## How to Report Issues

If you encounter any issues or have suggestions:

1. Check [Frontend Improvements Documentation](FRONTEND_IMPROVEMENTS.md) for details
2. Verify you're using a supported browser
3. Clear browser cache and localStorage
4. Report with:
   - Browser version
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots if applicable

---

*For detailed information about specific improvements, see [Frontend Improvements](FRONTEND_IMPROVEMENTS.md)*
