package net.guneyilmaz0.skyblocks.tasks

import cn.nukkit.Server
import cn.nukkit.scheduler.AsyncTask
import net.guneyilmaz0.skyblocks.Session

class AutoSaveTask : AsyncTask() {
    override fun onRun() {
        for (value in Server.getInstance().onlinePlayers.values) Session.get(value!!).save()
    }
}