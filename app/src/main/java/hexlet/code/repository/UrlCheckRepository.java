package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck check) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, check.getUrlId());
            ps.setInt(2, check.getStatusCode());
            ps.setString(3, check.getH1());
            ps.setString(4, check.getTitle());
            ps.setString(5, check.getDescription());
            ps.setTimestamp(6, check.getCreatedAt());
            ps.executeUpdate();
            var keys = ps.getGeneratedKeys();
            if (keys.next()) {
                check.setId(keys.getLong(1));
            }
        }
    }

    public static List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, urlId);
            var rs = ps.executeQuery();
            var checks = new ArrayList<UrlCheck>();
            while (rs.next()) {
                var check = new UrlCheck(
                        rs.getLong("id"),
                        rs.getLong("url_id"),
                        rs.getInt("status_code"),
                        rs.getString("h1"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
                checks.add(check);
            }
            return checks;
        }
    }

    public static UrlCheck findLastByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC LIMIT 1";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, urlId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new UrlCheck(
                        rs.getLong("id"),
                        rs.getLong("url_id"),
                        rs.getInt("status_code"),
                        rs.getString("h1"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
            }
            return null;
        }
    }


}
