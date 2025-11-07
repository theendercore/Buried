package com.theendercore.buried.world.inventory

import com.mojang.datafixers.util.Pair
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.inventory.InventoryMenu

class CustomArmorSlot(
    container: Container, id: Int, x: Int, y: Int, val emptyIcon: ResourceLocation?,
) : GraveSlot(container, id, x, y) {
    override fun getMaxStackSize(): Int = 1

    override fun getNoItemIcon(): Pair<ResourceLocation, ResourceLocation>? {
        return if (this.emptyIcon != null) Pair.of(InventoryMenu.BLOCK_ATLAS, this.emptyIcon) else super.getNoItemIcon()
    }
}

