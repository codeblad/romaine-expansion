package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleDomain extends Ability implements Ability.IToggled {
    private static final float PARTICLE_SIZE = 0.075F;
    private static final double X_STEP = 0.25D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target != null || owner.getHealth() / owner.getMaxHealth() < 0.5F) return false;

        AtomicBoolean result = new AtomicBoolean();

        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (DomainExpansionEntity ignored : cap.getDomains((ServerLevel) owner.level)) {
                    result.set(true);
                    break;
                }
            });
        }
        return result.get();
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 2, 0, false, false, false));

        if (!owner.level.isClientSide) {
            for (double phi = -Math.PI; phi < Math.PI; phi += X_STEP) {
                double x = owner.getX() + Math.cos(phi);
                double y = owner.getY() + PARTICLE_SIZE;
                double z = owner.getZ() + Math.sin(phi);

                ((ServerLevel) owner.level).sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public int getDuration() {
        return 20;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) {
                    cap.toggle(victim, JJKAbilities.SIMPLE_DOMAIN.get());

                    if (victim instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                    }
                }
            });
        }
    }
}
