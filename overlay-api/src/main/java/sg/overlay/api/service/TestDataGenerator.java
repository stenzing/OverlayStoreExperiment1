package sg.overlay.api.service;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDataGenerator {
    private static final Random rnd = new SecureRandom();

    public static void generateData(Connection con) {
        try {
            var stmt = con.createStatement();
            IntStream.range(1, 500000)
                    .boxed()
                    .map("path%06d"::formatted)
                    .peek(s -> {
                        if (rnd.nextFloat() < 0.8) createEntry(s, "01", false, stmt);
                    })
                    .peek(s -> {
                        if (rnd.nextFloat() < 0.4) createEntry(s, "02", rnd.nextBoolean(), stmt);
                    })
                    .peek(s -> {
                        if (rnd.nextFloat() < 0.2) createEntry(s, "03a", rnd.nextFloat() < 0.3, stmt);
                    })
                    .peek(s -> {
                        if (rnd.nextFloat() < 0.2) createEntry(s, "03b", rnd.nextFloat() < 0.1, stmt);
                    })
                    .toList();
            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createEntry(String path, String volume, boolean isDeleted, Statement stmt) {
        var content = rnd.ints(500).boxed().map(Object::toString).collect(Collectors.joining());
        try {
            stmt.execute("INSERT INTO entries VALUES ('" + path + "', '" + (isDeleted ? "" : content) + "', " + (isDeleted ? "true" : "false") + ", 1, '" + volume + "')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
