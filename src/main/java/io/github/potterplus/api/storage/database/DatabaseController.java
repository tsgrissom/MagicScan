package io.github.potterplus.api.storage.database;

import io.github.potterplus.api.storage.flatfile.DatabaseFile;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.github.potterplus.api.misc.PluginLogger.*;

@RequiredArgsConstructor
public class DatabaseController {

    @NonNull
    private final String host, db, user, pass;

    public DatabaseController(DatabaseFile<?> dbFile) {
        this(dbFile.getHost(), dbFile.getDatabase(), dbFile.getUsername(), dbFile.getPassword());
    }

    @Getter
    private Connection connection;

    private void openConnection() throws SQLException, ClassNotFoundException {
        if (this.connection != null && !this.connection.isClosed()) {
            return;
        }

        Class.forName("com.mysql.jdbc.Driver");

        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.db, this.user, this.pass);
    }

    public void connect() {
        try {
            openConnection();

            if (this.connection != null) {
                atInfo("Connected to database.");
            }
        } catch (Exception e) {
            atSevere("Failed to connect to database.");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (!this.connection.isClosed()) {
                this.connection.close();
                atInfo("Disconnected from database.");
            }
        } catch (Exception e) {
            atWarn("Failed to disconnect from database.");
            e.printStackTrace();
        }
    }
}
