package net.guneyilmaz0.skyblocks.commands

import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.island.IslandManager
import net.guneyilmaz0.skyblocks.utils.Translator
import org.allaymc.api.command.Command
import org.allaymc.api.command.CommandResult
import org.allaymc.api.command.SenderType
import org.allaymc.api.command.tree.CommandContext
import org.allaymc.api.command.tree.CommandTree
import org.allaymc.api.entity.Entity
import org.allaymc.api.entity.interfaces.EntityPlayer
import org.allaymc.api.form.type.ModalForm
import org.allaymc.api.player.Player
import org.allaymc.api.server.Server

class IslandCommand : Command("island", "Island command", SkyBlockS.instance.config.getString("island_permission", "skyblocks.command.island")) {

    init {
        this.aliases.add("is")
    }

    override fun prepareCommandTree(tree: CommandTree) {
        tree.root
            .key("create")
            .enums("type", "normal", "desert")
            .exec(
                { ctx, player ->
                    val type = ctx.getResultOr<String>(1, "normal")
                    return@exec handleCreateIsland(ctx, player as EntityPlayer, type)
                }, SenderType.PLAYER
            ).root()
            .key("tp")
            .exec(
                { ctx, player ->
                    return@exec handleTpIsland(ctx, player as EntityPlayer)
                }, SenderType.PLAYER
            ).root()
            .key("delete")
            .exec(
                { ctx, player ->
                    return@exec handleDeleteIsland(ctx, player as EntityPlayer)
                }, SenderType.PLAYER
            ).root()
            .key("invite")
            .playerTarget("target")
            .exec(
                { ctx, player ->
                    val targets = ctx.getResult<List<Entity>>(1)
                    if (targets.isEmpty()) return@exec ctx.fail()
                    val target = targets[0] as? EntityPlayer ?: return@exec ctx.fail()
                    return@exec handleInvitePlayer(ctx, player as EntityPlayer, target.controller)
                }, SenderType.PLAYER
            ).root()
            .key("remove")
            .wildcardTarget("target")
            .exec(
                { ctx, player ->
                    val targetName = ctx.getResult<String>(1)
                    return@exec handleRemoveMember(ctx, player as EntityPlayer, targetName)
                }, SenderType.PLAYER
            ).root()
            .key("accept")
            .exec(
                { ctx, player ->
                    IslandManager.acceptInvite((player as EntityPlayer).controller)
                    return@exec ctx.success()
                }, SenderType.PLAYER
            ).root()
            .key("kick")
            .playerTarget("target")
            .exec(
                { ctx, player ->
                    val targets = ctx.getResult<List<Entity>>(1)
                    if (targets.isEmpty()) return@exec ctx.fail()
                    val target = targets[0] as? EntityPlayer ?: return@exec ctx.fail()
                    return@exec handleKickPlayer(ctx, player as EntityPlayer, target.controller)
                }, SenderType.PLAYER
            ).root()
            .key("leave")
            .exec(
                { ctx, player ->
                    return@exec handleLeaveIsland(ctx, player as EntityPlayer)
                }, SenderType.PLAYER
            ).root()
            .key("spawn")
            .exec(
                { ctx, player ->
                    return@exec handleSetSpawn(ctx, player as EntityPlayer)
                }, SenderType.PLAYER
            ).root()
            .key("lock")
            .exec(
                { ctx, player ->
                    return@exec handleLockIsland(ctx, player as EntityPlayer, true)
                }, SenderType.PLAYER
            ).root()
            .key("unlock")
            .exec(
                { ctx, player ->
                    return@exec handleLockIsland(ctx, player as EntityPlayer, false)
                }, SenderType.PLAYER
            ).root()
            .key("visit")
            .playerTarget("target")
            .exec(
                { ctx, player ->
                    val targets = ctx.getResult<List<Entity>>(1)
                    if (targets.isEmpty()) return@exec ctx.fail()
                    val target = targets[0] as? EntityPlayer ?: return@exec ctx.fail()
                    return@exec handleVisitIsland(ctx, player as EntityPlayer, target.controller)
                }, SenderType.PLAYER
            ).root()
            .key("language")
            .str("lang")
            .exec(
                { ctx, player ->
                    val lang = ctx.getResult<String>(1)
                    return@exec handleSetLanguage(ctx, player as EntityPlayer, lang)
                }, SenderType.PLAYER
            ).root()
            .key("help")
            .exec(
                { ctx, player ->
                    (player as EntityPlayer).sendMessage(Translator.translate(player.controller, "island_help"))
                    return@exec ctx.success()
                }, SenderType.PLAYER
            ).root()
            .key("info")
            .exec(
                { ctx, player ->
                    return@exec handleSendInformation(ctx, player as EntityPlayer)
                }, SenderType.PLAYER
            ).root()
    }

    private fun handleCreateIsland(ctx: CommandContext, player: EntityPlayer, type: String): CommandResult {
        val session = Session.get(player.controller)
        if (session.getIsland() != null) {
            player.sendMessage(Translator.translate(player.controller, "already_have_island"))
            return ctx.fail()
        }
        if (type.lowercase() !in arrayOf("normal", "desert")) {
            player.sendMessage(Translator.translate(player.controller, "invalid_island_type"))
            return ctx.fail()
        }
        IslandManager.createIsland(player.controller, type)
        return ctx.success()
    }

    private fun handleTpIsland(ctx: CommandContext, player: EntityPlayer): CommandResult {
        val island = getIsland(player) ?: return ctx.fail()
        island.teleportPlayer(player)
        return ctx.success()
    }

    private fun handleDeleteIsland(ctx: CommandContext, player: EntityPlayer): CommandResult {
        val island = getIsland(player) ?: return ctx.fail()
        if (!island.isOwner(player.controller.originName)) {
            player.sendMessage(Translator.translate(player.controller, "must_be_owner"))
            return ctx.fail()
        }
        val form = ModalForm()
            .title(Translator.translate(player.controller, "form_delete_island_title"))
            .content(Translator.translate(player.controller, "form_delete_island_content"))
            .trueButton(Translator.translate(player.controller, "form_delete_island_true")) { IslandManager.deleteIsland(player.controller) }
            .falseButton(Translator.translate(player.controller, "form_delete_island_false")) {}
        player.controller.viewForm(form)
        return ctx.success()
    }

    private fun handleInvitePlayer(ctx: CommandContext, player: EntityPlayer, target: Player): CommandResult {
        val island = getIsland(player) ?: return ctx.fail()
        if (!island.isOwner(player.controller.originName)) {
            player.sendMessage(Translator.translate(player.controller, "must_be_owner"))
            return ctx.fail()
        }
        if (island.isMember(target.originName)) {
            player.sendMessage(Translator.translate(player.controller, "player_already_member", target.originName))
            return ctx.fail()
        }
        IslandManager.inviteMember(player.controller, target)
        return ctx.success()
    }

    private fun handleRemoveMember(ctx: CommandContext, player: EntityPlayer, targetName: String): CommandResult {
        val island = getIsland(player) ?: return ctx.fail()
        if (!island.isOwner(player.controller.originName)) {
            player.sendMessage(Translator.translate(player.controller, "must_be_owner"))
            return ctx.fail()
        }
        if (!island.database.members.contains(targetName)) {
            player.sendMessage(Translator.translate(player.controller, "player_not_member", targetName))
            return ctx.fail()
        }
        if (island.isOwner(targetName)) {
            player.sendMessage(Translator.translate(player.controller, "cannot_remove_owner"))
            return ctx.fail()
        }
        val target = Server.getInstance().playerManager.getPlayerByName(targetName)
        if (target == null) {
            val profile = SkyBlockS.provider.getProfile(targetName)
            profile?.islandId = null
            profile?.let { SkyBlockS.provider.saveProfile(it) }
        } else {
            val targetSession = Session.get(target)
            targetSession.islandId = null
            targetSession.profile.islandId = null
            target.sendMessage(Translator.translate(target, "removed_from_island"))
        }
        island.database.members -= targetName
        island.save()
        player.sendMessage(Translator.translate(player.controller, "player_removed", targetName))
        return ctx.success()
    }

    private fun handleKickPlayer(ctx: CommandContext, player: EntityPlayer, target: Player): CommandResult {
        val island = getIsland(player) ?: return ctx.fail()
        if (!island.isMember(target.originName)) {
            player.sendMessage(Translator.translate(player.controller, "cannot_kick_member"))
            return ctx.fail()
        }
        if (target.controlledEntity.dimension.world.name != island.id) {
            player.sendMessage(Translator.translate(player.controller, "player_not_on_island"))
            return ctx.fail()
        }
        target.controlledEntity.teleport(Server.getInstance().worldPool.defaultWorld.spawnPoint)
        target.sendMessage(Translator.translate(target, "kicked_from_island"))
        player.sendMessage(Translator.translate(player.controller, "player_kicked", target.originName))
        return ctx.success()
    }

    private fun handleLeaveIsland(ctx: CommandContext, player: EntityPlayer): CommandResult {
        val session = Session.get(player.controller)
        val island = getIsland(player) ?: return ctx.fail()
        if (island.isOwner(player.controller.originName)) {
            player.sendMessage(Translator.translate(player.controller, "island_owner_leave"))
            return ctx.fail()
        }
        island.database.members -= player.controller.originName
        island.save()
        session.islandId = null
        session.profile.islandId = null
        player.sendMessage(Translator.translate(player.controller, "island_left"))
        player.teleport(Server.getInstance().worldPool.defaultWorld.spawnPoint)
        return ctx.success()
    }

    private fun handleSetSpawn(ctx: CommandContext, player: EntityPlayer): CommandResult {
        val island = getIsland(player) ?: return ctx.fail()
        island.setSpawn(player.controller)
        return ctx.success()
    }

    private fun handleLockIsland(ctx: CommandContext, player: EntityPlayer, lock: Boolean): CommandResult {
        val island = getIsland(player) ?: return ctx.fail()
        island.database.lock = lock
        island.save()
        player.sendMessage(Translator.translate(player.controller, if (lock) "island_locked" else "island_unlocked"))
        return ctx.success()
    }

    private fun handleVisitIsland(ctx: CommandContext, player: EntityPlayer, target: Player): CommandResult {
        val island = Session.get(target).getIsland() ?: run {
            player.sendMessage(Translator.translate(player.controller, "player_has_no_island", target.originName))
            return ctx.fail()
        }
        if (!island.isMember(player.controller.originName) && island.database.lock) {
            player.sendMessage(Translator.translate(player.controller, "island_locked_target"))
            return ctx.fail()
        }
        island.teleportPlayer(player)
        player.sendMessage(Translator.translate(player.controller, "teleported_to_island", target.originName))
        return ctx.success()
    }

    private fun handleSetLanguage(ctx: CommandContext, player: EntityPlayer, lang: String): CommandResult {
        if (!Translator.isLanguageSupported(lang)) {
            player.sendMessage(Translator.translate(player.controller, "language_not_supported", Translator.getSupportedLanguages().toString()))
            return ctx.fail()
        }
        Session.get(player.controller).profile.selectedLang = lang
        player.sendMessage(Translator.translate(player.controller, "language_changed", lang))
        return ctx.success()
    }

    private fun handleSendInformation(ctx: CommandContext, player: EntityPlayer): CommandResult {
        val island = getIsland(player) ?: return ctx.fail()
        val isLocked = if (island.database.lock) Translator.translate(player.controller, "island_information_locked") else Translator.translate(player.controller, "island_information_unlocked")
        val members = if (island.database.members.isEmpty()) Translator.translate(player.controller, "island_information_noMembers") else island.database.members.joinToString(", ")
        player.sendMessage(Translator.translate(player.controller, "island_information", island.database.owner, members, island.database.type.replaceFirstChar { it.uppercase() }, isLocked))
        return ctx.success()
    }

    private fun getIsland(player: EntityPlayer): Island? {
        val island = Session.get(player.controller).getIsland()
        if (island == null) player.sendMessage(Translator.translate(player.controller, "island_not_found"))
        return island
    }
}