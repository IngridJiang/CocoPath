# TinyBrakeVSUM Case Study

## 1. Domain Overview

TinyBrakeVSUM is a Vitruvius model transformation case study bridging two engineering
viewpoints of a braking system:

```
┌─────────────────────────────────────┐       Vitruvius VSUM        ┌──────────────────────────────────────┐
│         BrakeSystem  (model)        │ ──── consistency rules ────▶ │       ControlSystem  (model2)        │
│                                     │                              │                                      │
│  BrakeDisc                          │                              │  AxleControlUnit                     │
│    diameter : double                │  BrakeDisc ←1:1→ AxleUnit   │    controlProfile : String           │
│    material : String                │                              │    absDecelThreshold : double        │
│    (basePressure : double)          │                              │    maxBrakingTorque  : double        │
│                                     │                              │    calibrationOffset : double        │
│                                     │                              │    effectiveBrakeGain : double       │
└─────────────────────────────────────┘                              └──────────────────────────────────────┘
    Mechanical / hardware domain                                          Software / control domain
```

**Semantics**: every `BrakeDisc` added to the `BrakeSystem` must have a *consistent*
counterpart `AxleControlUnit` in the `ControlSystem`.  The Vitruvius reactions enforce
this invariant automatically whenever the source model changes.

---

## 2. Reaction Chain on BrakeDisc Insertion

When a `BrakeDisc` is inserted, two Vitruvius reactions fire in sequence:

```
BrakeDisc inserted
       │
       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Reaction: CreateAndConfigureAxleUnit                                       │
│                                                                             │
│  1. Ask user: "Enter drive aggressiveness (0–100, or -1 to skip)"          │
│     (symbolic variable:  profileChoice ∈ [−1, 100])                        │
│                                                                             │
│  2. if profileChoice < 0  ─── SKIP ──→  no AxleControlUnit created         │
│     elif profileChoice < 34 ─ OFF-ROAD → create unit, absDecel = d/50·0.70 │
│     elif profileChoice < 67 ─ COMFORT  → create unit, absDecel = d/50·0.85 │
│     else ──────────────── SPORT    → create unit, absDecel = d/50·1.20    │
│                                                                             │
│  3. Also sets maxBrakingTorque = μ·15000·(d/2)/1000                        │
│     where μ = 0.45 (carbon ceramic) | 0.35 (cast iron) | 0.30 (default)   │
└─────────────────────────────────────────────────────────────────────────────┘
       │
       │  always calls  applyCalibration(disc)
       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Reaction: ApplyCalibration                                                 │
│                                                                             │
│  Match: find AxleControlUnit corresponding to this disc                     │
│    └─ if none found (profile = SKIP) → silently returns, NO-OP             │
│                                                                             │
│  1. Ask user: "Enter calibration level (0–100)"                            │
│     (symbolic variable:  calibChoice ∈ [0, 100])                           │
│                                                                             │
│  2. if calibChoice < 33 ── CONSERVATIVE → calibrationOffset = −0.5        │
│     elif calibChoice < 67 ─ STANDARD     → calibrationOffset =  0.0       │
│     else ──────────────── TRACK         → calibrationOffset = +0.5        │
│                                                                             │
│  3. effectiveBrakeGain = absDecelThreshold + calibrationOffset             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Single-Disc Path Tree — Option 4 (`--brake`): **10 paths**

The single-disc exploration uses **two symbolic variables** per disc:
- `profileChoice ∈ [−1, 100]`  (initial = −1, i.e. the skip path is explored first)
- `calibChoice   ∈ [ 0, 100]`  (initial =  0, i.e. conservative)

```
                        profileChoice  ∈ [−1, 100]
                               │
            ┌──────────────────┼───────────────────┬─────────────────────┐
            │                  │                   │                     │
         LT 0               GE 0,LT 34         GE 34,LT 67           GE 67
         SKIP               OFF-ROAD             COMFORT               SPORT
      (agr. < 0)          (agr. 0–33)          (agr. 34–66)          (agr. 67–100)
    absDecel = n/a       ×0.70 · d/50         ×0.85 · d/50         ×1.20 · d/50
            │                  │                   │                     │
       [no calib          ┌────┼────┐         ┌────┼────┐          ┌────┼────┐
        dialog]           │    │    │         │    │    │          │    │    │
                         LT33 ≥33  ≥67       LT33 ≥33  ≥67       LT33 ≥33  ≥67
                         CONS STD TRACK      CONS STD TRACK      CONS STD TRACK
                         −0.5  0.0 +0.5      −0.5  0.0 +0.5      −0.5  0.0 +0.5
                          │    │    │          │    │    │          │    │    │
                         P2   P3   P4         P5   P6   P7        P8   P9  P10
            │
           P1

Total:  1 (skip)  +  3 (off-road)  +  3 (comfort)  +  3 (sport)  =  10 paths
```

### Why 10?

| profileChoice interval | AxleControlUnit created? | calibChoice branches | Paths |
|------------------------|--------------------------|----------------------|-------|
| < 0  (skip)            | No                       | 0  (no dialog shown) |   1   |
| 0–33 (off-road)        | Yes, ×0.70               | 3                    |   3   |
| 34–66 (comfort)        | Yes, ×0.85               | 3                    |   3   |
| 67–100 (sport)         | Yes, ×1.20               | 3                    |   3   |
| **Total**              |                          |                      | **10**|

The skip branch short-circuits the calibration reaction (match fails → no-op), so it
contributes only **1** path instead of 4.  The other three profile intervals each combine
with all three calibration intervals, giving 3 × 3 = 9.  Total: **1 + 9 = 10**.

---

## 4. Two-Disc Path Space — Option 5 (`--brake-multivar`): **81 paths**

The two-disc exploration inserts **two** `BrakeDisc` elements (diameters 300 mm and
350 mm) simultaneously, using **four symbolic variables**:

```
  Disc 1 (⌀ 300 mm)            Disc 2 (⌀ 350 mm)
  ─────────────────            ─────────────────
  profileChoice_disc_300  ∈ [0, 100]   profileChoice_disc_350  ∈ [0, 100]
  calibChoice_disc_300    ∈ [0, 100]   calibChoice_disc_350    ∈ [0, 100]
```

> **Note**: the initial values are `[0, 0, 0, 0]` — both discs start in the
> off-road / conservative cell.  The domain lower bound is technically −1, but
> because the exploration starts at 0 (not −1) and skip paths structurally suppress
> the calibration variable (no dialog shown → no constraint collected), the
> systematic negation algorithm stays within the 3×3 non-skip region per disc.

The path space is the **Cartesian product** of each disc's non-skip paths:

```
                   Disc 1 profile  ×  Disc 1 calib  ×  Disc 2 profile  ×  Disc 2 calib
                        3          ×       3         ×       3          ×       3
                   ──────────────────────────────────────────────────────────────────
                                             = 81 paths
```

Visualised as a 9×9 grid (each disc contributes a 3×3 sub-space):

```
              ╔═══════════════════════════════════════════════╗
              ║         Disc 2 (⌀ 350 mm)                    ║
              ║                                               ║
              ║      OFF-ROAD    COMFORT      SPORT           ║
              ║   ┌───────────┬───────────┬───────────┐       ║
              ║ C │ C  S  T   │ C  S  T   │ C  S  T   │       ║
  Disc 1   OF ║ O │ ○  ○  ○   │ ○  ○  ○   │ ○  ○  ○   │       ║
  (⌀300mm) F- ║ N │           │           │           │       ║
           R  ║ S ├───────────┼───────────┼───────────┤       ║
           C  ║ T │ C  S  T   │ C  S  T   │ C  S  T   │       ║
           O  ║ A │ ○  ○  ○   │ ○  ○  ○   │ ○  ○  ○   │       ║
           M- ║ N │           │           │           │       ║
           F  ║ D ├───────────┼───────────┼───────────┤       ║
           O  ║ A │ C  S  T   │ C  S  T   │ C  S  T   │       ║
           R  ║ R │ ○  ○  ○   │ ○  ○  ○   │ ○  ○  ○   │       ║
           T  ║ D │           │           │           │       ║
           S  ║   └───────────┴───────────┴───────────┘       ║
   (each      ║   C=Conservative  S=Standard  T=Track         ║
    row:       ╚═══════════════════════════════════════════════╝
    3 calib)
```

Each `○` represents one distinct behavioural path (one concrete pair of EMF models
produced).  9 cells × 9 cells = **81 paths**.

### Why not 100 (10 × 10)?

The single-disc case has 10 paths because the skip path (profileChoice < 0) is
explicitly included via the initial value −1.  In the two-disc case:

- The initial values are `[0, 0, 0, 0]` — all in the off-road range.
- When a disc uses **skip**, the `ApplyCalibrationRoutine` match fails → the
  `calibChoice` dialog is *never shown* → `getOrMakeSymbolicInt` for that disc's
  `calibChoice` is *never called* → **no domain or branch constraint is collected**
  for that calibration variable.
- The systematic negation algorithm in `PathExplorer` relies on the constraint graph
  built during each execution.  With no calibration constraint for a skipped disc,
  the algorithm cannot enumerate calibration variants for that disc, effectively
  treating "skip + any calibration" as a single unexplorable region from the
  `[0, 0, 0, 0]` starting point.

In short: **skip paths require the calibration variable to be absent**, which breaks
the 4-variable exploration's assumption that all four variables are always constrained.
Starting from `[0, 0, 0, 0]` naturally stays within the 3×3 non-skip region per disc.

> To include skip paths in the two-disc case, the initial values would need to be
> `[-1, 0, -1, 0]` and the skip-path constraint collection would need special handling
> (e.g. recording a sentinel constraint for the absent calibration variable).

---

## 5. Effective Brake Gain Formula

For a non-skip path, the `effectiveBrakeGain` produced for each disc is:

```
effectiveBrakeGain = absDecelThreshold + calibrationOffset

where:
  absDecelThreshold  =  (diameter / 50.0)  ×  profileMultiplier
  profileMultiplier  =  0.70  (off-road)
                     |  0.85  (comfort)
                     |  1.20  (sport)
  calibrationOffset  =  −0.5  (conservative)
                     |   0.0  (standard)
                     |  +0.5  (track)
```

Example values for disc ⌀ 300 mm (default material, μ = 0.30):

| Profile   | absDecelThreshold | Calibration  | offset | effectiveBrakeGain |
|-----------|------------------:|--------------|-------:|-------------------:|
| Off-road  |  300/50 × 0.70 = **4.20** | Conservative | −0.50  | **3.70**  |
| Off-road  |  4.20             | Standard     |  0.00  | **4.20**  |
| Off-road  |  4.20             | Track        | +0.50  | **4.70**  |
| Comfort   |  300/50 × 0.85 = **5.10** | Conservative | −0.50  | **4.60**  |
| Comfort   |  5.10             | Standard     |  0.00  | **5.10**  |
| Comfort   |  5.10             | Track        | +0.50  | **5.60**  |
| Sport     |  300/50 × 1.20 = **7.20** | Conservative | −0.50  | **6.70**  |
| Sport     |  7.20             | Standard     |  0.00  | **7.20**  |
| Sport     |  7.20             | Track        | +0.50  | **7.70**  |
| *Skip*    |  *n/a*            | *not shown*  | *n/a*  | *n/a*     |

---

## 6. CoCoPath Exploration Summary

| Option | Script flag       | Symbolic vars | Expected paths   | Actual found |
|--------|-------------------|:-------------:|:----------------:|:------------:|
| 1      | `--internal`      | 1             |  5               |  5           |
| 2      | `--external`      | 1             |  5               |  5           |
| 3      | `--multivar`      | 2             | 25               | 25           |
| 4      | `--brake`         | 2             | 10               | **10**       |
| 5      | `--brake-multivar`| 4             | up to 100        | **81**       |

Options 4 and 5 differ in **initial values** and **disc count**:

- **Option 4**: `profileChoice` starts at **−1** → skip path included →  1 + 9 = 10.
- **Option 5**: all variables start at **0** → only non-skip profiles reachable →
  9 × 9 = 81 (3 profiles × 3 calibs per disc, both discs independent).
