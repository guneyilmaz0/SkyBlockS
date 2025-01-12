package net.guneyilmaz0.skyblocks.utils

import cn.nukkit.Server
import cn.nukkit.item.Item
import net.guneyilmaz0.skyblocks.SkyBlockS
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

    fun getChestContents() : Map<Int, Item> {
        val map = mutableMapOf<Int, Item>()
        val list = SkyBlockS.instance.config.getStringList("chest_contents")
        for (i in list.indices) {
            val split = list[i].split(":")
            val id = split[0].toInt()
            val meta = split[1].toInt()
            val count = split[2].toInt()
            map[i] = Item.get(id, meta, count)
        }
        return map
    }
}