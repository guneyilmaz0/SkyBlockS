package net.guneyilmaz0.skyblocks.island.generators

import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.level.generator.`object`.tree.ObjectTree
import cn.nukkit.nbt.tag.CompoundTag
import net.guneyilmaz0.skyblocks.utils.Utils

class DefaultIslandGenerator(options: MutableMap<String, Any>) : IslandGeneratorBase(options) {

    override fun getId(): Int = 4

    override fun getName(): String = "default_island"

    override fun generateChunk(chunkX: Int, chunkZ: Int) {
        val chunk = level.getChunk(chunkX, chunkZ)
        if (chunkX % 20 == 0 && chunkZ % 20 == 0) {
            for (x in 6..11) {
                for (z in 6..11) {
                    chunk.setBlock(x, 61, z, DIRT)
                    chunk.setBlock(x, 62, z, DIRT)
                    chunk.setBlock(x, 63, z, GRASS)
                }
            }

            for (airX in 9..11) {
                for (airZ in 9..11) {
                    chunk.setBlock(airX, 61, airZ, AIR)
                    chunk.setBlock(airX, 62, airZ, AIR)
                    chunk.setBlock(airX, 63, airZ, AIR)
                }
            }

            ObjectTree.growTree(level, 10, 64, 7, random, 0)

        }
    }

    override fun populateChunk(x: Int, z: Int) {
        val chunk = level.getChunk(x, z)
        chunk.setBlock(9, 64, 7, CHEST)
        val entity = chunk.getTile(9, 64, 7) as BlockEntityChest
        entity.inventory.contents = Utils.getChestContents()
    }
}