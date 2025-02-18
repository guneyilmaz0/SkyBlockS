package net.guneyilmaz0.skyblocks.objects

import net.guneyilmaz0.mongos.MongoSObject
import net.guneyilmaz0.skyblocks.events.IslandExperienceChangedEvent
import net.guneyilmaz0.skyblocks.island.Island

data class IslandData(
    val id: String,
    var owner: String,
    var type: String,
    var members: List<String> = mutableListOf(),
    var lock: Boolean = false,
    var level: Level = Level(1, 1, 0)
) : MongoSObject() {

    data class Level(
        var level: Int,
        var totalXp: Int,
        var xp: Int
    ) {
        fun getRequiredXp(): Int = (50 * level) * level

        fun changeXp(amount: Int, island: Island) {
            val oldLevel = level

            xp += amount
            totalXp += amount

            while (xp >= getRequiredXp()) {
                xp -= getRequiredXp()
                level++
            }

            while (xp < 0 && level > 1) {
                level--
                xp += getRequiredXp()
            }

            if (level == 1 && xp < 0) xp = 0

            val event = IslandExperienceChangedEvent(island, oldLevel, level, xp)
            event.call()
        }
    }

    override fun toString(): String = super.toString()

}