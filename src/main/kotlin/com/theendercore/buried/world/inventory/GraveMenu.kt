package com.theendercore.buried.world.inventory

import com.mojang.datafixers.util.Pair
import com.theendercore.buried.core.component.InventoryData
import com.theendercore.buried.entity.Grave
import com.theendercore.buried.init.BMenus
import com.theendercore.buried.network.protocol.common.EntityIdPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.Map

class GraveMenu(syncId: Int, playerInventory: Inventory, grave: Grave) :
    AbstractContainerMenu(BMenus.GRAVE_MENU, syncId) {
    private val inventory: Container = SimpleContainer(27)
    val grave: Grave = grave

    constructor(syncId: Int, inventory: Inventory, data: EntityIdPayload) : this(
        syncId, inventory, inventory.player.level().getEntity(data.id) as Grave
    )

    init {
        val data = grave.getGraveData()?.filterIsInstance<InventoryData>()?.firstOrNull()
        if (data == null) {
            playerInventory.player.openMenu(null)
        } else {
            inventory.startOpen(playerInventory.player)

            val graveInv = GraveContainer(data)

            for (i in 0..3) {
                val equipmentSlot = SLOT_IDS[i]
                addSlot(CustomArmorSlot(graveInv, 39 - i, 8, 8 + i * 18, TEXTURE_EMPTY_SLOTS[equipmentSlot]))
            }

            for (i in 0..2) {
                for (j in 0..8) {
                    addSlot(GraveSlot(graveInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18))
                }
            }

            for (i in 0..8) {
                addSlot(GraveSlot(graveInv, i, 8 + i * 18, 142))
            }

            addSlot(object : GraveSlot(graveInv, 40, 77, 62) {
                override fun getNoItemIcon(): Pair<ResourceLocation, ResourceLocation> {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD)
                }
            })


            addPlayerInventory(playerInventory)
            addPlayerHotbar(playerInventory)
        }
    }

    override fun quickMoveStack(player: Player, invSlot: Int): ItemStack {
        var newStack: ItemStack = ItemStack.EMPTY
        val slot = slots[invSlot]
        if (slot.hasItem()) {
            val originalStack: ItemStack = slot.item
            newStack = originalStack.copy()
            if (invSlot < inventory.containerSize) {
                if (!moveItemStackTo(originalStack, inventory.containerSize, slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!moveItemStackTo(originalStack, 0, inventory.containerSize, false)) {
                return ItemStack.EMPTY
            }

            if (originalStack.isEmpty) {
                slot.setByPlayer(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }

        return newStack
    }

    override fun stillValid(player: Player): Boolean {
        return inventory.stillValid(player)
    }


    private fun addPlayerInventory(playerInventory: Inventory) {
        for (i in 0..2) {
            for (l in 0..8) {
                addSlot(Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, (INV_OFF) + 84 + i * 18))
            }
        }
    }

    override fun canDragTo(slot: Slot): Boolean {
        return slot.containerSlot == 0
    }

    override fun canTakeItemForPickAll(stack: ItemStack, slot: Slot): Boolean {
        return slot.containerSlot == 0
    }

    private fun addPlayerHotbar(playerInventory: Inventory) {
        for (i in 0..8) {
            addSlot(Slot(playerInventory, i, 8 + i * 18, (INV_OFF) + 142))
        }
    }

    companion object {
        const val INV_OFF = 87

        val TEXTURE_EMPTY_SLOTS: MutableMap<EquipmentSlot, ResourceLocation> = Map.of(
            EquipmentSlot.FEET,
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
            EquipmentSlot.LEGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            EquipmentSlot.CHEST,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            EquipmentSlot.HEAD,
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
        )
        val SLOT_IDS = arrayOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)

    }
}

