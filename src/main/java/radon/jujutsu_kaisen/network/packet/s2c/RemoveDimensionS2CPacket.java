package radon.jujutsu_kaisen.network.packet.s2c;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;

public record RemoveDimensionS2CPacket(ResourceKey<Level> key) implements CustomPacketPayload {
    public static final Type<RemoveDimensionS2CPacket> TYPE = new Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "remove_dimension_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RemoveDimensionS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            RemoveDimensionS2CPacket::key,
            RemoveDimensionS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientWrapper.removeDimension(this.key));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}