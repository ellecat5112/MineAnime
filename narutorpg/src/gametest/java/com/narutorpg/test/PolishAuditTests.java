package com.narutorpg.test;

import com.animecore.qa.PolishAudit;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

/** The production standard (CLAUDE.md): every Naruto jutsu, form, character, quest and item is complete. */
public final class PolishAuditTests implements FabricGameTest {
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void productionStandard(TestContext ctx) {
        var issues = PolishAudit.audit(ctx.getWorld().getRegistryManager(), NarutoRPG.CTX);
        if (!issues.isEmpty()) com.animecore.AnimeCore.LOGGER.error("NarutoRPG polish audit:\n{}", PolishAudit.report(issues));
        ctx.assertTrue(issues.isEmpty(), "polish audit: " + PolishAudit.report(issues));
        ctx.complete();
    }
}
