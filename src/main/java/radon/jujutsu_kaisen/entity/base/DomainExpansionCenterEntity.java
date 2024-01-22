package radon.jujutsu_kaisen.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class DomainExpansionCenterEntity extends Entity implements GeoEntity {
    @Nullable
    private UUID domainUUID;
    @Nullable
    private DomainExpansionEntity cachedDomain;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DomainExpansionCenterEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DomainExpansionCenterEntity(EntityType<?> pType, DomainExpansionEntity domain) {
        super(pType, domain.level());

        this.setDomain(domain);
    }

    @Override
    public void tick() {
        DomainExpansionEntity domain = this.getDomain();

        if (!this.level().isClientSide && (domain == null || domain.isRemoved() || !domain.isAlive())) {
            this.discard();
        } else {
            super.tick();
        }
    }

    public void setDomain(@Nullable DomainExpansionEntity domain) {
        if (domain != null) {
            this.domainUUID = domain.getUUID();
            this.cachedDomain = domain;
        }
    }

    @Nullable
    public DomainExpansionEntity getDomain() {
        if (this.cachedDomain != null && !this.cachedDomain.isRemoved()) {
            return this.cachedDomain;
        } else if (this.domainUUID != null && this.level() instanceof ServerLevel) {
            this.cachedDomain = (DomainExpansionEntity) ((ServerLevel) this.level()).getEntity(this.domainUUID);
            return this.cachedDomain;
        } else {
            return null;
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.domainUUID != null) {
            pCompound.putUUID("domain", this.domainUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("domain")) {
            this.domainUUID = pCompound.getUUID("domain");
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
