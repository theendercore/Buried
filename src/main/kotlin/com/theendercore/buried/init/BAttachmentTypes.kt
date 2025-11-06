package com.theendercore.buried.init

import com.theendercore.buried.Buried.id
import com.theendercore.buried.core.component.GraveData
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType

object BAttachmentTypes {
    fun init() = Unit

    @JvmField
    val GRAVE_DATA: AttachmentType<List<GraveData>> = AttachmentRegistry.create(id("grave_data")) {
        it.initializer { listOf() }.persistent(GraveData.CODEC.listOf())
    }
}