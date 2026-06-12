package hr.from.juricamigac.mockclean.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public final class AemJsonCleanerSettingsTest {

    @Test
    public void parsesAndNormalizesVariablesText() {
        final List<String> variables = AemJsonCleanerSettings.parseVariablesText("""
                jcr:primaryType

                  sling:resourceType
                cq:template
                sling:resourceType
                """);

        assertEquals(List.of("jcr:primaryType", "sling:resourceType", "cq:template"), variables);
    }

    @Test
    public void resetsToDefaultsAndReturnsNormalizedSet() {
        final AemJsonCleanerSettings settings = new AemJsonCleanerSettings();
        settings.setExcludedVariables(new ArrayList<>(Arrays.asList("  sling:resourceType  ", null, "jcr:primaryType", "sling:resourceType")));

        assertEquals(Set.of("sling:resourceType", "jcr:primaryType"), settings.getExcludedVariableSet());

        settings.resetToDefaults();

        assertTrue(settings.getExcludedVariables().contains("jcr:primaryType"));
        assertTrue(settings.getExcludedVariables().contains("sling:resourceType"));
        assertTrue(settings.getExcludedVariables().contains("cq:template"));
        assertTrue(settings.getExcludedVariables().contains("jcr:lastModifiedBy"));
    }
}
