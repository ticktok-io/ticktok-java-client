# Ticktok Java client
[![CircleCI](https://circleci.com/gh/ticktok-io/ticktok-java-client.svg?style=svg)](https://circleci.com/gh/ticktok-io/ticktok-java-client)
[![License](http://img.shields.io/:license-apache2.0-blue.svg)](http://doge.mit-license.org)

This is the official Java sdk for *[Ticktok.io](https://ticktok.io/)* service.


## Import sdk
artifacts available @ [*bintray repositories*](https://bintray.com/ticktok-io/maven/ticktok-java-client)

### Gradle
```
repositories {
    jcenter()
}

... 

dependencies {
  ...
    compile 'io.ticktok:ticktok-java-client:0.4.0'
}
```

### Maven
```xml
<repositories>
    ...
    <repository>
        <id>jcenter</id>
        <url>https://jcenter.bintray.com/</url>
    </repository>
</repositories>

...

<dependencies>
    ...
    <dependency>
        <groupId>io.ticktok</groupId>
        <artifactId>ticktok-java-client</artifactId>
        <version>0.4.0</version>
    </dependency>
</dependencies>

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

##Community

Come & chat with us on [Slack](https://ticktokio.slack.com/messages/CF0DYKN0Y/details/)
