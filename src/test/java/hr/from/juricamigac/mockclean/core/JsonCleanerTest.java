package hr.from.juricamigac.mockclean.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

public final class JsonCleanerTest {

    @Test
    public void removesCustomProjectSpecificProperties() throws Exception {
        final String input = """
                {
                  "project": {
                    "jcr:primaryType": "nt:unstructured",
                    "sling:resourceType": "example/components/item",
                    "cq:template": "/conf/example/settings/wcm/templates/page",
                    "name": "Item"
                  }
                }
                """;

        final Set<String> excluded = new LinkedHashSet<>();
        excluded.add("jcr:primaryType");
        excluded.add("sling:resourceType");
        excluded.add("cq:template");

        final JsonCleanResult result = JsonCleaner.clean(input, excluded);

        assertEquals(3, result.getRemovedCount());
        assertFalse(result.getJson().contains("jcr:primaryType"));
        assertFalse(result.getJson().contains("sling:resourceType"));
        assertFalse(result.getJson().contains("cq:template"));
    }

    @Test
    public void reportsInvalidJson() {
        final Set<String> excluded = Set.of("jcr:created");

        try {
            JsonCleaner.clean("{", excluded);
            fail("Expected JsonCleanerException");
        } catch (final JsonCleanerException exception) {
            assertFalse(exception.getMessage() == null || exception.getMessage().isBlank());
        }
    }
}
