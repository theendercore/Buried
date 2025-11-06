package org.teamvoided.template.client.renderer.entity

import com.mojang.authlib.properties.PropertyMap
import com.mojang.blaze3d.vertex.PoseStack
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
import org.teamvoided.template.entity.Grave
import java.util.*

class GraveRenderer(context: EntityRendererProvider.Context) : EntityRenderer<Grave>(context) {
    var model = SkullModel(context.modelSet.bakeLayer(ModelLayers.PLAYER_HEAD))
    override fun render(
        grave: Grave, f: Float, g: Float, poseStack: PoseStack, multiBufferSource: MultiBufferSource, i: Int,
    ) {
        if (grave.ownerUUID.isEmpty) return

        poseStack.pushPose()
        poseStack.translate(0f, 0.25f, 0f)
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation())
        poseStack.translate(-0.5, 0.0, -0.5)
        val resolvableProfile = ResolvableProfile(Optional.empty(), Optional.of(grave.ownerUUID.get()), PropertyMap()).resolve().get()
        val renderType = SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, resolvableProfile)

        SkullBlockRenderer.renderSkull(null, 180.0f, 0f, poseStack, multiBufferSource, i, model, renderType)

        poseStack.popPose()
        super.render(grave, f, g, poseStack, multiBufferSource, i)
    }

    override fun getTextureLocation(entity: Grave?): ResourceLocation = TextureAtlas.LOCATION_BLOCKS
}