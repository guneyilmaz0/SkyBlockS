package net.guneyilmaz0.skyblocks.island.generators

import cn.nukkit.level.format.ChunkState
import cn.nukkit.level.generator.ChunkGenerateContext
import cn.nukkit.level.generator.GenerateStage

class EmptyGenerateStage : GenerateStage() {

    companion object {
        const val NAME: String = "empty_generate"
    }

    override fun name(): String = NAME

    override fun apply(context: ChunkGenerateContext) {
        context.chunk.chunkState = ChunkState.POPULATED
    }
}