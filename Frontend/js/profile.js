// profile.js - fetch and display user profile information
document.addEventListener('DOMContentLoaded', async () => {
    const usernameEl = document.getElementById('profile-username');
    const emailEl = document.getElementById('profile-email');
    const createdEl = document.getElementById('profile-created');
    const tripsEl = document.getElementById('profile-trips-count');
    const budgetsEl = document.getElementById('profile-budgets-count');

    const loadingToast = () => {
        const toast = document.createElement('div');
        toast.textContent = 'Loading profile...';
        toast.style.position = 'fixed';
        toast.style.top = '1rem';
        toast.style.right = '1rem';
        toast.style.padding = '0.75rem 1rem';
        toast.style.background = 'var(--accent-light)';
        toast.style.borderRadius = 'var(--radius-sm)';
        toast.style.boxShadow = 'var(--shadow-sm)';
        toast.id = 'profile-loading-toast';
        document.body.appendChild(toast);
    };

    const hideLoading = () => {
        const toast = document.getElementById('profile-loading-toast');
        if (toast) toast.remove();
    };

    const showError = (msg) => {
        const err = document.createElement('div');
        err.textContent = msg;
        err.style.position = 'fixed';
        err.style.top = '1rem';
        err.style.right = '1rem';
        err.style.padding = '0.75rem 1rem';
        err.style.background = 'rgba(255,0,0,0.15)';
        err.style.border = '1px solid rgba(255,0,0,0.4)';
        err.style.borderRadius = 'var(--radius-sm)';
        err.style.boxShadow = 'var(--shadow-sm)';
        document.body.appendChild(err);
        setTimeout(() => err.remove(), 5000);
    };

    loadingToast();
    try {
        const resp = await fetch(`${API_BASE_URL}/profile/me`, {
            method: 'GET',
            credentials: 'include',
            headers: { 'Accept': 'application/json' }
        });
        hideLoading();
        if (!resp.ok) {
            showError('Failed to load profile');
            return;
        }
        const json = await resp.json();
        if (!json.success || !json.data) {
            showError('Profile data unavailable');
            return;
        }
        const data = json.data;
        if (usernameEl) usernameEl.textContent = data.name || data.email;
        if (emailEl) emailEl.textContent = data.email;
        if (createdEl) {
            const date = new Date(data.createdAt);
            const options = { month: 'short', year: 'numeric' };
            createdEl.textContent = date.toLocaleDateString('en-US', options);
        }
        if (tripsEl) tripsEl.textContent = data.tripsCount;
        if (budgetsEl) budgetsEl.textContent = data.budgetsCount;
    } catch (e) {
        hideLoading();
        console.error('Profile fetch error', e);
        showError('Error loading profile');
    }
});
