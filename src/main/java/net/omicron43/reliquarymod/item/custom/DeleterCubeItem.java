package net.omicron43.reliquarymod.item.custom;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
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
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 19980;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    int useTime = 0;
    public boolean StartedUsing = false;
    public boolean stopUsing = false;

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
        controllers.add(new AnimationController<>(this, "on_attack_controller", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("attack_start", ATTACK_START));
                // We've marked the "attack_start" animation as being triggerable from the server

        controllers.add(new AnimationController<>(this, "beam_loop_controller", 0, state -> PlayState.CONTINUE)
                        .triggerableAnim("beam_loop", BEAM_LOOP)                .setSoundKeyframeHandler(state -> {
                    // Use helper method to avoid client-code in common class
                    //PlayerEntity player = ClientUtil.getClientPlayer();

                    //if (player != null)
                    //player.playSound(SoundRegistry.JACK_MUSIC, 1, 1);
                }));
        controllers.add(new AnimationController<>(this, "attack_end_controller", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("attack_end", ATTACK_END));
        controllers.add(new AnimationController<>(this, "idle_controller", 0, this::idlePredicate)
                .triggerableAnim("idle", IDLE));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            if (!StartedUsing /*this means that you JUST pressed "use" */ && useTime <= 100 && useTime > 1) {
                StartedUsing = true;
                stopUsing = false;
                useTime++;
            }
        }
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        System.out.println("Ticks passed: " + useTime);
        useTime = 0;
        StartedUsing = false;
        stopUsing = true;
    }

    private PlayState idlePredicate(AnimationState<DeleterCubeItem> deleterCubeItemAnimationState) {
        if (StartedUsing && !stopUsing) {
            deleterCubeItemAnimationState.getController().stop();
        }
        deleterCubeItemAnimationState.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
