package hr.from.juricamigac.mockclean.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

@State(name = "AemJsonCleanerSettings",
       storages = @Storage("aem-json-cleaner.xml"))
public final class AemJsonCleanerSettings implements PersistentStateComponent<AemJsonCleanerSettings.PluginState> {

    public static final List<String> DEFAULT_EXCLUDED_VARIABLES = List.of("jcr:created", "cq:lastModified", "jcr:createdBy", "cq:lastModifiedBy", "jcr:lastModified", "cq:lastPublished",
                                                                          "cq:lastPublishedBy", "cq:lastReplicated", "cq:lastReplicatedBy", "jcr:lastModifiedBy");

    private PluginState state = new PluginState();

    public static AemJsonCleanerSettings getInstance() {
        return ApplicationManager.getApplication()
                                 .getService(AemJsonCleanerSettings.class);
    }

    @Override
    public PluginState getState() {
        this.ensureDefaults();
        return this.state;
    }

    @Override
    public void loadState(final @NotNull PluginState loadedState) {
        this.state = loadedState;
        this.ensureDefaults();
    }

    public Set<String> getExcludedVariableSet() {
        this.ensureDefaults();
        return normalize(this.state.excludedVariables);
    }

    public List<String> getExcludedVariables() {
        this.ensureDefaults();
        return new ArrayList<>(normalize(this.state.excludedVariables));
    }

    public void setExcludedVariables(final List<String> variables) {
        this.state.excludedVariables = new ArrayList<>(normalize(variables));
        this.ensureDefaults();
    }

    public void resetToDefaults() {
        this.state.excludedVariables = new ArrayList<>(DEFAULT_EXCLUDED_VARIABLES);
    }

    public String getExcludedVariablesAsText() {
        return String.join("\n", this.getExcludedVariables());
    }

    public static List<String> parseVariablesText(final String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(normalize(Arrays.asList(text.split("\\R"))));
    }

    private static LinkedHashSet<String> normalize(final List<String> variables) {
        final LinkedHashSet<String> normalized = new LinkedHashSet<>();
        if (variables == null) {
            return normalized;
        }
        for (final String variable : variables) {
            if (variable == null) {
                continue;
            }
            final String trimmed = variable.trim();
            if (!trimmed.isEmpty()) {
                normalized.add(trimmed);
            }
        }
        return normalized;
    }

    private void ensureDefaults() {
        if (this.state.excludedVariables == null) {
            this.state.excludedVariables = new ArrayList<>(DEFAULT_EXCLUDED_VARIABLES);
        }
    }

    public static final class PluginState {

        public List<String> excludedVariables = new ArrayList<>(DEFAULT_EXCLUDED_VARIABLES);

    }

}
