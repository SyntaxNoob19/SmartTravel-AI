# Architecture Cleanup Report - SmartTravel

This report chronicles the file restructuring, duplicate code deletions, obsolete files removal, and CSS tokens consolidation implemented to make the SmartTravel repository clean, maintainable, and portfolio-ready.

---

## 1. Relocated Files & Folders

To clean up the workspace root directory, dataset files and python scripts were moved to dedicated folders:

| Original Path | New Target Path | Reason |
| :--- | :--- | :--- |
| `/india_travel_dataset_cleaned_v2.csv` | `/datasets/india_travel_dataset_cleaned_v2.csv` | Group dataset files into a clean `datasets/` repository folder. |
| `/import_csv.py` | `/scripts/import_csv.py` | Relocate utility DB seed scripts to a clean `scripts/` folder. |

*Note: The candidate path mapping lists inside [CsvImportTool.java](file:///d:/travel-planner/Backend/src/main/java/com/riya/smarttravel/util/CsvImportTool.java) and [import_csv.py](file:///d:/travel-planner/scripts/import_csv.py) were updated to handle these relocations without breaking database seeding procedures.*

---

## 2. Deleted Dead & Unused Files

The following files and folders were identified as unreferenced, duplicate, or obsolete, and have been purged:

### 2.1 Frontend Files
- **`Frontend/js/trip-card.js`**: Purged. The functions `renderTripCard` and `renderSkeletonCard` exported here were completely overridden by local re-declarations inside `itinerary.js` using `.saved-trip-card` classes.
- **`Frontend/css/trip-dashboard.css`**: Purged. Contained style declarations for `.trip-card` objects that are no longer referenced.

### 2.2 Backend Files & Directories
- **`Backend/src/main/java/com/riya/smarttravel/controller/LandingController.java`**: Purged. All endpoints (trending, regional recommendations, duration queries) were unreferenced by client scripts.
- **`Backend/src/main/java/com/riya/smarttravel/cleanup/`** empty folders:
  - Deleted empty sub-packages `analyzer/`, `controller/`, `generator/`, `service/`, `validator/` which only contained empty placeholders. Only the required `model/` package remains.

### 2.3 Temporary Log & Development Scratch Files
- Deleted empty `scratch/` directory at the root workspace.
- Deleted `quick-commands.txt` developer startup notes (its contents are fully integrated into [DEPLOYMENT_GUIDE.md](file:///d:/travel-planner/docs/DEPLOYMENT_GUIDE.md)).
- Purged temporary log outputs from the Backend root:
  - `planner.json`
  - `planner_req.json`
  - `request.json`
  - `test-output.txt`
  - `test_output.txt`

---

## 3. Styling Tokenization & Consolidation
To resolve CSS variables duplications and potential styling drifts:
- Created the master styling file **`Frontend/css/design-tokens.css`** to hold all CSS variable declarations.
- Removed duplicate `:root` custom properties declarations from:
  - `Frontend/css/utilities.css`
  - `Frontend/css/itinerary.css`
- Linked `design-tokens.css` first in all HTML pages (Home + 11 sub-views) to guarantee consistent color variables, fonts, and reset styles.
