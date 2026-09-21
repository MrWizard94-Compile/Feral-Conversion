package com.mrwizard.generatedferallanterns.client;

import com.mrwizard.generatedferallanterns.GeneratedFeralLanterns;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = GeneratedFeralLanterns.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(GeneratedFeralLanterns.MIGRATION_MENU.get(), MigrationPromptScreen::new);
    }
}
