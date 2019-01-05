# Ticktok Java client
[![CircleCI](https://circleci.com/gh/ticktok-io/ticktok-java-client.svg?style=svg)](https://circleci.com/gh/ticktok-io/ticktok-java-client)
[![License](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**Ticktok-Java-Client** is the official java sdk for [Ticktok.io](https://ticktok.io/).

## Import sdk
artifacts available @ [*bintray repositories*](https://bintray.com/ticktok-io/maven/ticktok-java-client)

### Gradle
```
dependencies {
  ...
    compile 'io.ticktok:ticktok-java-client:0.3.0'
}

```
### Maven
```
<dependency>
  <groupId>io.ticktok</groupId>
  <artifactId>ticktok-java-client</artifactId>
  <version>0.3.0</version>
</dependency>
```

## Quick Start
```java
new Ticktok(new TicktokOptions("<Ticktok-Domain>", "<Ticktok token>")).
        newClock("my_clock_name").
        on("every.5.seconds").
        invoke(() -> System.out.print("tick message by defined schedule"));
}
```

## Related
[Ticktok.io](https://ticktok.io) - ticktok.io website

