package net.guneyilmaz0.skyblocks

import net.guneyilmaz0.skyblocks.commands.*
import net.guneyilmaz0.skyblocks.dataprovider.*
import net.guneyilmaz0.skyblocks.generators.DefaultIslandNoiser
import net.guneyilmaz0.skyblocks.generators.DesertIslandNoiser
import net.guneyilmaz0.skyblocks.listeners.*
import net.guneyilmaz0.skyblocks.tasks.*
import net.guneyilmaz0.skyblocks.utils.Utils
import org.allaymc.api.plugin.Plugin
import org.allaymc.api.registry.Registries
import org.allaymc.api.server.Server
import org.allaymc.api.utils.config.Config
import org.allaymc.api.world.generator.WorldGenerator

class SkyBlockS : Plugin() {
    companion object {
        lateinit var instance: SkyBlockS
        lateinit var provider: DataProvider
    }

    lateinit var config: Config

    override fun onLoad() {
        instance = this
        Utils.saveResource("config.yml")
        Utils.saveResource("lang/en.yml")
        Utils.saveResource("lang/tr.yml")
        config = Config("${pluginContainer.dataFolder}/config.yml")
        loadProvider()
    }

    override fun onEnable() {
        registerGenerators()
        registerTasks()
        registerListeners()
        registerCommands()
        sendPrefix()
    }

    override fun onDisable() {
        for (player in Server.getInstance().playerManager.players.values) {
            Session.get(player).close()
        }
    }

    fun loadProvider() {
        provider = when (config.getString("provider", "json")) {
            "mongo" -> MongoProvider()
            else -> JSONProvider()
        }
        provider.connect()
    }

    fun registerGenerators() {
        Registries.WORLD_GENERATOR_FACTORIES.register("island_default") { preset ->
            WorldGenerator.builder()
                .name("island_default")
                .preset(preset ?: "")
                .noisers(DefaultIslandNoiser())
                .build()
        }

        Registries.WORLD_GENERATOR_FACTORIES.register("island_desert") { preset ->
            WorldGenerator.builder()
                .name("island_desert")
                .preset(preset ?: "")
                .noisers(DesertIslandNoiser())
                .build()
        }
    }

    fun registerTasks() {
        val scheduler = Server.getInstance().scheduler
        scheduler.scheduleRepeating(this, AutoSaveTask(), config.getInt("auto_save_interval", 1200), true)
    }

    private fun registerListeners() {
        val eventBus =  Server.getInstance().eventBus
        eventBus.registerListener(PlayerListener())
    }

    private fun registerCommands() {
        Registries.COMMANDS.register(IslandCommand())
    }

    private fun sendPrefix() {
        pluginLogger.info("""
        
        
         ░▒▓███████▓▒░ 
        ░▒▓█▓▒░        
        ░▒▓█▓▒░        
         ░▒▓██████▓▒░  
               ░▒▓█▓▒░ 
               ░▒▓█▓▒░ 
        ░▒▓███████▓▒░  
                          
        §aSkyBlockS §rv version §ahas been enabled.
        §aSelected provider: §r${provider::class.simpleName}
        """.trimIndent()
        )
    }
}