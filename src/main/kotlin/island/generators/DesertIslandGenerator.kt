package net.guneyilmaz0.skyblocks.island.generators

import cn.nukkit.block.Block
import cn.nukkit.block.BlockID
import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.level.ChunkManager
import cn.nukkit.level.generator.Generator
import cn.nukkit.math.NukkitRandom
import cn.nukkit.math.Vector3
import cn.nukkit.nbt.tag.CompoundTag

class DesertIslandGenerator(private val options: MutableMap<String, Any>) : Generator() {

    override fun getId(): Int = 5

    private lateinit var level: ChunkManager
    private lateinit var random: NukkitRandom

    override fun init(chunkManager: ChunkManager, nukkitRandom: NukkitRandom) {
        this.level = chunkManager
        this.random = nukkitRandom
    }

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
            chunk.setBlock(9, 63, 7, Block.CHEST)
            BlockEntityChest.createBlockEntity(BlockEntityChest.CHEST, chunk, CompoundTag()) as BlockEntityChest
        }
    }

    override fun populateChunk(x: Int, z: Int) { }

    override fun getSettings(): MutableMap<String, Any> = options

    override fun getName(): String = "desert_island"

    override fun getSpawn(): Vector3 = Vector3(7.0, 66.0, 7.0)

    override fun getChunkManager(): ChunkManager = level
}