package com.theendercore.buried.client.renderer.entity

import com.mojang.authlib.properties.PropertyMap
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.theendercore.buried.entity.Grave
import net.minecraft.client.model.SkullModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.level.block.SkullBlock
import java.util.*

class GraveRenderer(context: EntityRendererProvider.Context) : EntityRenderer<Grave>(context) {
    var head = SkullModel(context.modelSet.bakeLayer(ModelLayers.PLAYER_HEAD))
    var skull = SkullModel(context.modelSet.bakeLayer(ModelLayers.SKELETON_SKULL))
    override fun render(
        grave: Grave, f: Float, g: Float, poseStack: PoseStack, multiBufferSource: MultiBufferSource, i: Int,
    ) {
        poseStack.pushPose()
        poseStack.rotateAround(Axis.YN.rotationDegrees(grave.getPreciseBodyRotation(f)), 0f, 0f, 0f)
        poseStack.translate(-0.5, 0.0, -0.5)
        if (grave.ownerUUID.isPresent) {
            val resolvableProfile =
                ResolvableProfile(Optional.empty(), Optional.of(grave.ownerUUID.get()), PropertyMap()).resolve().get()
            val renderType = SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, resolvableProfile)
            SkullBlockRenderer.renderSkull(null, 180.0f, 0f, poseStack, multiBufferSource, i, head, renderType)
        } else {
            val renderType = SkullBlockRenderer.getRenderType(SkullBlock.Types.SKELETON, null)
            SkullBlockRenderer.renderSkull(null, 180.0f, 0f, poseStack, multiBufferSource, i, skull, renderType)
        }

        poseStack.popPose()
        super.render(grave, f, g, poseStack, multiBufferSource, i)
    }

    override fun getTextureLocation(entity: Grave?): ResourceLocation = TextureAtlas.LOCATION_BLOCKS
}