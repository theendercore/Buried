package org.teamvoided.template.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import org.teamvoided.template.init.BEntities
import java.util.*

class Grave(entityType: EntityType<*>, level: Level) : Entity(entityType, level) {
    constructor(level: Level) : this(BEntities.GRAVE, level)

    var entityLevel: Level? = level()
    var cachedOwner: Entity? = null
        get() {
            return if (field != null && !field!!.isRemoved) field
            else if (ownerUUID.isPresent && entityLevel is ServerLevel) {
                field = (entityLevel as ServerLevel).getEntity(ownerUUID.get())
                field
            } else null
        }

    var ownerUUID: Optional<UUID>
        get() = entityData.get(OWNER)
        set(value) = entityData.set(OWNER, value)


    fun setOwner(entity: Entity) {
        cachedOwner = entity
        ownerUUID = Optional.of(entity.uuid)
    }


    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(OWNER, Optional.empty())
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        if (nbt.contains("owner")) ownerUUID = Optional.of(nbt.getUUID("owner"))
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        if (ownerUUID.isPresent) nbt.putUUID("owner", ownerUUID.get())
    }

    companion object {
        val OWNER: EntityDataAccessor<Optional<UUID>> =
            SynchedEntityData.defineId<Optional<UUID>>(Grave::class.java, EntityDataSerializers.OPTIONAL_UUID)

        fun createGrave(player: Player): Grave {
            val grave = Grave(player.level())
//            grave.setOwner(player)
            grave.setPos(player.position())
            return grave
        }
    }
}