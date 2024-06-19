package net.guneyilmaz0.skyblocks

import cn.nukkit.level.generator.Generator
import cn.nukkit.plugin.PluginBase
import net.guneyilmaz0.skyblocks.commands.IslandCommand
import net.guneyilmaz0.skyblocks.island.IslandGenerator
import net.guneyilmaz0.skyblocks.listeners.*

class SkyblockS : PluginBase() {
    companion object {
        lateinit var instance: SkyblockS
    }

    override fun onEnable() {
        instance = this
        //Register generator
        Generator.addGenerator(IslandGenerator::class.java, "island", 4)
        //Register listeners
        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(ProtectionListener(), this)
        //Register commands
        server.commandMap.register("island", IslandCommand())
    }

}