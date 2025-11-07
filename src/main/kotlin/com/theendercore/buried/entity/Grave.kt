package com.theendercore.buried.entity

import com.theendercore.buried.Buried.log
import com.theendercore.buried.init.BAttachmentTypes
import com.theendercore.buried.init.BEntities
import com.theendercore.buried.init.BGraveData
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.*

@Suppress("UnstableApiUsage")
class Grave(entityType: EntityType<out Entity>, level: Level) : Entity(entityType, level) {
    constructor(level: Level) : this(BEntities.GRAVE, level)

    init {
        setAttached(BAttachmentTypes.GRAVE_DATA, listOf())
    }

    var cachedOwner: Entity? = null
        get() {
            return if (field != null && !field!!.isRemoved) field
            else if (ownerUUID.isPresent && level() is ServerLevel) {
                field = (level() as ServerLevel).getEntity(ownerUUID.get())
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


    fun isOwner(entity: Entity): Boolean =
        if (cachedOwner != null) cachedOwner == entity else ownerUUID.isPresent && ownerUUID.get() == entity.uuid


    var creationTime: Long
        get() = entityData.get(CREATION_TIME)
        set(value) = entityData.set(CREATION_TIME, value)

    override fun getDefaultGravity(): Double = 0.05
    override fun canCollideWith(entity: Entity): Boolean = Boat.canVehicleCollide(this, entity)
    override fun canBeCollidedWith(): Boolean = true
    override fun isPushable(): Boolean = true
    override fun push(entity: Entity) {
        if (entity is Boat) {
            if (entity.boundingBox.minY < boundingBox.maxY) {
                super.push(entity)
            }
        } else if (entity.boundingBox.minY <= boundingBox.minY) {
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
        val superResult = super.interact(player, hand)

        return if (superResult != InteractionResult.PASS) superResult
        else if (interactWithGrave(player, hand)) InteractionResult.SUCCESS
        else InteractionResult.PASS
    }

    fun interactWithGrave(player: Player, hand: InteractionHand): Boolean {
        if (player !is ServerPlayer) return false
        if (hand != InteractionHand.MAIN_HAND) return false

        if (player.isSecondaryUseActive && player.getItemInHand(hand).isEmpty && isOwner(player)) {
            getAttachedOrThrow(BAttachmentTypes.GRAVE_DATA).forEach { it.extract(player) }
            discard()
            return true
        }

        if (!player.isSecondaryUseActive) {
            player.sendSystemMessage(Component.literal("Open Inventory"))
            return true
        }

        return false
    }

    override fun hurt(damage: DamageSource, amount: Float): Boolean {
        if (isRemoved) return true
        markHurt()
        val player = damage.entity
        if (player is Player && player.isSecondaryUseActive && (isOwner(player) || player.isCreative)) {
            destroy()
            return true
        }

        return false
    }

    fun destroy() {
        getAttachedOrThrow(BAttachmentTypes.GRAVE_DATA).forEach { it.destroy(this) }
        discard()
    }

    override fun tick() {
        super.tick()

        moveGrave();
    }

    fun setFluidMovement() {
        val vec3 = deltaMovement
        setDeltaMovement(vec3.x * 0.96f, vec3.y + (if (vec3.y < 0.06f) 0.0005f else 0.0f), vec3.z * 0.96f)
    }

    private fun moveGrave() {
        level().profiler.push("grave_move")
        if (isInWater || isInLava) setFluidMovement()
        else applyGravity()

        if (y <= level().minBuildHeight) {
            setPos(x, level().minBuildHeight + 1.0, z)
            deltaMovement = Vec3(deltaMovement.x * 0.01, 0.0, deltaMovement.z * 0.01)
        }
        move(MoverType.SELF, deltaMovement)

        var f = 0.99
        if (onGround()) {
            f *= level().getBlockState(blockPosBelowThatAffectsMyMovement).block.getFriction()
        }

        deltaMovement = deltaMovement.multiply(f, 1.0, f)
        level().profiler.pop()
    }

    companion object {
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
            var pos = player.position()

            if (level.minBuildHeight >= pos.y) {
                pos = Vec3(pos.x, level.minBuildHeight + 1.0, pos.z)
            }

            log.info(
                "Creating a grave for {} at {} {}",
                player.name.string, level.dimension().location(), pos
            )

            val grave = Grave(player.level())
            grave.setOwner(player)
            grave.yRot = player.yRot

            grave.setPos(pos)
            level.addFreshEntity(grave)

            val list = BGraveData.FACTORY_MAP.values.map { it.create(player) }.toList()
            grave.setAttached(BAttachmentTypes.GRAVE_DATA, list)
        }
    }
}