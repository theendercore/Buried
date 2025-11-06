package com.theendercore.buried.init

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import com.theendercore.buried.Buried.id
import com.theendercore.buried.core.component.GraveData

@Suppress("UnstableApiUsage")
object BAttachmentTypes {
    fun init() = Unit

    @JvmField
    val GRAVE_DATA: AttachmentType<List<GraveData>> =
        AttachmentRegistry.createPersistent(id("grave_data"), GraveData.CODEC.listOf())
}