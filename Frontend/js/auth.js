// ========== SMART TRAVEL - AUTH HELPER ==========

let currentUser = null;

// API base is set by config.js (loaded before this script)
const API_AUTH_BASE = `${window.SMARTTRAVEL_API_BASE}/auth`;
const API_BASE      = window.SMARTTRAVEL_API_BASE;

// ── Fetch session user ────────────────────────────────────────────────────────
async function fetchCurrentUser() {
  try {
    const resp = await fetch(`${API_AUTH_BASE}/me`, {
      credentials: 'include',
      headers: { Accept: 'application/json' },
    });
    if (resp.ok) {
      currentUser = (await resp.json()).data || null;
      if (currentUser) {
        localStorage.setItem('smarttravelCurrentUser', JSON.stringify(currentUser));
      } else {
        localStorage.removeItem('smarttravelCurrentUser');
      }
    } else {
      if (resp.status === 401) {
        currentUser = null;
        localStorage.removeItem('smarttravelCurrentUser');
      } else {
        currentUser = JSON.parse(localStorage.getItem('smarttravelCurrentUser') || 'null');
      }
    }
  } catch {
    currentUser = JSON.parse(localStorage.getItem('smarttravelCurrentUser') || 'null');
  }
  updateNavbar();
}

// ── Public helpers ────────────────────────────────────────────────────────────
function getCurrentUserAccount() { return currentUser; }

function logout() {
  fetch(`${API_AUTH_BASE}/logout`, { method: 'POST', credentials: 'include' })
    .finally(() => {
      currentUser = null;
      localStorage.removeItem('smarttravelCurrentUser');
      const base = location.pathname.includes('/pages/') ? '../' : '';
      location.href = `${base}index.html`;
    });
}

// ── Update dropdown content (called by navbar.js after DOM is ready) ──────────
function updateNavbar() {
  const guestPanel = document.getElementById('panel-guest');
  const userPanel  = document.getElementById('panel-user');
  const nameEl     = document.getElementById('panel-name');
  const emailEl    = document.getElementById('panel-email');
  const activityEl = document.getElementById('panel-activity-list');

  if (!guestPanel || !userPanel) {
    console.log('updateNavbar: panels not found yet');
    return; // navbar not in DOM yet
  }

  const loggedIn = !!currentUser?.email;

  // Use CSS classes instead of inline styles
  if (loggedIn) {
    guestPanel.classList.add('is-hidden');
    userPanel.classList.remove('is-hidden');
  } else {
    guestPanel.classList.remove('is-hidden');
    userPanel.classList.add('is-hidden');
  }

  if (loggedIn) {
    if (nameEl)  nameEl.textContent  = currentUser.name  || 'User';
    if (emailEl) emailEl.textContent = currentUser.email || '';
    _loadRecentActivity(activityEl);
    const btnLabel = document.querySelector('#account-btn span');
    if (btnLabel) btnLabel.textContent = currentUser.name || 'User';
  } else {
    const btnLabel = document.querySelector('#account-btn span');
    if (btnLabel) btnLabel.textContent = 'Account';
  }
}

// ── Recent activity ───────────────────────────────────────────────────────────
async function _loadRecentActivity(listEl) {
  if (!listEl) return;
  try {
    const resp = await fetch(`${API_BASE}/trips/user/recent?limit=3`, {
      credentials: 'include',
      headers: { Accept: 'application/json' },
    });
    if (!resp.ok) throw new Error();
    const trips = (await resp.json()).data || [];
    listEl.innerHTML = trips.length
      ? trips.map(t => `<li>• ${t.tripName || t.destination || 'Trip'}</li>`).join('')
      : '<li class="panel-activity-empty">No recent activity</li>';
  } catch {
    listEl.innerHTML = '<li class="panel-activity-empty">No recent activity</li>';
  }
}

// ── Login ─────────────────────────────────────────────────────────────────────
async function login() {
  const email    = document.getElementById('loginEmail')?.value.trim();
  const password = document.getElementById('loginPassword')?.value;
  if (!email || !password) { alert('Please enter both email and password.'); return; }

  try {
    const resp   = await fetch(`${API_AUTH_BASE}/login`, {
      method: 'POST', credentials: 'include',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    const result = await resp.json();
    if (!resp.ok || !result.success) throw new Error(result.message || 'Login failed.');

    currentUser = result.data;
    if (currentUser) {
      localStorage.setItem('smarttravelCurrentUser', JSON.stringify(currentUser));
    }
    updateNavbar();
    alert('Login successful! Redirecting...');
    location.href = location.pathname.includes('/pages/') ? 'itinerary.html' : 'pages/itinerary.html';
  } catch (err) {
    alert(err.message || 'Login error. Please try again.');
  }
}

// ── Register ──────────────────────────────────────────────────────────────────
async function register() {
  const name            = document.getElementById('registerName')?.value.trim();
  const email           = document.getElementById('registerEmail')?.value.trim();
  const password        = document.getElementById('registerPassword')?.value;
  const confirmPassword = document.getElementById('registerConfirmPassword')?.value;

  if (!name || !email || !password || !confirmPassword) { alert('Please fill in all fields.'); return; }
  if (password !== confirmPassword) { alert('Passwords do not match.'); return; }

  try {
    const resp   = await fetch(`${API_AUTH_BASE}/register`, {
      method: 'POST', credentials: 'include',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({ name, email, password }),
    });
    const result = await resp.json();
    if (!resp.ok || !result.success) throw new Error(result.message || 'Registration failed.');

    // Auto-login
    const loginResp   = await fetch(`${API_AUTH_BASE}/login`, {
      method: 'POST', credentials: 'include',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    const loginResult = await loginResp.json();
    if (loginResp.ok && loginResult.success) {
      currentUser = loginResult.data;
      if (currentUser) {
        localStorage.setItem('smarttravelCurrentUser', JSON.stringify(currentUser));
      }
      updateNavbar();
      location.href = location.pathname.includes('/pages/') ? 'itinerary.html' : 'pages/itinerary.html';
      return;
    }
  } catch (err) {
    alert(err.message || 'Registration error. Please try again.');
    return;
  }

  const base = location.pathname.includes('/pages/') ? '' : 'pages/';
  location.href = `${base}login.html`;
}

// ── Globals ───────────────────────────────────────────────────────────────────
window.getCurrentUserAccount = getCurrentUserAccount;
window.isLoggedIn  = () => !!currentUser?.email;
window.logout      = logout;
window.login       = login;
window.register    = register;
window.updateNavbar = updateNavbar;

// Boot
window.currentUserPromise = document.readyState === 'loading'
  ? new Promise(res => document.addEventListener('DOMContentLoaded', () => fetchCurrentUser().then(res)))
  : fetchCurrentUser();
