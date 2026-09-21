package com.mrwizard.generatedferallanterns;

import com.mojang.serialization.Codec;
import com.mrwizard.generatedferallanterns.menu.MigrationMenu;
import com.mrwizard.generatedferallanterns.migration.MigrationEvents;
import com.mrwizard.generatedferallanterns.migration.PendingMigrationChunks;
import com.mrwizard.generatedferallanterns.network.ModNetworking;
import com.mrwizard.generatedferallanterns.world.ConversionWorkQueue;
import com.mrwizard.generatedferallanterns.world.LanternChunkEvents;
import com.mrwizard.generatedferallanterns.world.RuntimePlacementProtection;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.NeoForge;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

@Mod(GeneratedFeralLanterns.MOD_ID)
public final class GeneratedFeralLanterns {
    public static final String MOD_ID = "generatedferallanterns";

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final Supplier<AttachmentType<Boolean>> MIGRATION_SCANNED = ATTACHMENTS.register(
            "migration_scanned",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build()
    );

    public static final Supplier<AttachmentType<Boolean>> GENERATED_CONVERSION_REQUIRED = ATTACHMENTS.register(
            "generated_conversion_required",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build()
    );

    private static final Codec<HashSet<Long>> PROTECTED_POSITIONS_CODEC = Codec.LONG.listOf().xmap(
            values -> new HashSet<>(values),
            values -> new ArrayList<>(values)
    );

    public static final Supplier<AttachmentType<HashSet<Long>>> RUNTIME_PLACED_PROTECTED = ATTACHMENTS.register(
            "runtime_placed_protected",
            () -> AttachmentType.<HashSet<Long>>builder(HashSet::new)
                    .serialize(PROTECTED_POSITIONS_CODEC)
                    .build()
    );

    public static final Supplier<MenuType<MigrationMenu>> MIGRATION_MENU = MENUS.register(
            "migration",
            () -> new MenuType<>(MigrationMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    public GeneratedFeralLanterns(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
        MENUS.register(modBus);
        modBus.addListener(ModNetworking::registerPayloads);

        NeoForge.EVENT_BUS.addListener(LanternChunkEvents::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(LanternChunkEvents::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(RuntimePlacementProtection::onEntityPlace);
        NeoForge.EVENT_BUS.addListener(ConversionWorkQueue::onServerTickPost);
        NeoForge.EVENT_BUS.addListener(MigrationEvents::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(GeneratedFeralLanterns::onServerStopped);
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        ConversionWorkQueue.clear();
        PendingMigrationChunks.clear();
    }
}
