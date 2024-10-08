package radon.jujutsu_kaisen.block.entity;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.JJKBlocks;

public class JJKBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<DomainBlockEntity>> DOMAIN = BLOCK_ENTITIES.register("domain", () ->
            BlockEntityType.Builder.of(DomainBlockEntity::new,
                    JJKBlocks.DOMAIN.get(),
                    JJKBlocks.DOMAIN_BARRIER.get(),
                    JJKBlocks.DOMAIN_AIR.get(),
                    JJKBlocks.DOMAIN_TRANSPARENT.get(),

                    JJKBlocks.FAKE_WATER_DOMAIN.get(),

                    JJKBlocks.CHIMERA_SHADOW_GARDEN.get()
            ).build(null));
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<DomainSkyBlockEntity>> DOMAIN_SKY = BLOCK_ENTITIES.register("domain_sky", () ->
            BlockEntityType.Builder.of(DomainSkyBlockEntity::new,
                    JJKBlocks.DOMAIN_SKY.get()
            ).build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<VeilRodBlockEntity>> VEIL_ROD = BLOCK_ENTITIES.register("veil_rod", () ->
            BlockEntityType.Builder.of(VeilRodBlockEntity::new,
                    JJKBlocks.VEIL_ROD.get()
            ).build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<VeilBlockEntity>> VEIL = BLOCK_ENTITIES.register("veil", () ->
            BlockEntityType.Builder.of(VeilBlockEntity::new,
                    JJKBlocks.VEIL.get()
            ).build(null));

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<DurationBlockEntity>> DURATION = BLOCK_ENTITIES.register("duration", () ->
            BlockEntityType.Builder.of(DurationBlockEntity::new,
                    JJKBlocks.FAKE_WATER_DURATION.get(),
                    JJKBlocks.FAKE_WOOD.get()
            ).build(null));
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MissionBlockEntity>> MISSION = BLOCK_ENTITIES.register("mission", () ->
            BlockEntityType.Builder.of(MissionBlockEntity::new,
                    JJKBlocks.MISSION.get()
            ).build(null));
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<CurseSpawnerBlockEntity>> CURSE_SPAWNER = BLOCK_ENTITIES.register("curse_spawner", () ->
            BlockEntityType.Builder.of(CurseSpawnerBlockEntity::new,
                    JJKBlocks.CURSE_SPAWNER.get(),
                    JJKBlocks.CURSE_BOSS_SPAWNER.get()
            ).build(null));
}
