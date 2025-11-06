package org.teamvoided.template

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.teamvoided.template.config.TemplateConfig

@Suppress("unused")
object Template {
    const val MODID = "template"

    @JvmField
    val log: Logger = LoggerFactory.getLogger(Template::class.simpleName)

    @JvmField
    var config = ConfigApi.registerAndLoadConfig(::TemplateConfig)

    fun init() {
        log.info("Hello from Common")
    }

    fun id(path: String) = ResourceLocation.fromNamespaceAndPath(MODID, path)
}
