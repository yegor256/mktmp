/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

/**
 * Test case for {@link MktmpResolver}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class MktmpResolverTest {

    /**
     * Temporary path field.
     */
    @Mktmp
    private Path field;

    /**
     * Temporary file field.
     */
    @Mktmp
    private File folder;

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

    @ParameterizedTest
    @CsvSource({
        "1, 1st", "2, 2nd", "3, 3rd", "4, 4th",
        "11, 11th", "12, 12th", "13, 13th",
        "21, 21st", "22, 22nd", "23, 23rd",
        "111, 111th", "112, 112th", "113, 113th"
    })
    void formatsOrdinalSuffix(final int num, final String expected) {
        MatcherAssert.assertThat(
            "ordinal suffix must follow English rules, teens always take 'th'",
            MktmpResolver.ordinal(num),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void rejectsBlockedParent(@TempDir final Path tmp) throws Exception {
        final Path parent = Paths.get(
            "target", "mktmp", tmp.getFileName().toString(), "rejectsBlockedParent"
        ).toAbsolutePath();
        Files.createDirectories(parent.getParent());
        Files.write(parent, new byte[0]);
        try {
            final ExtensionContext ext = Mockito.mock(ExtensionContext.class);
            Mockito.when(ext.getTestClass()).thenReturn(Optional.empty());
            Mockito.when(ext.getDisplayName()).thenReturn(tmp.getFileName().toString());
            final ParameterContext param = Mockito.mock(ParameterContext.class);
            Mockito.when(param.getParameter()).thenReturn(
                MktmpResolverTest.class.getDeclaredMethod(
                    "rejectsBlockedParent", Path.class
                ).getParameters()[0]
            );
            Assertions.assertTimeoutPreemptively(
                Duration.ofSeconds(2L),
                () -> {
                    final IllegalStateException err = Assertions.assertThrows(
                        IllegalStateException.class,
                        () -> new MktmpResolver().resolveParameter(param, ext),
                        "a blocked parent must fail instead of retrying forever"
                    );
                    MatcherAssert.assertThat(
                        "the failure identifies the parent directory",
                        err.getMessage(),
                        Matchers.containsString(parent.toString())
                    );
                    MatcherAssert.assertThat(
                        "the filesystem error is preserved",
                        err.getCause(),
                        Matchers.instanceOf(IOException.class)
                    );
                }
            );
        } finally {
            Files.delete(parent);
        }
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
    void worksWithPathField() {
        MatcherAssert.assertThat(
            "the path field is injected",
            this.field.toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void worksWithFileField() {
        MatcherAssert.assertThat(
            "the file field is injected",
            this.folder.exists(),
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
