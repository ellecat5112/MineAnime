package com.animecore.client.vfx;

import com.animecore.platform.AnimeParticles;
import com.animecore.vfx.VfxStyle;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;

/** Vanilla particles that sell each element (no custom sprites: zero asset bytes). */
public final class StyleParticles {
    private StyleParticles() {}

    /** Main body particle. */
    public static ParticleEffect primary(VfxStyle s, Random r) {
        return switch (s) {
            case FLAME -> r.nextFloat() < 0.75f ? ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME;
            case WATER -> r.nextFloat() < 0.6f ? ParticleTypes.SPLASH : ParticleTypes.BUBBLE_POP;
            case WIND -> r.nextFloat() < 0.5f ? ParticleTypes.CLOUD : ParticleTypes.WHITE_ASH;
            case LIGHTNING -> ParticleTypes.ELECTRIC_SPARK;
            case EARTH -> block(r.nextBoolean() ? Blocks.DIRT.getDefaultState() : Blocks.STONE.getDefaultState());
            case ICE -> r.nextFloat() < 0.6f ? ParticleTypes.SNOWFLAKE : block(Blocks.PACKED_ICE.getDefaultState());
            case PLANT -> r.nextFloat() < 0.5f ? ParticleTypes.COMPOSTER : block(Blocks.OAK_LOG.getDefaultState());
            case LAVA -> r.nextFloat() < 0.5f ? ParticleTypes.LAVA : ParticleTypes.FLAME;
            case SAND -> new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, Blocks.SAND.getDefaultState());
            case SHADOW -> r.nextFloat() < 0.5f ? ParticleTypes.SQUID_INK : ParticleTypes.LARGE_SMOKE;
            case LIGHT -> r.nextFloat() < 0.6f ? ParticleTypes.END_ROD : ParticleTypes.WAX_ON;
            case ILLUSION -> r.nextFloat() < 0.5f ? ParticleTypes.CHERRY_LEAVES : ParticleTypes.WITCH;
            case SPACE -> r.nextFloat() < 0.5f ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.PORTAL;
            case POISON -> r.nextFloat() < 0.5f ? AnimeParticles.dust(s.mid(), 1.2f) : ParticleTypes.SNEEZE;
            case BLOOD -> r.nextFloat() < 0.6f ? AnimeParticles.dust(s.mid(), 1.3f) : ParticleTypes.DAMAGE_INDICATOR;
            case INSECT -> AnimeParticles.dust(r.nextBoolean() ? s.outer() : s.mid(), 0.5f);
            case SMOKE -> r.nextFloat() < 0.5f ? ParticleTypes.LARGE_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
            case ENERGY -> ParticleTypes.END_ROD;
        };
    }

    /** Tinted dust in the effect colour (glow body). */
    public static ParticleEffect glow(VfxStyle s, int color, Random r, float scale) {
        int c = switch (r.nextInt(3)) {
            case 0 -> s == VfxStyle.ENERGY ? color : s.core();
            case 1 -> color;
            default -> s == VfxStyle.ENERGY ? color : s.outer();
        };
        return AnimeParticles.dust(c, scale);
    }

    /** Accent for impacts / bursts. */
    public static ParticleEffect accent(VfxStyle s, Random r) {
        return switch (s) {
            case FLAME, LAVA -> ParticleTypes.LARGE_SMOKE;
            case WATER -> ParticleTypes.FALLING_WATER;
            case WIND, SMOKE -> ParticleTypes.POOF;
            case LIGHTNING -> ParticleTypes.END_ROD;
            case EARTH, SAND -> block(Blocks.COARSE_DIRT.getDefaultState());
            case ICE -> ParticleTypes.ITEM_SNOWBALL;
            case PLANT -> ParticleTypes.SPORE_BLOSSOM_AIR;
            case SHADOW, SPACE -> ParticleTypes.REVERSE_PORTAL;
            case LIGHT -> ParticleTypes.GLOW;
            case ILLUSION -> ParticleTypes.ENCHANT;
            case POISON -> ParticleTypes.MYCELIUM;
            case BLOOD -> ParticleTypes.DAMAGE_INDICATOR;
            case INSECT -> ParticleTypes.MYCELIUM;
            case ENERGY -> ParticleTypes.ELECTRIC_SPARK;
        };
    }

    /** Wall / eruption block for ground techniques. */
    public static BlockState wallBlock(VfxStyle s) {
        return switch (s) {
            case ICE -> Blocks.PACKED_ICE.getDefaultState();
            case PLANT -> Blocks.OAK_LOG.getDefaultState();
            case SAND -> Blocks.SAND.getDefaultState();
            case LAVA -> Blocks.MAGMA_BLOCK.getDefaultState();
            case WATER -> Blocks.PRISMARINE.getDefaultState();
            case SHADOW, SPACE -> Blocks.OBSIDIAN.getDefaultState();
            case LIGHT, ENERGY, ILLUSION -> Blocks.AMETHYST_BLOCK.getDefaultState();
            default -> Blocks.STONE.getDefaultState();
        };
    }

    /** Elements whose matter is drawn as solid chunks rather than additive glow. */
    public static boolean solid(VfxStyle s) {
        return s == VfxStyle.EARTH || s == VfxStyle.SAND || s == VfxStyle.PLANT || s == VfxStyle.SHADOW || s == VfxStyle.INSECT;
    }

    private static ParticleEffect block(BlockState state) {
        return new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
    }
}
