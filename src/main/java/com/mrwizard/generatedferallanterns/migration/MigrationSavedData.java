package com.mrwizard.generatedferallanterns.migration;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public final class MigrationSavedData extends SavedData {
    private static final String DATA_NAME = "generatedferallanterns_migration";
    private static final String DECISION_KEY = "decision";

    private Decision decision = Decision.UNDECIDED;

    public static MigrationSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(MigrationSavedData::new, MigrationSavedData::load),
                DATA_NAME
        );
    }

    public static MigrationSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        MigrationSavedData data = new MigrationSavedData();
        String raw = tag.getString(DECISION_KEY);
        try {
            data.decision = raw.isEmpty() ? Decision.UNDECIDED : Decision.valueOf(raw);
        } catch (IllegalArgumentException ignored) {
            data.decision = Decision.UNDECIDED;
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString(DECISION_KEY, decision.name());
        return tag;
    }

    public Decision decision() {
        return decision;
    }

    public boolean decide(Decision newDecision) {
        if (decision != Decision.UNDECIDED || newDecision == Decision.UNDECIDED) {
            return false;
        }
        decision = newDecision;
        setDirty();
        return true;
    }

    public enum Decision {
        UNDECIDED,
        CONVERT_EXISTING,
        NEW_CHUNKS_ONLY
    }
}
