package radon.jujutsu_kaisen.ability.misc.lightning;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.EmittingLightningParticle;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.entity.effect.ElectricBlastEntity;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Discharge extends Ability implements IChanneled, IDurationable {
    private static final float DAMAGE = 7.5F;
    private static final float MAX_RADIUS = 20.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target) || owner.distanceTo(target) > this.getRadius(owner) / 2) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getType() == JujutsuType.CURSE || JJKAbilities.RCT1.get().isUnlocked(owner) ? owner.getHealth() / owner.getMaxHealth() < 0.9F :
                owner.getHealth() / owner.getMaxHealth() < 0.4F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    private float getRadius(LivingEntity owner) {
        return Math.min(MAX_RADIUS, this.getOutput(owner) * 2.0F);
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 3.0F, 1.0F);

        float radius = this.getRadius(owner);
        int count = (int) (radius * 0.1F * Math.PI * 2);

        Vec3 center = owner.position().add(0.0D, owner.getBbHeight() / 2, 0.0D);

        for (int i = 0; i < count; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;

            Vec3 direction = new Vec3(Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta), Math.cos(phi));
            Vec3 offset = center.add(direction.multiply(owner.getBbWidth() / 2.0F, owner.getBbHeight() / 2, owner.getBbWidth() / 2.0F));

            level.sendParticles(new EmittingLightningParticle.Options(ParticleColors.getCursedEnergyColorBright(owner),
                            direction, radius, 4), offset.x, offset.y, offset.z, 0,
                    0.0D, 0.0D, 0.0D, 0.0D);
        }

        for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), radius, radius, radius))) {
            if (!entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getOutput(owner))) continue;

            for (int i = 0; i < 4; i++) {
                double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * (entity.getBbHeight() * 1.25F);
                double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                level.sendParticles(new LightningParticle.Options(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                        x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            owner.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (data.getEnergy() >= data.getMaxEnergy() / 2.0F) {
            if (owner.isInWater() || owner.getBlockStateOn().getFluidState().is(Fluids.WATER)) {
                owner.level().addFreshEntity(new ElectricBlastEntity(owner, Math.min(this.getOutput(owner), data.getEnergy() * 0.01F),
                        owner.position().add(0.0F, owner.getBbHeight() / 2, 0.0F)));

                data.setEnergy(0.0F);

                if (owner instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
                }
            }
        }
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();
        return sorcererData.getNature() == CursedEnergyNature.LIGHTNING && abilityData.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get()) && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getDuration() {
        return 5;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.LIGHTNING;
    }
}
