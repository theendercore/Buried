package com.theendercore.buried.data.gen

import com.theendercore.buried.init.BEntities
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import java.util.concurrent.CompletableFuture

@Suppress("unused")
object BuriedData : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()

        pack.addProvider(::EnglishTranslationProvider)
    }

    override fun buildRegistry(gen: RegistrySetBuilder) {
//        gen.add(RegistryKeys.BIOME, TemplateBiomes::boostrap)
    }

    class EnglishTranslationProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) :
        FabricLanguageProvider(o, r) {

        override fun generateTranslations(lookup: HolderLookup.Provider, gen: TranslationBuilder) {
            BEntities.GRAVE.let { gen.add(it.getDescriptionId(), genLang(it.id)) }
        }

        private fun genLang(identifier: ResourceLocation): String =
            identifier.path.split("_").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

        val EntityType<*>.id get() = BuiltInRegistries.ENTITY_TYPE.getKey(this)
    }
}
