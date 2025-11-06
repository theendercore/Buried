package com.theendercore.buried

import com.mojang.brigadier.context.CommandContext
import com.theendercore.buried.config.BuriedConfig
import com.theendercore.buried.init.BAttachmentTypes
import com.theendercore.buried.init.BEntities
import com.theendercore.buried.init.BGraveData
import com.theendercore.buried.init.BRegistries
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
        log.info("Hello from Common")
        BRegistries.init()
        BGraveData.init()
        BAttachmentTypes.init()
        BEntities.init()

        if (isDev()) {
            CommandRegistrationCallback.EVENT.register { dispatcher, ctx, _ ->
                dispatcher.root.addChild(literal("grave").executes(::grave).build())
            }
        }
    }

    fun grave(cx: CommandContext<CommandSourceStack>): Int {
        val src = cx.source ?: return -1
        val world = src.level ?: return -1
        val player = src.player ?: return -1

        return 0
    }

    fun isDev() = FabricLoader.getInstance().isDevelopmentEnvironment

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)
}
