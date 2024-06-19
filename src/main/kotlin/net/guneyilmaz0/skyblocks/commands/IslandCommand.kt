package net.guneyilmaz0.skyblocks.commands

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.form.window.FormWindowModal
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.island.IslandManager

class IslandCommand : Command(
    "island",
    "Island command",
    "Usage: /island help",
    arrayOf("is")
) {

    init {
        this.commandParameters.clear()
    }

    override fun execute(sender: CommandSender, string: String, args: Array<String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return false
        }

        if (args.isNullOrEmpty()) {
            sender.sendMessage(usage)
            return false
        }

        when (args[0].lowercase()) {
            "create" -> createIsland(sender, args)
            "tp", "teleport" -> teleportIsland(sender)
            "delete" -> deleteIsland(sender)
            "invite" -> invitePlayer(sender, args)
            "kick" -> kickPlayerOnIsland(sender, args)
            "leave" -> leaveIsland(sender)
            "spawn", "setspawn" -> setSpawn(sender)
            "accept" -> IslandManager.acceptInvite(sender)
            else -> sender.sendMessage(usage)
        }
        return true
    }

    private fun createIsland(player: Player, args: Array<String>) {
        val session = Session.get(player)
        if (session.getIsland() != null) {
            player.sendMessage("§cYou already have an island.")
            return
        }

        if (args.size < 2) {
            player.sendMessage("§cUsage: /island create <type>")
            player.sendMessage("§gTypes: normal, snow, desert, end")
            return
        }

        val type = args[1].lowercase()
        if (type !in arrayOf("normal", "snow", "desert", "end")) {
            player.sendMessage("§cInvalid island type.")
            return
        }

        IslandManager.createIsland(player, type)
    }

    private fun teleportIsland(player: Player) {
        val island = getIsland(player)?: return

        island.teleportPlayer(player)
    }

    private fun deleteIsland(player: Player) {
        val island = getIsland(player)?: return

        if (!island.isOwner(player.name)) {
            player.sendMessage("§cYou must be the owner of the island to use this command.")
            return
        }

        val form = FormWindowModal(
            "Delete Island",
            "Are you sure you want to delete your island? §c§lThis action cannot be undone.",
            "Yes",
            "No"
        )
        player.showFormWindow(form, "delete_island".hashCode())
    }

    private fun invitePlayer(player: Player, args: Array<String>) {
        val island = getIsland(player)?: return

        if (!island.isOwner(player.name)) {
            player.sendMessage("§cYou must be the owner of the island to use this command.")
            return
        }

        if (args.size < 2) {
            player.sendMessage("§cUsage: /island invite <player>")
            return
        }

        val target = player.server.getPlayer(args[1]) ?: run {
            player.sendMessage("§cPlayer not found.")
            return
        }

        if (island.isMember(target.name)) {
            player.sendMessage("§f${target.name} §cis already a member of the island.")
            return
        }

        IslandManager.inviteMember(player, target)
    }

    private fun kickPlayerOnIsland(player: Player, args: Array<String>) {
        val island = getIsland(player)?: return

        if (args.size < 2) {
            player.sendMessage("§cUsage: /island kick <player>")
            return
        }

        val target = player.server.getPlayer(args[1]) ?: run {
            player.sendMessage("§cPlayer not found.")
            return
        }

        if (island.isMember(target.name)) {
            player.sendMessage("§You can't kick the member of the island.")
            return
        }

        if (!target.getLevel().folderName.equals(island.id)) {
            player.sendMessage("§cPlayer is not on your island.")
            return
        }

        target.teleport(player.server.defaultLevel.spawnLocation)
        target.sendMessage("§cYou have been kicked off the island.")
        player.sendMessage("${target.name} §chas been kicked off the island.")
    }

    private fun leaveIsland(player: Player) {
        val session = Session.get(player)
        val island = session.getIsland() ?: run {
            player.sendMessage("§cYou don't have an island.")
            return
        }

        if (island.isOwner(player.name)) {
            player.sendMessage("§cYou can't leave your own island.")
            return
        }

        island.database.members -= player.name
        session.islandId = null
        session.profile.islandId = null
        player.sendMessage("§cYou left the island.")
        player.teleport(player.server.defaultLevel.spawnLocation)
    }

    private fun setSpawn(player: Player) {
        val island = getIsland(player)?: return
        island.setSpawn(player)
    }

    private fun getIsland(player: Player) : Island? {
        val session = Session.get(player)
        val island = session.getIsland() ?: run {
            player.sendMessage("§cYou don't have an island.")
            return null
        }
        return island
    }
}