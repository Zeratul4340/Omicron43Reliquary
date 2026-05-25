package com.omicron43.omicrons_reliquary.entity.projectile;

import com.omicron43.omicrons_reliquary.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/*actually actually defines the laser entity to be employed by this mod*/
public class DeleterLaserEntity extends AbstractLaserEntity{
private static final EntityDataAccessor<Boolean> DATA_IS_PLAYER = SynchedEntityData.defineId(DeleterLaserEntity.class, EntityDataSerializers.BOOLEAN);
private static final EntityDataAccessor<Integer> DATA_USER = SynchedEntityData.defineId(DeleterLaserEntity.class, EntityDataSerializers.INT);
    public static final double RADIUS = 20;

    @OnlyIn(Dist.CLIENT)
    private Vec3[] attractorPos;

    public DeleterLaserEntity(EntityType<? extends DeleterLaserEntity> type, Level level) {
        super(type, level, 20);
        if (level.isClientSide) {
            this.attractorPos = new Vec3[]{new Vec3(0, 0, 0)};
        }
    }

    public DeleterLaserEntity(Level level, LivingEntity user, double x, double y, double z, int duration) {
        this(ModEntities.LASER.get(), level);
        this.setOwner(user);
        this.setYaw((float) Math.toRadians(user.yHeadRot + 90));
        this.setPitch((float) Math.toRadians(-user.getXRot()));
        this.setDuration(duration);
        this.setPos(x, y, z);
        int id = 0;
        this.getEntityData().set(DATA_USER, id);
        if (!level().isClientSide) {
            setCasterId(user.getId());
        }
        this.calculateEndPos(RADIUS);
    }

    @Override
    protected void beamTick() {
        if (!this.level().isClientSide) {
            if (this.caster instanceof Player) {
                this.updateWithPlayer();
            } else if (this.caster != null) {
                this.updateWithEntity(0F, 0.75F);
            }
        }

        if (caster != null) {
            this.yaw = (float) Math.toRadians(caster.yHeadRot + 90);
            this.pitch = (float) -Math.toRadians(caster.getXRot());
        }

        if (this.tickCount >= this.getCountDown()) {
            this.calculateEndPos(RADIUS);

            raytraceEntities(this.level(), new Vec3(getX(), getY(), getZ()), new Vec3(endPosX, endPosY, endPosZ));

            if (this.blockSide != null) {
                this.spawnHitParticles();
            }
        }
    }

    @Override
    public CustomHitResult raytraceEntities(Level world, Vec3 from, Vec3 to) {
        CustomHitResult result = new CustomHitResult();
        result.setBlockHit(this.level().clip(new ClipContext(new Vec3(getX(), getY(), getZ()), new Vec3(endPosX, endPosY, endPosZ),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)));
        if (result.getBlockHit() != null) {
            Vec3 hitVec = result.getBlockHit().getLocation();
            collidePosX = hitVec.x;
            collidePosY = hitVec.y;
            collidePosZ = hitVec.z;
            blockSide = result.getBlockHit().getDirection();
        } else {
            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(Math.min(getX(), collidePosX), Math.min(getY(), collidePosY), Math.min(getZ(), collidePosZ), Math.max(getX(), collidePosX), Math.max(getY(), collidePosY), Math.max(getZ(), collidePosZ)).inflate(1, 1, 1));
            for (LivingEntity entity : entities) {
                if (entity == this.caster) {
                    continue;
                }
                float pad = entity.getPickRadius() + getBaseScale();
                AABB aabb = entity.getBoundingBox().inflate(pad, pad, pad);
                Optional<Vec3> hit = aabb.clip(from, to);
                if (aabb.contains(from)) {
                    result.addEntityHit(entity);
                } else if (hit.isPresent()) {
                    result.addEntityHit(entity);
                }
            }

            var target = result.getEntities().stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(this.caster)));
            if (target.isPresent()) {
                collidePosX = target.get().getX();
                collidePosY = target.get().getY();
                collidePosZ = target.get().getZ();
            } else {
                collidePosX = endPosX;
                collidePosY = endPosY;
                collidePosZ = endPosZ;
            }
            blockSide = null;
        }

        return result;
    }

    public void spawnHitParticles(){}

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_PLAYER, true);
        this.entityData.define(DATA_USER, 0);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.caster == null) {
            discard();
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    private void updateWithPlayer(){
        this.setYaw((float) Math.toRadians(caster.yHeadRot + 90));
        this.setPitch((float) Math.toRadians(-caster.getXRot()));
        Vec3 vecOffset = caster.getLookAngle().normalize().scale(1.25);
        this.setPos(caster.getX() + vecOffset.x(), caster.getY() + caster.getBbHeight() * 0.5F + vecOffset.y(), caster.getZ() + vecOffset.z());
    }

    private void updateWithEntity(float offset, float yOffset){
        double radians = Math.toRadians(this.caster.yHeadRot + 90);
        this.setYaw((float) radians);
        this.setPitch((float) ((double) (-this.caster.getXRot()) * Math.PI / 180.0));
        double offsetX = Math.cos(radians) * offset;
        double offsetZ = Math.sin(radians) * offset;
        this.setPos(this.caster.getX() + offsetX, this.caster.getY(yOffset), this.caster.getZ() + offsetZ);
    }
}
