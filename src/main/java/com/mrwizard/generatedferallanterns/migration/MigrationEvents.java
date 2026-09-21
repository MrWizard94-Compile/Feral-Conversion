package com.mrwizard.generatedferallanterns.migration;

import com.mrwizard.generatedferallanterns.menu.MigrationMenu;
import com.mrwizard.generatedferallanterns.network.ModNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class MigrationEvents {
    private MigrationEvents() {
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        MinecraftServer server = player.getServer();
        if (server == null || !ModNetworking.canChoose(player, server)) {
            return;
        }

        if (MigrationSavedData.get(server).decision() == MigrationSavedData.Decision.UNDECIDED) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, ignoredPlayer) -> new MigrationMenu(containerId, inventory),
                    Component.translatable("screen.generatedferallanterns.migration.title")
            ));
        }
    }
}
