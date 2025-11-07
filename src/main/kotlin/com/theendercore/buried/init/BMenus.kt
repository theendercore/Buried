package com.theendercore.buried.init

import com.theendercore.buried.Buried.id
import com.theendercore.buried.network.protocol.common.EntityIdPayload
import com.theendercore.buried.world.inventory.GraveMenu
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType.ExtendedFactory
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.inventory.AbstractContainerMenu

object BMenus {
    fun init() = Unit
    val GRAVE_MENU = register("grave", ::GraveMenu, EntityIdPayload.CODEC)


    fun <T : AbstractContainerMenu, D> register(
        name: String, menu: ExtendedFactory<T, D>, codec: StreamCodec<in RegistryFriendlyByteBuf, D>,
    ): ExtendedScreenHandlerType<T, D> {
        return Registry.register(BuiltInRegistries.MENU, id(name), ExtendedScreenHandlerType<T, D>(menu, codec))
    }
}