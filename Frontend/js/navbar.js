// navbar.js — loads shared navbar and wires up behaviour

export async function loadNavbar() {
  try {
    const basePath = location.pathname.includes('/pages/') ? '../' : '';
    const resp = await fetch(`${basePath}components/navbar.html?_=${Date.now()}`);
    if (!resp.ok) throw new Error('Could not load navbar');

    let html = await resp.text();

    // Fix relative hrefs for pages/ subdirectory
    html = html.replace(/href="(?!https?:\/\/)([^"#]+)"/g, (match, p1) => {
      if (p1.startsWith('#') || p1.startsWith('mailto:')) return match;
      return `href="${basePath}${p1}"`;
    });

    const tmp = document.createElement('div');
    tmp.innerHTML = html;
    const navEl = tmp.firstElementChild;

    const placeholder = document.querySelector('nav.navbar');
    if (placeholder) placeholder.replaceWith(navEl);
    else document.body.prepend(navEl);

    _initActiveLink();
    _initHamburger();
    _initAccountDropdown();

    // If auth.js already resolved, paint the dropdown now
    if (typeof window.updateNavbar === 'function') window.updateNavbar();

  } catch (err) {
    console.error('Navbar load failed:', err);
  }
}

// ── Active link ──────────────────────────────────────────────────────────────
function _initActiveLink() {
  const map = {
    '': 'home', 'index': 'home',
    'destinations': 'explore',
    'itinerary': 'trips',
    'group': 'budget',
    'about': 'about',
  };
  const filename = location.pathname.replace(/\.html$/, '').split('/').pop() || '';
  const active = map[filename] || '';
  document.querySelectorAll('.nav-link').forEach(l =>
    l.classList.toggle('active', l.dataset.page === active)
  );
}

// ── Hamburger (mobile) ───────────────────────────────────────────────────────
function _initHamburger() {
  const btn = document.querySelector('.hamburger');
  const menu = document.querySelector('.nav-menu');
  if (!btn || !menu) return;

  btn.addEventListener('click', () => {
    const open = btn.getAttribute('aria-expanded') === 'true';
    btn.setAttribute('aria-expanded', String(!open));
    menu.classList.toggle('open', !open);
  });

  document.querySelectorAll('.nav-link').forEach(l =>
    l.addEventListener('click', () => {
      btn.setAttribute('aria-expanded', 'false');
      menu.classList.remove('open');
    })
  );
}

// ── Account dropdown ─────────────────────────────────────────────────────────
function _initAccountDropdown() {
  const btn   = document.getElementById('account-btn');
  const panel = document.getElementById('account-panel');
  if (!btn || !panel) return;

  function open()  {
    panel.classList.add('is-open');
    btn.setAttribute('aria-expanded', 'true');
    panel.setAttribute('aria-hidden', 'false');
  }
  function close() {
    panel.classList.remove('is-open');
    btn.setAttribute('aria-expanded', 'false');
    panel.setAttribute('aria-hidden', 'true');
  }
  function toggle() { panel.classList.contains('is-open') ? close() : open(); }

  btn.addEventListener('click', (e) => { e.stopPropagation(); toggle(); });
  document.addEventListener('click', (e) => {
    if (!btn.closest('.account-wrap').contains(e.target)) close();
  });
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape') close(); });
}

// ── Boot ─────────────────────────────────────────────────────────────────────
if (document.readyState !== 'loading') loadNavbar();
else document.addEventListener('DOMContentLoaded', loadNavbar);
