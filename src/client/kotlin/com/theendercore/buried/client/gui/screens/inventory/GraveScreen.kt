package com.theendercore.buried.client.gui.screens.inventory

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.theendercore.buried.Buried.id
import com.theendercore.buried.world.inventory.GraveMenu
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.atan


class GraveScreen(handler: GraveMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<GraveMenu>(handler, inventory, title) {

    init {
        imageWidth = 176
        imageHeight = 253
        titleLabelX += 70
        inventoryLabelY += 89
    }

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.render(guiGraphics, i, j, f)
        renderTooltip(guiGraphics, i, j)
    }

    override fun renderBg(gui: GuiGraphics, delta: Float, mouseX: Int, mouseY: Int) {
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        gui.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight)
        renderEntityInInventoryFollowsMouse(
            gui, leftPos + 26, topPos + 8, leftPos + 75, topPos + 78, 30, 0.0625f,
            mouseX.toFloat(), mouseY.toFloat(), menu.grave
        )

    }

    companion object {
        val BACKGROUND = id("textures/gui/container/grave.png")

        fun renderEntityInInventoryFollowsMouse(
            guiGraphics: GuiGraphics,
            i: Int,
            j: Int,
            k: Int,
            l: Int,
            m: Int,
            f: Float,
            g: Float,
            h: Float,
            entity: Entity,
        ) {
            val n = (i + k) / 2.0f
            val o = (j + l) / 2.0f
            guiGraphics.enableScissor(i, j, k, l)
            val p = atan(((n - g) / 40.0f).toDouble()).toFloat()
            val q = atan(((o - h) / 40.0f).toDouble()).toFloat()
            val rot1 = Quaternionf().rotateZ(Math.PI.toFloat())
            val rot2 = Quaternionf().rotateX(q * 20.0f * (Math.PI / 180.0).toFloat())
            rot1.mul(rot2)
            val yRot = entity.yRot
            val xRot = entity.xRot
            entity.yRot = 180.0f + p * 40.0f
            entity.xRot = -q * 20.0f
            entity.yHeadRot = entity.yRot
            val scale = 1f
            val vector3f = Vector3f(0.0f, entity.bbHeight / 2.0f + f * scale, 0.0f)
            val x = m / scale
            renderEntityInInventory(guiGraphics, n, o, x, vector3f, rot1, rot2, entity)
            guiGraphics.disableScissor()
            entity.yRot = yRot
            entity.xRot = xRot
        }

        fun renderEntityInInventory(
            guiGraphics: GuiGraphics, x: Float, y: Float, scale: Float, vector3f: Vector3f,
            rot1: Quaternionf, rot2: Quaternionf, entity: Entity,
        ) {
            guiGraphics.pose().pushPose()
            guiGraphics.pose().translate(x.toDouble(), y.toDouble(), 50.0)
            guiGraphics.pose().scale(scale, scale, -scale)
            guiGraphics.pose().translate(vector3f.x, vector3f.y, vector3f.z)
            guiGraphics.pose().mulPose(rot1)
            Lighting.setupForEntityInInventory()
            val dispatch = Minecraft.getInstance().entityRenderDispatcher
            dispatch.overrideCameraOrientation(
                rot2.conjugate(Quaternionf()).rotateY(Math.PI.toFloat())
            )

            dispatch.setRenderShadow(false)
            RenderSystem.runAsFancy {
                dispatch
                    .render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880)
            }
            guiGraphics.flush()
            dispatch.setRenderShadow(true)
            guiGraphics.pose().popPose()
            Lighting.setupFor3DItems()
        }
    }
}
