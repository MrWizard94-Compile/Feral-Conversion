package com.mrwizard.generatedferallanterns.world;

import com.mrwizard.generatedferallanterns.GeneratedFeralLanterns;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class ConversionWorkQueue {
    private static final int MAX_PLANS_PER_TICK = 16;
    private static final ConcurrentLinkedDeque<Plan> QUEUE = new ConcurrentLinkedDeque<>();

    private ConversionWorkQueue() {
    }

    public static void enqueue(ServerLevel level, LevelChunk chunk, List<LanternReplacer.Candidate> candidates) {
        QUEUE.addLast(new Plan(level.dimension(), chunk.getPos(), candidates));
    }

    public static void onServerTickPost(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        for (int i = 0; i < MAX_PLANS_PER_TICK; i++) {
            Plan plan = QUEUE.pollFirst();
            if (plan == null) return;
            ServerLevel level = server.getLevel(plan.dimension());
            if (level == null) continue;
            LevelChunk chunk = level.getChunkSource().getChunkNow(plan.chunkPos().x, plan.chunkPos().z);
            if (chunk == null) continue;
            if (chunk.getData(GeneratedFeralLanterns.MIGRATION_SCANNED.get())) continue;
            LanternReplacer.applySnapshot(level, chunk, plan.candidates());
            chunk.setData(GeneratedFeralLanterns.GENERATED_CONVERSION_REQUIRED.get(), false);
            chunk.setData(GeneratedFeralLanterns.MIGRATION_SCANNED.get(), true);
            chunk.setUnsaved(true);
        }
    }

    public static void clear() { QUEUE.clear(); }

    private record Plan(ResourceKey<Level> dimension, ChunkPos chunkPos, List<LanternReplacer.Candidate> candidates) {}
}
