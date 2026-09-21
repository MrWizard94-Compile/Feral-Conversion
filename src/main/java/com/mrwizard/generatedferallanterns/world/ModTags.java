package com.mrwizard.generatedferallanterns.world;

import com.mrwizard.generatedferallanterns.GeneratedFeralLanterns;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    public static final TagKey<Block> SOURCE_LANTERNS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GeneratedFeralLanterns.MOD_ID, "source_lanterns"));
    private ModTags() {}
}
