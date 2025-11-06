package org.teamvoided.template.client

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import org.teamvoided.template.Template
import org.teamvoided.template.client.renderer.entity.GraveRenderer
import org.teamvoided.template.init.BEntities

@Suppress("unused")
object TemplateClient {
    fun init() {
        Template.log.info("Hello from Client")
        EntityRendererRegistry.register(BEntities.GRAVE, ::GraveRenderer)
    }
}