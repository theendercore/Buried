package org.teamvoided.template.core.component

import com.mojang.serialization.Codec
import net.minecraft.world.entity.player.Player
import org.teamvoided.template.entity.Grave
import org.teamvoided.template.init.BRegistries.GRAVE_DATA_TYPE

interface GraveData {
    fun extract(player: Player)
    fun destroy(grave: Grave)
    fun getType(): GraveDataType<out GraveData>

    companion object {
        val CODEC: Codec<GraveData> =
            GRAVE_DATA_TYPE.byNameCodec().dispatch(GraveData::getType, GraveDataType<out GraveData>::codec)
    }
}
