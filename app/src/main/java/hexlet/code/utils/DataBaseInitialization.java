package hexlet.code.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.App;
import hexlet.code.repository.BaseRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DataBaseInitialization {
    private static String getDatabaseUrl() {

        return System.getenv().getOrDefault("DATABASE_URL", "jdbc:h2:mem:project");
    }

    public static void initDatabase() throws Exception {
        String dbUrl = getDatabaseUrl();
        String dbUser = System.getenv().getOrDefault("USERNAME", "");
        String dbPassword = System.getenv().getOrDefault("PASSWORD", "");

        var config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        if (!dbUser.isEmpty()) {
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
        }
        config.setMaximumPoolSize(10);

        var dataSource = new HikariDataSource(config);
        BaseRepository.dataSource = dataSource;

        var schemaStream = App.class.getClassLoader().getResourceAsStream("schema.sql");
        if (schemaStream == null) {
            throw new RuntimeException("schema.sql not found");
        }
        var sql = new BufferedReader(new InputStreamReader(schemaStream))
                .lines().collect(Collectors.joining("\n"));
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
