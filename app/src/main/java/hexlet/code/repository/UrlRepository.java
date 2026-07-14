package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, url.getName());
            ps.setTimestamp(2, Timestamp.valueOf(url.getCreatedAt()));
            ps.executeUpdate();
            var keys = ps.getGeneratedKeys();
            if (keys.next()) {
                url.setId(keys.getLong(1));
            }
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Url(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
            return Optional.empty();
        }
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Url(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
            return Optional.empty();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT * FROM urls ORDER BY id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery(sql);
            var list = new ArrayList<Url>();
            while (rs.next()) {
                list.add(new Url(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
            return list;
        }
    }
}
