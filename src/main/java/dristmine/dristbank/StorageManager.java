package dristmine.dristbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StorageManager {
    private final DristBank plugin;
    private Connection connection;

    public StorageManager(DristBank plugin) {
        this.plugin = plugin;
        connect();
        createTables();
    }

    // Connect to the MySQL database
    private void connect() {
        String hostname = plugin.getConfig().getString("database.hostname");
        String user = plugin.getConfig().getString("database.user");
        String password = plugin.getConfig().getString("database.password");
        String database = plugin.getConfig().getString("database.database");

        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + database, user, password);
            plugin.getLogger().info("Successfully connected to the MySQL database.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Could not connect to the MySQL database.");
        }
    }

    // Create the necessary tables if they do not exist
    private void createTables() {
        String createPlayerBalancesTableSQL = "CREATE TABLE IF NOT EXISTS player_balances ("
                + "player_uuid VARCHAR(36) PRIMARY KEY,"
                + "balance DOUBLE NOT NULL"
                + ");";

        String createTotalDebrisTableSQL = "CREATE TABLE IF NOT EXISTS total_debris ("
                + "id INT PRIMARY KEY,"
                + "total DOUBLE NOT NULL"
                + ");";

        try (PreparedStatement playerBalancesStatement = connection.prepareStatement(createPlayerBalancesTableSQL);
             PreparedStatement totalDebrisStatement = connection.prepareStatement(createTotalDebrisTableSQL)) {
            playerBalancesStatement.execute();
            totalDebrisStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Could not create the database tables.");
        }

        // Initialize the total debris row if it does not exist
        String initializeTotalDebrisSQL = "INSERT IGNORE INTO total_debris (id, total) VALUES (1, 0)";
        try (PreparedStatement statement = connection.prepareStatement(initializeTotalDebrisSQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Could not initialize the total debris row.");
        }
    }

    // Get the balance of a specific player
    public double getBalance(String playerUUID) {
        String query = "SELECT balance FROM player_balances WHERE player_uuid = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUUID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Update the balance of a specific player
    public void updateBalance(String playerUUID, double newBalance) {
        String query = "REPLACE INTO player_balances (player_uuid, balance) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUUID);
            statement.setDouble(2, newBalance);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get the total debris in the system
    public double getTotalDebris() {
        String query = "SELECT total FROM total_debris WHERE id = 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Update the total debris in the system
    public void updateTotalDebris(double totalDebris) {
        String query = "UPDATE total_debris SET total = ? WHERE id = 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, totalDebris);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeDebris(double amount) {
        String query = "UPDATE total_debris SET total = total - ? WHERE id = 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get balances for all players
    public Map<String, Double> getAllBalances() {
        Map<String, Double> balances = new HashMap<>();
        String query = "SELECT player_uuid, balance FROM player_balances";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    balances.put(resultSet.getString("player_uuid"), resultSet.getDouble("balance"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balances;
    }

    // Migrate data from config.yml to the database
    public void migrateDataFromConfig(ConfigManager configManager) {
        if (configManager.getConfig().contains("player-info")) {
            for (String playerUUID : configManager.getConfig().getConfigurationSection("player-info").getKeys(false)) {
                double balance = configManager.getConfig().getDouble("player-info." + playerUUID, 0);
                updateBalance(playerUUID, balance);
                configManager.getConfig().set("player-info." + playerUUID, null); // Delete the data from the config
            }
            configManager.saveConfig(); // Save the changes to the config file
        }

        double totalDebris = configManager.getConfig().getDouble("total_debris", 0);
        if (totalDebris > 0) {
            updateTotalDebris(totalDebris);
            configManager.getConfig().set("total_debris", null); // Delete the data from the config
            configManager.saveConfig(); // Save the changes to the config file
        }
    }
}
