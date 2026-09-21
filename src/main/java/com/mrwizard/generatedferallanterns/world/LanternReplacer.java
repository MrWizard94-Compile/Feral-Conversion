package com.mrwizard.generatedferallanterns.world;

import com.mrwizard.generatedferallanterns.GeneratedFeralLanterns;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class LanternReplacer {
    private static final ResourceLocation FERAL_FLARE_LANTERN = ResourceLocation.fromNamespaceAndPath("torchmaster", "feral_flare_lantern");
    private LanternReplacer() {}

    public static List<Candidate> snapshotTaggedLanterns(LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections();
        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();
        List<Candidate> candidates = new ArrayList<>();
        Set<Long> protectedPositions = chunk.getData(GeneratedFeralLanterns.RUNTIME_PLACED_PROTECTED.get());
        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];
            if (section.hasOnlyAir() || !section.maybeHas(state -> state.is(ModTags.SOURCE_LANTERNS))) continue;
            int sectionY = chunk.getSectionYFromSectionIndex(sectionIndex);
            int baseY = SectionPos.sectionToBlockCoord(sectionY);
            for (int localY = 0; localY < 16; localY++) for (int localZ = 0; localZ < 16; localZ++) for (int localX = 0; localX < 16; localX++) {
                BlockState source = section.getBlockState(localX, localY, localZ);
                if (!source.is(ModTags.SOURCE_LANTERNS)) continue;
                BlockPos pos = new BlockPos(baseX + localX, baseY + localY, baseZ + localZ);
                if (!protectedPositions.contains(pos.asLong())) candidates.add(new Candidate(pos, source));
            }
        }
        return List.copyOf(candidates);
    }

    public static int applySnapshot(ServerLevel level, LevelChunk chunk, List<Candidate> candidates) {
        Optional<Block> targetOptional = BuiltInRegistries.BLOCK.getOptional(FERAL_FLARE_LANTERN);
        if (targetOptional.isEmpty()) return 0;
        Block targetBlock = targetOptional.get();
        int replaced = 0;
        Set<Long> protectedPositions = chunk.getData(GeneratedFeralLanterns.RUNTIME_PLACED_PROTECTED.get());
        for (Candidate candidate : candidates) {
            if (protectedPositions.contains(candidate.pos().asLong())) continue;
            BlockState current = level.getBlockState(candidate.pos());
            if (!current.equals(candidate.expectedState())) continue;
            BlockState replacement = replacementState(targetBlock, current);
            if (level.setBlock(candidate.pos(), replacement, Block.UPDATE_ALL)) replaced++;
        }
        return replaced;
    }

    private static BlockState replacementState(Block targetBlock, BlockState source) {
        BlockState replacement = targetBlock.defaultBlockState();
        if (replacement.hasProperty(BlockStateProperties.FACING)) {
            Direction direction = source.hasProperty(BlockStateProperties.HANGING) && source.getValue(BlockStateProperties.HANGING) ? Direction.UP : Direction.DOWN;
            replacement = replacement.setValue(BlockStateProperties.FACING, direction);
        }
        return replacement;
    }

    public record Candidate(BlockPos pos, BlockState expectedState) {}
}
