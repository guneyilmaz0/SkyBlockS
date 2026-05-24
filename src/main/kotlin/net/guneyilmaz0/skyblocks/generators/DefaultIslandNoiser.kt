package net.guneyilmaz0.skyblocks.generators

import net.guneyilmaz0.skyblocks.utils.Utils
import org.allaymc.api.block.property.enums.MinecraftCardinalDirection
import org.allaymc.api.block.property.type.BlockPropertyTypes
import org.allaymc.api.block.type.BlockTypes
import org.allaymc.api.blockentity.interfaces.BlockEntityChest
import org.allaymc.api.container.Container
import org.allaymc.api.world.chunk.Chunk
import org.allaymc.api.world.feature.WorldFeatureContext
import org.allaymc.api.world.feature.WorldFeatures
import org.allaymc.api.world.generator.context.NoiseContext
import org.allaymc.api.world.generator.function.Noiser

class DefaultIslandNoiser : Noiser {

    override fun apply(context: NoiseContext): Boolean {
        val chunk = context.currentChunk
        val cx = chunk.x
        val cz = chunk.z

        if (cx % 20 == 0 && cz % 20 == 0) {
            for (x in 6..11) {
                for (z in 6..11) {
                    chunk.setBlockState(x, 61, z, BlockTypes.DIRT.defaultState, 0, false)
                    chunk.setBlockState(x, 62, z, BlockTypes.DIRT.defaultState, 0, false)
                    chunk.setBlockState(x, 63, z, BlockTypes.GRASS_BLOCK.defaultState, 0, false)
                }
            }

            for (airX in 9..11) {
                for (airZ in 9..11) {
                    chunk.setBlockState(airX, 61, airZ, BlockTypes.AIR.defaultState, 0, false)
                    chunk.setBlockState(airX, 62, airZ, BlockTypes.AIR.defaultState, 0, false)
                    chunk.setBlockState(airX, 63, airZ, BlockTypes.AIR.defaultState, 0, false)
                }
            }

            // Chest
            // TODO fix container inventory
            val chestState = BlockTypes.CHEST.defaultState
                .setPropertyValue(BlockPropertyTypes.MINECRAFT_CARDINAL_DIRECTION, MinecraftCardinalDirection.WEST)
            chunk.setBlockState(9, 64, 7, chestState, 0, false)

            val blockEntity = chunk.getBlockEntity(9, 64, 7)
            if (blockEntity is BlockEntityChest) {
                val container: Container = blockEntity.getContainer()
                val contents = Utils.getChestContents()
                for (item in contents) {
                    container.tryAddItem(item)
                }
            }

            // Oak Tree
            val worldX = (cx shl 4) + 10
            val worldZ = (cz shl 4) + 7
            val safeChunk = chunk.toSafeChunk()
            val featureContext = WorldFeatureContext { x, z -> if (x == cx && z == cz) safeChunk else null }
            WorldFeatures.OAK_TREE.place(featureContext, worldX, 64, worldZ)
        }

        return true
    }
}