package hr.from.juricamigac.mockclean.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;

import org.junit.Test;

import hr.from.juricamigac.mockclean.settings.AemJsonCleanerSettings;

public final class JsonCleanerIntegrationTest {

    @Test
    public void cleansProvidedAemPageFixtureEndToEnd() throws Exception {
        final String input = readResource("/fixtures/project-page-input.json");
        final String expected = readResource("/fixtures/project-page-cleaned.json");

        final JsonCleanResult result = JsonCleaner.clean(input, new LinkedHashSet<>(AemJsonCleanerSettings.DEFAULT_EXCLUDED_VARIABLES));

        assertEquals(15, result.getRemovedCount());
        assertEquals(expected, result.getJson());
    }

    private static String readResource(final String path) throws IOException {
        try (final InputStream inputStream = JsonCleanerIntegrationTest.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Missing test resource: " + path);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
