-- ==============================================================================
-- SEED DATA: SMART MUNICIPAL DISTRICT COOLING & ICE THERMAL ENERGY STORAGE
-- ==============================================================================

-- 1. Municipal District Zones
INSERT INTO municipal_zones (zone_name, zone_type, peak_demand_tr, current_demand_tr, supply_water_temp_c, return_water_temp_c, water_flow_rate_m3h, target_delta_t_c)
VALUES 
('Metropolitan General Hospital', 'HOSPITAL', 1800.0, 1450.0, 5.0, 13.5, 532.0, 8.5),
('Civic Government Center & Courts', 'CIVIC_CENTER', 1200.0, 850.0, 5.5, 13.2, 345.0, 7.7),
('Financial & Technology Tower Cluster', 'COMMERCIAL', 3500.0, 2900.0, 5.2, 13.8, 1080.0, 8.6),
('Greenfield Municipal Residential Park', 'RESIDENTIAL', 2000.0, 1200.0, 6.0, 13.0, 480.0, 7.0),
('Municipal University Campus & Library', 'EDUCATIONAL', 1500.0, 950.0, 5.5, 13.3, 380.0, 7.8);

-- 2. Cooling Substations (Energy Transfer Stations)
INSERT INTO cooling_substations (substation_code, zone_id, heat_exchanger_capacity_kw, control_valve_opening_pct, differential_pressure_kpa, status)
VALUES 
('ETS-HOSP-01', 1, 6330.0, 82.5, 160.0, 'OPERATIONAL'),
('ETS-CIVIC-01', 2, 4220.0, 65.0, 148.0, 'OPERATIONAL'),
('ETS-FIN-01', 3, 7030.0, 88.0, 172.0, 'OPERATIONAL'),
('ETS-FIN-02', 3, 5275.0, 75.0, 165.0, 'OPERATIONAL'),
('ETS-RES-01', 4, 7030.0, 55.0, 142.0, 'OPERATIONAL'),
('ETS-UNIV-01', 5, 5275.0, 62.0, 150.0, 'OPERATIONAL');

-- 3. Central Chiller Units
INSERT INTO chiller_units (unit_code, chiller_type, rated_cooling_capacity_tr, rated_electrical_power_kw, current_cooling_load_tr, current_power_consumption_kw, cop, status, chilled_water_setpoint_c, evaporator_temp_c, condenser_temp_c)
VALUES 
('CH-01-CENT', 'CENTRIFUGAL', 2500.0, 1500.0, 2200.0, 1320.0, 5.86, 'RUNNING', 5.0, 4.2, 32.5),
('CH-02-CENT', 'CENTRIFUGAL', 2500.0, 1500.0, 2100.0, 1280.0, 5.77, 'RUNNING', 5.0, 4.3, 32.8),
('CH-03-DUAL', 'DUAL_MODE_GLYCOL', 2000.0, 1350.0, 1800.0, 1215.0, 5.21, 'RUNNING', 5.0, -4.5, 33.0),
('CH-04-DUAL', 'DUAL_MODE_GLYCOL', 2000.0, 1350.0, 0.0, 0.0, 5.21, 'STANDBY', -5.5, -6.0, 31.0),
('CH-05-ABS', 'ABSORPTION', 1000.0, 85.0, 900.0, 76.5, 1.25, 'RUNNING', 6.0, 5.5, 34.0);

-- 4. Ice Thermal Energy Storage (ITES) Tanks
INSERT INTO ice_storage_tanks (tank_code, tank_name, storage_type, total_capacity_tr_hr, current_ice_inventory_tr_hr, state_of_charge_pct, glycol_inlet_temp_c, glycol_outlet_temp_c, max_charge_rate_tr, max_discharge_rate_tr, operating_mode)
VALUES 
('ITES-TANK-01', 'East Quad Internal-Melt Ice Tank', 'INTERNAL_MELT_ICE_ON_COIL', 8000.0, 6800.0, 85.0, -3.8, -1.2, 1200.0, 1600.0, 'DISCHARGING_MELT'),
('ITES-TANK-02', 'West Quad Internal-Melt Ice Tank', 'INTERNAL_MELT_ICE_ON_COIL', 8000.0, 6400.0, 80.0, -3.9, -1.4, 1200.0, 1600.0, 'DISCHARGING_MELT'),
('ITES-TANK-03', 'Central Modular Ice Bank Alpha', 'ENCAPSULATED_ICE', 6000.0, 5520.0, 92.0, -4.2, -1.8, 900.0, 1200.0, 'DISCHARGING_MELT'),
('ITES-TANK-04', 'Central Modular Ice Bank Beta', 'ENCAPSULATED_ICE', 6000.0, 5400.0, 90.0, -4.1, -1.7, 900.0, 1200.0, 'IDLE_STORAGE');

-- 5. Time-of-Use Electricity Tariff Schedule
INSERT INTO tariff_rates (tier_name, start_hour, end_hour, rate_per_kwh, demand_charge_per_kw, is_active)
VALUES 
('OFF_PEAK_NIGHT', 23, 7, 0.045, 0.00, TRUE),   -- Cheap nighttime power: Ideal for Ice Charging
('MID_PEAK_MORNING', 7, 12, 0.115, 8.50, TRUE),  -- Normal working rate
('ON_PEAK_AFTERNOON', 12, 18, 0.285, 24.00, TRUE), -- Expensive peak power: Shave peak using ITES ice melt!
('MID_PEAK_EVENING', 18, 23, 0.120, 8.50, TRUE);

-- 6. Baseline Dispatch Strategy Sample
INSERT INTO dispatch_strategies (total_district_demand_tr, chiller_cooling_supplied_tr, ice_discharge_cooling_tr, grid_electrical_power_kw, operating_mode, hourly_energy_cost_usd, baseline_cost_without_storage_usd, hourly_savings_usd)
VALUES 
(7000.0, 4200.0, 2800.0, 2604.0, 'PEAK_SHAVING_DISCHARGE', 742.14, 1236.90, 494.76),
(7350.0, 4500.0, 2850.0, 2790.0, 'PEAK_SHAVING_DISCHARGE', 795.15, 1298.78, 503.63);

-- 7. IoT Telemetry Sensors & Readings
INSERT INTO sensor_readings (sensor_code, sensor_type, reading_value, unit, location)
VALUES 
('TE-CHW-SUP-01', 'TEMPERATURE', 5.2, '°C', 'Central Chilled Water Primary Supply Header'),
('TE-CHW-RET-01', 'TEMPERATURE', 13.6, '°C', 'District Return Chilled Water Header'),
('FE-CHW-TOT-01', 'FLOW_METER', 2817.0, 'm3/h', 'Main Underground Cooling Ring Distribution'),
('PE-CHW-DIFF-01', 'PRESSURE', 220.5, 'kPa', 'Hydraulic Differential Pressure Bypass'),
('TE-GLY-SUP-01', 'TEMPERATURE', -3.9, '°C', 'Ice Storage Glycol Freezing Loop Supply'),
('TE-GLY-RET-01', 'TEMPERATURE', -1.3, '°C', 'Ice Storage Glycol Loop Return'),
('PWR-PLANT-TOT', 'POWER_METER', 3891.5, 'kW', 'Municipal Plant 11kV Substation Feed'),
('ICE-THICK-TK01', 'ICE_LEVEL', 85.0, '%', 'Tank 01 Ultrasonic Ice Inventory Gauge');

-- 8. System Safety & Operating Alerts
INSERT INTO system_alerts (alert_code, severity, component, description, is_resolved)
VALUES 
('ALT-DT-01', 'WARNING', 'District Distribution Loop', 'Low Delta-T syndrome detected at Greenfield Residential Substation (Delta-T = 5.2°C, below target 7.0°C). Secondary balancing recommended.', FALSE),
('ALT-EFF-02', 'INFO', 'Smart Dispatch Engine', 'Off-peak ice charging cycle completed successfully with 92% tank SOC achieved prior to 07:00 AM.', TRUE);
