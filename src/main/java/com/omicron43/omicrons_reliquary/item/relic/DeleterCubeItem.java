package com.omicron43.omicrons_reliquary.item.relic;

import com.omicron43.omicrons_reliquary.client.renderer.item.DeleterCubeRenderer;
import com.omicron43.omicrons_reliquary.entity.projectile.DeleterLaserEntity;
import com.omicron43.omicrons_reliquary.init.ModEntities;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class DeleterCubeItem extends Item implements GeoItem {
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("animation.deleter_cube.idle");
    private static final RawAnimation ATTACK_START = RawAnimation.begin().thenPlay("animation.deleter_cube.attack_start");
    private static final RawAnimation BEAM_LOOP = RawAnimation.begin().thenPlay("animation.deleter_cube.beam_loop");
    private static final RawAnimation ATTACK_END = RawAnimation.begin().thenPlay("animation.deleter_cube.attack_end");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DeleterCubeItem(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private DeleterCubeRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new DeleterCubeRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "deleter_cube_controller", 20, state -> PlayState.STOP)
                .triggerableAnim("idle", IDLE)
                .triggerableAnim("attack_start", ATTACK_START)
                .triggerableAnim("attack_end", ATTACK_END)
                .triggerableAnim("loop", BEAM_LOOP)

        );

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        double px = pPlayer.getX();
        double py = pPlayer.getEyeY();
        double pz = pPlayer.getZ();
        DeleterLaserEntity laser = new DeleterLaserEntity(pPlayer.level(), pPlayer, px, py, pz, 20);
        pPlayer.level().addFreshEntity(laser);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity entity, ItemStack pStack, int pRemainingUseDuration) {
        if (pLevel instanceof ServerLevel serverLevel && entity instanceof Player) {
            entity.sendSystemMessage(Component.literal("using..."));
        }
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity entity, int pTimeCharged) {
        if (pLevel instanceof ServerLevel serverLevel && entity instanceof Player) {
            entity.sendSystemMessage(Component.literal("released using"));
        }
    }

    @Override
    public void onStopUsing(ItemStack pStack, LivingEntity entity, int count) {
        if (entity.level() instanceof ServerLevel serverLevel && entity instanceof Player) {
            entity.sendSystemMessage(Component.literal("released using"));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
