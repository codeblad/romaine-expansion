package radon.jujutsu_kaisen.item.veil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class TransparentModifier extends Modifier {
    public TransparentModifier() {
        super(Type.TRANSPARENT);
    }

    public TransparentModifier(CompoundTag nbt) {
        super(nbt);
    }

    @Override
    public Component getComponent() {
        return Component.translatable(String.format("item.%s.veil_rod.transparent", JujutsuKaisen.MOD_ID));
    }
}
