package radon.jujutsu_kaisen.data.curse_manipulation;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import java.util.List;
import java.util.Set;

public interface ICurseManipulationData extends INBTSerializable<CompoundTag> {
    void tick();

    void absorb(CursedTechnique technique);

    void absorb(Set<CursedTechnique> techniques);

    void unabsorb(CursedTechnique technique);

    Set<CursedTechnique> getAbsorbed();

    void setCurrentAbsorbed(@Nullable CursedTechnique technique);

    @Nullable CursedTechnique getCurrentAbsorbed();

    void addCurse(AbsorbedCurse curse);

    void removeCurse(AbsorbedCurse curse);

    List<AbsorbedCurse> getCurses();

    @Nullable
    AbsorbedCurse getCurse(EntityType<?> type);

    boolean hasCurse(EntityType<?> type);
}
