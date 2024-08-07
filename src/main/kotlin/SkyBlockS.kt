package net.guneyilmaz0.skyblocks

import cn.nukkit.level.generator.Generator
import cn.nukkit.plugin.PluginBase
import net.guneyilmaz0.skyblocks.commands.IslandCommand
import net.guneyilmaz0.skyblocks.island.generators.*
import net.guneyilmaz0.skyblocks.listeners.*
import net.guneyilmaz0.skyblocks.tasks.AutoSaveTask

class SkyBlockS : PluginBase() {
    companion object {
        lateinit var instance: SkyBlockS
    }

    override fun onLoad() {
        saveResource("lang/en.yml")
        saveResource("lang/tr.yml")
    }

    override fun onEnable() {
        instance = this
        //Register generator
        Generator.addGenerator(DefaultIslandGenerator::class.java, "default_island", 4)
        Generator.addGenerator(DesertIslandGenerator::class.java, "desert_island", 5)
        //Register listeners
        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(ProtectionListener(), this)
        //Register commands
        server.commandMap.register("island", IslandCommand())
        //Register tasks
        server.scheduler.scheduleRepeatingTask(this, AutoSaveTask(), config.getInt("auto_save_interval", 1200))
    }

    override fun onDisable() {
        for (player in server.onlinePlayers.values) Session.get(player).close()
    }
}