package net.guneyilmaz0.skyblocks.island.generators

import cn.nukkit.block.Block
import cn.nukkit.block.BlockID
import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.nbt.tag.CompoundTag
import net.guneyilmaz0.skyblocks.utils.Utils

class DesertIslandGenerator(options: MutableMap<String, Any>) : IslandGeneratorBase(options) {

    override fun getId(): Int = 5

    override fun getName(): String = "desert_island"

    override fun generateChunk(chunkX: Int, chunkZ: Int) {
        val chunk = level.getChunk(chunkX, chunkZ)
        if (chunkX % 20 == 0 && chunkZ % 20 == 0) {
            for (x in 6..11) {
                for (z in 6..11) {
                    chunk.setBlock(x, 61, z, Block.SANDSTONE)
                    chunk.setBlock(x, 62, z, Block.SAND)
                    chunk.setBlock(x, 63, z, Block.SAND)
                }
            }

            for (airX in 9..11) {
                for (airZ in 9..11) {
                    chunk.setBlock(airX, 61, airZ, AIR)
                    chunk.setBlock(airX, 62, airZ, AIR)
                    chunk.setBlock(airX, 63, airZ, AIR)
                }
            }

            chunk.setBlock(10, 64, 7, BlockID.CACTUS)
            chunk.setBlock(10, 65, 7, BlockID.CACTUS)
            chunk.setBlock(10, 66, 7, BlockID.CACTUS)

            chunk.setBlock(9, 63, 7, CHEST)

            val chestPosition = CompoundTag()
                .putString("id", "Chest")
                .putInt("x",  9)
                .putInt("y", 63)
                .putInt("z",  7)

            BlockEntityChest.createBlockEntity("Chest", chunk, chestPosition).also {
                (it as? BlockEntityChest)?.inventory?.contents = Utils.getChestContents()
            }
        }
    }
}