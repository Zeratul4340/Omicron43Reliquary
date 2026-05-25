package com.omicron43.omicrons_reliquary.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/*grabbed this idea from EEEABs mobs, an abstract class for all AoE effects (lasers, booms, what have you)*/
public abstract class AreaEffectEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_CASTER_ID = SynchedEntityData.defineId(AreaEffectEntity.class, EntityDataSerializers.INT);
    public LivingEntity caster;
    public UUID casterUUID;

    /*constructor matching super yada yada*/
    public AreaEffectEntity(EntityType<?> type, Level pLevel) {super(type, pLevel);}

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_CASTER_ID, -1);
    }

    public void setCasterId(int id) {
        entityData.set(DATA_CASTER_ID, id);
    }

    public int getCasterId() {
        return entityData.get(DATA_CASTER_ID);
    }

    @Override
    public void tick() {
        this.baseTick();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        if (this.caster == null && this.casterUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.casterUUID);
            if (entity instanceof LivingEntity) {
                this.caster = (LivingEntity) entity;
            }
        }
        return this.caster;
    }

    public void setOwner(@Nullable LivingEntity Owner) {
        this.caster = Owner;
        this.casterUUID = Owner == null ? null : Owner.getUUID();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void push(Entity entityIn) {
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("owner")) {
            this.casterUUID = compoundTag.getUUID("owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.casterUUID != null) {
            compoundTag.putUUID("owner", this.casterUUID);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public List<LivingEntity> getNearByLivingEntities(double range) {
        return this.getNearByEntities(LivingEntity.class, range, range, range, range);
    }

    public List<LivingEntity> getNearByLivingEntities(double rangeX, double height, double rangeZ, double radius) {
        return this.getNearByEntities(LivingEntity.class, rangeX, height, rangeZ, radius);
    }

    public <T extends Entity> List<T> getNearByEntities(Class<T> entityClass, double x, double y, double z, double radius) {
        return level().getEntitiesOfClass(entityClass, getBoundingBox().inflate(x, y, z), targetEntity -> targetEntity != this &&
                distanceTo(targetEntity) <= radius + targetEntity.getBbWidth() / 2f && targetEntity.getY() <= getY() + y);
    }

    /*eeabs stuffs i ripped probs never gonna use this but who knows*/
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vec3 = (new Vec3(x, y, z)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy)).scale((double) velocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    protected void onHit(HitResult hitResult) {
        HitResult.Type hitresult$type = hitResult.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) hitResult);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, hitResult.getLocation(), GameEvent.Context.of(this, null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult) hitResult;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        }
    }

    protected void onHitEntity(EntityHitResult result) {
    }

    protected void onHitBlock(BlockHitResult result) {
    }

    protected float getDamage() {
        return 0F;
    }

    protected float getPlayerDamage() {
        return 0F;
    }
}
