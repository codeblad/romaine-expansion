package radon.jujutsu_kaisen.ability.disaster_tides;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.ICharged;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.IToggled;
import radon.jujutsu_kaisen.entity.effect.WaterballEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DisasterTides extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(20) == 0 && target != null && owner.distanceTo(target) <= 10.0D && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        WaterballEntity waterball = new WaterballEntity(owner, this.getOutput(owner));
        owner.level().addFreshEntity(waterball);
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public Classification getClassification() {
        return Classification.WATER;
    }
}
