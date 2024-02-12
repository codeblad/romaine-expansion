package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;

public class SetCursedEnergyColorC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "set_cursed_energy_color_serverbound");

    private final int cursedEnergyColor;

    public SetCursedEnergyColorC2SPacket(int cursedEnergyColor) {
        this.cursedEnergyColor = cursedEnergyColor;
    }

    public SetCursedEnergyColorC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            ISorcererData data = sender.getData(JJKAttachmentTypes.SORCERER);

            data.setCursedEnergyColor(this.cursedEnergyColor);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.cursedEnergyColor);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}