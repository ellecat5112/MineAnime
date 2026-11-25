package com.animecore.ward;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.TransparentBlock;

/** Lasting, very tough gate formed by three or more barrier tags placed in sequence. Collapses when a tag breaks. */
public class BarrierGateBlock extends TransparentBlock {
    public static final MapCodec<BarrierGateBlock> CODEC = createCodec(BarrierGateBlock::new);

    public BarrierGateBlock(Settings settings) {
        super(settings);
    }

    @Override protected MapCodec<? extends TransparentBlock> getCodec() { return CODEC; }
}
