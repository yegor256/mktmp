/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.yegor256;

import java.io.File;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link MktmpResolver}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class MktmpResolverTest {

    @Test
    void createsTempDir(@Mktmp final Path tmp) {
        MatcherAssert.assertThat(
            "the directory is there",
            tmp.toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void worksAlsoWithFile(@Mktmp final File tmp) {
        MatcherAssert.assertThat(
            "the directory is there too",
            tmp.exists(),
            Matchers.is(true)
        );
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void createsProperDirName(@Mktmp final Path tmp) {
        MatcherAssert.assertThat(
            "the directory name is proper",
            tmp.toString(),
            Matchers.endsWith("target/mktmp/createsProperDirName(Path)/0-0")
        );
    }

    @Test
    void createsTwoDirectories(@Mktmp final Path first, @Mktmp final Path second) {
        MatcherAssert.assertThat(
            "the directory is there",
            first.toString().equals(second.toString()),
            Matchers.is(false)
        );
    }

    @Test
    void doesntConflictWithJunit(@TempDir final Path first, @Mktmp final Path second) {
        MatcherAssert.assertThat(
            "the first directory is there (from JUnit)",
            first.toFile().exists(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "the second directory is there (from Mktmp)",
            second.toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void makesAbsolutePath(@Mktmp final Path tmp) {
        MatcherAssert.assertThat(
            "the path is absolute",
            tmp.toString(),
            Matchers.startsWith("/")
        );
    }
}
