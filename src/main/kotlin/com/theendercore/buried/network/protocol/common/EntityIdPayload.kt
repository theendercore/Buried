package com.theendercore.buried.network.protocol.common

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

@JvmRecord
data class EntityIdPayload(val id: Int) {
    companion object {
        var CODEC: StreamCodec<FriendlyByteBuf, EntityIdPayload> =
            StreamCodec.composite(ByteBufCodecs.INT, EntityIdPayload::id, ::EntityIdPayload)
    }
}

