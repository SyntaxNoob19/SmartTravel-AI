// ========== SMART TRAVEL PLANNER - SPLIT BUDGET CONTROLLER ==========

/**
 * Front‑end controller for the Split Budget page.
 * All data is now persisted server‑side via the Budget API.
 * The UI remains fully responsive and uses modern JS (async/await).
 */

// Helper to get the currently authenticated user (defined elsewhere)
function getCurrentUser() {
    return getCurrentUserAccount(); // returns {email: string, ...}
}

/**
 * Fetch the current budget plan for the logged‑in user.
 */
async function loadBudgetPlan() {
    const user = getCurrentUser();
    if (!user?.email) return null;
    try {
        const resp = await fetch(`${API_BASE_URL}/budget/current`, {
            method: 'GET',
            credentials: 'include', // send session cookie
            headers: { 'Accept': 'application/json' }
        });
        if (!resp.ok) return null;
        const data = await resp.json();
        // API returns the raw BudgetPlan entity
        return data;
    } catch (e) {
        console.error('Failed to load budget plan', e);
        return null;
    }
}

/**
 * Save (create or update) the budget plan for the current user.
 * @param {Object} plan – partial BudgetPlan object
 */
async function saveBudgetPlan(plan) {
    const user = getCurrentUser();
    if (!user?.email) return null;
    try {
        const resp = await fetch(`${API_BASE_URL}/budget/save`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(plan)
        });
        if (!resp.ok) {
            const err = await resp.text();
            throw new Error(err);
        }
        return await resp.json();
    } catch (e) {
        console.error('Failed to save budget plan', e);
        alert('Could not save budget. Please try again.');
    }
}

/**
 * Delete the current budget plan.
 */
async function deleteBudgetPlan() {
    const plan = await loadBudgetPlan();
    if (!plan?.id) return;
    if (!confirm('Delete the entire budget plan?')) return;
    try {
        const resp = await fetch(`${API_BASE_URL}/budget/delete/${plan.id}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Delete failed');
        // Reload UI – the page will treat it as a fresh plan
        initPage();
    } catch (e) {
        console.error(e);
        alert('Could not delete budget.');
    }
}

/**
 * Initialise the page after DOM is ready.
 * Loads any existing plan and populates the UI.
 */
async function initPage() {
    const user = getCurrentUser();
    if (!user?.email) {
        document.querySelector('.group-page').innerHTML = `
            <div class="itinerary-empty-container" style="max-width:600px; margin:40px auto;">
                <div class="itinerary-empty-icon-wrap"><i class="fas fa-wallet"></i></div>
                <h3>Sign in to split budget</h3>
                <p>Track shared trip expenses, add travel companions, and calculate exact per‑person costs. Sign in to save and manage your shared budgets.</p>
                <div class="itinerary-empty-actions"><a href="login.html" class="btn btn-primary"><i class="fas fa-right-to-bracket"></i> Log In to Account</a></div>
            </div>`;
        return;
    }
    const plan = await loadBudgetPlan();
    populateUI(plan);
}

/**
 * Populate UI elements from a BudgetPlan object (may be null).
 */
function populateUI(plan) {
    const budgetInput = document.getElementById('budgetInput');
    const memberSelect = document.getElementById('memberCount');
    if (budgetInput) budgetInput.value = plan?.totalAmount ?? '';
    if (memberSelect) {
        memberSelect.value = plan?.participants ?? '';
        generateMemberInputs(plan?.memberNames ?? []);
    }
    // Set global currentExpenses from database
    currentExpenses = plan?.expenses ?? [];
    // Expenses – render list
    renderExpenses(currentExpenses);
    // Update calculations
    updateBudgetDisplay(plan);
}

/**
 * Set total budget – called from UI.
 */
async function setBudget() {
    const budgetInput = document.getElementById('budgetInput');
    if (!budgetInput || !budgetInput.value) {
        alert('Please enter a budget amount');
        return;
    }
    const amount = parseFloat(budgetInput.value);
    if (isNaN(amount) || amount <= 0) {
        alert('Please enter a valid budget amount');
        return;
    }
    const participants = parseInt(document.getElementById('memberCount')?.value || '0');
    const memberNames = Array.from(document.querySelectorAll('.member-name')).map(i => i.value.trim());
    const expenses = currentExpenses; // global list managed by UI
    const plan = {
        totalAmount: amount,
        participants: participants || null,
        memberNames: memberNames.filter(n => n),
        expenses: expenses
    };
    await saveBudgetPlan(plan);
    alert(`Total budget saved: ₹${Math.round(amount).toLocaleString('en-IN')}`);
    // Refresh UI with persisted data
    const refreshed = await loadBudgetPlan();
    populateUI(refreshed);
}

/**
 * Generate member input fields. Accept optional existing names array.
 */
function generateMemberInputs(existingNames = []) {
    const memberCountSelect = document.getElementById('memberCount');
    const container = document.getElementById('memberInputs');
    if (!memberCountSelect || !container) return;
    const count = parseInt(memberCountSelect.value) || 0;
    container.innerHTML = '';
    for (let i = 1; i <= count; i++) {
        const nameVal = existingNames[i - 1] || '';
        const div = document.createElement('div');
        div.className = 'field';
        div.innerHTML = `
            <label class="field-label"><i class="fas fa-user-tag text-muted"></i> Member ${i} Name</label>
            <input type="text" placeholder="Enter name of traveler ${i}" class="input-modern member-name" value="${escapeHtml(nameVal)}" maxlength="30" oninput="saveMemberNames()" />
        `;
        container.appendChild(div);
    }
    // After rebuilding inputs, persist any changes immediately
    saveMemberNames();
}

/**
 * Save member names – stores them in a temporary global variable.
 * The final plan is persisted when the user clicks "Save Budget".
 */
function saveMemberNames() {
    // No longer using localStorage – member names are kept in the UI until saved.
    // This placeholder keeps the function signature for any future extensions.
}

/**
 * Global in‑memory list of expenses for the current session.
 * It mirrors the server‑side list after each save.
 */
let currentExpenses = [];

/**
 * Add a new expense entry.
 */
function addExpense() {
    const nameInput = document.getElementById('expenseName');
    const amountInput = document.getElementById('expenseAmount');
    if (!nameInput || !amountInput) return;
    const name = nameInput.value.trim();
    const amountStr = amountInput.value.trim();
    if (!name || !amountStr) {
        alert('Please enter expense name and amount');
        return;
    }
    const amount = parseFloat(amountStr);
    if (isNaN(amount) || amount <= 0) {
        alert('Please enter a valid expense amount');
        return;
    }
    const expense = {
        id: Date.now(),
        name,
        amount,
        date: new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'short' })
    };
    currentExpenses.push(expense);
    renderExpenses(currentExpenses);
    // Clear UI fields
    nameInput.value = '';
    amountInput.value = '';
    // Persist the updated plan
    persistCurrentState();
}

/**
 * Delete an expense by id.
 */
function deleteExpense(id) {
    if (!confirm('Are you sure you want to delete this expense?')) return;
    currentExpenses = currentExpenses.filter(e => e.id !== id);
    renderExpenses(currentExpenses);
    persistCurrentState();
}

/**
 * Render the expense list into the DOM.
 */
function renderExpenses(expenses) {
    const listEl = document.getElementById('expenseList');
    const countEl = document.getElementById('expenseCount');
    if (countEl) countEl.textContent = String(expenses.length);
    if (!listEl) return;
    if (!expenses.length) {
        listEl.className = 'expense-list empty-state';
        listEl.innerHTML = `
            <div style="text-align:center; padding:16px; color:var(--text-muted);">
                <i class="fas fa-receipt" style="font-size:24px; margin-bottom:8px; display:block; opacity:0.5;"></i>
                No expenses logged yet
            </div>`;
        return;
    }
    listEl.className = 'expense-list';
    listEl.innerHTML = expenses
        .slice()
        .reverse()
        .map(exp => `
            <div class="expense-item" style="display:flex; justify-content:space-between; align-items:center; transition: var(--transition);">
                <div>
                    <strong style="display:block; color:var(--primary);">${escapeHtml(exp.name)}</strong>
                    <span style="font-size:11px; color:var(--text-muted);">${escapeHtml(exp.date)}</span>
                </div>
                <div style="display:flex; align-items:center; gap:12px;">
                    <b style="color:var(--accent-dark);">₹${Math.round(exp.amount).toLocaleString('en-IN')}</b>
                    <button onclick="deleteExpense(${exp.id})" title="Delete cost" style="background:none; border:none; color:#e74c3c; cursor:pointer; font-size:14px; padding:4px; transition: var(--transition);" onmouseover="this.style.transform='scale(1.15)'" onmouseout="this.style.transform='none'">
                        <i class="fas fa-trash-can"></i>
                    </button>
                </div>
            </div>`)
        .join('');
}

/**
 * Re‑calculate and display budget summary.
 * Optionally takes a BudgetPlan to avoid extra fetches.
 */
function updateBudgetDisplay(plan = null) {
    const budgetKey = null; // no localStorage usage
    const totalBudgetEl = document.getElementById('totalBudget');
    const totalSpentEl = document.getElementById('totalSpent');
    const remainingEl = document.getElementById('remaining');
    const perPersonBudgetEl = document.getElementById('perPersonBudget');
    const perPersonSpentEl = document.getElementById('perPersonSpent');
    const perPersonRemainingEl = document.getElementById('perPersonRemaining');
    const selectedGroupSizeEl = document.getElementById('selectedGroupSize');
    const expenseCountEl = document.getElementById('expenseCount');

    const format = v => Math.round(v).toLocaleString('en-IN');

    const totalBudget = plan?.totalAmount ?? 0;
    const expenses = plan?.expenses ?? currentExpenses;
    const totalSpent = expenses.reduce((s, e) => s + e.amount, 0);
    const memberCount = plan?.participants ?? parseInt(document.getElementById('memberCount')?.value || '0');
    const remaining = totalBudget - totalSpent;
    const perPersonBudget = totalBudget > 0 && memberCount > 0 ? totalBudget / memberCount : 0;
    const spentPerPerson = memberCount > 0 ? totalSpent / memberCount : 0;
    const remainingPerPerson = memberCount > 0 ? remaining / memberCount : 0;

    if (totalBudgetEl) totalBudgetEl.textContent = format(totalBudget);
    if (totalSpentEl) totalSpentEl.textContent = format(totalSpent);
    if (remainingEl) {
        remainingEl.textContent = format(remaining);
        remainingEl.parentElement.style.color = remaining < 0 ? '#e74c3c' : '';
    }
    if (perPersonBudgetEl) perPersonBudgetEl.textContent = memberCount > 0 ? format(perPersonBudget) : '—';
    if (perPersonSpentEl) perPersonSpentEl.textContent = memberCount > 0 ? format(spentPerPerson) : '—';
    if (perPersonRemainingEl) perPersonRemainingEl.textContent = memberCount > 0 ? format(remainingPerPerson) : '—';
    if (selectedGroupSizeEl) selectedGroupSizeEl.textContent = memberCount > 0 ? String(memberCount) : 'Select';
    if (expenseCountEl) expenseCountEl.textContent = String(expenses.length);
    // Refresh expense list UI
    renderExpenses(expenses);
}

/**
 * Persist the current UI state (budget, participants, names, expenses) to the server.
 */
async function persistCurrentState() {
    const participants = parseInt(document.getElementById('memberCount')?.value || '0');
    const memberNames = Array.from(document.querySelectorAll('.member-name')).map(i => i.value.trim());
    const budgetInput = document.getElementById('budgetInput');
    const totalAmount = budgetInput && budgetInput.value ? parseFloat(budgetInput.value) : null;
    const plan = {
        totalAmount,
        participants: participants || null,
        memberNames: memberNames.filter(n => n),
        expenses: currentExpenses
    };
    await saveBudgetPlan(plan);
    // Refresh displayed calculations
    const refreshed = await loadBudgetPlan();
    updateBudgetDisplay(refreshed);
}

// Initialise page once DOM is ready
document.addEventListener('DOMContentLoaded', async () => {
    if (window.currentUserPromise) {
        await window.currentUserPromise;
    }
    initPage();
});
