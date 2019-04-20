# Ticktok Java client
[![CircleCI](https://circleci.com/gh/ticktok-io/ticktok-java-client.svg?style=svg)](https://circleci.com/gh/ticktok-io/ticktok-java-client)
[![Release](https://img.shields.io/github/release/ticktok-io/ticktok-java-client.svg)](https://github.com/ticktok-io/ticktok-java-client/releases/tag)
[![License](http://img.shields.io/:license-apache2.0-red.svg)](http://doge.mit-license.org)

This is the official Java sdk for *[Ticktok.io](https://ticktok.io/)* service.


## Quick Start

### Import sdk
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

### Example
```java
new Ticktok(options().domain("<ticktok-domain>").token("<ticktok-token>")).
    schedule("my first clock", "every.5.seconds", () -> {
        System.out.print("tick me baby one more time");
    });
```

## Community
Come & chat with us on [Slack](https://ticktokio.slack.com/messages/CF0DYKN0Y/details/)
