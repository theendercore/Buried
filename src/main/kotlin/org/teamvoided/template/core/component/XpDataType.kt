package org.teamvoided.template.core.component

import com.mojang.serialization.Codec
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.player.Player
import org.teamvoided.template.entity.Grave

class XpDataType : GraveDataType<Int> {
    override fun create(player: Player): Int {
        val xp = player.totalExperience
        player.experienceProgress = 0f
        player.experienceLevel = 0
        player.totalExperience = 0

        return xp
    }

    override fun extract(data: Int, player: Player) {
        player.giveExperiencePoints(data)
    }

    override fun destroy(data: Int, grave: Grave) {
        val level = grave.level()
        if (level is ServerLevel) {
            ExperienceOrb.award(level, grave.position(), data)
        }
    }

    override fun isSynced(): Boolean = false
    override fun codec(): Codec<Int> = Codec.INT
}
