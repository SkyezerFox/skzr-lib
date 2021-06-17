# skzr-lib:config

Configuration and migration framework.

## Overview

When developing plugins for Minecraft, configuration - and more importantly - updating between config versions can be a massive pain. This module implements a seamless way to manage your configuration, and perform config migrations between different config versions. This

## Examples

### Kotlin

```kotlin
// declare your config schema
val version1 = schema {
    // supports different field types
    string("name")
    string("age")
}

val version2 = schema {
    // supports default values
    string("first-name", "Skye")
    int("age")
}

val version1_to_version2 = migration(version1, version2) {
    rename {
        "name" to "first-name"
    }
    migrate {
        string("age") to int("age") {
            it.toString()
        }
    }
}
```

### Java

```java
Schema version1 = new SchemaBuilder()
    .string("name")
    .string("age")
    .build();

Schema version2 = new SchemaBuilder()
    .string("first-name", "Skye")
    .string("last-name", "Elliot")
    .int("age")
    .build();

Migration version1_to_version2 = new MigrationBuilder(version1, version2)
    .rename(Field.String("name"), Field.String("first-name"))
    .migrate(Field.String("age"), Field.Int("age"), (age) -> Integer.valueOf(age))
    .build();
```
