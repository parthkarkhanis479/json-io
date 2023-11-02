package com.cedarsoftware.util.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class URLTest
{

    private static final String LOCALHOST = "http://localhost";
    private static final String OUTSIDE_DOMAIN = "https://foo.bar.com";

    private static Stream<Arguments> argumentsForOldFormatValidation() {
        return Stream.of(
                Arguments.of("old-format-file-relative-path.json", "file:/path/to/file"),
                Arguments.of("old-format-file-server-path.json", "file://servername/path/to/file.json"),
                Arguments.of("old-format-ftp-full-with-port.json", "ftp://user:password@host:8192/foo/bar.txt"),
                Arguments.of("old-format-ftp-user-no-password.json", "ftp://user@bar.com/foo/bar.txt"),
                Arguments.of("old-format-ftp-user-password.json", "ftp://user:password@bar.com/foo/bar.txt"),
                Arguments.of("old-format-http-host-file-with-parameters-and-ref.json", "http://domain.com:8080/file/path/filename.html?key=value&extra=more#AnchorLocation"),
                Arguments.of("old-format-http-host-path-file-with-parameters.json", "https://localhost:80/some/path?foo=1&bar=2&qux=pickle"),
                Arguments.of("old-format-http-host-path-ref.json", "http://domain.com:8080/file/path/filename.html#AnchorLocation"),
                Arguments.of("old-format-http-host-port.json", "http://localhost:8080"),
                Arguments.of("old-format-http-host-port-file.json", "https://localhost:80/some/path"),
                Arguments.of("old-format-http-localhost.json", LOCALHOST),
                Arguments.of("old-format-http-with-encoded-param.json", "https://foo.bar.com/path/file.html?text=Hello+G%C3%BCnter"),
                Arguments.of("old-format-http-with-encoded-path.json", "https://foo.bar.com/path/foo%20bar.html"),
                Arguments.of("old-format-https-host-external.json", "https://google.com"),
                Arguments.of("old-format-jar.json", "jar:file:/c://my.jar!/"),
                Arguments.of("old-format-jar-file.json", "jar:file:/c://my.jar!/com/mycompany/MyClass.class")
        );
    }
    @ParameterizedTest
    @MethodSource("argumentsForOldFormatValidation")
    void testUrl_readingJsonWithOldFormat_stillWorks(String fileName, String expectedUrl) throws Exception
    {
        String json = TestUtil.fetchResource("url/" + fileName);
        URL actual = (URL) TestUtil.toJava(json);

        assertThat(actual.toString()).isEqualTo(expectedUrl);
    }

    private static Stream<Arguments> argumentsForUrlTesting() {
        return Stream.of(
                Arguments.of("https://domain.com"),
                Arguments.of("http://localhost"),
                Arguments.of("http://localhost:8080"),
                Arguments.of("http://localhost:8080/file/path"),
                Arguments.of("http://localhost:8080/path/file.html"),
                Arguments.of("http://localhost:8080/path/file.html?foo=1&bar=2"),
                Arguments.of("http://localhost:8080/path/file.html?foo=bar&qux=quy#AnchorLocation"),
                Arguments.of("https://foo.bar.com/"),
                Arguments.of("https://foo.bar.com/path/foo%20bar.html"),
                Arguments.of("https://foo.bar.com/path/file.html?text=Hello+G%C3%BCnter"),
                Arguments.of("ftp://user@bar.com/foo/bar.txt"),
                Arguments.of("ftp://user:password@host/foo/bar.txt"),
                Arguments.of("ftp://user:password@host:8192/foo/bar.txt"),
                Arguments.of("file:/path/to/file"),
                Arguments.of("file://localhost/path/to/file.json"),
                Arguments.of("file://servername/path/to/file.json"),
                Arguments.of("jar:file:/c://my.jar!/"),
                Arguments.of("jar:file:/c://my.jar!/com/mycompany/MyClass.class")
        );
    }
    @ParameterizedTest
    @MethodSource("argumentsForUrlTesting")
    void testToString_withDifferentUrls_works(String input) throws Exception {
        URL url = new URL(input);
        assertThat(url.toString()).isEqualTo(input);
    }

    @ParameterizedTest
    @MethodSource("argumentsForUrlTesting")
    void testSerialization_withDifferentUrls_works(String input) throws Exception {
        URL url = new URL(input);
        String json = TestUtil.toJson(url);

        TestUtil.printLine("json=" + json);
        Object read = TestUtil.toJava(json);
        assertThat(read).isEqualTo(url);
    }

    @Test
    void testURL_hasTypeAndUrl_and_noOtherUrlParams() throws Exception {
        URL url = new URL(OUTSIDE_DOMAIN);
        String json = TestUtil.toJson(url);
        assertThatJsonIsNewStyle(json);
    }

    private static void assertThatJsonIsNewStyle(String json) {
        assertThat(json)
                .contains("@type")
                .doesNotContain("protocol")
                .doesNotContain("file")
                .doesNotContain("ref")
                .doesNotContain("authority")
                .doesNotContain("host")
                .doesNotContain("port");
    }

    @Test
    void testUrl_inGenericSubobject_serializeBackCorrectly() throws Exception {
        URL url = new URL(LOCALHOST);
        GenericSubObject initial = new GenericSubObject<>(url);
        String json = TestUtil.toJson(initial);

        TestUtil.printLine("json=" + json);
        GenericSubObject actual = (GenericSubObject)TestUtil.toJava(json);
        assertThat(actual.getObject()).isEqualTo(initial.getObject());
    }

    @Test
    void testUrl_inNestedObject_serializeBackCorrectly() throws Exception {
        URL url = new URL(OUTSIDE_DOMAIN);
        NestedUrl initial = new NestedUrl(url);
        String json = TestUtil.toJson(initial);
        assertThatJsonIsNewStyle(json);

        TestUtil.printLine("json=" + json);
        NestedUrl actual = (NestedUrl)TestUtil.toJava(json);

        assertThat(actual.getUrl()).isEqualTo(initial.getUrl());
    }

    @Test
    void testURL_referencedInArray() throws Exception {
        URL url = new URL(OUTSIDE_DOMAIN);
        List<URL> list = List.of(url, url, url, url, url);
        String json = TestUtil.toJson(list);

        List<URL> actual = (List<URL>)TestUtil.toJava(json);

        assertThat(actual).containsAll(list);
    }

    @Test
    void testURL_referencedInObject() throws Exception {
        var expected = new NestedTwice(new URL(OUTSIDE_DOMAIN));

        String json = TestUtil.toJson(expected);

        var actual = (NestedTwice)TestUtil.toJava(json);

        assertThat(expected.getUrl1()).isEqualTo(actual.getUrl1());
        assertThat(expected.getUrl2()).isEqualTo(actual.getUrl2());
    }

    /**
     * testMethod to validate if url being created is proper when using createUrlOldWay method
     */
    @Test
    void testCreateUrlOldWay() {
        JsonObject jObj = new JsonObject();
        jObj.put("protocol", "http");
        jObj.put("host", "127.0.0.0");
        Long port = 8090L;
        jObj.put("port", port);
        Readers.URLReader urlReader = new Readers.URLReader();
        URL url = null;
        try {
            url = urlReader.createUrlOldWay(jObj);
        } catch (MalformedURLException e) {
            fail("Malformed Exception thrown :"+e);
        }
        assertTrue(url.toString().equals("http://127.0.0.0:8090"));

    }



    private static class NestedUrl {
        private final URL url;

        public NestedUrl(URL url) {
            this.url = url;
        }

        public URL getUrl() {
            return this.url;
        }
    }

    private static class NestedTwice {
        private final URL url1;

        private final URL url2;

        public NestedTwice(URL url) {
            this.url1 = url;
            this.url2 = url;
        }

        public URL getUrl1() {
            return url1;
        }

        public URL getUrl2() {
            return url2;
        }
    }
}
