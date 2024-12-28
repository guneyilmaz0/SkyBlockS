package net.guneyilmaz0.skyblocks.provider

import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.objects.Profile
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class MySQLProvider(plugin: SkyBlockS) : Provider(plugin) {

    private lateinit var connection: Connection

    override fun initialize() {
        connection = DriverManager.getConnection(
            plugin.config.getString("mysql.url"),
            plugin.config.getString("mysql.user"),
            plugin.config.getString("mysql.password")
        )

        connection.createStatement().use { statement->
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS profiles (" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "nickName VARCHAR(16) NOT NULL," +
                        "islandId VARCHAR(36) NULL," +
                        "selectedLang VARCHAR(4) NOT NULL" +
                        ")"
            )

            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS islands (" +
                        "id VARCHAR(36) PRIMARY KEY," +
                        "owner VARCHAR(36) NOT NULL," +
                        "type VARCHAR(16) NOT NULL," +
                        "members TEXT NULL," +
                        "locked BOOLEAN NOT NULL," +
                        ")"
            )
        }

    }

    override fun getProfile(name: String): Profile? {
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT * FROM profiles WHERE nickName = '$name'").use { resultSet ->
                if (!resultSet.next()) return null

                return Profile(
                    UUID.fromString(resultSet.getString("uuid")),
                    resultSet.getString("nickName"),
                    resultSet.getString("islandId"),
                    resultSet.getString("selectedLang")
                )
            }
        }
    }

    override fun isProfileExists(name: String): Boolean {
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT * FROM profiles WHERE nickName = '$name'").use { resultSet ->
                return resultSet.next()
            }
        }
    }

    override fun saveProfile(profile: Profile) {
        connection.createStatement().use { statement ->
            statement.executeUpdate(
                "INSERT INTO profiles (uuid, nickName, islandId, selectedLang) VALUES (" +
                        "'${profile.uuid}', '${profile.nickName}', '${profile.islandId}', '${profile.selectedLang}'" +
                        ") ON DUPLICATE KEY UPDATE " +
                        "nickName = '${profile.nickName}', islandId = '${profile.islandId}', selectedLang = '${profile.selectedLang}'"
            )
        }
    }

    override fun getIsland(id: String): IslandData? {
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT * FROM islands WHERE id = '$id'").use { resultSet ->
                if (!resultSet.next()) return null

                return IslandData(
                    resultSet.getString("id"),
                    resultSet.getString("owner"),
                    resultSet.getString("type"),
                    resultSet.getString("members").split(","),
                    resultSet.getBoolean("locked")
                )
            }
        }
    }

    override fun isIslandExists(id: String): Boolean {
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT * FROM islands WHERE id = '$id'").use { resultSet ->
                return resultSet.next()
            }
        }
    }

    override fun saveIsland(island: IslandData) {
        connection.createStatement().use { statement ->
            statement.executeUpdate(
                "INSERT INTO islands (id, owner, type, members, locked) VALUES (" +
                        "'${island.id}', '${island.owner}', '${island.type}', '${island.members.joinToString(",")}', ${island.lock}" +
                        ") ON DUPLICATE KEY UPDATE " +
                        "owner = '${island.owner}', type = '${island.type}', members = '${island.members.joinToString(",")}', locked = ${island.lock}"
            )
        }
    }

    override fun removeIsland(island: IslandData) {
        connection.createStatement().use { statement ->
            statement.executeUpdate("DELETE FROM islands WHERE id = '${island.id}'")
        }
    }
}