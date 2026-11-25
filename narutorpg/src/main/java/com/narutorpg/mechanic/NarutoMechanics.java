package com.narutorpg.mechanic;

import com.animecore.api.event.AnimeCoreEvents;
import com.narutorpg.content.NarutoContent;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

/** Naruto-only rules attached through the engine event bus (no engine code is touched). */
public final class NarutoMechanics {
    private NarutoMechanics() {}

    public static void register() {
        // Fire Release jutsu fizzle while submerged.
        AnimeCoreEvents.PRE_ABILITY_CAST.register((player, ability, ctx) -> {
            boolean fire = ability.value().affinity().map(NarutoContent.FIRE.getValue()::equals).orElse(false);
            if (fire && player.isSubmergedInWater()) {
                player.getServerWorld().spawnParticles(ParticleTypes.BUBBLE, player.getX(), player.getEyeY(), player.getZ(), 12, 0.3, 0.3, 0.3, 0.05);
                player.sendMessage(Text.translatable("message.narutorpg.fire_underwater").formatted(Formatting.AQUA), true);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // Active Sharingan reads the flow of chakra: hand-sign jutsu cost 10% less chakra.
        AnimeCoreEvents.ENERGY_CONSUME.register((player, power, pool, amount) -> {
            boolean sharinganActive = power.activeTransformation()
                    .map(id -> id.getNamespace().equals(power.context().modId()) && id.getPath().contains("sharingan"))
                    .orElse(false);
            boolean casting = !power.comboBuffer().isEmpty() || power.chargingAbility().isPresent();
            return sharinganActive && casting && pool.equals(NarutoContent.CHAKRA) ? amount * 0.9f : amount;
        });
    }
}
