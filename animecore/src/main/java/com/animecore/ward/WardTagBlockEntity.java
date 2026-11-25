package com.animecore.ward;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Owner + arming state for a placed tag (explosive / smoke tags trigger on nearby non-owners). */
public class WardTagBlockEntity extends BlockEntity {
    private @Nullable UUID owner;
    private int armDelay = 30;

    public WardTagBlockEntity(BlockPos pos, BlockState state) {
        super(WardBlocks.WARD_TAG_ENTITY, pos, state);
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        markDirty();
    }

    public @Nullable UUID owner() { return owner; }

    public static void serverTick(World world, BlockPos pos, BlockState state, WardTagBlockEntity tag) {
        WardTagBlock.Mode mode = state.get(WardTagBlock.MODE);
        if (mode == WardTagBlock.Mode.BARRIER || !(world instanceof ServerWorld server)) return;
        if (tag.armDelay > 0) {
            tag.armDelay--;
            return;
        }
        if (world.getTime() % 5 != 0) return;
        List<LivingEntity> near = world.getEntitiesByClass(LivingEntity.class, new Box(pos).expand(1.5, 1, 1.5),
                e -> e.isAlive() && !e.isSpectator() && !e.getUuid().equals(tag.owner));
        if (near.isEmpty()) return;
        world.removeBlock(pos, false);
        if (mode == WardTagBlock.Mode.EXPLOSIVE) {
            server.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 2.5f, World.ExplosionSourceType.NONE);
        } else {
            AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            cloud.setRadius(4f);
            cloud.setDuration(200);
            cloud.setPotionContents(new PotionContentsComponent(Optional.empty(), Optional.of(0x9CCC65),
                    List.of(new StatusEffectInstance(StatusEffects.BLINDNESS, 80), new StatusEffectInstance(StatusEffects.SLOWNESS, 80))));
            world.spawnEntity(cloud);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        if (owner != null) nbt.putUuid("owner", owner);
        nbt.putInt("arm_delay", armDelay);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        owner = nbt.containsUuid("owner") ? nbt.getUuid("owner") : null;
        armDelay = nbt.getInt("arm_delay");
    }
}
