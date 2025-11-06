package org.teamvoided.template.init

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import org.teamvoided.template.Template.id
import org.teamvoided.template.core.component.CreationTimeDataType
import org.teamvoided.template.core.component.GraveDataType
import org.teamvoided.template.core.component.XpDataType

object BGraveData {
    fun init() = Unit

    val XP = register("xp", XpDataType())
    val CREATION_TIME = register("creation_time", CreationTimeDataType())

    internal fun <T> register(id: String, data: GraveDataType<T>): GraveDataType<T> = register(id(id), data)
    fun <T> register(id: ResourceLocation, data: GraveDataType<T>): GraveDataType<T> =
        Registry.register(BRegistries.GRAVE_DATA, id, data)
}