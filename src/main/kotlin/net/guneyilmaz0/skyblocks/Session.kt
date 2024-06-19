package net.guneyilmaz0.skyblocks

import cn.nukkit.Player
import cn.nukkit.Server
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.objects.Profile
import java.util.*

data class Session(val player: Player) {

    companion object {
        private val data: WeakHashMap<Player, Session> = WeakHashMap()

        fun get(player: Player): Session = data.computeIfAbsent(player) { Session(it) }
    }

    var profile: Profile
    var islandId: String? = null

    init {
        if (Profile.isProfileExists(player.name)) profile = Profile.getProfile(player.name)!!
        else {
            profile = Profile(player.uniqueId, player.name, null)
            save()
        }

        profile.islandId?.let {
            islandId = it
            if (!Server.getInstance().isLevelLoaded(islandId)) Server.getInstance().loadLevel(islandId)
        }
    }

    fun getIsland(): Island? = islandId?.let { Island.get(it) }

    fun save() {
        Profile.saveProfile(profile)
        if (islandId != null) getIsland()!!.save()
    }

    fun close() {
        save()
        data.remove(player)
    }
}