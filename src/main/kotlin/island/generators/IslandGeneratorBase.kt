package net.guneyilmaz0.skyblocks.island.generators

import cn.nukkit.level.ChunkManager
import cn.nukkit.level.generator.Generator
import cn.nukkit.math.NukkitRandom
import cn.nukkit.math.Vector3

abstract class IslandGeneratorBase(
    private val options: MutableMap<String, Any>,
) : Generator() {

    lateinit var level: ChunkManager
    lateinit var random: NukkitRandom

    override fun init(chunkManager: ChunkManager, nukkitRandom: NukkitRandom) {
        this.level = chunkManager
        this.random = nukkitRandom
    }

    override fun populateChunk(x: Int, z: Int) { }

    override fun getSettings(): MutableMap<String, Any> = options

    override fun getSpawn(): Vector3 = Vector3(7.0, 66.0, 7.0)

    override fun getChunkManager(): ChunkManager = level
}