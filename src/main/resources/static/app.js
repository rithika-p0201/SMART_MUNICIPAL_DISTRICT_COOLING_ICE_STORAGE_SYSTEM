/**
 * SMART MUNICIPAL DISTRICT COOLING & ITES - DASHBOARD APPLICATION SCRIPT
 */

// Global State
let zonesData = [];
let chillersData = [];
let tanksData = [];
let tariffsData = [];
let dispatchPlan = null;
let telemetryData = [];
let alertsData = [];
let billsData = [];
let dispatchChart = null;
let storageChart = null;

// DOM Ready
document.addEventListener('DOMContentLoaded', () => {
    initClock();
    initTabs();
    initCharts();
    initEventListeners();
    fetchAllData();
    // Auto-refresh every 10 seconds
    setInterval(fetchAllData, 10000);
});

// Clock widget
function initClock() {
    const clockEl = document.getElementById('clockWidget');
    function update() {
        const now = new Date();
        clockEl.textContent = now.toLocaleTimeString() + ' | ' + now.toLocaleDateString();
    }
    update();
    setInterval(update, 1000);
}

// Navigation Tabs
function initTabs() {
    const navButtons = document.querySelectorAll('.nav-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');

    navButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetId = btn.getAttribute('data-tab');
            navButtons.forEach(b => b.classList.remove('active'));
            tabPanes.forEach(p => p.classList.remove('active'));

            btn.classList.add('active');
            const targetPane = document.getElementById(targetId);
            if (targetPane) targetPane.classList.add('active');
        });
    });
}

// Toast notification
function showToast(message) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.add('show');
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Event Listeners for action buttons
function initEventListeners() {
    document.getElementById('btnRefresh').addEventListener('click', () => {
        fetchAllData();
        showToast('Live telemetry & plant data refreshed');
    });

    document.getElementById('btnRunOptimizer').addEventListener('click', executeDispatch);
    const tabDispatchBtn = document.getElementById('btnExecuteDispatchTab');
    if (tabDispatchBtn) tabDispatchBtn.addEventListener('click', executeDispatch);

    document.getElementById('btnChargeMode').addEventListener('click', () => {
        tanksData.forEach(t => chargeTank(t.id));
        showToast('All ITES Ice Banks switched to CHARGING (FREEZE) Mode');
    });

    document.getElementById('btnDischargeMode').addEventListener('click', () => {
        tanksData.forEach(t => dischargeTank(t.id));
        showToast('All ITES Ice Banks switched to DISCHARGING (MELT) Mode');
    });

    document.getElementById('btnOpenAddZone').addEventListener('click', () => {
        const name = prompt('Enter new Municipal Zone Name:');
        if (!name) return;
        const demand = parseFloat(prompt('Enter estimated Cooling Demand in TR:') || '1200');
        createZone(name, demand);
    });

    document.getElementById('btnGenerateBill').addEventListener('click', () => {
        if (zonesData.length === 0) return;
        const zoneId = zonesData[0].id;
        const month = new Date().getMonth() + 1;
        const year = new Date().getFullYear();
        generateBill(zoneId, month, year);
    });
}

// ================= API CALLS =================
async function fetchAllData() {
    const isOnline = await checkConnection();
    updateConnectionStatus(isOnline);

    try {
        await Promise.allSettled([
            fetchZones(),
            fetchSubstations(),
            fetchChillers(),
            fetchTanks(),
            fetchTariffs(),
            fetchDispatchPlan(),
            fetchDispatchHistory(),
            fetchTelemetry(),
            fetchAlerts(),
            fetchBills(),
            fetchSustainability()
        ]);
    } catch (e) {
        console.warn('API fetch warning, using cache/mock data:', e);
    }

    renderAllViews();
}

async function checkConnection() {
    try {
        const res = await fetch('/api/district/total-load', { method: 'GET', cache: 'no-cache' });
        return res.ok;
    } catch {
        return false;
    }
}

function updateConnectionStatus(online) {
    const statusEl = document.getElementById('connStatus');
    const textEl = document.getElementById('connText');
    if (online) {
        statusEl.style.color = 'var(--success)';
        statusEl.style.background = 'rgba(16, 185, 129, 0.1)';
        statusEl.style.borderColor = 'rgba(16, 185, 129, 0.3)';
        textEl.textContent = 'Connected to Spring Boot API';
    } else {
        statusEl.style.color = '#fbbf24';
        statusEl.style.background = 'rgba(245, 158, 11, 0.1)';
        statusEl.style.borderColor = 'rgba(245, 158, 11, 0.3)';
        textEl.textContent = 'Simulated SCADA / Offline Mode';
    }
}

// Fetchers
async function fetchZones() {
    try {
        const res = await fetch('/api/district/zones');
        if (res.ok) zonesData = await res.json();
    } catch {
        if (zonesData.length === 0) zonesData = getMockZones();
    }
}

async function fetchSubstations() {
    try {
        const res = await fetch('/api/district/substations');
        if (res.ok) substationsData = await res.json();
    } catch {
        substationsData = getMockSubstations();
    }
}

async function fetchChillers() {
    try {
        const res = await fetch('/api/chillers');
        if (res.ok) chillersData = await res.json();
    } catch {
        if (chillersData.length === 0) chillersData = getMockChillers();
    }
}

async function fetchTanks() {
    try {
        const res = await fetch('/api/ice-storage/tanks');
        if (res.ok) tanksData = await res.json();
    } catch {
        if (tanksData.length === 0) tanksData = getMockTanks();
    }
}

async function fetchTariffs() {
    try {
        const res = await fetch('/api/tariffs');
        if (res.ok) tariffsData = await res.json();
    } catch {
        tariffsData = getMockTariffs();
    }
}

async function fetchDispatchPlan() {
    try {
        const res = await fetch('/api/dispatch/plan');
        if (res.ok) dispatchPlan = await res.json();
    } catch {
        dispatchPlan = getMockDispatchPlan();
    }
}

async function fetchDispatchHistory() {
    try {
        const res = await fetch('/api/dispatch/history');
        if (res.ok) dispatchHistoryData = await res.json();
    } catch {
        dispatchHistoryData = getMockDispatchHistory();
    }
}

async function fetchTelemetry() {
    try {
        const res = await fetch('/api/telemetry/readings');
        if (res.ok) telemetryData = await res.json();
    } catch {
        telemetryData = getMockTelemetry();
    }
}

async function fetchAlerts() {
    try {
        const res = await fetch('/api/telemetry/alerts');
        if (res.ok) alertsData = await res.json();
    } catch {
        alertsData = getMockAlerts();
    }
}

async function fetchBills() {
    try {
        const res = await fetch('/api/billing/bills');
        if (res.ok) billsData = await res.json();
    } catch {
        billsData = getMockBills();
    }
}

async function fetchSustainability() {
    try {
        const res = await fetch('/api/billing/sustainability-metrics');
        if (res.ok) sustainabilityMetrics = await res.json();
    } catch {
        sustainabilityMetrics = { totalCarbonReductionMetricTons: 1482.0, totalPeakDemandAvoidedMW: 2.85 };
    }
}

// Action POST/PUT
async function updateZoneDemand(id, newDemand) {
    try {
        await fetch(`/api/district/zones/${id}/demand?demandTR=${newDemand}`, { method: 'PUT' });
        showToast(`Updated cooling demand for Zone #${id} to ${newDemand} TR`);
    } catch (e) {
        showToast(`Simulated: Updated Zone #${id} to ${newDemand} TR`);
    }
    const zone = zonesData.find(z => z.id === id);
    if (zone) zone.currentDemandTR = parseFloat(newDemand);
    renderAllViews();
}

async function createZone(name, demand) {
    const payload = {
        zoneName: name,
        zoneType: 'COMMERCIAL',
        peakDemandTR: demand * 1.25,
        currentDemandTR: demand,
        supplyWaterTempC: 5.2,
        returnWaterTempC: 13.5,
        waterFlowRateM3h: demand * 0.36,
        targetDeltaTC: 8.0
    };
    try {
        await fetch('/api/district/zones', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        showToast(`Created new zone: ${name}`);
        fetchZones();
    } catch {
        payload.id = Date.now();
        zonesData.push(payload);
        showToast(`Added Zone ${name}`);
        renderAllViews();
    }
}

async function chargeTank(id) {
    try {
        await fetch(`/api/ice-storage/tanks/${id}/charge?rateTR=1200`, { method: 'POST' });
        showToast(`Tank #${id} set to Ice Charging (Freeze) Mode`);
    } catch {
        showToast(`Simulated: Tank #${id} Charging`);
    }
    const tank = tanksData.find(t => t.id === id);
    if (tank) {
        tank.operatingMode = 'CHARGING_FREEZE';
        tank.stateOfChargePct = Math.min(100, tank.stateOfChargePct + 5);
    }
    renderAllViews();
}

async function dischargeTank(id) {
    try {
        await fetch(`/api/ice-storage/tanks/${id}/discharge?rateTR=1500`, { method: 'POST' });
        showToast(`Tank #${id} set to Ice Discharging (Melt) Mode`);
    } catch {
        showToast(`Simulated: Tank #${id} Discharging`);
    }
    const tank = tanksData.find(t => t.id === id);
    if (tank) {
        tank.operatingMode = 'DISCHARGING_MELT';
        tank.stateOfChargePct = Math.max(0, tank.stateOfChargePct - 5);
    }
    renderAllViews();
}

async function setTankStandby(id) {
    try {
        await fetch(`/api/ice-storage/tanks/${id}/standby`, { method: 'POST' });
        showToast(`Tank #${id} set to Idle Storage`);
    } catch {
        showToast(`Simulated: Tank #${id} Idle`);
    }
    const tank = tanksData.find(t => t.id === id);
    if (tank) tank.operatingMode = 'IDLE_STORAGE';
    renderAllViews();
}

async function toggleChillerMode(id, status) {
    try {
        await fetch(`/api/chillers/${id}/mode?status=${status}`, { method: 'PUT' });
        showToast(`Chiller #${id} switched to ${status}`);
    } catch {
        showToast(`Simulated: Chiller #${id} set to ${status}`);
    }
    const ch = chillersData.find(c => c.id === id);
    if (ch) ch.status = status;
    renderAllViews();
}

async function executeDispatch() {
    try {
        const res = await fetch('/api/dispatch/execute', { method: 'POST' });
        if (res.ok) {
            const data = await res.json();
            showToast(`Optimization executed! Mode: ${data.operatingMode}, Hourly Savings: $${data.hourlySavingsUSD.toFixed(2)}`);
        }
    } catch {
        showToast('Simulated: Peak-shaving dispatch executed (Saved: $503.63/hr)');
    }
    fetchAllData();
}

async function resolveAlert(id) {
    try {
        await fetch(`/api/telemetry/alerts/${id}/resolve`, { method: 'PUT' });
        showToast(`Alert #${id} marked as resolved`);
    } catch {
        showToast(`Simulated: Alert #${id} resolved`);
    }
    alertsData = alertsData.filter(a => a.id !== id);
    renderAllViews();
}

async function generateBill(zoneId, month, year) {
    try {
        const res = await fetch(`/api/billing/generate?zoneId=${zoneId}&month=${month}&year=${year}`, { method: 'POST' });
        if (res.ok) {
            showToast(`Generated bill for Month ${month}/${year}`);
            fetchBills();
        }
    } catch {
        showToast('Generated simulated municipal cooling bill');
    }
}

// ================= RENDER VIEWS =================
function renderAllViews() {
    renderKPIs();
    renderZones();
    renderSubstations();
    renderTanks();
    renderChillers();
    renderTariffs();
    renderDispatch();
    renderTelemetry();
    renderAlerts();
    renderBills();
    updateCharts();
}

function renderKPIs() {
    const totalDemandTR = zonesData.reduce((sum, z) => sum + (z.currentDemandTR || 0), 0);
    document.getElementById('kpiDemandTR').innerHTML = `${totalDemandTR.toLocaleString()} <span class="unit">TR</span>`;
    document.getElementById('kpiDemandKW').textContent = `${Math.round(totalDemandTR * 3.51685).toLocaleString()} kW Thermal Equivalent`;

    const totalPowerKW = chillersData.reduce((sum, c) => sum + (c.currentPowerConsumptionKW || 0), 0);
    document.getElementById('kpiPowerKW').innerHTML = `${totalPowerKW.toLocaleString()} <span class="unit">kW</span>`;

    const totalIceInv = tanksData.reduce((sum, t) => sum + (t.currentIceInventoryTRHr || 0), 0);
    const totalIceCap = tanksData.reduce((sum, t) => sum + (t.totalCapacityTRHr || 0), 0);
    const avgSOC = totalIceCap > 0 ? (totalIceInv / totalIceCap) * 100 : 85.0;

    document.getElementById('kpiIceInv').innerHTML = `${Math.round(totalIceInv).toLocaleString()} <span class="unit">TR-hr</span>`;
    document.getElementById('kpiAvgSOC').textContent = `Average State of Charge: ${avgSOC.toFixed(1)}%`;

    if (dispatchPlan) {
        document.getElementById('plantCurrentMode').textContent = dispatchPlan.recommendedMode;
        document.getElementById('kpiTariffTier').textContent = dispatchPlan.activeTariffTier;
        document.getElementById('kpiTariffRate').innerHTML = `$${dispatchPlan.tariffRatePerKWh.toFixed(3)} <span class="unit">/kWh</span>`;
        document.getElementById('kpiHourlySavings').textContent = `Active Savings: +$${dispatchPlan.hourlySavingsUSD.toFixed(2)}/hr`;
    }
}

function renderZones() {
    const tbody = document.getElementById('zonesTableBody');
    if (!tbody) return;
    tbody.innerHTML = '';

    zonesData.forEach(z => {
        const deltaT = (z.returnWaterTempC - z.supplyWaterTempC).toFixed(1);
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><code>ZONE-${z.id}</code></td>
            <td><strong>${z.zoneName}</strong></td>
            <td><span class="badge" style="background: rgba(37,99,235,0.15); color: #60a5fa;">${z.zoneType}</span></td>
            <td><strong style="color: var(--ice-cyan);">${z.currentDemandTR} TR</strong></td>
            <td>${z.peakDemandTR} TR</td>
            <td>${z.supplyWaterTempC}°C</td>
            <td>${z.returnWaterTempC}°C</td>
            <td><span style="color: ${deltaT >= 7.0 ? 'var(--success)' : 'var(--warning)'}; font-weight:700;">${deltaT}°C</span></td>
            <td>${z.waterFlowRateM3h} m³/h</td>
            <td>
                <input type="range" min="200" max="${z.peakDemandTR * 1.1}" value="${z.currentDemandTR}" 
                    style="width: 100px; accent-color: var(--ice-cyan);"
                    onchange="updateZoneDemand(${z.id}, this.value)">
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function renderSubstations() {
    const tbody = document.getElementById('substationsTableBody');
    if (!tbody || !window.substationsData) return;
    tbody.innerHTML = '';

    substationsData.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><code>${s.substationCode}</code></td>
            <td>${s.zone ? s.zone.zoneName : 'Zone District'}</td>
            <td>${s.heatExchangerCapacityKW.toLocaleString()} kW</td>
            <td>${s.controlValveOpeningPct}%</td>
            <td>${s.differentialPressureKPa} kPa</td>
            <td><span class="badge" style="background: rgba(16,185,129,0.15); color: #34d399;">${s.status}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderTanks() {
    const container = document.getElementById('tanksContainer');
    if (!container) return;
    container.innerHTML = '';

    tanksData.forEach(t => {
        const card = document.createElement('div');
        card.className = 'tank-card';
        card.innerHTML = `
            <div class="tank-card-header">
                <div>
                    <h4>${t.tankName}</h4>
                    <span style="font-size:0.75rem; color:var(--text-muted); font-family:var(--font-mono);">${t.tankCode}</span>
                </div>
                <span class="badge" style="background: rgba(56,189,248,0.15); color: var(--ice-cyan);">${t.operatingMode}</span>
            </div>
            <div class="tank-visual">
                <div class="ice-level-fill" style="height: ${t.stateOfChargePct}%;"></div>
                <div class="tank-soc-text">${t.stateOfChargePct.toFixed(0)}%</div>
            </div>
            <div style="font-size:0.8rem; color:var(--text-muted); line-height:1.6;">
                <div>Capacity: <strong>${t.totalCapacityTRHr.toLocaleString()} TR-hr</strong></div>
                <div>Ice Stored: <strong style="color:var(--ice-cyan);">${Math.round(t.currentIceInventoryTRHr).toLocaleString()} TR-hr</strong></div>
                <div>Glycol Loop: <strong>${t.glycolInletTempC}°C In / ${t.glycolOutletTempC}°C Out</strong></div>
            </div>
            <div class="tank-controls">
                <button class="btn btn-secondary btn-sm" onclick="chargeTank(${t.id})">❄️ Charge (Freeze)</button>
                <button class="btn btn-primary btn-sm" onclick="dischargeTank(${t.id})">🧊 Melt (Peak Shave)</button>
                <button class="btn btn-secondary btn-sm" onclick="setTankStandby(${t.id})">Idle</button>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderChillers() {
    const container = document.getElementById('chillersContainer');
    if (!container) return;
    container.innerHTML = '';

    chillersData.forEach(c => {
        const loadRatio = c.ratedCoolingCapacityTR > 0 ? (c.currentCoolingLoadTR / c.ratedCoolingCapacityTR) * 100 : 0;
        const card = document.createElement('div');
        card.className = 'chiller-card';
        card.innerHTML = `
            <div class="chiller-header">
                <div>
                    <h4>${c.unitCode}</h4>
                    <span style="font-size:0.75rem; color:var(--text-muted);">${c.chillerType}</span>
                </div>
                <span class="badge ${c.status === 'RUNNING' ? 'status-active' : c.status === 'ICE_MODE' ? 'status-cooling' : ''}">
                    ${c.status}
                </span>
            </div>
            <div style="font-size:0.8rem; display:flex; justify-content:space-between; margin-bottom:0.25rem;">
                <span>Cooling Load: <strong>${c.currentCoolingLoadTR} / ${c.ratedCoolingCapacityTR} TR</strong></span>
                <span><strong>${loadRatio.toFixed(0)}%</strong></span>
            </div>
            <div class="progress-bar-wrap">
                <div class="progress-bar" style="width: ${loadRatio}%;"></div>
            </div>
            <div style="font-size:0.8rem; color:var(--text-muted); line-height:1.6;">
                <div>Power Consumption: <strong style="color:var(--text-main);">${c.currentPowerConsumptionKW} kW</strong></div>
                <div>Rated COP: <strong style="color:var(--success);">${c.cop}</strong></div>
                <div>Chilled Water Setpoint: <strong>${c.chilledWaterSetpointC}°C</strong></div>
                <div>Evap / Condenser: <strong>${c.evaporatorTempC}°C / ${c.condenserTempC}°C</strong></div>
            </div>
            <div style="display:flex; gap:0.5rem; margin-top:1rem;">
                <button class="btn btn-secondary btn-sm" onclick="toggleChillerMode(${c.id}, 'RUNNING')">Run Chiller</button>
                <button class="btn btn-outline btn-sm" onclick="toggleChillerMode(${c.id}, 'ICE_MODE')">Ice Freeze Mode</button>
                <button class="btn btn-secondary btn-sm" onclick="toggleChillerMode(${c.id}, 'STANDBY')">Standby</button>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderTariffs() {
    const tbody = document.getElementById('tariffTableBody');
    if (!tbody) return;
    tbody.innerHTML = '';

    tariffsData.forEach(t => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${t.tierName}</strong></td>
            <td><code>${t.startHour}:00 - ${t.endHour}:00</code></td>
            <td><strong style="color: ${t.ratePerKWh > 0.2 ? 'var(--warning)' : 'var(--success)'};">$${t.ratePerKWh.toFixed(3)}</strong></td>
            <td>$${t.demandChargePerKW.toFixed(2)}</td>
            <td><span style="font-size:0.75rem; color:var(--text-muted);">${t.ratePerKWh > 0.2 ? 'Discharge Ice Storage' : t.ratePerKWh < 0.08 ? 'Freeze Ice Full Charge' : 'Base Chiller Cooling'}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderDispatch() {
    const box = document.getElementById('dispatchPlanBox');
    if (!box || !dispatchPlan) return;

    box.innerHTML = `
        <div class="dispatch-stat-row">
            <span>Recommended Operation Mode:</span>
            <strong style="color:var(--ice-cyan);">${dispatchPlan.recommendedMode}</strong>
        </div>
        <div class="dispatch-stat-row">
            <span>Active Electricity Tariff Tier:</span>
            <strong>${dispatchPlan.activeTariffTier} ($${dispatchPlan.tariffRatePerKWh.toFixed(3)}/kWh)</strong>
        </div>
        <div class="dispatch-stat-row">
            <span>Total Municipal Demand:</span>
            <strong>${dispatchPlan.totalDemandTR} TR</strong>
        </div>
        <div class="dispatch-stat-row">
            <span>Central Chillers Output:</span>
            <strong>${dispatchPlan.chillerCoolingTR} TR</strong>
        </div>
        <div class="dispatch-stat-row">
            <span>Ice Storage Melt Discharge:</span>
            <strong style="color:var(--ice-cyan);">${dispatchPlan.iceMeltCoolingTR} TR</strong>
        </div>
        <div class="dispatch-stat-row">
            <span>Optimized Plant Electrical Power:</span>
            <strong style="color:var(--success);">${Math.round(dispatchPlan.optimizedGridPowerKW)} kW</strong>
        </div>
        <div class="dispatch-stat-row">
            <span>Baseline Power (Without ITES):</span>
            <span style="color:var(--text-muted); text-decoration:line-through;">${Math.round(dispatchPlan.baselineGridPowerKW)} kW</span>
        </div>
        <div class="dispatch-stat-row">
            <span>Estimated Hourly Savings:</span>
            <strong style="color:var(--success); font-size:1.1rem;">+$${dispatchPlan.hourlySavingsUSD.toFixed(2)}/hr</strong>
        </div>
    `;

    // History Table
    const histBody = document.getElementById('dispatchHistoryTableBody');
    if (histBody && window.dispatchHistoryData) {
        histBody.innerHTML = '';
        dispatchHistoryData.forEach(h => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><code>${h.timestamp ? h.timestamp.replace('T', ' ') : 'Just Now'}</code></td>
                <td>${h.totalDistrictDemandTR} TR</td>
                <td>${h.chillerCoolingSuppliedTR} TR</td>
                <td><strong style="color:var(--ice-cyan);">${h.iceDischargeCoolingTR} TR</strong></td>
                <td>${Math.round(h.gridElectricalPowerKW)} kW</td>
                <td><span class="badge status-cooling">${h.operatingMode}</span></td>
                <td>$${h.hourlyEnergyCostUSD.toFixed(2)}</td>
                <td>$${h.baselineCostWithoutStorageUSD.toFixed(2)}</td>
                <td><strong style="color:var(--success);">+$${h.hourlySavingsUSD.toFixed(2)}</strong></td>
            `;
            histBody.appendChild(tr);
        });
    }
}

function renderTelemetry() {
    const tbody = document.getElementById('telemetryTableBody');
    if (!tbody) return;
    tbody.innerHTML = '';

    telemetryData.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><code>${s.sensorCode}</code></td>
            <td><span class="badge primary">${s.sensorType}</span></td>
            <td><strong style="font-family:var(--font-mono); font-size:1rem;">${s.readingValue}</strong></td>
            <td>${s.unit}</td>
            <td>${s.location}</td>
            <td><span class="badge" style="background:rgba(16,185,129,0.15); color:#34d399;">NORMAL</span></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderAlerts() {
    const container = document.getElementById('alertsListContainer');
    const badge = document.getElementById('alertCountBadge');
    if (badge) badge.textContent = alertsData.length;
    if (!container) return;

    if (alertsData.length === 0) {
        container.innerHTML = `<p style="color:var(--text-muted); font-size:0.85rem;">No active alerts. All district cooling loops operating within optimal parameters.</p>`;
        return;
    }

    container.innerHTML = '';
    alertsData.forEach(a => {
        const div = document.createElement('div');
        div.className = `alert-item ${a.severity.toLowerCase()}`;
        div.innerHTML = `
            <div class="alert-info">
                <h4>[${a.severity}] ${a.alertCode}: ${a.component}</h4>
                <p>${a.description}</p>
            </div>
            <button class="btn btn-secondary btn-sm" onclick="resolveAlert(${a.id})">Acknowledge & Resolve</button>
        `;
        container.appendChild(div);
    });
}

function renderBills() {
    const tbody = document.getElementById('billsTableBody');
    if (!tbody) return;
    tbody.innerHTML = '';

    billsData.forEach(b => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><code>BILL-${b.id}</code></td>
            <td><strong>${b.zone ? b.zone.zoneName : 'Municipal Zone'}</strong></td>
            <td>${b.billingMonth}/${b.billingYear}</td>
            <td>${Math.round(b.totalTonHoursConsumed).toLocaleString()} TR-hr</td>
            <td>$${b.energyChargeUSD.toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
            <td>$${b.peakDemandChargeUSD.toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
            <td><strong style="color:var(--text-main);">$${b.totalBillAmountUSD.toLocaleString(undefined, {minimumFractionDigits: 2})}</strong></td>
            <td><strong style="color:var(--success);">${Math.round(b.peakDemandAvoidedKW)} kW</strong></td>
            <td>${Math.round(b.carbonReductionKgCO2).toLocaleString()} kg</td>
            <td><span class="badge" style="background:rgba(245,158,11,0.15); color:#fbbf24;">${b.paymentStatus}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

// ================= CHARTS =================
function initCharts() {
    // 24-hr load chart
    const ctx1 = document.getElementById('dispatchChart');
    if (ctx1) {
        const hours = Array.from({ length: 24 }, (_, i) => `${i}:00`);
        const demand = [3100, 2900, 2800, 2750, 2700, 2850, 3200, 4100, 5200, 6100, 6800, 7100, 7500, 7800, 7950, 7850, 7400, 6900, 6200, 5500, 4800, 4200, 3700, 3300];
        const chillerLoad = [5100, 4900, 4800, 4750, 4700, 4850, 3200, 4100, 5200, 6100, 6800, 5500, 4500, 4500, 4500, 4500, 4500, 4500, 5500, 5500, 4800, 4200, 3700, 5300];
        const iceDischarge = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1600, 3000, 3300, 3450, 3350, 2900, 2400, 700, 0, 0, 0, 0, 0];

        dispatchChart = new Chart(ctx1, {
            type: 'line',
            data: {
                labels: hours,
                datasets: [
                    {
                        label: 'Total Municipal Demand (TR)',
                        data: demand,
                        borderColor: '#94a3b8',
                        borderWidth: 2,
                        borderDash: [5, 5],
                        tension: 0.3,
                        pointRadius: 2
                    },
                    {
                        label: 'Central Chillers Output (TR)',
                        data: chillerLoad,
                        borderColor: '#38bdf8',
                        backgroundColor: 'rgba(56, 189, 248, 0.15)',
                        fill: true,
                        tension: 0.3,
                        pointRadius: 2
                    },
                    {
                        label: 'Ice Storage Peak-Shaving Melt (TR)',
                        data: iceDischarge,
                        borderColor: '#10b981',
                        backgroundColor: 'rgba(16, 185, 129, 0.2)',
                        fill: true,
                        tension: 0.3,
                        pointRadius: 2
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { labels: { color: '#94a3b8', font: { family: 'Plus Jakarta Sans', size: 11 } } }
                },
                scales: {
                    x: { ticks: { color: '#64748b' }, grid: { color: '#1e293b' } },
                    y: { ticks: { color: '#64748b' }, grid: { color: '#1e293b' }, title: { display: true, text: 'Cooling Capacity (TR)', color: '#64748b' } }
                }
            }
        });
    }

    // Storage SOC chart
    const ctx2 = document.getElementById('storageChart');
    if (ctx2) {
        storageChart = new Chart(ctx2, {
            type: 'bar',
            data: {
                labels: ['Tank 01 (East)', 'Tank 02 (West)', 'Bank Alpha', 'Bank Beta'],
                datasets: [{
                    label: 'State of Charge (%)',
                    data: [85, 80, 92, 90],
                    backgroundColor: ['#0284c7', '#38bdf8', '#06b6d4', '#60a5fa'],
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    x: { ticks: { color: '#64748b' }, grid: { display: false } },
                    y: { max: 100, ticks: { color: '#64748b' }, grid: { color: '#1e293b' } }
                }
            }
        });
    }
}

function updateCharts() {
    if (storageChart && tanksData.length >= 4) {
        storageChart.data.datasets[0].data = tanksData.slice(0, 4).map(t => t.stateOfChargePct);
        storageChart.update();
    }
}

// ================= MOCK FALLBACK DATA =================
function getMockZones() {
    return [
        { id: 1, zoneName: 'Metropolitan General Hospital', zoneType: 'HOSPITAL', peakDemandTR: 1800, currentDemandTR: 1450, supplyWaterTempC: 5.0, returnWaterTempC: 13.5, waterFlowRateM3h: 532 },
        { id: 2, zoneName: 'Civic Government Center & Courts', zoneType: 'CIVIC_CENTER', peakDemandTR: 1200, currentDemandTR: 850, supplyWaterTempC: 5.5, returnWaterTempC: 13.2, waterFlowRateM3h: 345 },
        { id: 3, zoneName: 'Financial & Technology Tower Cluster', zoneType: 'COMMERCIAL', peakDemandTR: 3500, currentDemandTR: 2900, supplyWaterTempC: 5.2, returnWaterTempC: 13.8, waterFlowRateM3h: 1080 },
        { id: 4, zoneName: 'Greenfield Municipal Residential Park', zoneType: 'RESIDENTIAL', peakDemandTR: 2000, currentDemandTR: 1200, supplyWaterTempC: 6.0, returnWaterTempC: 13.0, waterFlowRateM3h: 480 },
        { id: 5, zoneName: 'Municipal University Campus & Library', zoneType: 'EDUCATIONAL', peakDemandTR: 1500, currentDemandTR: 950, supplyWaterTempC: 5.5, returnWaterTempC: 13.3, waterFlowRateM3h: 380 }
    ];
}

function getMockSubstations() {
    return [
        { substationCode: 'ETS-HOSP-01', zone: { zoneName: 'Metropolitan General Hospital' }, heatExchangerCapacityKW: 6330, controlValveOpeningPct: 82.5, differentialPressureKPa: 160, status: 'OPERATIONAL' },
        { substationCode: 'ETS-CIVIC-01', zone: { zoneName: 'Civic Government Center' }, heatExchangerCapacityKW: 4220, controlValveOpeningPct: 65.0, differentialPressureKPa: 148, status: 'OPERATIONAL' },
        { substationCode: 'ETS-FIN-01', zone: { zoneName: 'Financial Towers' }, heatExchangerCapacityKW: 7030, controlValveOpeningPct: 88.0, differentialPressureKPa: 172, status: 'OPERATIONAL' },
        { substationCode: 'ETS-RES-01', zone: { zoneName: 'Residential Park' }, heatExchangerCapacityKW: 7030, controlValveOpeningPct: 55.0, differentialPressureKPa: 142, status: 'OPERATIONAL' }
    ];
}

function getMockChillers() {
    return [
        { id: 1, unitCode: 'CH-01-CENT', chillerType: 'CENTRIFUGAL', ratedCoolingCapacityTR: 2500, currentCoolingLoadTR: 2200, currentPowerConsumptionKW: 1320, cop: 5.86, status: 'RUNNING', chilledWaterSetpointC: 5.0, evaporatorTempC: 4.2, condenserTempC: 32.5 },
        { id: 2, unitCode: 'CH-02-CENT', chillerType: 'CENTRIFUGAL', ratedCoolingCapacityTR: 2500, currentCoolingLoadTR: 2100, currentPowerConsumptionKW: 1280, cop: 5.77, status: 'RUNNING', chilledWaterSetpointC: 5.0, evaporatorTempC: 4.3, condenserTempC: 32.8 },
        { id: 3, unitCode: 'CH-03-DUAL', chillerType: 'DUAL_MODE_GLYCOL', ratedCoolingCapacityTR: 2000, currentCoolingLoadTR: 1800, currentPowerConsumptionKW: 1215, cop: 5.21, status: 'RUNNING', chilledWaterSetpointC: 5.0, evaporatorTempC: -4.5, condenserTempC: 33.0 },
        { id: 4, unitCode: 'CH-04-DUAL', chillerType: 'DUAL_MODE_GLYCOL', ratedCoolingCapacityTR: 2000, currentCoolingLoadTR: 0, currentPowerConsumptionKW: 0, cop: 5.21, status: 'STANDBY', chilledWaterSetpointC: -5.5, evaporatorTempC: -6.0, condenserTempC: 31.0 }
    ];
}

function getMockTanks() {
    return [
        { id: 1, tankCode: 'ITES-TANK-01', tankName: 'East Quad Internal-Melt Ice Tank', totalCapacityTRHr: 8000, currentIceInventoryTRHr: 6800, stateOfChargePct: 85, glycolInletTempC: -3.8, glycolOutletTempC: -1.2, operatingMode: 'DISCHARGING_MELT' },
        { id: 2, tankCode: 'ITES-TANK-02', tankName: 'West Quad Internal-Melt Ice Tank', totalCapacityTRHr: 8000, currentIceInventoryTRHr: 6400, stateOfChargePct: 80, glycolInletTempC: -3.9, glycolOutletTempC: -1.4, operatingMode: 'DISCHARGING_MELT' },
        { id: 3, tankCode: 'ITES-TANK-03', tankName: 'Central Modular Ice Bank Alpha', totalCapacityTRHr: 6000, currentIceInventoryTRHr: 5520, stateOfChargePct: 92, glycolInletTempC: -4.2, glycolOutletTempC: -1.8, operatingMode: 'DISCHARGING_MELT' },
        { id: 4, tankCode: 'ITES-TANK-04', tankName: 'Central Modular Ice Bank Beta', totalCapacityTRHr: 6000, currentIceInventoryTRHr: 5400, stateOfChargePct: 90, glycolInletTempC: -4.1, glycolOutletTempC: -1.7, operatingMode: 'IDLE_STORAGE' }
    ];
}

function getMockTariffs() {
    return [
        { tierName: 'OFF_PEAK_NIGHT', startHour: 23, endHour: 7, ratePerKWh: 0.045, demandChargePerKW: 0.0 },
        { tierName: 'MID_PEAK_MORNING', startHour: 7, endHour: 12, ratePerKWh: 0.115, demandChargePerKW: 8.5 },
        { tierName: 'ON_PEAK_AFTERNOON', startHour: 12, endHour: 18, ratePerKWh: 0.285, demandChargePerKW: 24.0 },
        { tierName: 'MID_PEAK_EVENING', startHour: 18, endHour: 23, ratePerKWh: 0.120, demandChargePerKW: 8.5 }
    ];
}

function getMockDispatchPlan() {
    return {
        recommendedMode: 'PEAK_SHAVING_DISCHARGE',
        activeTariffTier: 'ON_PEAK_AFTERNOON',
        tariffRatePerKWh: 0.285,
        totalDemandTR: 7350,
        chillerCoolingTR: 4500,
        iceMeltCoolingTR: 2850,
        optimizedGridPowerKW: 2790,
        baselineGridPowerKW: 4557,
        hourlySavingsUSD: 503.63
    };
}

function getMockDispatchHistory() {
    return [
        { timestamp: '2026-09-27T14:00:00', totalDistrictDemandTR: 7350, chillerCoolingSuppliedTR: 4500, iceDischargeCoolingTR: 2850, gridElectricalPowerKW: 2790, operatingMode: 'PEAK_SHAVING_DISCHARGE', hourlyEnergyCostUSD: 795.15, baselineCostWithoutStorageUSD: 1298.78, hourlySavingsUSD: 503.63 },
        { timestamp: '2026-09-27T13:00:00', totalDistrictDemandTR: 7000, chillerCoolingSuppliedTR: 4200, iceDischargeCoolingTR: 2800, gridElectricalPowerKW: 2604, operatingMode: 'PEAK_SHAVING_DISCHARGE', hourlyEnergyCostUSD: 742.14, baselineCostWithoutStorageUSD: 1236.90, hourlySavingsUSD: 494.76 }
    ];
}

function getMockTelemetry() {
    return [
        { sensorCode: 'TE-CHW-SUP-01', sensorType: 'TEMPERATURE', readingValue: 5.2, unit: '°C', location: 'Central Chilled Water Primary Supply Header' },
        { sensorCode: 'TE-CHW-RET-01', sensorType: 'TEMPERATURE', readingValue: 13.6, unit: '°C', location: 'District Return Chilled Water Header' },
        { sensorCode: 'FE-CHW-TOT-01', sensorType: 'FLOW_METER', readingValue: 2817.0, unit: 'm3/h', location: 'Main Underground Cooling Ring Distribution' },
        { sensorCode: 'PE-CHW-DIFF-01', sensorType: 'PRESSURE', readingValue: 220.5, unit: 'kPa', location: 'Hydraulic Differential Pressure Bypass' },
        { sensorCode: 'PWR-PLANT-TOT', sensorType: 'POWER_METER', readingValue: 3891.5, unit: 'kW', location: 'Municipal Plant 11kV Substation Feed' },
        { sensorCode: 'ICE-THICK-TK01', sensorType: 'ICE_LEVEL', readingValue: 85.0, unit: '%', location: 'Tank 01 Ultrasonic Ice Gauge' }
    ];
}

function getMockAlerts() {
    return [
        { id: 1, alertCode: 'ALT-DT-01', severity: 'WARNING', component: 'District Distribution Loop', description: 'Low Delta-T syndrome detected at Greenfield Residential Substation (Delta-T = 5.2°C, below target 7.0°C).' }
    ];
}

function getMockBills() {
    return [
        { id: 1, zone: { zoneName: 'Metropolitan General Hospital' }, billingMonth: 9, billingYear: 2026, totalTonHoursConsumed: 696000, energyChargeUSD: 125280, peakDemandChargeUSD: 22500, totalBillAmountUSD: 147780, peakDemandAvoidedKW: 2215, carbonReductionKgCO2: 176227, paymentStatus: 'PENDING' },
        { id: 2, zone: { zoneName: 'Financial & Tech Towers' }, billingMonth: 9, billingYear: 2026, totalTonHoursConsumed: 1392000, energyChargeUSD: 250560, peakDemandChargeUSD: 43750, totalBillAmountUSD: 294310, peakDemandAvoidedKW: 4308, carbonReductionKgCO2: 352454, paymentStatus: 'PENDING' }
    ];
}
