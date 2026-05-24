package net.guneyilmaz0.skyblocks.generators

import org.allaymc.api.block.property.enums.MinecraftCardinalDirection
import org.allaymc.api.block.property.type.BlockPropertyTypes
import org.allaymc.api.block.type.BlockTypes
import org.allaymc.api.world.generator.context.NoiseContext
import org.allaymc.api.world.generator.function.Noiser

class DesertIslandNoiser : Noiser {

    override fun apply(context: NoiseContext): Boolean {
        val chunk = context.currentChunk
        val cx = chunk.x
        val cz = chunk.z

        if (cx % 20 == 0 && cz % 20 == 0) {
            for (x in 6..11) {
                for (z in 6..11) {
                    chunk.setBlockState(x, 61, z, BlockTypes.SANDSTONE.defaultState)
                    chunk.setBlockState(x, 62, z, BlockTypes.SAND.defaultState)
                    chunk.setBlockState(x, 63, z, BlockTypes.SAND.defaultState)
                }
            }

            for (airX in 9..11) {
                for (airZ in 9..11) {
                    chunk.setBlockState(airX, 61, airZ, BlockTypes.AIR.defaultState)
                    chunk.setBlockState(airX, 62, airZ, BlockTypes.AIR.defaultState)
                    chunk.setBlockState(airX, 63, airZ, BlockTypes.AIR.defaultState)
                }
            }

            chunk.setBlockState(10, 64, 7, BlockTypes.CACTUS.defaultState, 0, false)
            chunk.setBlockState(10, 65, 7, BlockTypes.CACTUS.defaultState, 0, false)
            chunk.setBlockState(10, 66, 7, BlockTypes.CACTUS.defaultState, 0, false)

            // Chest
            // TODO fix container inventory
            val chestState = BlockTypes.CHEST.defaultState
                .setPropertyValue(BlockPropertyTypes.MINECRAFT_CARDINAL_DIRECTION, MinecraftCardinalDirection.WEST)
            chunk.setBlockState(9, 63, 7, chestState, 0, false)
        }
        return true
    }
}