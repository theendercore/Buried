package com.theendercore.buried.core.component

import com.mojang.serialization.MapCodec

fun interface GraveDataType<T : GraveData> {
    fun codec(): MapCodec<T>
}