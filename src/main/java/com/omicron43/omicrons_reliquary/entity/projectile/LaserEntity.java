package com.omicron43.omicrons_reliquary.entity.projectile;

import com.omicron43.omicrons_reliquary.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/*actually actually defines the laser entity to be employed by this mod*/
public class LaserEntity extends AbstractLaserEntity{
    public static final double RADIUS = 512;

    public LaserEntity(EntityType<? extends LaserEntity> type, Level level) {
        super(type, level, 1);
    }

    public LaserEntity(Level level, LivingEntity user, double x, double y, double z, float yaw, float pitch, int duration) {
        super(ModEntities.LASER.get(), level, 1);
        this.user = user;
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setDuration(duration);
        this.setPos(x, y, z);
        this.calculateEndPos(RADIUS);
        if (!level().isClientSide) {
            setCasterId(user.getId());
        }
    }

    @Override
    protected void beamTick() {
        if (!this.level().isClientSide) {
            if (this.user instanceof Player) {
                this.updateWithPlayer();
            } else if (this.user != null) {
                this.updateWithEntity(0F, 0.75F);
            }
        }

        if (user != null) {
            this.yaw = (float) Math.toRadians(user.yHeadRot + 90);
            this.pitch = (float) -Math.toRadians(user.getXRot());
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
                if (entity == this.user) {
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

            var target = result.getEntities().stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(this.user)));
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
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.user == null) {
            discard();
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    private void updateWithPlayer(){
        this.setYaw((float) Math.toRadians(user.yHeadRot + 90));
        this.setPitch((float) Math.toRadians(-user.getXRot()));
        Vec3 vecOffset = user.getLookAngle().normalize().scale(1.25);
        this.setPos(user.getX() + vecOffset.x(), user.getY() + user.getBbHeight() * 0.5F + vecOffset.y(), user.getZ() + vecOffset.z());
    }

    private void updateWithEntity(float offset, float yOffset){
        double radians = Math.toRadians(this.user.yHeadRot + 90);
        this.setYaw((float) radians);
        this.setPitch((float) ((double) (-this.user.getXRot()) * Math.PI / 180.0));
        double offsetX = Math.cos(radians) * offset;
        double offsetZ = Math.sin(radians) * offset;
        this.setPos(this.user.getX() + offsetX, this.user.getY(yOffset), this.user.getZ() + offsetZ);
    }
}
