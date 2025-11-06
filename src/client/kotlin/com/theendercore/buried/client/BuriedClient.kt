package com.theendercore.buried.client

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import com.theendercore.buried.client.renderer.entity.GraveRenderer
import com.theendercore.buried.init.BEntities

@Suppress("unused")
object BuriedClient {
    fun init() {
        EntityRendererRegistry.register(BEntities.GRAVE, ::GraveRenderer)
    }
}