package net.guneyilmaz0.skyblocks.events

import cn.nukkit.Server
import cn.nukkit.event.plugin.PluginEvent
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.island.Island

//Don't change variables when listening this event because it won't be processed.
class IslandExperienceChangedEvent(
    val island: Island,
    val oldLevel: Int,
    var newLevel: Int,
    var newXp: Int
) : PluginEvent(SkyBlockS.instance) {
    fun call() = Server.getInstance().pluginManager.callEvent(this)
}