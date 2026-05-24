package net.guneyilmaz0.skyblocks.utils

import net.guneyilmaz0.skyblocks.SkyBlockS
import org.allaymc.api.item.ItemStack
import org.allaymc.api.registry.Registries
import org.allaymc.api.utils.identifier.Identifier
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.time.Instant

object Utils {

    fun createIslandId(): String = "is-" + Instant.now().epochSecond + (0..200).random()

    fun deleteLevel(level: String) {
        val source = File("worlds/$level")
        require(source.exists()) { "Source level not found" }
        source.deleteRecursively()
    }

    private fun deleteFolderRecursively(folder: File) {
        for (file in folder.listFiles()!!) {
            if (file.isDirectory) deleteFolderRecursively(file)
            file.delete()
        }
        folder.delete()
    }

    fun saveResource(resourceName: String) {
        val dataFolder = SkyBlockS.instance.pluginContainer.dataFolder()
        val targetPath = dataFolder.resolve(resourceName)

        if (Files.exists(targetPath)) return

        try {
            targetPath.parent?.let { Files.createDirectories(it) }

            javaClass.classLoader.getResourceAsStream(resourceName)?.use {
                Files.copy(it, targetPath)
            } ?: SkyBlockS.instance.pluginLogger.warn("Resource {} not found!", resourceName)
        } catch (e: IOException) {
            SkyBlockS.instance.pluginLogger.error("Failed to save resource: {}", resourceName, e)
        }
    }

    fun getChestContents(): List<ItemStack> {
        val list = mutableListOf<ItemStack>()
        val contents = SkyBlockS.instance.config.getStringList("chest_contents")
        for (line in contents) {
            // Format: "minecraft:apple:5" or "minecraft:apple"
            val parts = line.split(":")
            val identifier: Identifier
            val count: Int
            if (parts.size >= 3) {
                identifier = Identifier(parts[0], parts[1])
                count = parts[2].toIntOrNull() ?: 1
            } else if (parts.size == 2) {
                // Could be "namespace:path" or "path:count"
                val secondPartInt = parts[1].toIntOrNull()
                if (secondPartInt != null) {
                    identifier = Identifier("minecraft", parts[0])
                    count = secondPartInt
                } else {
                    identifier = Identifier(parts[0], parts[1])
                    count = 1
                }
            } else {
                identifier = Identifier("minecraft", line)
                count = 1
            }
            
            val itemType = Registries.ITEMS.get(identifier)
            if (itemType != null) {
                list.add(itemType.createItemStack(count))
            } else {
                SkyBlockS.instance.pluginLogger.warn("Item type not found: $identifier")
            }
        }
        return list
    }
}