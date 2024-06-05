package radon.jujutsu_kaisen.ability.disaster_flames;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.level.block.Blocks;
import radon.jujutsu_kaisen.ability.IClosedDomain;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.LavaRockProjectile;

import java.util.List;

public class CoffinOfTheIronMountain extends DomainExpansion implements IClosedDomain {
    private static final float DAMAGE = 10.0F;

    public List<Block> getBlocks() {
        return List.of(Blocks.MAGMA_BLOCK, Blocks.BASALT, Blocks.BLACKSTONE);
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        if (instant || owner.level().getGameTime() % 20 == 0) {
            float power = this.getOutput(owner) * (instant ? 0.5F : 1.0F);

            if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, this), DAMAGE * power)) {
                entity.setRemainingFireTicks(15 * 20);
            }

            if (owner.hasLineOfSight(entity)) {
                LavaRockProjectile rock = new LavaRockProjectile(owner, power, entity);
                rock.setDomain(true);
                owner.level().addFreshEntity(rock);
            }
        }
    }

    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);

        return domain;
    }
}
