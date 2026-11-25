package com.animecore.movement;

import net.minecraft.util.Identifier;

/**
 * How one power system grants one movement mode.
 *
 * @param strength flight: fly speed (vanilla creative = 0.05); burst step: distance in blocks; adhesion: unused
 * @param cooldownTicks only used by one-shot modes (burst step)
 */
public record MovementSpec(MovementMode mode, Identifier pool, float activationCost, float drainPerTick,
                           int requiredTier, float strength, int cooldownTicks) {
}
