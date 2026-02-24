# DynamisSky

**Physically-based sky, atmosphere & celestial rendering library for the Dynamis engine ecosystem.**

Bruneton multi-scattering atmosphere LUTs, Hosek-Wilkie & Preetham sky models, dynamic time-of-day scheduler, astronomically accurate sun/moon positioning, star field via AT-HYG catalog. Vulkan compute pipeline. Drives lighting, weather & aerial perspective downstream for DynamisVFX, DynamisTerrain & DynamicLightEngine.

---

## Overview

DynamisSky owns the environmental state of the engine. No other library invents sky color, sun position, or weather conditions — they consume what DynamisSky exposes. The library is organized around a clean producer/consumer model: DynamisSky generates LUT textures, sun/moon/weather state, and a star field vertex buffer that downstream systems read each frame.

The physically-based atmosphere implementation follows Bruneton et al. (2008) with the Hillaire (2020) multi-scattering approximation. Sky color is evaluated via precomputed transmittance and scattering LUTs at startup, with a lightweight per-frame sky view LUT update costing approximately 0.3ms at 1080p.

---

## Features

### 0.1.0 — Core Sky
- **Preetham (1999)** and **Hosek-Wilkie (2012)** analytical sky models
- **Bruneton multi-scattering atmosphere** — transmittance LUT, multi-scattering LUT, per-frame sky view LUT, per-frame aerial perspective volume LUT
- **Sun position math** — NOAA Solar Calculator algorithm, accurate to within 1 arc-minute for 1950–2050, driven by Julian date and geographic coordinates
- **Dynamic time-of-day scheduler** — variable time multipliers, lockable for cutscenes, event-driven time jumps
- **Color temperature curves** — piecewise Hermite curve over 24-hour cycle (2000K dawn → 5500K noon → 1800K sunset → 4100K night)
- **Moon renderer** — physically sized billboard, astronomically correct position, phase computed from sun-moon angle
- **Star field** — AT-HYG v3 catalog, magnitude-filtered (~9100 visible stars), spectral class colors, GPU vertex buffer
- **Celestial body billboards** — Venus, Mars, Jupiter, Saturn at correct positions
- **HDRI skybox fallback** — equirectangular, rotatable, for interiors or stylized scenes
- **Aerial perspective** — 32×32×32 volume LUT applied in post-process for atmospheric haze on distant geometry
- **WeatherState descriptor** — wind speed/direction, rain/snow intensity, fog density, cloud coverage — consumed by DynamisVFX and DynamisTerrain
- **DynamicLightEngine integration** — sun state drives directional light color and intensity automatically at dusk/dawn

### 0.2.0 — Volumetric Clouds *(planned)*
- Ray-marched volumetric cloud layers, weather-driven density via FastNoise curl
- Temporal reprojection for amortized cost
- Cloud shadow projection map
- Aurora / atmospheric phenomena
- Night sky light pollution falloff
- Milky Way band

---

## Architecture

```
Vectrix  ·  FastNoiseLiteNouveau  ·  AT-HYG catalog
                    ↓
            DynamisGPU 1.0.1
                    ↓
           DynamisSky 0.1.0
          ↙         ↓         ↘
  DynamisVFX  DynamisTerrain  DynamicLightEngine
```

DynamisSky has no dependency on its consumers. The dependency arrows point inward.

### Module Structure

| Module | Artifact ID | Dependencies | Purpose |
|--------|-------------|--------------|---------|
| API    | `dynamissky-api`    | none | Interfaces, descriptors, value types. Safe for all consumers. |
| Core   | `dynamissky-core`   | api · Vectrix · FastNoise | CPU sky models, sun math, time-of-day, descriptor builders. |
| Vulkan | `dynamissky-vulkan` | core · DynamisGPU | LUT compute, sky view pass, star field, moon, HDRI, DLE adapters. |
| Test   | `dynamissky-test`   | api | MockSkyService, deterministic harness, assertion helpers. |
| Bench  | `dynamissky-bench`  | vulkan · test · JMH 1.37 | LUT bake throughput, sky view cost, star field buffer build. |

---

## LUT Architecture

The Bruneton atmosphere model uses precomputed lookup tables baked once at startup and cached until `AtmosphereConfig` changes.

| LUT | Format | Dimensions | Update | Content |
|-----|--------|------------|--------|---------|
| Transmittance | `rgba16f` | 256 × 64 | Once (bake) | Transmittance T(p,v) — light survival from altitude p in direction v to space |
| MultiScattering | `rgba16f` | 32 × 32 | Once (bake) | Second-order scattering approximation (Hillaire 2020) |
| SkyView | `rgba16f` | 192 × 108 | Per-frame | Full sky hemisphere at current sun position |
| AerialPerspective | `rgba16f` | 32 × 32 × 32 | Per-frame | In-scattering + transmittance along frustum depth slices |

Per-frame GPU cost at 1080p: **~0.3ms** (SkyView + AerialPerspective dispatches combined).

---

## Quick Start

### Maven Dependency

```xml
<!-- API only — safe for game logic modules -->
<dependency>
    <groupId>org.dynamissky</groupId>
    <artifactId>dynamissky-api</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>

<!-- Vulkan implementation — engine-impl-vulkan only -->
<dependency>
    <groupId>org.dynamissky</groupId>
    <artifactId>dynamissky-vulkan</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Initialize

```java
VulkanSkyIntegration skyIntegration = VulkanSkyIntegration.create(
    device,
    memoryOps,
    bindlessHeap,
    SkyConfig.builder()
        .model(SkyModelType.BRUNETON)
        .latitude(43.7f).longitude(-79.4f)   // Toronto
        .startJulianDate(2460310.5)           // 2024-01-15 00:00 UTC
        .timeMultiplier(60f)                  // 1 real second = 1 game minute
        .build()
);
```

### Per-Frame Update

```java
// Before shadow pass — updates sky view LUT and sun state
skyIntegration.update(SkyFrameContext.of(commandBuffer, camera, frameIndex, deltaTime));

// Read sun state for directional light
SunState sun = skyIntegration.getSunState();
frameUniforms.setSunDirection(sun.direction());
frameUniforms.setSunColor(sun.color().mul(sun.intensity()));

// Read weather for VFX / terrain
vfxService.setWeatherSource(skyIntegration);
```

### Author a Sky

```java
SkyDescriptor dayClear = SkyDescriptor.builder()
    .model(SkyModelType.HOSEK_WILKIE)
    .atmosphere(AtmosphereConfig.EARTH_STANDARD)
    .turbidity(2.5f)
    .groundAlbedo(new float[]{0.1f, 0.12f, 0.08f})
    .weather(WeatherState.CLEAR)
    .build();

SkyDescriptor hdriInterior = SkyDescriptor.builder()
    .model(SkyModelType.HDRI)
    .hdriPath("assets/sky/industrial_sunset_4k.hdr")
    .hdriRotation(30f)
    .build();
```

### Time of Day

```java
TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
    .startJulianDate(2460310.5)
    .latitude(43.7f).longitude(-79.4f)
    .timeZone(-5f)
    .timeMultiplier(120f)      // 1 real second = 2 game minutes
    .build();

// Lock time for a cutscene
scheduler.lock();
scheduler.unlock();

// Jump to a specific time
scheduler.setTimeOfDay(19.5f);   // 7:30 PM game time
```

---

## Sun Position

Solar position is computed using the NOAA Solar Calculator algorithm, accurate to within 1 arc-minute for dates 1950–2050.

```java
SolarPosition pos = SolarPositionCalculator.compute(
    JulianDate.of(2024, 6, 21, 12, 0, 0),
    LatLon.of(43.7f, -79.4f),
    TimeZone.of(-4f)   // EDT
);

// pos.azimuth()  — degrees from North, clockwise (179.8° at Toronto summer solstice noon)
// pos.altitude() — degrees above horizon        (69.5° at Toronto summer solstice noon)
// pos.toWorldDirection(northAxis) — Vectrix Vec3
```

---

## Star Field

The star field uses the AT-HYG v3 catalog — a cross-referenced dataset of the Henry Draper, Yale Bright Star, and HYG catalogs. Stars are loaded, magnitude-filtered, and uploaded as a GPU vertex buffer once at startup.

```java
StarCatalog catalog = StarCatalogLoader.load(
    Path.of("assets/sky/athyg_v3.bin"),   // exported from TRIPS
    StarFilter.builder()
        .maxMagnitude(6.5f)               // ~9100 visible stars
        .excludeSun(true)
        .build()
);
// 9100 stars × 32 bytes/star = 291KB GPU vertex buffer
// Rendered as additive point sprites, brightness from magnitude
// Color from spectral class (O=blue-white, G=yellow, M=red)
```

---

## Weather Integration

`WeatherState` is the bridge between DynamisSky and downstream systems.

```java
skyIntegration.setWeather(WeatherState.builder()
    .windSpeed(8.5f)
    .windDirection(new float[]{0.7f, 0f, 0.7f})
    .rainIntensity(0.6f)
    .fogDensity(0.2f)
    .cloudCoverage(0.85f)
    .build());

// DynamisVFX reads it automatically:
vfxService.setWeatherSource(skyIntegration);

// Rain emitter rate scales with weather.rainIntensity()
// Snow emitter rate scales with weather.snowIntensity()
// Wind force direction driven by weather.windDirection()
// Fog particle density driven by weather.fogDensity()
```

---

## DynamicLightEngine Integration Points

| DLE Pass | DynamisSky Input | Effect |
|----------|-----------------|--------|
| `VulkanFrameUniforms` | `SunState.direction()`, `.color()`, `.intensity()` | Directional light tracks sun automatically at dusk/dawn |
| `VulkanShadowPassRecorder` | `SunState.direction()` | Shadow map projection tracks sun |
| `VulkanMainPassRecorderCore` | `SkyGpuResources` (all LUTs) | Sky background draw + aerial perspective |
| `VulkanLightClusterBuilder` | `TimeOfDayState.ambientIntensity()`, `.colorTemperature()` | Ambient light driven by time of day |
| `VulkanPostProcessPass` | `SkyGpuResources.aerialLut()` | Atmospheric haze on distant geometry |

---

## Testing

### MockSkyService

```java
MockSkyService sky = new MockSkyService();
sky.setTimeOfDay(TimeOfDayState.SOLAR_NOON);
sky.setWeather(WeatherState.HEAVY_RAIN);

// Wire to VFX test — no GPU required
vfxService.setWeatherSource(sky);
```

### Parity Gate

```bash
MVK_CONFIG_USE_METAL_ARGUMENT_BUFFERS=1 \
mvn -pl dynamissky-vulkan test \
  -Ddle.sky.parity.tests=true \
  -Dvk.validation=true
```

Parity tests validate:
- LUT bake produces non-zero transmittance values
- SkyView LUT is non-black at zenith
- Sun position matches NOAA reference data within 0.1°
- `TimeOfDayScheduler` produces monotonically advancing Julian dates
- `WeatherState` round-trips through JSON serialization exactly

---

## Benchmarks

```bash
mvn -pl dynamissky-bench package -DskipTests

java -jar dynamissky-bench/target/dynamissky-bench.jar \
  -wi 3 -i 5 -f 1 -t 1 -rff results.csv -rf csv
```

| Benchmark | Measures |
|-----------|----------|
| `TransmittanceLutBakeBenchmark` | Full bake throughput (ops/sec) |
| `SkyViewLutUpdateBenchmark` | Per-frame update cost at 192×108 (µs/op) |
| `SolarPositionBenchmark` | `compute()` calls per second |
| `StarCatalogLoadBenchmark` | Catalog load + GPU descriptor build (µs/op) |
| `HosekWilkieEvaluateBenchmark` | CPU sky color evaluation throughput |

---

## macOS / Apple Silicon

DynamisSky runs on Apple Silicon via MoltenVK. All compute passes translate cleanly to Metal compute. The 3D aerial perspective texture (`VK_IMAGE_TYPE_3D`) is supported on MoltenVK 1.2+.

```bash
MVK_CONFIG_USE_METAL_ARGUMENT_BUFFERS=1   # required
MVK_DEBUG_REPORT_LEVEL=2                  # recommended during testing
```

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Vulkan | 1.2+ |
| LWJGL | 3.4.1 |
| DynamisGPU | 1.0.1 |
| Vectrix | 1.10.9 |
| MoltenVK (macOS) | 1.2.0+ |

---

## Ecosystem

DynamisSky is part of the Dynamis engine library ecosystem:

| Library | Status | Role |
|---------|--------|------|
| Vectrix | 1.10.9 | Math — vectors, matrices, quaternions, curves |
| MeshForge | 1.1.0 | Mesh loading — glTF/GLB, morph targets |
| DynamisCollision | 1.1.0 | Collision detection |
| Animis | 1.0.0 | Skeletal animation — V1-V3, motion matching |
| DynamisGPU | 1.0.1 | GPU plumbing — bindless heap, staging, indirect |
| DynamisVFX | 0.1.0 | VFX — particle simulation, 5 renderer variants |
| **DynamisSky** | **0.1.0** | **Sky, atmosphere, celestial — this library** |
| DynamisTerrain | planned | Terrain — heightmap, LOD, splatmap |
| DynamisAudio | planned | Audio — spatial, HRTF, weather-reactive |
| DynamisScene | planned | Scene graph / ECS |
| DynamicLightEngine | — | Renderer — GPU-driven, Vulkan, bindless |

---

## License

See [LICENSE](LICENSE) for terms.
