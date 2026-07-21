package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.UrlUtils;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    private static Javalin app;
    private static MockWebServer mockWebServer;

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixtures(String fileName) throws IOException {
        var filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    @BeforeAll
    static void initMock() throws IOException {
        mockWebServer = new MockWebServer();
        var body = readFixtures("index.html");
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .setResponseCode(200));
        mockWebServer.start();
    }

    @AfterAll
    static void stopMockServer() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Указываем тестовую БД для H2
        System.setProperty("DATABASE_URL", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
        app = App.getApp();

        // Очистка таблиц и сброс автоинкремента
        try (var conn = BaseRepository.dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM url_checks");
            stmt.execute("DELETE FROM urls");
            stmt.execute("ALTER TABLE urls ALTER COLUMN id RESTART WITH 1");
            stmt.execute("ALTER TABLE url_checks ALTER COLUMN id RESTART WITH 1");
        }
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testIndex() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testShow() {
        JavalinTest.test(app, (server, client) -> {
            var link = "url=https://ru.hexlet.io/programs/java/projects/72";
            client.post(NamedRoutes.urlsPath(), link);
            var response = client.get(NamedRoutes.urlPath("1"));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://ru.hexlet.io");
            assertThat(UrlRepository.findByName("https://ru.hexlet.io").isPresent()).isTrue();
        });
    }

    @Test
    void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var link = "url=https://ru.hexlet.io/programs/java/projects/72";
            var response = client.post(NamedRoutes.urlsPath(), link);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://ru.hexlet.io");
            assertThat(UrlRepository.findByName("https://ru.hexlet.io").isPresent()).isTrue();
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/500");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testCheckUrl() throws IOException {
        var testUrl = mockWebServer.url("/").toString();

        JavalinTest.test(app, (server, client) -> {
            var url = new Url(testUrl);
            UrlRepository.save(url);
            var urlId = url.getId();

            client.post(NamedRoutes.urlPathChecks(urlId));

            var checks = UrlCheckRepository.findByUrlId(urlId);
            assertThat(checks).hasSize(1);
            var check = checks.get(0);
            assertEquals(200, check.getStatusCode());
            assertEquals("TestTitle", check.getTitle());
            assertEquals("TestH1", check.getH1());
            assertEquals("TestDescription", check.getDescription());
        });
    }

    @Test
    void testAddInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=not-a-url";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(422);
            var body = response.body().string();
            assertThat(body).contains("Некорректный URL");
            assertThat(body).contains("not-a-url");
        });
    }

    @Test
    void testAddDuplicateUrl() {
        JavalinTest.test(app, (server, client) -> {
            client.post(NamedRoutes.urlsPath(), "url=https://example.com");
            var response = client.post(NamedRoutes.urlsPath(), "url=https://example.com");
            assertThat(response.code()).isEqualTo(200);
            var body = response.body().string();
            assertThat(body).contains("https://example.com");
            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
        });
    }

    @Test
    void testCheckUrlFailure() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        var testUrl = mockWebServer.url("/").toString();

        JavalinTest.test(app, (server, client) -> {
            var url = new Url(testUrl);
            UrlRepository.save(url);
            var urlId = url.getId();

            client.post(NamedRoutes.urlPathChecks(urlId));

            var checks = UrlCheckRepository.findByUrlId(urlId);
            assertThat(checks).isEmpty();
        });
    }
    @Test
    void testFindByUrlIdEmpty() throws Exception {
        var checks = UrlCheckRepository.findByUrlId(999L);
        assertThat(checks).isEmpty();
    }

    @Test
    void testFindLastByUrlIdEmpty() throws Exception {
        var lastCheck = UrlCheckRepository.findLastByUrlId(999L);
        assertThat(lastCheck).isEmpty();
    }
    @Test
    void testBaseRepository() {
        BaseRepository repo = new BaseRepository();
        assertThat(repo).isNotNull();
    }
    @Test
    void testNormalizeUrlWithoutProtocol() throws Exception {
        assertThat(UrlUtils.normalizeUrl("example.com")).isEqualTo("http://example.com");
    }

    @Test
    void testNormalizeUrlWithPort() throws Exception {
        assertThat(UrlUtils.normalizeUrl("example.com:8080")).isEqualTo("http://example.com:8080");
    }
    @Test
    void testNormalizeUrlWithLocalhost() throws Exception {
        assertThat(UrlUtils.normalizeUrl("localhost:8080")).isEqualTo("http://localhost:8080");
    }

    @Test
    void testNormalizeUrlInvalid() {
        assertThatThrownBy(() -> UrlUtils.normalizeUrl("not-a-url"))
                .isInstanceOf(URISyntaxException.class);
    }
    @Test
    void testNormalizeUrlWithProtocol() throws Exception {
        assertThat(UrlUtils.normalizeUrl("https://example.com")).isEqualTo("https://example.com");
    }
}
