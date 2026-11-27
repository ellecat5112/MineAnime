package com.jojorpg.test;

import com.animecore.clan.ClanRollService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Mock GameTest players go through the normal join path; without this every test player would get a random clan
 * (some discount chakra or evade hits), making numeric assertions nondeterministic. Tests start Clanless instead and
 * assign clans explicitly.
 */
public final class TestServerSetup implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.getGameRules().get(ClanRollService.AUTO_ROLL).set(false, server));
    }
}
