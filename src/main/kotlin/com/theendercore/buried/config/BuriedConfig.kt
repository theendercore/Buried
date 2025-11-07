package com.theendercore.buried.config

import com.theendercore.buried.Buried.MODID
import com.theendercore.buried.Buried.id
import me.fzzyhmstrs.fzzy_config.config.Config

@Suppress("unused")
class BuriedConfig : Config(id(MODID)) {
    var quickGravePickup = true
}