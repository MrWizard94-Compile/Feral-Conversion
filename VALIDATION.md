# Validation Record

## Target

- Minecraft: 1.21.1
- NeoForge development target: 21.1.251
- Java: 21
- Torchmaster target block: `torchmaster:feral_flare_lantern`

## Verified against upstream 1.21.1 APIs/source

- `ChunkEvent.Load#isNewChunk()` identifies freshly generated chunks and only returns true on the logical server.
- NeoForge explicitly warns that `ChunkEvent.Load` may occur before the `LevelChunk` reaches `FULL`, so level mutation is deferred to `ServerTickEvent.Post`.
- `PayloadRegistrar#playToServer(...)` exists in the NeoForge 1.21.1 branch.
- `IPayloadContext#player()` returns the relevant play-phase player; server-bound payloads use a `ServerPlayer`.
- `PacketDistributor#sendToServer(...)` exists for the targeted 1.21.1 line.
- `AttachmentType.Builder#serialize(Codec<T>)` exists in the NeoForge 1.21.1 branch.
- `LevelChunkSection#maybeHas(Predicate<BlockState>)` exists and uses palette-aware matching.
- `ServerTickEvent.Post` fires once after each server tick and exposes the server instance.
- `SavedData.Factory` supports the `(Supplier, BiFunction<CompoundTag, HolderLookup.Provider, T>)` constructor used here.
- `DimensionDataStorage#computeIfAbsent(...)` and the `SavedData#save(CompoundTag, HolderLookup.Provider)` signature match the implementation.
- Torchmaster 1.21.1 registers the target as `torchmaster:feral_flare_lantern` and its block uses a six-way `FACING` state.

## Local static checks performed

- All JSON resources parse successfully.
- Expanded `neoforge.mods.toml` parses successfully as TOML.
- Java source delimiter sanity checks pass.
- A Java 21 parser pass found no parser/syntax diagnostics. Missing Minecraft/NeoForge classes are expected because the sandbox does not contain the Gradle-resolved development classpath.
- No stale references to the abandoned `DistExecutor`/old client-payload approach remain.

## Gradle integration validation

GitHub Actions successfully completed the repository's full NeoForge 1.21.1 Gradle build using Java 21 and the checked-in Gradle wrapper. The successful run executed `compileJava`, resource processing, JAR assembly, and the `build` lifecycle task against the resolved Minecraft/NeoForge development classpath.

The implementation has therefore moved beyond parser/static validation: it is compile-verified against the configured NeoForge 21.1.251 toolchain.

## Not yet claimed

A successful build does not replace an in-game integration test. Singleplayer and dedicated-server launch tests with Torchmaster installed, new-chunk generation, the first-load migration prompt, and player-placement protection should still be exercised before calling version 1.0.0 release-tested.
