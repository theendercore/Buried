package org.teamvoided.template.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.Level
import org.teamvoided.template.Template.log
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

    var xp = 0

    override fun canCollideWith(entity: Entity): Boolean = Boat.canVehicleCollide(this, entity)
    override fun canBeCollidedWith(): Boolean = true
    override fun isPushable(): Boolean = true
    override fun push(entity: Entity) {
        if (entity is Boat) {
            if (entity.boundingBox.minY < this.boundingBox.maxY) {
                super.push(entity)
            }
        } else if (entity.boundingBox.minY <= this.boundingBox.minY) {
            super.push(entity)
        }
    }


    override fun isPickable(): Boolean = true

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(OWNER, Optional.empty())
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        if (nbt.contains("owner")) ownerUUID = Optional.of(nbt.getUUID("owner"))
        if (nbt.contains("xp")) xp = nbt.getInt("xp")
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        if (ownerUUID.isPresent) nbt.putUUID("owner", ownerUUID.get())
        if (xp != 0) nbt.putInt("xp", xp)
    }

    override fun canUsePortal(bl: Boolean): Boolean = true

    override fun interact(player: Player, interactionHand: InteractionHand): InteractionResult {
        val interactionResult = super.interact(player, interactionHand)
        if (interactionResult != InteractionResult.PASS) {
            return interactionResult
        } else if (player.isSecondaryUseActive) {
//            println(xp)
//            println(ownerUUID)
            return InteractionResult.PASS
        }

        player.giveExperiencePoints(xp)
        this.discard()
        return InteractionResult.SUCCESS
    }

    companion object {
        val OWNER: EntityDataAccessor<Optional<UUID>> =
            SynchedEntityData.defineId<Optional<UUID>>(Grave::class.java, EntityDataSerializers.OPTIONAL_UUID)

        @JvmStatic
        fun createGrave(player: Player): Grave {
            val level = player.level()
            log.info(
                "Creating a grave for {} at {} {}",
                player.name.string, level.dimension().location(), player.position()
            )
            val grave = Grave(player.level())
            grave.setOwner(player)
            grave.setPos(player.position())
//            grave.setItems()
            grave.xp = player.totalExperience
            level.addFreshEntity(grave)
            player.experienceProgress = 0f
            player.experienceLevel = 0
            player.totalExperience = 0
            return grave
        }
    }
}