package net.guneyilmaz0.skyblocks.listeners

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.player.PlayerInteractEvent
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.objects.IslandData

@Suppress("unused")
class ProtectionListener : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onBreakBlock(event: BlockBreakEvent) {
        handleEvent(event.player, event)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlaceBlock(event: BlockPlaceEvent) {
        handleEvent(event.player, event)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onTouch(event: PlayerInteractEvent) {
        handleEvent(event.player, event)
    }

    private fun handleEvent(player: Player, event: Event) {
        if (event.isCancelled || player.isOp || !IslandData.isIslandExists(player.getLevel().folderName)) return

        val island: Island = Island.get(player.getLevel().folderName)
        if (island.isMember(player.name) || player.hasPermission("skyblocks.touch.island"))
            event.isCancelled = false
        else event.setCancelled()
    }
}