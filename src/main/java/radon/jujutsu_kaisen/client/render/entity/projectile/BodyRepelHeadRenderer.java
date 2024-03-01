package radon.jujutsu_kaisen.client.render.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.curse.WormCurseEntity;
import radon.jujutsu_kaisen.entity.projectile.BodyRepelProjectile;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BodyRepelHeadRenderer extends GeoEntityRenderer<BodyRepelProjectile> {
    public BodyRepelHeadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "body_repel_head")));
    }
}
