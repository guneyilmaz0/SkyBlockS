package net.guneyilmaz0.skyblocks.island.generators

import cn.nukkit.level.DimensionData
import cn.nukkit.level.generator.GenerateStage
import cn.nukkit.level.generator.Generator
import cn.nukkit.level.generator.stages.FinishedStage
import cn.nukkit.registry.Registries

class EmptyGenerator(dimensionData: DimensionData?, options: Map<String?, Any?>?
) : Generator(dimensionData, options) {
    companion object {
        const val NAME: String = "empty"
    }

    override fun getName(): String = NAME

    override fun stages(builder: GenerateStage.Builder) {
        builder.start(Registries.GENERATE_STAGE.get(EmptyGenerateStage.NAME))
        builder.next(Registries.GENERATE_STAGE[FinishedStage.NAME])
    }
}