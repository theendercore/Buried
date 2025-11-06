package org.teamvoided.template.init

import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import org.teamvoided.template.Template.id
import org.teamvoided.template.core.component.GraveData
import org.teamvoided.template.core.component.GraveDataType
import org.teamvoided.template.core.component.InventoryData
import org.teamvoided.template.core.component.XpData

object BGraveData {
    val FACTORY_MAP = mutableMapOf<GraveDataType<out GraveData>, GraveDataFactory<out GraveData>>()
    fun init() = Unit

    val XP = register("xp", XpData.CODEC, XpData::create)
    val INVENTORY = register("inventory", InventoryData.CODEC, InventoryData::create)

    fun <T : GraveData> register(id: String, codec: MapCodec<T>, factory: GraveDataFactory<T>): GraveDataType<T> =
        register(id(id), codec, factory)

    fun <T : GraveData> register(
        id: ResourceLocation, codec: MapCodec<T>, factory: GraveDataFactory<T>,
    ): GraveDataType<T> {
        val type = Registry.register(BRegistries.GRAVE_DATA_TYPE, id, GraveDataType { codec })
        FACTORY_MAP[type] = factory
        return type
    }

    fun interface GraveDataFactory<T : GraveData> {
        fun create(player: Player): T
    }
}