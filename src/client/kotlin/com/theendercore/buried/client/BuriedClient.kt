package com.theendercore.buried.client

import com.theendercore.buried.client.gui.screens.inventory.GraveScreen
import com.theendercore.buried.client.renderer.entity.GraveRenderer
import com.theendercore.buried.init.BEntities
import com.theendercore.buried.init.BMenus
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.gui.screens.MenuScreens

@Suppress("unused")
object BuriedClient {
    fun init() {
        EntityRendererRegistry.register(BEntities.GRAVE, ::GraveRenderer)
        MenuScreens.register(BMenus.GRAVE_MENU, ::GraveScreen)
    }
}