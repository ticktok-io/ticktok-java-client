# Ticktok Java client
[![CircleCI](https://circleci.com/gh/ticktok-io/ticktok-java-client.svg?style=svg)](https://circleci.com/gh/ticktok-io/ticktok-java-client)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

**Ticktok-Java-Client** is the official java sdk for *[Ticktok.io](https://ticktok.io/)* service.

## Requirements
```
  - jdk8
```

## Import sdk
artifacts available @ [*bintray repositories*](https://bintray.com/ticktok-io/maven/ticktok-java-client)

### Gradle
```
dependencies {
  ...
    compile 'io.ticktok:ticktok-java-client:<version>'
}

```

### Maven
```
<dependency>
  <groupId>io.ticktok</groupId>
  <artifactId>ticktok-java-client</artifactId>
  <version>_version_</version>
</dependency>
```

## Example
```java
new Ticktok(new TicktokOptions("<Ticktok-Domain>", "<Ticktok token>")).
        newClock("my_clock_name").
        on("every.5.seconds").
        invoke(() -> {
            System.out.print("tick message by defined schedule");
        });
}
```

## Related
[Ticktok.io](https://github.com/ticktok-io/ticktok.io) - ticktok.io service

## License
MIT

-----

Come & chat with us on [Slack](https://ticktokio.slack.com/messages/CF0DYKN0Y/details/)
