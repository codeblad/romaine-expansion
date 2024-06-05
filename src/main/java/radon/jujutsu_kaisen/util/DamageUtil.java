package radon.jujutsu_kaisen.util;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.projectile.WorldSlashProjectile;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.item.registry.JJKItems;

public class DamageUtil {
    public static boolean isBlockable(LivingEntity target, Projectile projectile) {
        if (projectile instanceof WorldSlashProjectile) return false;
        if (projectile.getOwner() == target) return false;

        if (projectile instanceof ThrownChainProjectile chain) {
            if (chain.getStack().is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) return false;
        }

        if (projectile instanceof JujutsuProjectile jujutsu) {
            return !jujutsu.isDomain();
        }
        return true;
    }

    public static boolean isBlockable(LivingEntity target, DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.is(DamageTypes.STARVE) || source.is(JJKDamageSources.SOUL)) return false;

        if (source.getDirectEntity() instanceof Projectile projectile && !isBlockable(target, projectile)) return false;

        if (source.getDirectEntity() instanceof DomainExpansionEntity) return false;

        return source.getEntity() != target;
    }

    public static boolean isMelee(DamageSource source) {
        return !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SPLIT_SOUL_KATANA)) ||
                source instanceof JJKDamageSources.JujutsuDamageSource cap && cap.getAbility() != null && cap.getAbility().isMelee();
    }

}
