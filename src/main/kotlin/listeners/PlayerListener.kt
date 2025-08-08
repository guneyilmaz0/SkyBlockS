package net.guneyilmaz0.skyblocks.listeners

import cn.nukkit.Server
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerFormRespondedEvent
import cn.nukkit.event.player.PlayerLoginEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.form.window.FormWindowModal
import cn.nukkit.scheduler.NukkitRunnable
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.island.IslandManager

@Suppress("unused")
class PlayerListener : Listener {

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        if (!event.isCancelled) Session.get(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val session = Session.get(event.player)
        session.close()
        if (session.getIsland() == null) return
        Server.getInstance().scheduler.scheduleDelayedTask(SkyBlockS.instance, object : NukkitRunnable() {
            override fun run() {
                val level = Server.getInstance().getLevelByName(session.islandId) ?: return
                if (Island.get(session.islandId!!).getOnlineMembers().isNotEmpty()) return
                Server.getInstance().unloadLevel(level)
                session.getIsland()!!.close()
            }
        }, 1)
    }

    @EventHandler
    fun onFormResponded(event: PlayerFormRespondedEvent) {
        if (event.formID != "delete_island".hashCode() || event.response == null) return
        val form: FormWindowModal = event.window as FormWindowModal
        if (form.response.clickedButtonId != 0) return
        IslandManager.deleteIsland(event.player)
    }
}