package com.animecore.client.render;

import com.animecore.entity.ManifestationEntity;
import com.animecore.manifestation.ManifestationDefinition;
import com.animecore.quest.StoryNpcDefinition;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Set;

/**
 * Manifested Stands: a glowing, translucent figure drawn like a story character (tinted humanoid layers) or with its
 * low-poly model. Poses follow the synced {@link ManifestationEntity.State}: a guard pose while idle, a straight punch,
 * the rush (arms pumping with ghost fists around them - "ORA ORA ORA"), a flying strike and a pointing cast.
 */
public class ManifestationRenderer extends LivingEntityRenderer<ManifestationEntity, ManifestationRenderer.StandModel> {
    private static final Identifier EMPTY = StoryNpcRenderer.tex("empty");
    private static final Set<String> OUTFITS = Set.of("shirt", "jacket", "cloak", "robe", "bodysuit", "armor");
    private static final Set<String> HAIR = Set.of("short", "spiky", "long", "ponytail", "bowl", "buns", "wild", "bald");
    private static final Set<String> MARKS = Set.of("whiskers", "fangs", "forehead", "red_eyes", "pale_eyes", "rinnegan", "mask",
            "paint", "scar", "forehead_kanji", "glasses");
    static final int GLOW = LightmapTextureManager.MAX_LIGHT_COORDINATE;

    public ManifestationRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new StandModel(ctx.getPart(EntityModelLayers.PLAYER)), 0f);
        addFeature(new Layers(this));
    }

    @Override
    public void render(ManifestationEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider buffers, int light) {
        ManifestationDefinition def = entity.definition().orElse(null);
        if (def == null) return;
        var model = def.appearance().model().map(FormModels::get).orElse(null);
        if (model != null) {
            int a = Math.round(def.alpha() * 255f);
            FormModelRenderer.render(entity, model, tickDelta, matrices, buffers, GLOW, (a << 24) | 0xFFFFFF, def.alpha() < 1f);
            return;
        }
        super.render(entity, yaw, tickDelta, matrices, buffers, GLOW);
    }

    @Override protected boolean hasLabel(ManifestationEntity entity) { return false; }

    @Override public Identifier getTexture(ManifestationEntity entity) { return EMPTY; }

    @Override
    protected RenderLayer getRenderLayer(ManifestationEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return RenderLayer.getEntityTranslucent(EMPTY);
    }

    /** The player model, posed by the Stand's state instead of walking. */
    public static final class StandModel extends PlayerEntityModel<ManifestationEntity> {
        StandModel(ModelPart root) { super(root, false); }

        @Override
        public void setAngles(ManifestationEntity e, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
            super.setAngles(e, 0f, 0f, age, headYaw, headPitch);
            float t = age - e.clientStateStart;
            // legs trail behind: Stands float
            rightLeg.pitch = 0.32f + MathHelper.sin(age * 0.07f) * 0.06f;
            leftLeg.pitch = 0.12f - MathHelper.sin(age * 0.07f) * 0.06f;
            rightLeg.roll = 0.06f;
            leftLeg.roll = -0.06f;
            switch (e.state()) {
                case PUNCH -> {
                    float k = Math.min(1f, t / 3f);
                    rightArm.pitch = -1.55f * k;
                    rightArm.yaw = 0.1f;
                    leftArm.pitch = -0.6f;
                    body.yaw = -0.25f * k;
                }
                case RUSH -> {
                    rightArm.pitch = -1.45f + MathHelper.sin(t * 2.6f) * 0.35f;
                    leftArm.pitch = -1.45f + MathHelper.sin(t * 2.6f + MathHelper.PI) * 0.35f;
                    rightArm.yaw = 0.15f * MathHelper.cos(t * 1.7f);
                    leftArm.yaw = -0.15f * MathHelper.cos(t * 1.9f);
                    body.yaw = 0.12f * MathHelper.sin(t * 2.6f);
                }
                case STRIKE -> {
                    rightArm.pitch = -2.6f;
                    leftArm.pitch = -2.6f;
                    rightArm.roll = 0.15f;
                    leftArm.roll = -0.15f;
                    rightLeg.pitch = 0.6f;
                    leftLeg.pitch = 0.5f;
                }
                case CAST -> {
                    rightArm.pitch = -1.65f;
                    rightArm.yaw = -0.1f;
                    leftArm.pitch = -0.35f;
                    leftArm.roll = -0.3f;
                }
                default -> {
                    // guard: fists raised, slight sway
                    rightArm.pitch = -0.75f + MathHelper.sin(age * 0.08f) * 0.05f;
                    leftArm.pitch = -0.75f - MathHelper.sin(age * 0.08f) * 0.05f;
                    rightArm.yaw = -0.35f;
                    leftArm.yaw = 0.35f;
                    rightArm.roll = 0.1f;
                    leftArm.roll = -0.1f;
                }
            }
            leftSleeve.copyTransform(leftArm);
            rightSleeve.copyTransform(rightArm);
            leftPants.copyTransform(leftLeg);
            rightPants.copyTransform(rightLeg);
            jacket.copyTransform(body);
        }
    }

    private static final class Layers extends FeatureRenderer<ManifestationEntity, StandModel> {
        Layers(FeatureRendererContext<ManifestationEntity, StandModel> ctx) { super(ctx); }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider buffers, int light, ManifestationEntity entity,
                           float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
            ManifestationDefinition def = entity.definition().orElse(null);
            if (def == null) return;
            StoryNpcDefinition.Appearance a = def.appearance();
            float alpha = def.alpha();
            drawAll(matrices, buffers, a, alpha);
            if (entity.state() == ManifestationEntity.State.RUSH) ghostFists(matrices, buffers, entity, a, alpha, animationProgress);
        }

        private void drawAll(MatrixStack m, VertexConsumerProvider b, StoryNpcDefinition.Appearance a, float alpha) {
            draw(m, b, "skin", a.skin(), alpha);
            draw(m, b, "face", 0xFFFFFF, alpha);
            draw(m, b, "eyes", a.eyes(), alpha);
            draw(m, b, "pants", a.pants(), alpha);
            String outfit = OUTFITS.contains(a.outfit()) ? a.outfit() : "bodysuit";
            draw(m, b, "outfit_" + outfit, a.top(), alpha);
            draw(m, b, "outfit_" + outfit + "_accent", a.accent(), alpha);
            String hair = HAIR.contains(a.hairStyle()) ? a.hairStyle() : "short";
            if (!hair.equals("bald")) draw(m, b, "hair_" + hair, a.hair(), alpha);
            a.headband().ifPresent(c -> draw(m, b, "headband", c, alpha));
            a.mark().filter(MARKS::contains).ifPresent(mk -> draw(m, b, "mark_" + mk, 0xFFFFFF, alpha));
        }

        /** Three extra pairs of fists blurring around the real ones. */
        private void ghostFists(MatrixStack m, VertexConsumerProvider b, ManifestationEntity e, StoryNpcDefinition.Appearance a, float alpha, float age) {
            StandModel model = getContextModel();
            float rp = model.rightArm.pitch, lp = model.leftArm.pitch, ry = model.rightArm.yaw, ly = model.leftArm.yaw;
            setBodyVisible(model, false);
            String outfit = OUTFITS.contains(a.outfit()) ? a.outfit() : "bodysuit";
            for (int k = 1; k <= 3; k++) {
                float t = age * 2.6f + k * 1.9f;
                model.rightArm.pitch = -1.45f + MathHelper.sin(t) * 0.45f;
                model.leftArm.pitch = -1.45f + MathHelper.sin(t + MathHelper.PI) * 0.45f;
                model.rightArm.yaw = 0.35f * (k - 2) + 0.1f;
                model.leftArm.yaw = -0.35f * (k - 2) - 0.1f;
                model.rightSleeve.copyTransform(model.rightArm);
                model.leftSleeve.copyTransform(model.leftArm);
                float ga = alpha * 0.4f;
                draw(m, b, "skin", a.skin(), ga);
                draw(m, b, "outfit_" + outfit, a.top(), ga);
            }
            setBodyVisible(model, true);
            model.rightArm.pitch = rp; model.leftArm.pitch = lp; model.rightArm.yaw = ry; model.leftArm.yaw = ly;
            model.rightSleeve.copyTransform(model.rightArm);
            model.leftSleeve.copyTransform(model.leftArm);
        }

        private static void setBodyVisible(StandModel model, boolean v) {
            model.head.visible = v; model.hat.visible = v; model.body.visible = v; model.jacket.visible = v;
            model.rightLeg.visible = v; model.leftLeg.visible = v; model.rightPants.visible = v; model.leftPants.visible = v;
        }

        private void draw(MatrixStack m, VertexConsumerProvider b, String layer, int rgb, float alpha) {
            int a = MathHelper.clamp(Math.round(alpha * 255f), 0, 255);
            getContextModel().render(m, b.getBuffer(RenderLayer.getEntityTranslucent(StoryNpcRenderer.tex(layer))), GLOW,
                    OverlayTexture.DEFAULT_UV, (a << 24) | (rgb & 0xFFFFFF));
        }
    }
}
