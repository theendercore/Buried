package com.theendercore.buried

import com.mojang.brigadier.context.CommandContext
import com.theendercore.buried.config.BuriedConfig
import com.theendercore.buried.init.*
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object Buried {
    const val MODID = "buried"

    @JvmField
    val log: Logger = LoggerFactory.getLogger(Buried::class.simpleName)

    @JvmField
    var config = ConfigApi.registerAndLoadConfig(::BuriedConfig)

    fun init() {
        log.info("Im gonna bury you!")
        BRegistries.init()
        BGraveData.init()
        BAttachmentTypes.init()
        BEntities.init()
        BMenus.init()

        if (isDev()) {
            CommandRegistrationCallback.EVENT.register { dispatcher, ctx, _ ->
                dispatcher.root.addChild(literal("grave").executes(::grave).build())
            }
        }
    }

    fun grave(cx: CommandContext<CommandSourceStack>): Int {
        return 0
    }

    fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)
}
