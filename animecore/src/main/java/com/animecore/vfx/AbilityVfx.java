package com.animecore.vfx;

import com.animecore.ability.AbilityContext;
import com.animecore.ability.action.AbilityActions;
import com.animecore.ability.action.AreaEffectAction;
import com.animecore.ability.action.IAbilityAction;
import com.animecore.ability.action.SelfBuffAction;
import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.AffinityDefinition;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

/**
 * Server half of the technique VFX pipeline: resolves an ability's {@link VfxStyle} / colour once per cast and
 * broadcasts compact {@link VfxPayload}s to everyone in range. Actions call {@link #emit}; nothing here is
 * ability-specific, so techniques added later by any content mod are animated automatically.
 */
public final class AbilityVfx {
    private AbilityVfx() {}

    /** Effects further than this from a viewer are not sent. */
    public static final double RANGE = 96.0;

    public static void init() {
        AnimeNetworking.registerS2C(VfxPayload.ID, VfxPayload.CODEC);
        AnimeNetworking.registerS2C(ScreenEffectPayload.ID, ScreenEffectPayload.CODEC);
    }

    /** explicit visuals.style > affinity vfx_style > keyword guess (affinity, then ability id) > ENERGY. */
    public static VfxStyle resolveStyle(DynamicRegistryManager rm, AbilityDefinition def, Identifier abilityId) {
        if (def.visuals().style().isPresent()) return def.visuals().style().get();
        if (def.affinity().isPresent()) {
            Optional<AffinityDefinition> aff = AnimeRegistries.affinities(rm).getOrEmpty(def.affinity().get());
            if (aff.isPresent() && aff.get().vfxStyle().isPresent()) return aff.get().vfxStyle().get();
            VfxStyle g = VfxStyle.guess(def.affinity().get().getPath());
            if (g != null) return g;
        }
        VfxStyle g = VfxStyle.guess(abilityId.getPath());
        return g != null ? g : VfxStyle.ENERGY;
    }

    public static int resolveColor(DynamicRegistryManager rm, AbilityDefinition def, VfxStyle style) {
        if (def.visuals().color().isPresent()) return def.visuals().color().get();
        if (style == VfxStyle.ENERGY && def.affinity().isPresent()) {
            return AnimeRegistries.affinities(rm).getOrEmpty(def.affinity().get()).map(AffinityDefinition::color).orElse(style.mid());
        }
        return style.mid();
    }

    /** Screen effect for players caught by an area technique (AUTO decides from style and applied effects). */
    public static ScreenEffect resolveScreen(AbilityDefinition def, VfxStyle style, AreaEffectAction area) {
        ScreenEffect s = def.visuals().screenEffect();
        if (s != ScreenEffect.AUTO) return s;
        boolean nausea = false, blind = false, heavySlow = false;
        for (SelfBuffAction.Effect e : area.effects()) {
            if (e.effect().equals(StatusEffects.NAUSEA)) nausea = true;
            if (e.effect().equals(StatusEffects.BLINDNESS) || e.effect().equals(StatusEffects.DARKNESS)) blind = true;
            if (e.effect().equals(StatusEffects.SLOWNESS) && e.amplifier() >= 3) heavySlow = true;
        }
        if (style == VfxStyle.ILLUSION || nausea) return ScreenEffect.ILLUSION;
        if (style == VfxStyle.SHADOW && (heavySlow || blind)) return ScreenEffect.DARKNESS;
        if (style == VfxStyle.LIGHT && blind) return ScreenEffect.FLASH;
        if (blind) return ScreenEffect.DARKNESS;
        return ScreenEffect.NONE;
    }

    // ------------------------------------------------------------------ emission

    public static void emit(AbilityContext ctx, VfxKind kind, Vec3d origin, Vec3d dir, float size, float extra) {
        emit(ctx.world(), ctx.caster(), kind, ctx.vfxStyle(), ctx.vfxColor(), origin, dir, size * ctx.definition().visuals().scale(), extra);
    }

    public static void emit(ServerWorld world, Entity source, VfxKind kind, VfxStyle style, int color, Vec3d origin, Vec3d dir,
                            float size, float extra) {
        VfxPayload payload = new VfxPayload(kind, style, color, source.getId(), origin, dir, size, extra);
        for (ServerPlayerEntity p : PlayerLookup.around(world, origin, RANGE)) AnimeNetworking.send(p, payload);
    }

    public static void screen(ServerPlayerEntity target, ScreenEffect effect, VfxStyle style, int color, int ticks) {
        if (effect == ScreenEffect.NONE || effect == ScreenEffect.AUTO) return;
        AnimeNetworking.send(target, new ScreenEffectPayload(effect, style, color, ticks));
    }

    // ------------------------------------------------------------------ default cast animations

    /**
     * Animation played when an ability declares none, picked from its first action and style:
     * {@code animecore:cast_exhale | cast_thrust | cast_point | cast_burst | cast_gaze | cast_slam | cast_focus | cast_lunge}.
     */
    public static Identifier defaultAnimation(AbilityDefinition def, VfxStyle style) {
        if (def.actions().isEmpty()) return Ids.core("cast_focus");
        IAbilityAction first = def.actions().get(0);
        var type = first.type();
        String anim;
        if (type == AbilityActions.PROJECTILE) {
            anim = style == VfxStyle.FLAME || style == VfxStyle.WATER || style == VfxStyle.WIND || style == VfxStyle.LAVA
                    || style == VfxStyle.SMOKE || style == VfxStyle.POISON ? "cast_exhale" : "cast_thrust";
        } else if (type == AbilityActions.BEAM_RAYCAST) anim = "cast_point";
        else if (type == AbilityActions.AOE_SPHERE) anim = "cast_burst";
        else if (type == AbilityActions.AREA_EFFECT) anim = style == VfxStyle.ILLUSION || style == VfxStyle.SHADOW ? "cast_gaze" : "cast_sweep";
        else if (type == AbilityActions.RAISE_WALL || type == AbilityActions.SUMMON_ENTITY) anim = "cast_slam";
        else if (type == AbilityActions.DASH_STRIKE) anim = "cast_lunge";
        else anim = "cast_focus";
        return Ids.core(anim);
    }
}
