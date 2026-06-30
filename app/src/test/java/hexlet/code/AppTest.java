package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private Javalin app;

    @BeforeAll
    static void initDb() throws SQLException {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
        var dataSource = new HikariDataSource(config);
        BaseRepository.dataSource = dataSource;

        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS urls");
            stmt.execute("CREATE TABLE urls (" + "id BIGINT AUTO_INCREMENT PRIMARY KEY, " + "name VARCHAR(255) NOT NULL UNIQUE, " + "created_at TIMESTAMP NOT NULL)");
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        app = App.getApp();

        try (var conn = BaseRepository.dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM urls");
            stmt.execute("ALTER TABLE urls ALTER COLUMN id RESTART WITH 1");
        }
    }

    @Test
    void testRootPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    void testAddUrlSuccess() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.com");

            List<Url> urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
            var savedUrl = urls.get(0);
            assertThat(savedUrl.getName()).isEqualTo("https://example.com");
            assertThat(savedUrl.getId()).isEqualTo(1L);
        });
    }

    @Test
    void testAddDuplicateUrl() {
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=https://example.com");
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.com");
            List<Url> urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
            assertThat(urls.get(0).getId()).isEqualTo(1L);
        });
    }


    @Test
    void testAddInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=not-a-url";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(422);
            var body = response.body().string();
            assertThat(body).contains("Некорректный URL");
            assertThat(body).contains("not-a-url");
        });
    }

    @Test
    void testUrlsList() {
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=https://example.com");
            client.post("/urls", "url=https://google.com");

            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            var body = response.body().string();
            assertThat(body).contains("https://example.com");
            assertThat(body).contains("https://google.com");
            assertThat(body).contains("/urls/1");
            assertThat(body).contains("/urls/2");
        });
    }

    @Test
    void testShowUrl() {
        JavalinTest.test(app, (server, client) -> {
            client.post("/urls", "url=https://example.com");

            var response = client.get("/urls/1");
            assertThat(response.code()).isEqualTo(200);
            var body = response.body().string();
            assertThat(body).contains("https://example.com");
            assertThat(body).contains("Дата создания");
        });
    }

    @Test
    void testShowNonExistentUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

}
