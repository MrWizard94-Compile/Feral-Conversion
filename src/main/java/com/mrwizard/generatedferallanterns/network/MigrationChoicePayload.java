package com.mrwizard.generatedferallanterns.network;

import com.mrwizard.generatedferallanterns.GeneratedFeralLanterns;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MigrationChoicePayload(boolean convertExisting) implements CustomPacketPayload {
    public static final Type<MigrationChoicePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(GeneratedFeralLanterns.MOD_ID, "migration_choice")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MigrationChoicePayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> buffer.writeBoolean(payload.convertExisting()),
            buffer -> new MigrationChoicePayload(buffer.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
