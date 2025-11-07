package com.theendercore.buried.world.inventory

import com.theendercore.buried.core.component.InventoryData
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack

class GraveContainer : SimpleContainer {
    constructor(i: Int) : super(i)
    constructor(vararg stack: ItemStack) : super(*stack)
    constructor(data: InventoryData): this(128){
        for ((slot, stack) in data.inventory) {
            setItem(slot, stack)
        }
    }


}
