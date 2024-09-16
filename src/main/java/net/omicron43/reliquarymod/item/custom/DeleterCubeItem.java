package net.omicron43.reliquarymod.item.custom;

import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.omicron43.reliquarymod.client.renderer.item.DeleterCubeRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

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
        if (deleterCubeItemAnimationState.isCurrentAnimation(ATTACK_START) || deleterCubeItemAnimationState.isCurrentAnimation(BEAM_LOOP)) {
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
        BlockHitResult blockHitResult;
        PlayerEntity player;
        HitResult target = user.raycast(20, 0, false);

        if (remainingUseTicks < 0 || !(user instanceof PlayerEntity)) {
            user.stopUsingItem();
            return;
        }
        if (user instanceof PlayerEntity) {
            Vec3d pos = target.getPos();
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            if (world instanceof ServerWorld serverWorld && user instanceof PlayerEntity) {
                if (i >= 3) {
                    triggerAnim(user, GeoItem.getOrAssignId(user.getStackInHand(user.getActiveHand()), serverWorld), "attacking_controller", "beam_loop");
                }
                user.sendMessage(Text.literal("ticks passed: " + i));
                serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, x, y, z, 5, 0.2, 0.2, 0.2, 0.0);
                }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (world instanceof ServerWorld serverWorld) {
                triggerAnim(user, GeoItem.getOrAssignId(user.getStackInHand(user.getActiveHand()), serverWorld), "attacking_controller", "attack_end");
                ((ServerWorld) world).spawnParticles(ParticleTypes.ANGRY_VILLAGER, user.getX(), user.getY(), user.getZ(), 5, 0.2, 0.2, 0.2, 0.1);
                user.sendMessage(Text.literal("Stopped using Item"));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}