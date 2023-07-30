package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class RedProjectile extends JujutsuProjectile {
    private static final double LAUNCH_POWER = 10.0D;
    private static final float EXPLOSIVE_POWER = 1.0F;
    private static final int DELAY = 20;
    private static final int DURATION = 5 * 20;
    private static final float SPEED = 5.0F;
    private static final float DAMAGE = 25.0F;

    public RedProjectile(EntityType<? extends Projectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public RedProjectile(LivingEntity pShooter) {
        super(JJKEntities.RED.get(), pShooter.level, pShooter);

        Vec3 look = pShooter.getLookAngle();
        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox().inflate(1.0D);

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (Entity entity : HelperMethods.getEntityCollisions(this.level, bounds)) {
                    if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

                    if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner), DAMAGE * cap.getGrade().getPower())) {
                        entity.setDeltaMovement(this.getLookAngle().scale(LAUNCH_POWER));
                    }
                }
            });
        }
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);

        if (this.level.isClientSide) return;

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                float radius = EXPLOSIVE_POWER * cap.getGrade().getPower();

                Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
                this.level.explode(owner, JJKDamageSources.indirectJujutsuAttack(this, owner), null, offset, radius, false,
                        this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
            });
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3 src = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

            // Get the entity's velocity vector
            Vec3 dir = this.getLookAngle().normalize();

            for (int i = 0; i < 16; i++) {
                // Generate some random offsets along the x, y and z axes
                double offsetX = this.random.nextGaussian();
                double offsetY = this.random.nextGaussian();
                double offsetZ = this.random.nextGaussian();

                // Subtract the direction vector from the source position and add the offsets
                Vec3 dst = src.subtract(dir).add(offsetX, offsetY, offsetZ);

                // Spawn a particle at the destination position
                this.level.addParticle(new TravelParticle.TravelParticleOptions(dst.toVector3f(), TravelParticle.TravelParticleOptions.RED_COLOR,
                        0.1F, 1.0F, 5), src.x(), src.y(), src.z(), 0.0D, 0.0D, 0.0D);
            }


            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    if (this.getTime() % 5 == 0) {
                        owner.swing(InteractionHand.MAIN_HAND);
                    }
                    Vec3 look = owner.getLookAngle();
                    Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
                    this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
                }
            }
        }

        if (!this.level.isClientSide) {
            if (this.getTime() >= DURATION) {
                this.discard();
            } else if (this.getTime() >= DELAY) {
                if (this.getTime() == DELAY) {
                    this.setDeltaMovement(this.getLookAngle().scale(SPEED));
                } else if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    this.discard();
                }
                this.hurtEntities();
            }
        }
    }
}
