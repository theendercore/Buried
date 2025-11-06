package org.teamvoided.template.init

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.teamvoided.template.Template.id
import org.teamvoided.template.entity.Grave

object BEntities {

    val GRAVE = register(
        "grave",
        EntityType.Builder.of(::Grave, MobCategory.MONSTER)
            .fireImmune()
            .sized(1f, 1f)
            .eyeHeight(0.5f)
            .passengerAttachments(1f)
    )


    fun init() {
    }


    public fun <T : Entity> register(id: String, entityType: EntityType.Builder<T>): EntityType<T> {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id(id), entityType.build(id))
    }
}