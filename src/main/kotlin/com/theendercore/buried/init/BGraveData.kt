package com.theendercore.buried.init

import com.mojang.serialization.MapCodec
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import com.theendercore.buried.Buried.id
import com.theendercore.buried.core.component.GraveData
import com.theendercore.buried.core.component.GraveDataType
import com.theendercore.buried.core.component.InventoryData
import com.theendercore.buried.core.component.XpData

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