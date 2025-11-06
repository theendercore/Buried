package org.teamvoided.template.core.component

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.teamvoided.template.entity.Grave
import org.teamvoided.template.init.BGraveData


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
                player.drop(stack, false)
            }
        }
    }

    override fun destroy(grave: Grave) {
        val level = grave.level()
        if (level is ServerLevel) {
            for ((_, stack) in inventory) {
                println(stack)
            }
        }
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
                val itemStack = inventory.getItem(i)
                if (!itemStack.isEmpty) {
                    list[i] = itemStack
                }
            }
            inventory.clearContent()
            return InventoryData(list)
        }
    }
}
