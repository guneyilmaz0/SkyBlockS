package net.guneyilmaz0.skyblocks.provider

import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.objects.Profile
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class SQLiteProvider(plugin: SkyBlockS) : Provider(plugin) {

    private lateinit var connection: Connection

    override fun initialize() {
        try {
            Class.forName("org.sqlite.JDBC")
        } catch (ex: ClassNotFoundException) {
            throw RuntimeException("Error while trying to load SQLite driver", ex)
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.dataFolder.resolve("skyblocks.db").path)

        connection.createStatement().use { statement ->
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS profiles (" +
                        "nickName TEXT PRIMARY KEY," +
                        "uuid TEXT NOT NULL," +
                        "islandId TEXT," +
                        "selectedLang TEXT NOT NULL" +
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
                        "xp INTEGER NOT NULL" +
                        ")"
            )
        }
    }

    override fun getProfile(name: String): Profile? {
        connection.prepareStatement("SELECT * FROM profiles WHERE nickName = ?").use { statement ->
            statement.setString(1, name)
            statement.executeQuery().use { resultSet ->
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
        connection.prepareStatement("SELECT 1 FROM profiles WHERE nickName = ?").use { statement ->
            statement.setString(1, name)
            statement.executeQuery().use { resultSet ->
                return resultSet.next()
            }
        }
    }

    override fun saveProfile(profile: Profile) {
        connection.prepareStatement(
            "INSERT INTO profiles (uuid, nickName, islandId, selectedLang) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT(nickName) DO UPDATE SET uuid = excluded.uuid, islandId = excluded.islandId, selectedLang = excluded.selectedLang"
        ).use { statement ->
            statement.setString(1, profile.uuid.toString())
            statement.setString(2, profile.nickName)
            statement.setString(3, profile.islandId)
            statement.setString(4, profile.selectedLang)
            statement.executeUpdate()
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
                    IslandData.Level(resultSet.getInt("isLevel"), resultSet.getInt("xp"))
                )
            }
        }
    }

    override fun isIslandExists(id: String): Boolean {
        connection.prepareStatement("SELECT 1 FROM islands WHERE id = ?").use { statement ->
            statement.setString(1, id)
            statement.executeQuery().use { resultSet ->
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
        connection.prepareStatement("DELETE FROM islands WHERE id = ?").use { statement ->
            statement.setString(1, island.id)
            statement.executeUpdate()
        }
    }
}
