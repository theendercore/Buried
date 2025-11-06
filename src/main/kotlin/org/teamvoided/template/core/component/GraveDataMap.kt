package org.teamvoided.template.core.component

import com.google.common.collect.Iterators
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
import org.teamvoided.template.core.component.GraveDataMap.Companion.TypedGraveData

@Suppress("UNCHECKED_CAST")
class GraveDataMap(val map: Reference2ObjectMap<GraveDataType<*>, Any>) : Iterable<TypedGraveData<Any>> {
    constructor(map: Map<GraveDataType<*>, Any>) : this(Reference2ObjectOpenHashMap(map))
    constructor() : this(Reference2ObjectOpenHashMap())

    fun has(type: GraveDataType<*>): Boolean = map.containsKey(type)
    fun <T> get(type: GraveDataType<out T>): T = map[type] as T
    fun <T> set(type: GraveDataType<out T>, data: T) {
        map[type] = data
    }

    val size: Int = map.size
    val keys: Set<GraveDataType<*>> = map.keys
    fun isEmpty(): Boolean = map.isEmpty()
    override fun iterator(): Iterator<TypedGraveData<Any>> =
        Iterators.transform(Reference2ObjectMaps.fastIterator(map)) { TypedGraveData.fromEntryUnchecked(it) }


    override fun toString(): String = map.toString()

    companion object {
        val CODEC: Codec<GraveDataMap> = makeCodecFromMap(GraveDataType.VALUE_MAP_CODEC)

        fun makeCodecFromMap(codec: Codec<Map<GraveDataType<*>, Any>>): Codec<GraveDataMap> {
            return codec.flatComapMap(::GraveDataMap) {
                DataResult.success(if (it.size == 0) Reference2ObjectMaps.emptyMap() else Reference2ObjectArrayMap(it.map))
            }
        }

        data class TypedGraveData<T>(val type: GraveDataType<T>, val data: T) {
            companion object {
                fun fromEntryUnchecked(entry: Map.Entry<GraveDataType<*>, Any>) =
                    createUnchecked(entry.key as GraveDataType<Any>, entry.value)

                fun <T> createUnchecked(type: GraveDataType<T>, data: Any): TypedGraveData<T> =
                    TypedGraveData(type, data as T)

            }
        }
    }
}