package net.guneyilmaz0.skyblocks.utils

import cn.nukkit.Server
import java.io.File
import java.time.Instant

object Utils {

    fun createIslandId(): String = "is-"+Instant.now().epochSecond+(0..200).random()

    fun deleteLevel(level: String) {
        val source = File(Server.getInstance().dataPath + "/worlds/" + level)
        if (!source.exists()) throw IllegalArgumentException("Source level not found")
        deleteFolderRecursively(source)
    }

    private fun deleteFolderRecursively(folder: File) {
        for (file in folder.listFiles()!!) {
            if (file.isDirectory) deleteFolderRecursively(file)
            file.delete()
        }
        folder.delete()
    }
}