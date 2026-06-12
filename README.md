# MockClean AEM

An IntelliJ IDEA plugin for cleaning AEM mock JSON files by recursively removing configured metadata properties from the current editor document.

This is useful when working with AEM unit-test fixtures or content exports that contain repository noise such as `jcr:primaryType`, `sling:resourceType`, or `cq:template`.

## What it removes

By default, the plugin removes these properties anywhere in the JSON tree:

```text
jcr:created
cq:lastModified
jcr:createdBy
cq:lastModifiedBy
jcr:lastModified
cq:lastPublished
cq:lastPublishedBy
cq:lastReplicated
cq:lastReplicatedBy
jcr:lastModifiedBy
jcr:primaryType
cq:template
sling:resourceType
```

You can customize this list in the plugin settings.

## Highlights

- Recursively removes configured properties from nested objects and arrays
- Preserves the rest of the JSON structure
- Rewrites the current editor document in place
- Supports custom cleanup rules through Settings

## Requirements

- IntelliJ IDEA 2025.1 or newer
- Java 21 for building from source

## Install

### From source

```bash
gradle buildPlugin
```

The installable plugin ZIP is written to:

```text
build/distributions/
```

### From ZIP

1. Open IntelliJ IDEA.
2. Go to `Settings | Plugins`.
3. Click the gear icon.
4. Select `Install Plugin from Disk...`.
5. Choose the plugin ZIP from `build/distributions/`.
6. Restart IntelliJ IDEA if prompted.

## Usage

1. Open a JSON file in the editor.
2. Run `Tools | Clean MockClean AEM JSON`.
3. The plugin removes the configured property names from the document and saves the updated file.

If the file is not valid JSON, nothing is changed and IntelliJ shows an error notification.

## Configure cleanup rules

1. Open `Settings | Tools | MockClean AEM`.
2. Add one property name per line.
3. Apply the changes.

You can also restore the default property list from the settings panel.

## Keyboard shortcut

1. Open `Settings | Keymap`.
2. Search for `Clean MockClean AEM JSON`.
3. Assign any shortcut you prefer.

## Development

### Run tests

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-home ./gradlew test
```

### Project layout

```text
src/main/java
src/main/resources
src/test/java
src/test/resources
```

## Example

Input:

```json
{
  "jcr:primaryType": "cq:Page",
  "jcr:content": {
    "jcr:primaryType": "cq:PageContent",
    "jcr:title": "Join us",
    "cq:template": "/conf/cyber64/settings/wcm/templates/page-content",
    "sling:resourceType": "cyber64/components/page"
  }
}
```

Output:

```json
{
  "jcr:primaryType": "cq:Page",
  "jcr:content": {
    "jcr:title": "Join us"
  }
}
```

## Notes

- The cleaner uses Gson internally for JSON parsing and serialization.
- The plugin focuses on property removal, not semantic rewriting of JSON content.
