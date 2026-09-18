# 02 — Android Studio Project Foundation

## Objective
Create the native Android foundation and build pipeline before implementing game features.

## Tasks
1. Create the Android Studio project using Kotlin.
2. Configure a stable minimum/target SDK strategy appropriate for the current Google Play requirements.
3. Enable Jetpack Compose and Material 3.
4. Configure dependency versions centrally.
5. Add Navigation Compose, Coroutines/Flow, Supabase client libraries, serialization, Room, and test dependencies.
6. Create build variants for `debug`, `staging`, and `release` if practical.
7. Add environment configuration without committing secrets.
8. Create the package boundaries defined in `01_ARCHITECTURE.md`.
9. Add a root-level error/result convention.
10. Add a simple launch screen and placeholder navigation graph.
11. Add baseline unit and instrumentation tests.
12. Add static analysis/lint configuration.
13. Add CI that compiles and runs tests on every change.

## Constraints
- No Firebase.
- No Unity.
- No Supabase service-role key in client code.
- No hard-coded production URLs or credentials.
- Do not implement gameplay yet.

## Deliverables
- Compiling Android project.
- Dependency manifest/version catalog.
- Environment/config strategy.
- Initial package structure.
- CI/build instructions.
- Smoke test proving the app launches.

## Verification
Run debug build, unit tests, lint, and an emulator launch test.
