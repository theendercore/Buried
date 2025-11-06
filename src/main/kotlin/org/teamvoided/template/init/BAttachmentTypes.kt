package org.teamvoided.template.init

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.network.codec.ByteBufCodecs
import org.teamvoided.template.Template.id
import org.teamvoided.template.core.component.GraveDataMap

@Suppress("UnstableApiUsage")
object BAttachmentTypes {
    fun init() = Unit

    @JvmField
    val SERVER_GRAVE_DATA: AttachmentType<GraveDataMap> =
        AttachmentRegistry.createPersistent(id("server_grave_data"), GraveDataMap.CODEC)

    @JvmField
    val SYNCED_GRAVE_DATA: AttachmentType<GraveDataMap> =
        AttachmentRegistry.create(id("synced_grave_data")) { builder ->
            builder.persistent(GraveDataMap.CODEC)
                .syncWith(ByteBufCodecs.fromCodec(GraveDataMap.CODEC), AttachmentSyncPredicate.all())
        }
}