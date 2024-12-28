package net.guneyilmaz0.skyblocks.objects

import java.util.*

data class Profile(
    val uuid: UUID,
    var nickName: String,
    var islandId: String?,
    var selectedLang: String = "en"
)