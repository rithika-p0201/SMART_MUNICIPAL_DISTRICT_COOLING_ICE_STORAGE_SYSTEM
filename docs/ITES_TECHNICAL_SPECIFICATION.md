# Ice Thermal Energy Storage (ITES) Technical Specification

## 1. Thermodynamic Principles

### 1.1 Latent Heat of Fusion
The ITES system stores thermal cooling capacity primarily through phase change (liquid water to solid ice):

$$Q_{latent} = m \cdot L_f$$

Where:
- $m$: Mass of water frozen (kg)
- $L_f$: Latent heat of fusion of water ($\approx 334 \text{ kJ/kg} = 92.78 \text{ kWh/tonne}$)

In standard refrigeration units:
- $1 \text{ Ton of Refrigeration (TR)} = 12,000 \text{ BTU/hr} = 3.517 \text{ kW}$
- $1 \text{ Ton-Hour (TR-hr)} = 12,000 \text{ BTU} = 3.517 \text{ kWh}$ of thermal cooling.
- Melting 1 short ton of ice provides 1 TR-day (24 TR-hrs) of cooling.

---

## 2. Operating Modes & Dispatch Strategies

### 2.1 Mode 1: Off-Peak Ice Charging (23:00 - 07:00)
- **Grid State**: Lowest electricity tariff ($0.045 / kWh).
- **Chiller State**: Dual-mode glycol chillers operate at -6°C suction temp.
- **ITES State**: Brine circulating through coils; ice builds on outer surfaces up to 90–95% SOC.
- **Municipal Demand**: Low base load satisfied by dedicated centrifugal chiller.

### 2.2 Mode 2: Peak Shaving / Full Storage Discharge (12:00 - 18:00)
- **Grid State**: Critical peak electricity tariff ($0.285 / kWh) and peak demand charge ($24.00 / kW).
- **Chiller State**: Compressors unloaded or ramped down to baseline minimum.
- **ITES State**: Warm chilled water return melted through ice banks, discharging up to 3,000+ TR of cooling without consuming heavy electrical power.
- **Benefit**: Up to 60-70% reduction in plant peak electrical power draw (kW).

### 2.3 Mode 3: Partial Storage / Hybrid Operation (07:00 - 12:00 & 18:00 - 23:00)
- **Grid State**: Mid-peak tariff ($0.115 - $0.120 / kWh).
- **Operation**: Chillers run at maximum efficiency (near 100% full-load COP); ice storage is held in standby or dispatched for unexpected demand spikes.

---

## 3. Energy & Economic Benefit Equations

1. **Daily Cost Savings**:
   $$\text{Savings} = (\text{Cost}_{\text{Baseline Chiller Only}} - \text{Cost}_{\text{Chiller + ITES}})$$

2. **Monthly Demand Charge Savings**:
   $$\Delta \text{Charge} = (P_{\text{peak, baseline}} - P_{\text{peak, shaved}}) \times \text{DemandRate}_{\$/kW}$$

3. **Carbon Emission Offset**:
   $$\text{CO}_2 \text{ Avoided (kg)} = \Delta E_{\text{peak}} \times \text{Grid Emission Factor} \times (\eta_{\text{night}} - \eta_{\text{day}})$$
