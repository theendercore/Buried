package org.teamvoided.template.init

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import org.teamvoided.template.Template.id
import org.teamvoided.template.core.component.GraveData

@Suppress("UnstableApiUsage")
object BAttachmentTypes {
    fun init() = Unit

    @JvmField
    val GRAVE_DATA: AttachmentType<List<GraveData>> =
        AttachmentRegistry.createPersistent(id("grave_data"), GraveData.CODEC.listOf())
}