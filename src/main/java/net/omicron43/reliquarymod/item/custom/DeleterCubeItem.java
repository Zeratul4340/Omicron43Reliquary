package net.omicron43.reliquarymod.item.custom;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.omicron43.reliquarymod.client.renderer.item.DeleterCubeRenderer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.mixin.client.BlockEntityWithoutLevelRendererMixin;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public final class DeleterCubeItem extends Item implements GeoItem {
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 5;
    }
    int usetime = 0;
    public boolean alreadyStartedUsing = false;

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
            @Nullable
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
        controllers.add(new AnimationController<>(this, "idle_controller", 0, this::idlePredicate));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world instanceof ServerWorld serverWorld) {
            if (!alreadyStartedUsing /*this means that its starting to be used */ && usetime <= 10) {
                triggerAnim(player, GeoItem.getOrAssignId(player.getActiveItem(), serverWorld), "on_attack_controller", "attack_start");
                alreadyStartedUsing = true;
                usetime++;
                System.out.println("THE USETIME IS: " + usetime);
                if(usetime == 10)
                {
                    usetime = 0;
                    use(world,player,hand);
                    triggerAnim(player, GeoItem.getOrAssignId(player.getActiveItem(), serverWorld), "attack_end_controller", "attack_end");
                    return super.use(world, player, hand);
                }
                return super.use(world, player, hand);
            }
            else if(alreadyStartedUsing){
                triggerAnim(player, GeoItem.getOrAssignId(player.getActiveItem(), serverWorld), "attack_end_controller", "attack_end");
                alreadyStartedUsing = false;
                usetime = 0;
                System.out.println("THE USETIME IS: SHOULD BE ZERO BTW  " + usetime);
                return super.use(world, player, hand);
            }
            triggerAnim(player, GeoItem.getOrAssignId(player.getActiveItem(), serverWorld), "idle_controller", "idle");
        }
        return super.use(world, player, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {

    }



    private PlayState idlePredicate(AnimationState<DeleterCubeItem> deleterCubeItemAnimationState) {
        if (alreadyStartedUsing) {
            deleterCubeItemAnimationState.getController().stop();
            return PlayState.STOP;
        }
        deleterCubeItemAnimationState.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
