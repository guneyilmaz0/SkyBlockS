package net.guneyilmaz0.skyblocks.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

object Utils {
    fun getToday(): String {
        val format = buildString {
            append("dd/MM/yyyy")
            append(" HH")
            append(":mm")
        }
        return SimpleDateFormat(format).format(Date())
    }

    fun createIslandId(): String = "is-"+Instant.now().epochSecond+(0..200).random()
}