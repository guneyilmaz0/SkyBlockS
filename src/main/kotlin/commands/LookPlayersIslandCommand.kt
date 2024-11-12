package net.guneyilmaz0.skyblocks.commands

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.objects.Profile
import net.guneyilmaz0.skyblocks.utils.Translator

class LookPlayersIslandCommand : Command(
    "look_island",
    "Look Island command",
    "Usage: /look_island <player>"
) {

    init {
        this.commandParameters.clear()
        permission = SkyBlockS.instance.config.getString("island_permission","skyblocks.command.look_island")
    }

    override fun execute(sender: CommandSender, string: String, args: Array<String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return false
        }

        if (!testPermission(sender)) return false

        if (args.isNullOrEmpty()) {
            sender.sendMessage(usage)
            return false
        }

        val player = args[0]
        val profile = Profile.getProfile(player) ?: run {
            sender.sendMessage(Translator.translate(sender, "look_island_player_not_found"))
            return false
        }

        if (profile.islandId == null) {
            sender.sendMessage(Translator.translate(sender, "look_island_player_not_have_island"))
            return false
        }

        val island = Island.get(profile.islandId!!)
        island.teleportPlayer(sender)
        sender.sendMessage(Translator.translate(sender, "look_island_teleported", player))
        return true
    }
}