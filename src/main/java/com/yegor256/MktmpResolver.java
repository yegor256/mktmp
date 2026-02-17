/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * This class is instantiated and then called by JUnit when
 * an argument of a test method is marked with the {@link Mktmp}
 * annotation.
 *
 * @since 0.1.0
 */
public final class MktmpResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(final ParameterContext context,
        final ExtensionContext ext) {
        return (context.getParameter().getType().equals(Path.class)
            || context.getParameter().getType().equals(File.class))
            && context.isAnnotated(Mktmp.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext context,
        final ExtensionContext ext) {
        final Path target = Paths.get("target").toAbsolutePath();
        Path path = target
            .resolve("mktmp")
            .resolve(
                ext.getTestClass()
                    .map(Class::getSimpleName)
                    .orElse(ext.getDisplayName())
            )
            .resolve(
                context.getParameter()
                    .getDeclaringExecutable()
                    .getName()
            );
        while (true) {
            final Path sub = path.resolve(
                String.format(
                    "%s-%s",
                    MktmpResolver.ordinal(context.getIndex() + 1),
                    DateTimeFormatter.ofPattern("mm'm'ss's'SSS", Locale.ROOT)
                        .format(LocalDateTime.now())
                )
            );
            if (sub.toFile().mkdirs()) {
                path = sub;
                break;
            }
        }
        final Object ret;
        if (context.getParameter().getType().equals(File.class)) {
            ret = path.toFile();
        } else {
            ret = path;
        }
        return ret;
    }

    /**
     * Turn index into an ordinal number.
     * @param num The number
     * @return Ordinal one (1st, 2nd, 3rd, 8th, etc.)
     */
    private static String ordinal(final int num) {
        final String tail;
        if (num % 10 == 1) {
            tail = "st";
        } else if (num % 10 == 2) {
            tail = "nd";
        } else if (num % 10 == 3) {
            tail = "rd";
        } else {
            tail = "th";
        }
        return String.format("%d%s", num, tail);
    }

}
