# REST API Endpoint Documentation

Base URL: `http://localhost:8080`

---

## 1. District Cooling & Municipal Zones
- `GET /api/district/zones` - Retrieve all municipal zones and current demand.
- `GET /api/district/zones/{id}` - Retrieve details of a specific zone.
- `POST /api/district/zones` - Register a new municipal district zone.
- `PUT /api/district/zones/{id}/demand?demandTR=...` - Update real-time cooling load demand for a zone.
- `GET /api/district/total-load` - Get aggregated cooling load across the entire municipality.
- `GET /api/district/substations` - List all Energy Transfer Stations (ETS).

---

## 2. Central Chiller Plant
- `GET /api/chillers` - List all central chillers with COP, status, power, and setpoints.
- `GET /api/chillers/{id}` - Retrieve status of a single chiller.
- `PUT /api/chillers/{id}/mode?status=RUNNING|STANDBY|ICE_MODE` - Update chiller operational status.
- `PUT /api/chillers/{id}/setpoint?tempC=5.0` - Set chilled water supply setpoint temperature.
- `GET /api/chillers/total-power` - Get total electrical power currently consumed by chillers in kW.

---

## 3. Ice Thermal Energy Storage (ITES)
- `GET /api/ice-storage/tanks` - List all ITES tanks, capacity, inventory, and State of Charge (SOC %).
- `GET /api/ice-storage/status` - Aggregated thermal storage overview (total stored TR-hr, avg SOC %).
- `POST /api/ice-storage/tanks/{id}/charge?rateTR=1200` - Start off-peak ice building / charging cycle.
- `POST /api/ice-storage/tanks/{id}/discharge?rateTR=1500` - Start peak-shaving ice melting / discharge cycle.
- `POST /api/ice-storage/tanks/{id}/standby` - Place storage tank in idle storage mode.

---

## 4. Energy Tariff & Grid Schedule
- `GET /api/tariffs` - List all Time-of-Use (TOU) electricity pricing tiers.
- `GET /api/tariffs/current` - Determine active tariff tier according to current hour.
- `POST /api/tariffs` - Add or update a tariff schedule rate.

---

## 5. Smart Dispatch & Optimization Engine
- `GET /api/dispatch/plan` - Generate current optimal cooling dispatch plan (chiller TR vs ice melt TR).
- `POST /api/dispatch/execute` - Execute and log an automated dispatch decision with energy cost savings.
- `GET /api/dispatch/history` - Retrieve dispatch logs and historical savings.

---

## 6. IoT Telemetry & Sensor Alarms
- `GET /api/telemetry/readings` - View latest IoT sensor streams (temperatures, flow rates, pressure).
- `POST /api/telemetry/record` - Ingest a new sensor reading.
- `GET /api/telemetry/alerts` - List active system alerts (warnings, alarms, critical issues).
- `PUT /api/telemetry/alerts/{id}/resolve` - Mark an alert as resolved.

---

## 7. Municipal Billing & Carbon Sustainability
- `GET /api/billing/bills` - Retrieve all monthly municipal bills.
- `POST /api/billing/generate?zoneId=1&month=9&year=2026` - Generate cooling consumption bill with avoided peak penalty.
- `GET /api/billing/sustainability-metrics` - Aggregated municipal carbon emissions avoided (kg CO2) and peak kW shaved.
