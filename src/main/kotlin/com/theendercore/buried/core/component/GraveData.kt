package com.theendercore.buried.core.component

import com.mojang.serialization.Codec
import net.minecraft.world.entity.player.Player
import com.theendercore.buried.entity.Grave
import com.theendercore.buried.init.BRegistries.GRAVE_DATA_TYPE

interface GraveData {
    fun extract(player: Player)
    fun destroy(grave: Grave)
    fun getType(): GraveDataType<out GraveData>

    companion object {
        val CODEC: Codec<GraveData> =
            GRAVE_DATA_TYPE.byNameCodec().dispatch(GraveData::getType, GraveDataType<out GraveData>::codec)
    }
}
