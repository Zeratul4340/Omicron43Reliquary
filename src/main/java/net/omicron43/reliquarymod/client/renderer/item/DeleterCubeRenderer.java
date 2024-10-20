package net.omicron43.reliquarymod.client.renderer.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.omicron43.reliquarymod.Omicron43Reliquary;
import net.omicron43.reliquarymod.client.renderer.ModRenderLayer;
import net.omicron43.reliquarymod.item.custom.DeleterCubeItem;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DeleterCubeRenderer extends GeoItemRenderer<DeleterCubeItem> {
    public DeleterCubeRenderer(){
        super(new DefaultedItemGeoModel<>(Omicron43Reliquary.id("deleter_cube")));
    }

    private static final Identifier DELETERCUBE_RAY = Omicron43Reliquary.id("textures/entity/deletercube_ray");

    private static void renderBeam(MatrixStack matrixStack, VertexConsumerProvider consumerProvider, Vec3d vec3d, float useTime, float offset) {
        float f2 = -1.0f * (offset * 0.25f % 1.0f);
        matrixStack.push();
        float length = (float) (vec3d.length());
        vec3d = vec3d.normalize();
        float f5 = (float) Math.acos(vec3d.y);
        float f6 = (float) Math.atan2(vec3d.z, vec3d.x);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((MathHelper.PI/2F) - f6) * MathHelper.DEGREES_PER_RADIAN));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f5 * MathHelper.DEGREES_PER_RADIAN));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(offset * 3.0F));
        float f8 = 1F;
        int j = (int) (f8 * 255.0F);
        int k = (int) (f8 * 255.0F);
        int l = (int) (f8 * 255.0F);
        float v = -1.0F + f2;
        float v1 = length * 1F + v;
        float endWidth = 1.3F;
        float startMiddle = 0;
        VertexConsumer ivertexbuilder = consumerProvider.getBuffer(ModRenderLayer.getDeleterCubeBeam(DELETERCUBE_RAY));
        MatrixStack.Entry matrixstack$entry = matrixStack.peek();
        matrixStack.push();
        Matrix4f matrix4f = matrixstack$entry.getPositionMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormalMatrix();

        vertex(ivertexbuilder, matrix4f, matrix3f, startMiddle, 0.0F, 0, j, k, l, 0.5F, v);
        vertex(ivertexbuilder, matrix4f, matrix3f, -endWidth, length, 0, j, k, l, 0.0F, v1);
        vertex(ivertexbuilder, matrix4f, matrix3f, endWidth, length, 0, j, k, l, 1.0F, v1);

        vertex(ivertexbuilder, matrix4f, matrix3f, 0, 0.0F, startMiddle, j, k, l, 0.5F, v);
        vertex(ivertexbuilder, matrix4f, matrix3f, 0, length, endWidth, j, k, l, 1F, v1);
        vertex(ivertexbuilder, matrix4f, matrix3f, 0, length, -endWidth, j, k, l, 0F, v1);
        matrixStack.pop();
        matrixStack.pop();

    }

    private static void vertex(VertexConsumer consumer, Matrix4f m4, Matrix3f m3, float x, float y, float z, int i1, int i2, int i3, float u, float v) {
        /*tbh I think this should convert m3 into the "Entry" type idk how tho*/

        consumer.vertex(m4, x, y, z)
                .color(i1, i2, i3, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(240)
                .normal(0.0F, 1.0F, 0.0F);
    }

    public static void renderBeamsFor(LivingEntity entity, Vec3d beamFrom, MatrixStack matrixStack, VertexConsumerProvider consumerProvider, float partialTick, boolean firstPerson, int firstPersonPass) {
        if (entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof DeleterCubeItem && entity.isUsingItem()) {
            ItemStack stack = entity.getStackInHand(Hand.MAIN_HAND);
            float useDeleterCubeAmount = DeleterCubeItem.getUseTime(stack) / 5f;
            float ageInTicks = entity.age + partialTick;
            Vec3d rayPosition = DeleterCubeItem.getLerpedBeamPosition(stack, partialTick);
            if (rayPosition != null && DeleterCubeItem.getUseTime(stack) >= 3F) {
                Vec3d gunPos = getGunOffset(entity, partialTick, firstPerson, entity.getMainArm() == Arm.LEFT);
                Vec3d vec3 = rayPosition.subtract(beamFrom.add(gunPos));
                matrixStack.push();
                matrixStack.translate(gunPos.x, gunPos.y, gunPos.z);
                if(firstPersonPass == 0 || firstPersonPass == 1){
                    DeleterCubeRenderer.renderBeam(matrixStack, consumerProvider, vec3, useDeleterCubeAmount, ageInTicks);
                }
                if((firstPersonPass == 0 || firstPersonPass == 2)){
                    DeleterCubeRenderer.renderBeam(matrixStack, consumerProvider, vec3, useDeleterCubeAmount, ageInTicks);
                }
                matrixStack.pop();
            }
        }
    }

    private static Vec3d getGunOffset(LivingEntity entity, float partialTicks, boolean firstPerson, boolean left) {
        int i = left ? -1 : 1;
        if(firstPerson){
            double d7 = 1000.0D / (double) MinecraftClient.getInstance().getEntityRenderDispatcher().gameOptions.getFov().getValue();
            Vec3d vec3 = MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getProjection().getPosition((float)i * 0.35F, -0.25F);
            float f = entity.getHandSwingProgress(partialTicks);
            float f1 = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
            vec3 = vec3.multiply(d7);
            vec3 = vec3.rotateY(f1 * 0.5F);
            vec3 = vec3.rotateX(-f1 * 0.7F);
            return vec3;
        }else{
            float yBodyRot = MathHelper.lerp(partialTicks, entity.prevBodyYaw, entity.bodyYaw);
            Vec3d offset = new Vec3d(entity.getWidth() * -0.5F * i, entity.getHeight() * 0.8F, 0).rotateY((float) Math.toRadians(-yBodyRot));
            Vec3d armViewExtra = entity.getRotationVec(partialTicks).normalize().multiply(1.5F);
            return offset.add(armViewExtra);
        }
    }
}
