package org.teamvoided.template

import com.mojang.brigadier.context.CommandContext
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.teamvoided.template.config.TemplateConfig
import org.teamvoided.template.entity.Grave
import org.teamvoided.template.init.BEntities
import java.util.*

@Suppress("unused")
object Template {
    const val MODID = "template"

    @JvmField
    val log: Logger = LoggerFactory.getLogger(Template::class.simpleName)

    @JvmField
    var config = ConfigApi.registerAndLoadConfig(::TemplateConfig)

    fun init() {
        log.info("Hello from Common")
        BEntities.init()

        CommandRegistrationCallback.EVENT.register { dispatcher, ctx, _ ->
            dispatcher.root.addChild(literal("grave").executes(::grave).build())
        }
    }

    fun grave(cx: CommandContext<CommandSourceStack>): Int {
        val src = cx.source ?: return -1
        val world = src.level ?: return -1
        val player = src.player ?: return -1

        return 0
    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)
}
