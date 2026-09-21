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

## Not claimed

This package has **not** been Gradle-compiled or launched inside this sandbox. The sandbox has no Gradle installation, no pre-populated NeoForge/Minecraft Gradle dependency cache, and no direct dependency-download access. A real local `gradlew build` and game launch remain the final integration checks.
