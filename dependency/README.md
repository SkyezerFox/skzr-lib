# skzr-lib:dependency

Runtime dependency downloading for minimization of jar size.

## Overview

`skzr-lib:dependency` is a module designed for minimizing the size of jars shipped to server owners. By downloading jar dependencies at runtime, the initial jar size is greatly reduced.

## Examples

```kotlin
// declare the root dependency block
dependency {
    // tell dependency to use maven central
    useDefaultRepositoryConfig()
    dependencies {
        // depend on OkHttp
        implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    }
}
```

## License

This module is licensed under the MIT license. See [the LICENSE file](/LICENSE) for more information.
