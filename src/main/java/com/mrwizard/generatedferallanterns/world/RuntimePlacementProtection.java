package com.mrwizard.generatedferallanterns.world;

import com.mrwizard.generatedferallanterns.GeneratedFeralLanterns;
import com.mrwizard.generatedferallanterns.migration.MigrationSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Set;

public final class RuntimePlacementProtection {
    private RuntimePlacementProtection() {}

    public static void onEntityPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !event.getPlacedBlock().is(ModTags.SOURCE_LANTERNS)) return;
        if (MigrationSavedData.get(level.getServer()).decision() == MigrationSavedData.Decision.NEW_CHUNKS_ONLY) return;
        LevelChunk chunk = level.getChunkAt(event.getPos());
        if (chunk.getData(GeneratedFeralLanterns.MIGRATION_SCANNED.get())) return;
        Set<Long> protectedPositions = chunk.getData(GeneratedFeralLanterns.RUNTIME_PLACED_PROTECTED.get());
        if (protectedPositions.add(event.getPos().asLong())) chunk.setUnsaved(true);
    }
}
