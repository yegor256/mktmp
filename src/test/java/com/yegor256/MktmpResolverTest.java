/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.io.File;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link MktmpResolver}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class MktmpResolverTest {

    @BeforeAll
    static void onceTemp(@Mktmp final Path tmp) {
        MatcherAssert.assertThat(
            "the directory is there, once for all tests",
            tmp.toFile().exists(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "the directory name is proper",
            tmp.toString(),
            Matchers.allOf(
                Matchers.containsString("MktmpResolverTest"),
                Matchers.containsString("onceTemp"),
                Matchers.containsString("1st-")
            )
        );
    }

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
            Matchers.containsString(
                "target/mktmp/MktmpResolverTest/createsProperDirName/1st-"
            )
        );
    }

    @EnabledOnOs({OS.LINUX, OS.MAC})
    @ParameterizedTest
    @ValueSource(strings = {"first", "second"})
    void makesManyDirs(final String arg, @Mktmp final Path tmp) {
        MatcherAssert.assertThat(
            "the directory is properly named",
            tmp.toString(),
            Matchers.containsString("MktmpResolverTest/makesManyDirs/2nd-")
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
            "both directories must exist",
            first.toFile().exists() && second.toFile().exists(),
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
