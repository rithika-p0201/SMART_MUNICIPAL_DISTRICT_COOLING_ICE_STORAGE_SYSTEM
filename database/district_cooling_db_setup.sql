-- ==============================================================================
-- ALL-IN-ONE DATABASE SETUP SCRIPT FOR MYSQL
-- SMART MUNICIPAL DISTRICT COOLING & ICE THERMAL ENERGY STORAGE (ITES) SYSTEM
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS district_cooling_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE district_cooling_db;

-- Source Schema
source schema.sql;

-- Source Seed Data
source seed_data.sql;

COMMIT;
