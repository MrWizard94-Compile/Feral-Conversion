package com.mrwizard.generatedferallanterns.network;

import com.mrwizard.generatedferallanterns.migration.MigrationSavedData;
import com.mrwizard.generatedferallanterns.migration.PendingMigrationChunks;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                MigrationChoicePayload.TYPE,
                MigrationChoicePayload.STREAM_CODEC,
                ModNetworking::handleMigrationChoice
        );
    }

    private static void handleMigrationChoice(MigrationChoicePayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        if (!canChoose(player, server)) {
            player.sendSystemMessage(Component.translatable(
                    "message.generatedferallanterns.migration.not_authorized"));
            return;
        }

        MigrationSavedData data = MigrationSavedData.get(server);
        MigrationSavedData.Decision decision = payload.convertExisting()
                ? MigrationSavedData.Decision.CONVERT_EXISTING
                : MigrationSavedData.Decision.NEW_CHUNKS_ONLY;

        if (!data.decide(decision)) {
            player.sendSystemMessage(Component.translatable(
                    "message.generatedferallanterns.migration.already_decided"));
            return;
        }

        if (decision == MigrationSavedData.Decision.CONVERT_EXISTING) {
            PendingMigrationChunks.enqueueAllAndClear();
            player.sendSystemMessage(Component.translatable(
                    "message.generatedferallanterns.migration.accepted"));
        } else {
            PendingMigrationChunks.clear();
            player.sendSystemMessage(Component.translatable(
                    "message.generatedferallanterns.migration.declined"));
        }
    }

    public static boolean canChoose(ServerPlayer player, MinecraftServer server) {
        return server.isSingleplayerOwner(player.getGameProfile()) || player.hasPermissions(2);
    }
}
