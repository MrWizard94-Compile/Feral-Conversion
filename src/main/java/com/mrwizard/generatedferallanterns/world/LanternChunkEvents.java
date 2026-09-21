package com.mrwizard.generatedferallanterns.world;

import com.mrwizard.generatedferallanterns.GeneratedFeralLanterns;
import com.mrwizard.generatedferallanterns.migration.MigrationSavedData;
import com.mrwizard.generatedferallanterns.migration.PendingMigrationChunks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.level.ChunkEvent;

public final class LanternChunkEvents {
    private LanternChunkEvents() {}

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) return;
        if (chunk.getData(GeneratedFeralLanterns.MIGRATION_SCANNED.get())) return;
        boolean generatedRetry = chunk.getData(GeneratedFeralLanterns.GENERATED_CONVERSION_REQUIRED.get());
        if (event.isNewChunk() || generatedRetry) {
            if (event.isNewChunk()) {
                chunk.setData(GeneratedFeralLanterns.GENERATED_CONVERSION_REQUIRED.get(), true);
                chunk.setUnsaved(true);
            }
            queueSnapshot(level, chunk);
            return;
        }
        MigrationSavedData.Decision decision = MigrationSavedData.get(level.getServer()).decision();
        switch (decision) {
            case CONVERT_EXISTING -> queueSnapshot(level, chunk);
            case UNDECIDED -> PendingMigrationChunks.track(level, chunk, LanternReplacer.snapshotTaggedLanterns(chunk));
            case NEW_CHUNKS_ONLY -> { }
        }
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) PendingMigrationChunks.forget(level, chunk);
    }

    private static void queueSnapshot(ServerLevel level, LevelChunk chunk) {
        ConversionWorkQueue.enqueue(level, chunk, LanternReplacer.snapshotTaggedLanterns(chunk));
    }
}
