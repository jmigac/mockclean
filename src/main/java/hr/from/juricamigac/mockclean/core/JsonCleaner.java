package hr.from.juricamigac.mockclean.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public final class JsonCleaner {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
                                                      .disableHtmlEscaping()
                                                      .create();

    private JsonCleaner() {
    }

    public static JsonCleanResult clean(final String json, final Set<String> excludedPropertyNames) throws JsonCleanerException {
        if (json == null) {
            throw new JsonCleanerException("JSON content is empty.");
        }
        if (excludedPropertyNames == null || excludedPropertyNames.isEmpty()) {
            return new JsonCleanResult(ensureTrailingNewline(json), 0);
        }

        final JsonElement root;
        try {
            root = JsonParser.parseString(json);
        } catch (final JsonSyntaxException exception) {
            throw new JsonCleanerException(exception.getMessage() != null ? exception.getMessage() : "Invalid JSON.");
        }

        final Counter counter = new Counter();
        removeExcludedProperties(root, excludedPropertyNames, counter);

        return new JsonCleanResult(ensureTrailingNewline(GSON.toJson(root)), counter.value);
    }

    private static String ensureTrailingNewline(final String value) {
        if (value.endsWith("\n")) {
            return value;
        }
        return value + "\n";
    }

    private static void removeExcludedProperties(final JsonElement value, final Set<String> excludedPropertyNames, final Counter counter) {
        if (value == null || value.isJsonNull() || value.isJsonPrimitive()) {
            return;
        }

        if (value.isJsonObject()) {
            final JsonObject object = value.getAsJsonObject();
            final Iterator<Map.Entry<String, JsonElement>> iterator = object.entrySet()
                                                                            .iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, JsonElement> entry = iterator.next();
                if (excludedPropertyNames.contains(entry.getKey())) {
                    iterator.remove();
                    counter.value++;
                } else {
                    removeExcludedProperties(entry.getValue(), excludedPropertyNames, counter);
                }
            }
            return;
        }

        if (value.isJsonArray()) {
            final JsonArray array = value.getAsJsonArray();
            for (final JsonElement item : array) {
                removeExcludedProperties(item, excludedPropertyNames, counter);
            }
        }
    }

    private static final class Counter {

        private int value;

    }

}
