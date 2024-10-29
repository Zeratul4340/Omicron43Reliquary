package net.omicron43.reliquarymod.item.custom;

import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.omicron43.reliquarymod.client.component.ModDataComponentTypes;
import net.omicron43.reliquarymod.client.renderer.item.DeleterCubeRenderer;
import net.omicron43.reliquarymod.effect.DisintegrationStatusEffect;
import net.omicron43.reliquarymod.effect.ModEffects;
import net.omicron43.reliquarymod.server.misc.DamageTypes;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class DeleterCubeItem extends Item implements GeoItem {

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 19980;
    }

    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("animation.deleter_cube.idle");
    private static final RawAnimation ATTACK_START = RawAnimation.begin().thenPlay("animation.deleter_cube.attack_start");
    private static final RawAnimation BEAM_LOOP = RawAnimation.begin().thenPlay("animation.deleter_cube.beam_loop");
    private static final RawAnimation ATTACK_END = RawAnimation.begin().thenPlay("animation.deleter_cube.attack_end");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DeleterCubeItem(Settings settings) {
        super(settings);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private DeleterCubeRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new DeleterCubeRenderer();
                // Defer creation of our renderer then cache it so that it doesn't get instantiated too early

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "attacking_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("attack_start", ATTACK_START)
                .triggerableAnim("beam_loop", BEAM_LOOP)
                .triggerableAnim("attack_end", ATTACK_END));
                // marked the named animations as being triggerable from the server
        controllers.add(new AnimationController<>(this, "idle_controller", 0, this::shouldBeIdle
        ));
    }

    /*private PlayState shouldAttack(AnimationState<DeleterCubeItem> deleterCubeItemAnimationState) {
        if
    }*/

    private PlayState shouldBeIdle(AnimationState<DeleterCubeItem> deleterCubeItemAnimationState) {
        if (deleterCubeItemAnimationState.isCurrentAnimation(BEAM_LOOP)) {
            return PlayState.STOP;
        }
        else {
            return deleterCubeItemAnimationState.setAndContinue(IDLE);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.setCurrentHand(hand);
        ItemStack itemStack = player.getStackInHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            player.sendMessage(Text.literal("item used"));
            triggerAnim(player, GeoItem.getOrAssignId(player.getStackInHand(hand), serverWorld), "attacking_controller", "attack_start");

        }
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float beamLength = 43F;
        BlockHitResult blockHitResult;
        PlayerEntity player;
        HitResult fullBeam = user.raycast(beamLength, 0, false);
        HitResult entityHitter = ProjectileUtil.getCollision(user, Entity::canBeHitByProjectile, beamLength);
        //Note: add particles later (Search for "ParticleOptions")

        if (remainingUseTicks < 0 || !(user instanceof PlayerEntity)) {
            user.stopUsingItem();
            return;
        }
        Vec3d pos = fullBeam.getPos();
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        Direction blastHitDirection = null;
        Vec3d blastHitPos = null;

        if (!world.isClient) {
            stack.set(ModDataComponentTypes.RAYCAST_COORDINATES, pos);
        }

        Box maxAABB = user.getBoundingBox().expand(beamLength);
        float simRaytrace = 1.0F;
        Vec3d startClip = user.getEyePos();
        while (simRaytrace < beamLength) {
            startClip = startClip.add(user.getRotationVec(1.0F));
            Vec3d endClip = startClip.add(user.getRotationVec(1.0F));
            HitResult hitScanner = ProjectileUtil.getEntityCollision(world, user, startClip, endClip, maxAABB, Entity::canBeHitByProjectile);
            if (hitScanner != null) {
                entityHitter = hitScanner;
                break;
            }
            simRaytrace++;
        }
        if (entityHitter instanceof EntityHitResult entityHitResult) {
            blastHitPos = entityHitResult.getEntity().getPos();
            blastHitDirection = Direction.UP;
        }
        if (blastHitPos != null && i % 2 == 0) {
            float offset = 0.05F + world.random.nextFloat() * 0.09F;
            Vec3d particleVec = blastHitPos.add(offset * blastHitDirection.getOffsetX(), offset * blastHitDirection.getOffsetY(), offset * blastHitDirection.getOffsetZ());
            world.addParticle(ParticleTypes.SONIC_BOOM, particleVec.x, particleVec.y, particleVec.z, blastHitDirection.getId(), 0, 0);
        }

        if (world instanceof ServerWorld serverWorld && user instanceof PlayerEntity) {
            if (i >= 3) {
                triggerAnim(user, GeoItem.getOrAssignId(user.getStackInHand(user.getActiveHand()), serverWorld), "attacking_controller", "beam_loop");
            }
            Box hitBox = new Box(x-1, y-1, z-1, x+1, y+1, z+1);
            for (Entity entity : world.getOtherEntities(user, hitBox, Entity::canBeHitByProjectile)) {
                if (!entity.isPartOf(user) && !entity.isTeammate(user) && !user.isTeammate(entity) && !user.isConnectedThroughVehicle(entity)) {
                    entity.damage(DamageTypes.disintegrating(world.getRegistryManager()), 1.0F);
                    if(entity instanceof LivingEntity target) {
                        if(target.hasStatusEffect(ModEffects.DISINTEGRATION)){
                            int upgradeAmp = Objects.requireNonNull(target.getStatusEffect(ModEffects.DISINTEGRATION)).getAmplifier() + 1;
                            Objects.requireNonNull(target.getStatusEffect(ModEffects.DISINTEGRATION)).upgrade(new StatusEffectInstance(ModEffects.DISINTEGRATION, 800, upgradeAmp));
                        }
                        else {
                            target.addStatusEffect(new StatusEffectInstance(ModEffects.DISINTEGRATION, 800, 1));
                        }
                    }
                }
            }
            /*serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, x, y, z, 5, 0.2, 0.2, 0.2, 0.0);*/
        }

    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world instanceof ServerWorld serverWorld) {
                triggerAnim(user, GeoItem.getOrAssignId(user.getStackInHand(user.getActiveHand()), serverWorld), "attacking_controller", "attack_end");
                ((ServerWorld) world).spawnParticles(ParticleTypes.ANGRY_VILLAGER, user.getX(), user.getY(), user.getZ(), 5, 0.2, 0.2, 0.2, 0.1);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public static int getUseTime(ItemStack stack) {
        NbtCompound compound = (NbtCompound) stack.streamTags();
        return compound != null ? compound.getInt("UseTime") : 0;
    }
}