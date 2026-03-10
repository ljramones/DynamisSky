# DynamisSky Architecture Review

## Repo Overview

- Repository: `DynamisSky`
- Modules:
  - `dynamissky-api`
  - `dynamissky-core`
  - `dynamissky-vulkan`
  - `dynamissky-test`
  - `dynamissky-bench`
- Build/runtime profile:
  - Java 21 target, Maven multi-module
  - Vulkan backend depends on both `dynamis-gpu-api` and `dynamis-gpu-vulkan`

Grounded structure:

- `dynamissky-api` exposes descriptor/state/service contracts (`SkyService`, `SkyDescriptor`, `SunState`, `MoonState`, `TimeOfDayState`, `WeatherState`, `SkyGpuResources`).
- `dynamissky-core` owns sky models, solar/time math, scheduler, color/stars utilities.
- `dynamissky-vulkan` owns LUT compute/render backends and integration adapters for LightEngine-facing consumption.

## Strict Ownership Statement

### What DynamisSky should own

DynamisSky should own **sky/atmosphere/celestial feature authority**:

- Sky and atmosphere feature modeling (analytical + LUT-backed models).
- Sky-local time-of-day progression and sky-state derivation.
- Sun/moon/star state generation for render and lighting consumers.
- Sky-specific weather/environment descriptors used as downstream inputs.
- Render-facing sky resource production (LUT outputs, celestial draw inputs) for consumers.

### What DynamisSky must not own

DynamisSky must **not** own:

- Global render planning/frame-graph authority (LightEngine concern).
- Generic GPU orchestration/resource lifecycle authority (DynamisGPU concern).
- World authority or global simulation authority (WorldEngine concern).
- Scene ownership/hierarchy authority (SceneGraph concern).
- Terrain/VFX feature ownership (they consume sky outputs).
- Session/content/scripting control-plane authority.

## Dependency Rules

### Allowed dependencies for DynamisSky

- `DynamisGPU` as execution substrate dependency for sky backend implementation.
- `DynamisLightEngine` through integration-facing adapters and host orchestration seams.
- math/foundation libraries (`vectrix`) and internal core model utilities.

### Forbidden dependencies for DynamisSky

- Dependence on WorldEngine internals for world authority/state orchestration.
- Dependence on SceneGraph internals for scene ownership.
- Embedding render graph ownership inside Sky runtime.
- Owning generic GPU allocator/scheduler policy that belongs in DynamisGPU.

### Who may depend on DynamisSky

- LightEngine (for sky rendering and lighting inputs).
- VFX and Terrain (as weather/ambient/sun/moon consumers).
- World-level orchestration layer as a consumer/coordinator of time/weather sources.

## Public vs Internal Boundary Assessment

### Canonical public boundary

Primary public seam should be:

- `dynamissky-api` value/service contracts.
- `dynamissky-core` model/scheduler builders and pure calculations.

This split is clean and clear in module layout.

### Boundary concerns in current public surface

1. `dynamissky-vulkan` exports many backend packages (`descriptor`, `lut`, `pass`, `stars`, `moon`, `hdri`, `integration`) from module-info, which risks overexposing implementation internals.

2. Vulkan backend currently couples to DynamisGPU Vulkan internals through `LwjglGpuMemoryOps` (`org.dynamisgpu.vulkan.memory.VulkanMemoryOps`) rather than only a narrower substrate seam.

3. README language claims Sky "owns the environmental state of the engine". In strict boundaries, Sky should own sky/environment feature state generation, while WorldEngine retains global world authority.

### Internal/implementation areas (appropriate)

The following are appropriate backend internals and should remain implementation details:

- LUT bakers/updaters and resource alloc helpers
- pass recorders/selectors
- integration adapters and phase guards
- parity harnesses/bench code

## Policy Leakage / Overlap Findings

### DynamisLightEngine overlap

- Generally clean: `VulkanSkyIntegration` enforces `update -> background -> celestial` ordering and presents a feature integration facade.
- Constraint: Sky should provide pass content/inputs, while LightEngine remains authoritative for global pass orchestration decisions.

### DynamisGPU overlap (primary technical risk)

- Vulkan sky backend manages backend resource allocation and directly uses DynamisGPU Vulkan internals.
- This is workable but coupling-heavy; treat as constrained integration, not a boundary to broaden.

### DynamisWorldEngine overlap

- No direct WorldEngine dependency observed.
- Conceptual risk exists if sky time/weather mutators are treated as world authority rather than one world-consumed subsystem input.

### DynamisSceneGraph overlap

- No direct SceneGraph ownership detected.
- Sky consumes camera/frame context, not scene graph authority.

### DynamisVFX / DynamisTerrain overlap

- Current shape is correct: Sky emits weather/light inputs for downstream feature consumption.
- Keep one-way consumption; avoid Sky taking ownership of VFX/Terrain simulation policy.

## Ratification Result

**Result: ratified with constraints**

Why:

- The repo shows a strong feature-subsystem structure (API/core/backend/test/bench) with mostly coherent ownership.
- Sky modeling/state generation responsibilities are clear and largely well-scoped.
- Main constraints are boundary-coupling risks:
  - Vulkan backend coupling breadth into DynamisGPU internals.
  - Potential overreach in "environment authority" framing versus WorldEngine global authority.

## Recommended Next Step

1. Keep seams stable for now (no immediate refactor in this pass).
2. Continue with **DynamisTerrain** deep boundary ratification to finish the graphics-feature cluster.
3. After Sky + Terrain are both ratified, run a targeted cross-repo tightening plan for LightEngine/VFX/Sky/Terrain/GPU seams (narrow exposure + coupling review).

This document is a boundary-ratification review only and does not propose immediate package moves or API-breaking changes.
