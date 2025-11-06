package org.teamvoided.template.entity

import com.mojang.serialization.JsonOps
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
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
import org.teamvoided.template.core.component.GraveDataMap
import org.teamvoided.template.init.BAttachmentTypes
import org.teamvoided.template.init.BEntities
import org.teamvoided.template.init.BRegistries
import java.util.*

@Suppress("UnstableApiUsage")
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

    var creationTime: Long
        get() = entityData.get(CREATION_TIME)
        set(value) = entityData.set(CREATION_TIME, value)

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
        builder.define(CREATION_TIME, 0L)
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        if (nbt.contains(OWNER_KEY)) ownerUUID = Optional.of(nbt.getUUID(OWNER_KEY))
        if (nbt.contains(CREATION_TIME_KEY)) creationTime = nbt.getLong(CREATION_TIME_KEY)
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        if (ownerUUID.isPresent) nbt.putUUID(OWNER_KEY, ownerUUID.get())
        if (creationTime != 0L) nbt.putLong(CREATION_TIME_KEY, creationTime)
    }

    override fun canUsePortal(bl: Boolean): Boolean = true

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        val interactionResult = super.interact(player, hand)
        if (interactionResult != InteractionResult.PASS) {
            return interactionResult
        } else if (player.isSecondaryUseActive) {
            val nbt = CompoundTag()
            addAdditionalSaveData(nbt)
            player.sendSystemMessage(Component.literal("Nbt: $nbt"))
            return InteractionResult.PASS
        } else if (player.getItemInHand(hand).isEmpty) {
            getAttached(BAttachmentTypes.SERVER_GRAVE_DATA)?.forEach { (type, data) ->
                type.extract(data, player)
            }
            getAttached(BAttachmentTypes.SYNCED_GRAVE_DATA)?.forEach { (type, data) ->
                type.extract(data, player)
            }

            discard()
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }

    companion object {
        const val XP_KEY = "xp"
        const val OWNER_KEY = "owner"
        const val CREATION_TIME_KEY = "creation_time"

        val OWNER: EntityDataAccessor<Optional<UUID>> =
            SynchedEntityData.defineId<Optional<UUID>>(Grave::class.java, EntityDataSerializers.OPTIONAL_UUID)
        val CREATION_TIME: EntityDataAccessor<Long> =
            SynchedEntityData.defineId<Long>(Grave::class.java, EntityDataSerializers.LONG)

        @JvmStatic
        fun createGrave(player: Player) {
            val level = player.level()
            if (level.isClientSide) return
            val pos = player.position()

            log.info(
                "Creating a grave for {} at {} {}",
                player.name.string, level.dimension().location(), pos
            )

            val server = GraveDataMap()
            val synced = GraveDataMap()

            for (key in BRegistries.GRAVE_DATA) {
                val data = key.create(player)
                if (key.isSynced()) {
                    synced.set(key, data)
                } else {
                    server.set(key, data)
                }
            }


            GraveDataMap.CODEC.encodeStart(JsonOps.INSTANCE, server).ifSuccess(::println).ifError(::println)

            val grave = Grave(player.level())
            grave.setOwner(player)
            grave.setPos(pos)
            level.addFreshEntity(grave)

            grave.setAttached(BAttachmentTypes.SERVER_GRAVE_DATA, server)
            grave.setAttached(BAttachmentTypes.SYNCED_GRAVE_DATA, synced)
        }
    }
}