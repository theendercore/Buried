package org.teamvoided.template.core.component

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.player.Player
import org.teamvoided.template.entity.Grave
import org.teamvoided.template.init.BGraveData

data class XpData(val xp: Int) : GraveData {
    override fun extract(player: Player) {
        player.giveExperiencePoints(xp)
    }

    override fun destroy(grave: Grave) {
        val level = grave.level()
        if (level is ServerLevel) {
            ExperienceOrb.award(level, grave.position(), xp)
        }
    }

    override fun getType(): GraveDataType<out GraveData> = BGraveData.XP

    companion object {
        val CODEC: MapCodec<XpData> =
            RecordCodecBuilder.mapCodec { it.group(Codec.INT.fieldOf("xp").forGetter(XpData::xp)).apply(it, ::XpData) }

        fun create(player: Player): XpData {
            val xp = XpData(player.totalExperience)
            player.experienceProgress = 0f
            player.experienceLevel = 0
            player.totalExperience = 0

            return xp
        }
    }
}
