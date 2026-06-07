// ========== SMART TRAVEL - BACKEND CONFIG ==========
// Change this ONE value if your backend port changes.
window.SMARTTRAVEL_API_BASE = (() => {
  const origin = window.location.origin;
  // If the page is served directly from the backend (port 8080 or 9090), use same-origin
  if (origin.includes(':8080') || origin.includes(':9090')) return '/api';
  // Otherwise point to the backend explicitly — change port here if needed
  const host = window.location.hostname || 'localhost';
  return `http://${host}:9090/api`;
})();
