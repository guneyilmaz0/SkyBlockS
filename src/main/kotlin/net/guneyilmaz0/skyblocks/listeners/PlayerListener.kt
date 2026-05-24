package net.guneyilmaz0.skyblocks.listeners

import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.island.Island
import org.allaymc.api.eventbus.EventHandler
import org.allaymc.api.eventbus.event.server.PlayerJoinEvent
import org.allaymc.api.eventbus.event.server.PlayerQuitEvent
import org.allaymc.api.scheduler.Task
import org.allaymc.api.server.Server

class PlayerListener {

    @EventHandler
    fun onPlayerLogin(event: PlayerJoinEvent) {
        if (!event.isCancelled) Session.get(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val session = Session.get(event.player)
        val islandId = session.islandId
        session.close()
        if (islandId == null) return
        Server.getInstance().scheduler.scheduleDelayed(SkyBlockS.instance, Task {
            Server.getInstance().worldPool.getWorld(islandId) ?: return@Task false
            if (Island.get(islandId).getOnlineMembers().isNotEmpty()) return@Task false
            Server.getInstance().worldPool.unloadWorld(islandId)
            Island.get(islandId).close()
            return@Task true
        }, 1)
    }

}