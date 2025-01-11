package net.guneyilmaz0.skyblocks.listeners

import cn.nukkit.Server
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.level.Level
import cn.nukkit.level.Sound
import cn.nukkit.level.particle.HappyVillagerParticle
import cn.nukkit.scheduler.NukkitRunnable
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.utils.Translator.translate

class IsLevelListener : Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockBreak(event: BlockBreakEvent) = addXpAsync(event, -1)

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockPlace(event: BlockPlaceEvent) = addXpAsync(event, 1)

    private fun addXpAsync(event: BlockEvent, amount: Int) {
        if (event.isCancelled) return
        if (!SkyBlockS.provider.isIslandExists(event.block.level.folderName)) return

        val level = event.block.level
        val island = Island.get(level.folderName) ?: return

        Server.getInstance().scheduler.scheduleDelayedTask(SkyBlockS.instance, object : NukkitRunnable() {
            override fun run() {
                val i = island.database.level.increaseXp(amount)
                when (i) {
                    0 -> return
                    1 -> {
                        level.addParticle(HappyVillagerParticle(event.block))
                        level.addSound(event.block, Sound.RANDOM_LEVELUP, 1f, 1f, island.getOnlineMembers())
                        island.getOnlineMembers().forEach {
                            it.sendMessage(
                                translate(
                                    it, "island.level.up",
                                    island.database.level.level.toString()
                                )
                            )
                        }
                    }
                    else -> {
                        level.addSound(event.block, Sound.RANDOM_ANVIL_LAND, 1f, 1f, island.getOnlineMembers())
                        island.getOnlineMembers().forEach {
                            it.sendMessage(
                                translate(
                                    it, "island.level.down",
                                    island.database.level.level.toString()
                                )
                            )
                        }
                    }
                }
            }
        }, 5, true)
    }
}