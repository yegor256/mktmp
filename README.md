# Temp Directory in JUnit5 Tests, in the `target/`

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/mktmp)](http://www.rultor.com/p/yegor256/mktmp)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/mktmp/actions/workflows/mvn.yml/badge.svg)](https://github.com/yegor256/mktmp/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/mktmp)](http://www.0pdd.com/p?name=yegor256/mktmp)
[![Maven Central](https://img.shields.io/maven-central/v/com.yegor256/mktmp.svg)](https://maven-badges.herokuapp.com/maven-central/com.yegor256/mktmp)
[![Javadoc](http://www.javadoc.io/badge/com.yegor256/mktmp.svg)](http://www.javadoc.io/doc/com.yegor256/mktmp)
[![codecov](https://codecov.io/gh/yegor256/mktmp/branch/master/graph/badge.svg)](https://codecov.io/gh/yegor256/mktmp)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/mktmp)](https://hitsofcode.com/view/github/yegor256/mktmp)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/mktmp/blob/master/LICENSE.txt)

Very often, in [JUnit5][junit] tests you need a temporary directory,
which you can check after the tests fail. The standard
[`@TempDir`][TempDir] doesn't provide such a possibility, because it
deletes the directory when Maven build is over. The annotation
in this tiny package solves exactly this problem: it places temporary
files in the `target/` directory, letting you inspect them after
the tests finish.

First, you add this to your `pom.xml`:

```xml
<dependency>
  <groupId>com.yegor256</groupId>
  <artifactId>mktmp</artifactId>
  <version>0.0.5</version>
</dependency>
```

Then, you use it like this, in your JUnit5 test:

```java
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
@ExtendWith(MktmpResolver.class)
class FooTest {
  @Test
  void worksFine(@Mktmp Path tmp) {
    // The "tmp" directory is a subdirectory of
    // the "target/mktmp/" directory, where all
    // temporary directories of all tests will
    // be kept, in order to help you review the
    // leftovers after failed (or passed) tests.
  }
}
```

## How to Contribute

Fork repository, make changes, send us a
[pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 11+.

[junit]: https://junit.org/junit5/
[TempDir]: https://junit.org/junit5/docs/5.4.1/api/org/junit/jupiter/api/io/TempDir.html
