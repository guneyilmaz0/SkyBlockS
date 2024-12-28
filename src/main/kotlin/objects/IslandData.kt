package net.guneyilmaz0.skyblocks.objects

data class IslandData(
    val id: String,
    var owner: String,
    var type: String,
    var members: List<String> = mutableListOf(),
    var lock: Boolean = false
)