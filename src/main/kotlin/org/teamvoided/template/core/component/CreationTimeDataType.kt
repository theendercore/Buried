package org.teamvoided.template.core.component

import com.mojang.serialization.Codec
import net.minecraft.world.entity.player.Player
import org.teamvoided.template.entity.Grave

class CreationTimeDataType : GraveDataType<Long> {
    override fun create(player: Player): Long = player.level().gameTime
    override fun extract(data: Long, player: Player) = Unit
    override fun destroy(data: Long, grave: Grave) = Unit
    override fun isSynced(): Boolean = true
    override fun codec(): Codec<Long> = Codec.LONG
}
