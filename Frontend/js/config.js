// ========== SMART TRAVEL - BACKEND CONFIG ==========

window.SMARTTRAVEL_API_BASE = (() => {
  const origin = window.location.origin;
  const hostname = window.location.hostname;
  const protocol = window.location.protocol;

  // Local development
  if (
    hostname === 'localhost' ||
    hostname === '127.0.0.1' ||
    hostname.startsWith('192.168.') ||
    hostname.startsWith('10.')
  ) {
    return `http://${hostname}:9090/api`;
  }

  // Fallback for file:// or offline files
  if (protocol === 'file:' || !origin.startsWith('http')) {
    return 'http://localhost:9090/api';
  }

  // Production (Render backend)
  return 'https://smarttravel-ai.onrender.com/api';
})();
