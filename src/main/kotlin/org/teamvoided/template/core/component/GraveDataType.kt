package org.teamvoided.template.core.component

import com.mojang.serialization.Codec
import net.minecraft.world.entity.player.Player
import org.teamvoided.template.entity.Grave
import org.teamvoided.template.init.BRegistries

interface GraveDataType<T> {
    fun create(player: Player): T
    fun extract(data: T, player: Player)
    fun destroy(data: T, grave: Grave)
    fun isSynced(): Boolean = false
    fun codec(): Codec<T>

    companion object {
        val CODEC: Codec<GraveDataType<*>> = Codec.lazyInitialized { BRegistries.GRAVE_DATA.byNameCodec() }
        val VALUE_MAP_CODEC: Codec<Map<GraveDataType<*>, Any>> = Codec.dispatchedMap(CODEC) { it.codec() }
    }
}
