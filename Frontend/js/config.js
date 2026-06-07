// ========== SMART TRAVEL - BACKEND CONFIG ==========

window.SMARTTRAVEL_API_BASE = (() => {
  const origin = window.location.origin;

  // Local development
  if (
    origin.includes('localhost') ||
    origin.includes('127.0.0.1')
  ) {
    return 'http://localhost:9090/api';
  }

  // Production (Render backend)
  return 'https://smarttravel-ai.onrender.com/api';
})();
