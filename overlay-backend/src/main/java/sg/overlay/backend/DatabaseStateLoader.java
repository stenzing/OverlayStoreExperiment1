package sg.overlay.backend;

import sg.overlay.BaseVolume;
import sg.overlay.Entry;
import sg.overlay.IVolume;
import sg.overlay.Metadata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseStateLoader {
    public static IVolume fromDatabase(Connection connection, String volumeName) throws SQLException {
        var volume = BaseVolume.ofEntries();
        var stmt = connection.prepareStatement("SELECT path, content, is_deleted, version, volume_id FROM entries WHERE volume_id = ?");
        stmt.setString(1, volumeName);
        var rs = stmt.executeQuery();
        while (rs.next()) {
            if (rs.getBoolean("is_deleted")) {
                volume.addEntry(new Entry(
                        rs.getString("key"),
                        null,
                        Metadata.DELETED));
            } else {
                volume.addEntry(new Entry(
                        rs.getString("key"),
                        rs.getString("content"),
                        Metadata.DEFAULT));
            }
        }
        return volume;
    }
}
