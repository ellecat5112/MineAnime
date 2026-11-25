package com.animecore.ward;

import com.animecore.platform.Ids;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

/** Engine-owned ward blocks, shared by every anime mod (Naruto barrier tags, Bleach Kido barriers...). */
public final class WardBlocks {
    private WardBlocks() {}

    public static final WardTagBlock WARD_TAG = Registry.register(Registries.BLOCK, Ids.core("ward_tag"),
            new WardTagBlock(AbstractBlock.Settings.create().mapColor(MapColor.OFF_WHITE).noCollision().breakInstantly()
                    .sounds(BlockSoundGroup.WOOL).pistonBehavior(PistonBehavior.DESTROY).nonOpaque().dropsNothing()));

    public static final ChakraWallBlock CHAKRA_WALL = Registry.register(Registries.BLOCK, Ids.core("chakra_wall"),
            new ChakraWallBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN).strength(3f, 30f).nonOpaque()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK).luminance(s -> 7).dropsNothing().pistonBehavior(PistonBehavior.BLOCK)
                    .allowsSpawning((s, w, p, t) -> false).solidBlock((s, w, p) -> false).suffocates((s, w, p) -> false)
                    .blockVision((s, w, p) -> false)));

    public static final BarrierGateBlock BARRIER_GATE = Registry.register(Registries.BLOCK, Ids.core("barrier_gate"),
            new BarrierGateBlock(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).strength(50f, 1200f).nonOpaque()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK).luminance(s -> 10).dropsNothing().pistonBehavior(PistonBehavior.BLOCK)
                    .allowsSpawning((s, w, p, t) -> false).solidBlock((s, w, p) -> false).suffocates((s, w, p) -> false)
                    .blockVision((s, w, p) -> false)));

    public static final BlockEntityType<WardTagBlockEntity> WARD_TAG_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            Ids.core("ward_tag"), FabricBlockEntityTypeBuilder.create(WardTagBlockEntity::new, WARD_TAG).build());

    public static void init() {}
}
