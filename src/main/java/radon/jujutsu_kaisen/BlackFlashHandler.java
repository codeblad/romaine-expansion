package radon.jujutsu_kaisen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.effect.BlackFlashEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ClearChantsC2SPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.*;

public class BlackFlashHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class BlackFlashHandlerForgeEvents {
        private static final float MAX_DAMAGE = 50.0F;
        private static final int CLEAR_INTERVAL = 5 * 20;

        private static final Map<UUID, Integer> TIMERS = new HashMap<>();
        private static final Map<UUID, Integer> COMBOS = new HashMap<>();

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            Iterator<Map.Entry<UUID, Integer>> iter = TIMERS.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<UUID, Integer> entry = iter.next();

                if (!COMBOS.containsKey(entry.getKey())) {
                    iter.remove();
                    continue;
                }

                int remaining = entry.getValue();

                if (remaining > 0) {
                    TIMERS.put(entry.getKey(), --remaining);
                } else {
                    COMBOS.remove(entry.getKey());
                    iter.remove();
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity owner = event.getEntity();
            COMBOS.remove(owner.getUUID());
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!DamageUtil.isMelee(source)) return;

            if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (attacker instanceof ISorcerer sorcerer && !sorcerer.hasArms()) return;

            if (SorcererUtil.getGrade(cap.getExperience()).ordinal() < SorcererGrade.GRADE_1.ordinal() ||
                    (!(source instanceof JJKDamageSources.JujutsuDamageSource) && !cap.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get()) && !cap.hasToggled(JJKAbilities.BLUE_FISTS.get()))) return;

            int combo = COMBOS.getOrDefault(attacker.getUUID(), 0);
            COMBOS.put(attacker.getUUID(), ++combo);
            TIMERS.put(attacker.getUUID(), CLEAR_INTERVAL);

            if (HelperMethods.RANDOM.nextInt(Math.max(1, ConfigHolder.SERVER.blackFlashChance.get() / (cap.isInZone() ? 2 : 1) - combo)) != 0) return;

            COMBOS.remove(attacker.getUUID());

            long lastBlackFlashTime = cap.getLastBlackFlashTime();
            int seconds = (int) (attacker.level().getGameTime() - lastBlackFlashTime) / 20;

            if (lastBlackFlashTime != 0 && seconds <= 1) return;

            cap.onBlackFlash();

            if (attacker instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }

            event.setAmount(Math.min(MAX_DAMAGE, (float) Math.pow(event.getAmount(), 2.5D)));

            attacker.level().addFreshEntity(new BlackFlashEntity(attacker, victim));

            victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER, 2.0F, 0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F);
            victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);
        }
    }
}
