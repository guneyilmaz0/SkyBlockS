package net.guneyilmaz0.skyblocks.objects

import net.guneyilmaz0.mongos.MongoSObject

data class IslandData(
    val id: String,
    var owner: String,
    var type: String,
    var members: List<String> = mutableListOf(),
    var lock: Boolean = false,
    var level: Level = Level()
) : MongoSObject() {

    data class Level(
        var level: Int = 1,
        var xp: Int = 1
    ) {
        fun calculateNextLevelXp(): Int = (50 * level) * level

        fun increaseXp(amount: Int): Int {
            xp += amount
            if (xp <= 0) {
                level--
                xp = calculateNextLevelXp() - 1
                return -1

            }
            if (xp >= calculateNextLevelXp()) {
                level++
                xp = 0
                return 1
            }
            return 0
        }
    }

    override fun toString(): String = super.toString()

}