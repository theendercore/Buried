package com.theendercore.buried.world.inventory

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class GraveSlot(container: Container, id: Int, x: Int, y: Int) : Slot(container, id, x, y) {
    override fun mayPlace(itemStack: ItemStack): Boolean = false
}