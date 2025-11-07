package com.theendercore.buried.core.component

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.theendercore.buried.entity.Grave
import com.theendercore.buried.init.BGraveData
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3


data class InventoryData(val inventory: Map<Int, ItemStack>) : GraveData {
    override fun extract(player: Player) {
        val inv = player.inventory
        val list = mutableListOf<ItemStack>()
        for ((slot, stack) in inventory) {
            val existingStack = inv.getItem(slot)
            if (!existingStack.isEmpty) {
                list.add(existingStack)
            }
            inv.setItem(slot, stack)
        }
        inv.setChanged()
        for (stack in list) {
            if (!inv.add(stack)) {
                spawnItem(player.level(), player.position(), stack)
            }
        }
    }

    override fun destroy(grave: Grave) {
        val level = grave.level()
        for (item in inventory) {
            spawnItem(level, grave.position(), item.value)
        }
    }

    private fun spawnItem(level: Level, grave: Vec3, stack: ItemStack) {
        val item = ItemEntity(level, grave.x, grave.y + 0.25, grave.z, stack)
        item.setPickUpDelay(4)
        item.deltaMovement = Vec3(0.0, 0.1, 0.0)
        level.addFreshEntity(item)
    }


    override fun getType(): GraveDataType<out GraveData> = BGraveData.INVENTORY

    companion object {
        val CODEC: MapCodec<InventoryData> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, Int::toString), ItemStack.OPTIONAL_CODEC)
                    .fieldOf("inventory").forGetter(InventoryData::inventory)
            ).apply(it, ::InventoryData)
        }

        fun create(player: Player): InventoryData {
            val list = mutableMapOf<Int, ItemStack>()
            val inventory = player.getInventory()

            for (i in 0..<inventory.containerSize) {
                val itemStack = inventory.removeItemNoUpdate(i)
                if (!itemStack.isEmpty) {
                    list[i] = itemStack
                }
            }

            return InventoryData(list)
        }
    }
}
