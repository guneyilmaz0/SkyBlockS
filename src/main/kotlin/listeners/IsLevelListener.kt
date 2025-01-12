package net.guneyilmaz0.skyblocks.listeners

import cn.nukkit.Server
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.tasks.IslandXPTask

@Suppress("unused")
class IsLevelListener : Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return
        if (!SkyBlockS.provider.isIslandExists(event.block.level.folderName)) return
        Server.getInstance().scheduler.scheduleAsyncTask(SkyBlockS.instance, IslandXPTask(event.block.level, -1))
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.isCancelled) return
        if (!SkyBlockS.provider.isIslandExists(event.block.level.folderName)) return
        Server.getInstance().scheduler.scheduleAsyncTask(SkyBlockS.instance, IslandXPTask(event.block.level, 1))
    }

}