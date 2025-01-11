package net.guneyilmaz0.skyblocks.tasks

import cn.nukkit.level.Level
import cn.nukkit.scheduler.AsyncTask
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.utils.Translator.translate

class IslandXPTask(private val level: Level, private val amount: Int) : AsyncTask() {

    override fun onRun() {
        val island = Island.get(level.folderName)
        val result = island.database.level.increaseXp(amount)
        val messageKey = when (result) {
            1 -> "island.level.up"
            else -> "island.level.down"
        }
        if (result != 0) island.getOnlineMembers().forEach {
            it.sendMessage(translate(it, messageKey, island.database.level.level.toString()))
        }
    }
}