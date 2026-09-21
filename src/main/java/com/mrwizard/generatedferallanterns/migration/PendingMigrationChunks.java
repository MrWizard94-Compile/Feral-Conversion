package com.mrwizard.generatedferallanterns.migration;

import com.mrwizard.generatedferallanterns.world.ConversionWorkQueue;
import com.mrwizard.generatedferallanterns.world.LanternReplacer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PendingMigrationChunks {
    private static final Map<Key, PendingPlan> PENDING = new ConcurrentHashMap<>();

    private PendingMigrationChunks() {
    }

    public static void track(ServerLevel level, LevelChunk chunk, List<LanternReplacer.Candidate> candidates) {
        Key key = new Key(level.dimension(), chunk.getPos().toLong());
        PENDING.put(key, new PendingPlan(level, chunk.getPos(), candidates));
    }

    public static void forget(ServerLevel level, LevelChunk chunk) {
        PENDING.remove(new Key(level.dimension(), chunk.getPos().toLong()));
    }

    public static void enqueueAllAndClear() {
        for (PendingPlan plan : PENDING.values()) {
            LevelChunk chunk = plan.level().getChunkSource().getChunkNow(plan.chunkPos().x, plan.chunkPos().z);
            if (chunk != null) {
                ConversionWorkQueue.enqueue(plan.level(), chunk, plan.candidates());
            }
        }
        PENDING.clear();
    }

    public static void clear() {
        PENDING.clear();
    }

    private record Key(ResourceKey<Level> dimension, long chunkPos) {
    }

    private record PendingPlan(ServerLevel level, ChunkPos chunkPos, List<LanternReplacer.Candidate> candidates) {
    }
}
