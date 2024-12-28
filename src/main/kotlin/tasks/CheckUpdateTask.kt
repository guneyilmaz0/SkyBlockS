package net.guneyilmaz0.skyblocks.tasks

import cn.nukkit.Server
import cn.nukkit.scheduler.AsyncTask
import net.guneyilmaz0.skyblocks.SkyBlockS
import java.net.HttpURLConnection
import java.net.URI

class CheckUpdateTask : AsyncTask() {

    private var latestVersion: String? = null

    override fun onRun() {
        try {
            val apiUrl = "https://api.github.com/repos/guneyilmaz0/SkyBlocks/releases/latest"
            val url = URI(apiUrl).toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val response = connection.inputStream.bufferedReader().readText()

            val tagIndex = response.indexOf("\"tag_name\"")
            if (tagIndex != -1) {
                val startIndex = response.indexOf(":", tagIndex) + 2
                val endIndex = response.indexOf("\"", startIndex)
                latestVersion = response.substring(startIndex, endIndex)
            }
        } catch (e: Exception) {
            latestVersion = null
        }
    }

    override fun onCompletion(server: Server) {
        if (latestVersion == null) {
            server.logger.warning("§cThere was an error while checking for SkyBlockS updates!")
            return
        }

        if (SkyBlockS.instance.description.version != latestVersion)
            server.logger.info("§eThere is a new update available for SkyBlockS! Version: $latestVersion")
        else server.logger.info("§aYou are using the latest version of the SkyBlockS!")
    }
}