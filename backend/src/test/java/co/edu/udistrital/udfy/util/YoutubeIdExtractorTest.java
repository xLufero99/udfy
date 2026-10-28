package co.edu.udistrital.udfy.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class YoutubeIdExtractorTest {
    private final YoutubeIdExtractor extractor = new YoutubeIdExtractor();

    @Test
    void extractFromWatchUrl() {
        String id = extractor.extract("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        assertEquals("dQw4w9WgXcQ", id);
    }

    @Test
    void extractFromShortUrl() {
        String id = extractor.extract("https://youtu.be/dQw4w9WgXcQ");
        assertEquals("dQw4w9WgXcQ", id);
    }

    @Test
    void extractFromShortsUrl() {
        String id = extractor.extract("https://www.youtube.com/shorts/dQw4w9WgXcQ");
        assertEquals("dQw4w9WgXcQ", id);
    }

    @Test
    void extractFromEmbedUrl() {
        String id = extractor.extract("https://www.youtube.com/embed/dQw4w9WgXcQ");
        assertEquals("dQw4w9WgXcQ", id);
    }

    @Test
    void rejectInvalidUrl() {
        assertThrows(IllegalArgumentException.class, () -> extractor.extract("https://example.com/watch?v=dQw4w9WgXcQ"));
    }

    @Test
    void rejectInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> extractor.extract("https://www.youtube.com/watch?v=short"));
    }
}
