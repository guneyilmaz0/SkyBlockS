package net.guneyilmaz0.skyblocks.listeners

import cn.nukkit.Player
import cn.nukkit.event.Event
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.player.PlayerInteractEvent
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.island.Island

@Suppress("unused")
class ProtectionListener : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onBlockBreak(event: BlockBreakEvent) = handleEvent(event.player, event)

    @EventHandler(priority = EventPriority.LOW)
    fun onBlockPlace(event: BlockPlaceEvent) = handleEvent(event.player, event)

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerInteract(event: PlayerInteractEvent) = handleEvent(event.player, event)

    private fun handleEvent(player: Player, event: Event) {
        if (!event.isCancelled && !player.isOp && SkyBlockS.provider.isIslandExists(player.level.folderName))
            event.isCancelled = !Island.get(player.level.folderName)
                .isMember(player.name) &&
                    !player.hasPermission(SkyBlockS.instance.config.getString("touch_island_permission", "skyblocks.touch_island"))
    }
}
