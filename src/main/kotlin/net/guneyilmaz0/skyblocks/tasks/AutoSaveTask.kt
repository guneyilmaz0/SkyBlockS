package net.guneyilmaz0.skyblocks.tasks

import net.guneyilmaz0.skyblocks.Session
import org.allaymc.api.scheduler.Task
import org.allaymc.api.server.Server

class AutoSaveTask : Task {
    override fun onRun(): Boolean {
        for (value in Server.getInstance().playerManager.players.values) Session.get(value).save()
        return true
    }
}