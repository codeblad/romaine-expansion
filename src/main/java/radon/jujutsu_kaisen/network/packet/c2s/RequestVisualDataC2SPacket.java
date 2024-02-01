package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ReceiveVisualDataS2CPacket;

import java.util.UUID;
import java.util.function.Supplier;

public class RequestVisualDataC2SPacket {
    private final UUID src;

    public RequestVisualDataC2SPacket(UUID uuid) {
        this.src = uuid;
    }

    public RequestVisualDataC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null) return;

            LivingEntity target = (LivingEntity) sender.serverLevel().getEntity(this.src);

            if (target != null) {
                if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
                ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                ClientVisualHandler.ClientData data = new ClientVisualHandler.ClientData(cap.getToggled(), cap.getChanneled(), cap.getTraits(), JJKAbilities.getTechniques(target),
                        cap.getTechnique(), cap.getType(), cap.getExperience(), cap.getCursedEnergyColor());
                PacketHandler.sendToClient(new ReceiveVisualDataS2CPacket(this.src, data.serializeNBT()), sender);
            }
        });
        ctx.setPacketHandled(true);
    }
}