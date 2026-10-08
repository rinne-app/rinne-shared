# rinne-shared

Kotlin Multiplatform code shared by the Rinne client (`rinne-app/kmp`) and backend (`rinne-app/backend`).
Both projects consume it as a git submodule at `rinne-shared/`.

The `redesign` branch serves the ground-up rewrite of both apps. It keeps only what the rewrite uses:

- `libraries/logger` — `RinneLogger` (`core`) and its Kermit implementation (`kermit`).
- `libraries/date-time` — `RinneDate`, `RinneDateTime`, `RinneTime`, `RinneDuration`, `RinneTimeZone`.
  The only module allowed to depend on `kotlinx-datetime`.
- `libraries/error` — result/error types used by the client (`core`, `compose`).
- `shared/network/model` — request/response DTOs, the wire contract between client and backend.
- `shared-build-logic` — `rinne.shared.multiplatform.*` convention plugins.
- `gradle/libs.versions.toml` — the `sharedLibs` version catalog both apps import.

Everything else from the old apps lives on `develop`.
