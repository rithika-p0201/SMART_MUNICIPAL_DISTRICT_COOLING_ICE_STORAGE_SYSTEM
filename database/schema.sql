-- ==============================================================================
-- DATABASE SCHEMA: SMART MUNICIPAL DISTRICT COOLING & ICE THERMAL ENERGY STORAGE
-- ==============================================================================

CREATE TABLE IF NOT EXISTS municipal_zones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_name VARCHAR(100) NOT NULL UNIQUE,
    zone_type VARCHAR(50) NOT NULL, -- HOSPITAL, COMMERCIAL, CIVIC_CENTER, RESIDENTIAL, EDUCATIONAL
    peak_demand_tr DOUBLE NOT NULL,
    current_demand_tr DOUBLE NOT NULL,
    supply_water_temp_c DOUBLE NOT NULL DEFAULT 5.5,
    return_water_temp_c DOUBLE NOT NULL DEFAULT 13.5,
    water_flow_rate_m3h DOUBLE NOT NULL,
    target_delta_t_c DOUBLE NOT NULL DEFAULT 8.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cooling_substations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    substation_code VARCHAR(50) NOT NULL UNIQUE,
    zone_id BIGINT,
    heat_exchanger_capacity_kw DOUBLE NOT NULL,
    control_valve_opening_pct DOUBLE NOT NULL DEFAULT 50.0,
    differential_pressure_kpa DOUBLE NOT NULL DEFAULT 150.0,
    status VARCHAR(30) NOT NULL DEFAULT 'OPERATIONAL', -- OPERATIONAL, BYPASSED, MAINTENANCE
    FOREIGN KEY (zone_id) REFERENCES municipal_zones(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS chiller_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unit_code VARCHAR(50) NOT NULL UNIQUE,
    chiller_type VARCHAR(50) NOT NULL, -- CENTRIFUGAL, DUAL_MODE_GLYCOL, ABSORPTION
    rated_cooling_capacity_tr DOUBLE NOT NULL,
    rated_electrical_power_kw DOUBLE NOT NULL,
    current_cooling_load_tr DOUBLE NOT NULL DEFAULT 0.0,
    current_power_consumption_kw DOUBLE NOT NULL DEFAULT 0.0,
    cop DOUBLE NOT NULL DEFAULT 5.8,
    status VARCHAR(30) NOT NULL DEFAULT 'STANDBY', -- RUNNING, STANDBY, ICE_MODE, MAINTENANCE, FAULT
    chilled_water_setpoint_c DOUBLE NOT NULL DEFAULT 5.0,
    evaporator_temp_c DOUBLE NOT NULL DEFAULT 4.5,
    condenser_temp_c DOUBLE NOT NULL DEFAULT 32.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ice_storage_tanks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tank_code VARCHAR(50) NOT NULL UNIQUE,
    tank_name VARCHAR(100) NOT NULL,
    storage_type VARCHAR(50) NOT NULL DEFAULT 'INTERNAL_MELT_ICE_ON_COIL',
    total_capacity_tr_hr DOUBLE NOT NULL,
    current_ice_inventory_tr_hr DOUBLE NOT NULL DEFAULT 0.0,
    state_of_charge_pct DOUBLE NOT NULL DEFAULT 0.0,
    glycol_inlet_temp_c DOUBLE NOT NULL DEFAULT -4.0,
    glycol_outlet_temp_c DOUBLE NOT NULL DEFAULT -1.5,
    max_charge_rate_tr DOUBLE NOT NULL,
    max_discharge_rate_tr DOUBLE NOT NULL,
    operating_mode VARCHAR(30) NOT NULL DEFAULT 'IDLE_STORAGE', -- CHARGING_FREEZE, DISCHARGING_MELT, IDLE_STORAGE, DEFROST
    last_cycle_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tariff_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tier_name VARCHAR(50) NOT NULL UNIQUE, -- OFF_PEAK_NIGHT, MID_PEAK, ON_PEAK_AFTERNOON
    start_hour INT NOT NULL,
    end_hour INT NOT NULL,
    rate_per_kwh DOUBLE NOT NULL,
    demand_charge_per_kw DOUBLE NOT NULL DEFAULT 0.0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS dispatch_strategies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_district_demand_tr DOUBLE NOT NULL,
    chiller_cooling_supplied_tr DOUBLE NOT NULL,
    ice_discharge_cooling_tr DOUBLE NOT NULL,
    grid_electrical_power_kw DOUBLE NOT NULL,
    operating_mode VARCHAR(50) NOT NULL, -- OFF_PEAK_ICE_BUILDING, PEAK_SHAVING_DISCHARGE, CHILLER_DIRECT, HYBRID_COOLING
    hourly_energy_cost_usd DOUBLE NOT NULL,
    baseline_cost_without_storage_usd DOUBLE NOT NULL,
    hourly_savings_usd DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS sensor_readings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_code VARCHAR(50) NOT NULL,
    sensor_type VARCHAR(50) NOT NULL, -- TEMPERATURE, FLOW_METER, PRESSURE, POWER_METER, ICE_LEVEL
    reading_value DOUBLE NOT NULL,
    unit VARCHAR(20) NOT NULL,
    location VARCHAR(100) NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS system_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_code VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL, -- INFO, WARNING, CRITICAL
    component VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    triggered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS district_cooling_bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_id BIGINT NOT NULL,
    billing_month INT NOT NULL,
    billing_year INT NOT NULL,
    total_ton_hours_consumed DOUBLE NOT NULL,
    energy_charge_usd DOUBLE NOT NULL,
    peak_demand_charge_usd DOUBLE NOT NULL,
    total_bill_amount_usd DOUBLE NOT NULL,
    peak_demand_avoided_kw DOUBLE NOT NULL,
    carbon_reduction_kg_co2 DOUBLE NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (zone_id) REFERENCES municipal_zones(id) ON DELETE CASCADE
);
