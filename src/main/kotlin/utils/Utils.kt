package net.guneyilmaz0.skyblocks.utils

import cn.nukkit.Server
import java.io.File
import java.time.Instant

object Utils {

    fun createIslandId(): String = "is-"+Instant.now().epochSecond+(0..200).random()

    fun copyLevel(level: String, newLevel: String) {
        val source = File(Server.getInstance().dataPath + "/worlds/" + level)
        if (!source.exists()) throw IllegalArgumentException("Source level not found")
        val destination = File(source.parent, newLevel)
        if (destination.exists()) throw IllegalArgumentException("Destination level already exists")

        destination.mkdir()
        copyFolderRecursively(source, destination)
    }

    private fun copyFolderRecursively(sourceFolder: File, destinationFolder: File) {
        for (file in sourceFolder.listFiles()!!) {
            if (file.isDirectory) {
                val newSubfolder = File(destinationFolder, file.name)
                newSubfolder.mkdir()
                copyFolderRecursively(file, newSubfolder)
            } else {
                val destinationFile = File(destinationFolder, file.name)
                file.copyTo(destinationFile, overwrite = true)
            }
        }
    }

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