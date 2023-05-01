# Kotest Extensions H2

![Maintenance](https://img.shields.io/maintenance/yes/2023)
[![License](https://img.shields.io/github/license/LeoColman/kotest-extensions-h2)](LICENSE)
![Maven Central](https://img.shields.io/maven-central/v/br.com.colman/kotest-extensions-h2)


# Usage
Add the library to the project:
```kotlin
testImplementation("br.com.colman:kotest-extensions-h2:VERSION")
```

Add the listener to a test where you're using H2

```kotlin

class MyTest : FunSpec({
  val listener = listener(H2Listener())
  
  test("My h2 test") {
    val dataSource = listener.dataSource
    // ...
  }
})

```

