package org.teamvoided.template.init

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder.createSimple
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.teamvoided.template.Template.id
import org.teamvoided.template.core.component.GraveDataType

object BRegistries {
    fun init() = Unit
    val GRAVE_DATA_KEY = key<GraveDataType<*>>("grave_data")
    val GRAVE_DATA: MappedRegistry<GraveDataType<*>> = create(GRAVE_DATA_KEY)


    private fun <T> key(id: String): ResourceKey<Registry<T>> = ResourceKey.createRegistryKey(id(id))
    fun <T> create(key: ResourceKey<Registry<T>>): MappedRegistry<T> = createSimple(key).buildAndRegister()
}