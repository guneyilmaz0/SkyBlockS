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
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (ex: ClassNotFoundException) {
            throw RuntimeException("Error while trying to load MySql driver", ex)
        }

        connection = DriverManager.getConnection(
            plugin.config.getString("mysql.url"),
            plugin.config.getString("mysql.user"),
            plugin.config.getString("mysql.password")
        )

        connection.createStatement().use { statement->
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS profiles (" +
                        "nickName VARCHAR(16) PRIMARY KEY,"+
                        "uuid VARCHAR(36) NOT NULL," +
                        "islandId VARCHAR(36) NULL," +
                        "selectedLang VARCHAR(4) NOT NULL" +
                        ")"
            )

            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS islands (" +
                        "id TEXT PRIMARY KEY," +
                        "owner TEXT NOT NULL," +
                        "type TEXT NOT NULL," +
                        "members TEXT," +
                        "locked INTEGER NOT NULL," +
                        "isLevel INTEGER NOT NULL," +
                        "totalXp INTEGER NOT NULL" +
                        "xp INTEGER NOT NULL" +
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
        connection.prepareStatement("SELECT * FROM islands WHERE id = ?").use { statement ->
            statement.setString(1, id)
            statement.executeQuery().use { resultSet ->
                if (!resultSet.next()) return null

                return IslandData(
                    resultSet.getString("id"),
                    resultSet.getString("owner"),
                    resultSet.getString("type"),
                    resultSet.getString("members")?.split(",") ?: emptyList(),
                    resultSet.getInt("locked") != 0,
                    IslandData.Level(resultSet.getInt("isLevel"), resultSet.getInt("totalXp"), resultSet.getInt("xp"))
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
        connection.prepareStatement(
            "INSERT INTO islands (id, owner, type, members, locked, isLevel, xp) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET owner = excluded.owner, type = excluded.type, members = excluded.members, locked = excluded.locked, isLevel = excluded.isLevel, xp = excluded.xp"
        ).use { statement ->
            statement.setString(1, island.id)
            statement.setString(2, island.owner)
            statement.setString(3, island.type)
            statement.setString(4, island.members.joinToString(","))
            statement.setInt(5, if (island.lock) 1 else 0)
            statement.setInt(6, island.level.level)
            statement.setInt(7, island.level.xp)
            statement.executeUpdate()
        }
    }

    override fun removeIsland(island: IslandData) {
        connection.createStatement().use { statement ->
            statement.executeUpdate("DELETE FROM islands WHERE id = '${island.id}'")
        }
    }
}