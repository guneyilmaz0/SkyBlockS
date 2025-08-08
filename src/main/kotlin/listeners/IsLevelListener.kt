package net.guneyilmaz0.skyblocks.listeners

import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.scheduler.NukkitRunnable
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.events.IslandExperienceChangedEvent
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.utils.Translator

@Suppress("unused")
class IsLevelListener : Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return
        if (!SkyBlockS.provider.isIslandExists(event.block.level.folderName)) return
        val island = Island.get(event.block.level.folderName)
        island.database.level.changeXp(-1, island)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.isCancelled) return
        if (event.block.id == 0) return
        if (!SkyBlockS.provider.isIslandExists(event.block.level.folderName)) return
        object : NukkitRunnable() {
            override fun run() {
                val island = Island.get(event.block.level.folderName)
                island.database.level.changeXp(1, island)
            }
        }.runTaskLaterAsynchronously(SkyBlockS.instance, 2)

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onIslandExperienceChanged(event: IslandExperienceChangedEvent) {
        val island = event.island
        for (member in island.getOnlineMembers()) {
            if (event.newLevel > event.oldLevel) {
                member.sendMessage(Translator.translate(member, "island.level.up", event.newLevel.toString()))
                Session.get(member).playSound("random.levelup")
            } else if (event.newLevel < event.oldLevel) {
                member.sendMessage(Translator.translate(member, "island.level.down", event.newLevel.toString()))
            }
        }
    }
}