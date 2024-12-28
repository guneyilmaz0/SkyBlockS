package net.guneyilmaz0.skyblocks

import cn.nukkit.level.generator.Generator
import cn.nukkit.plugin.PluginBase
import net.guneyilmaz0.skyblocks.commands.IslandCommand
import net.guneyilmaz0.skyblocks.commands.LookPlayersIslandCommand
import net.guneyilmaz0.skyblocks.island.generators.*
import net.guneyilmaz0.skyblocks.listeners.*
import net.guneyilmaz0.skyblocks.provider.*
import net.guneyilmaz0.skyblocks.tasks.AutoSaveTask

class SkyBlockS : PluginBase() {
    companion object {
        lateinit var instance: SkyBlockS
        lateinit var provider: Provider
    }

    override fun onLoad() {
        saveResource("lang/en.yml")
        saveResource("lang/tr.yml")
        saveDefaultConfig()
        instance = this
        provider = when (config.getString("provider", "json")) {
            "mysql" -> MySQLProvider(this)
            "mongo" -> MongoProvider(this)
            else -> JSONProvider(this)
        }
    }

    override fun onEnable() {
        registerGenerators()
        registerListeners()
        registerCommands()
        registerTasks()
        sendPrefix()
    }

    private fun registerGenerators() {
        Generator.addGenerator(DefaultIslandGenerator::class.java, "default_island", 4)
        Generator.addGenerator(DesertIslandGenerator::class.java, "desert_island", 5)
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(ProtectionListener(), this)
    }

    private fun registerCommands() {
        server.commandMap.register("island", IslandCommand())
        server.commandMap.register("look_island", LookPlayersIslandCommand())
    }

    private fun registerTasks() {
        server.scheduler.scheduleRepeatingTask(this, AutoSaveTask(), config.getInt("auto_save_interval", 1200))
    }

    override fun onDisable() {
        for (player in server.onlinePlayers.values) Session.get(player).close()
    }

    private fun sendPrefix() {
        logger.info("""
        
        
         ░▒▓███████▓▒░ 
        ░▒▓█▓▒░        
        ░▒▓█▓▒░        
         ░▒▓██████▓▒░  
               ░▒▓█▓▒░ 
               ░▒▓█▓▒░ 
        ░▒▓███████▓▒░  
                          
        §aSkyBlockS §rv${description.version} §ahas been enabled.
        §aSelected provider: §r${provider::class.simpleName}
        """.trimIndent()
        )
    }
}