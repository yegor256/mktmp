/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * This class is instantiated and then called by JUnit when
 * an argument of a test method is marked with the {@link Mktmp}
 * annotation.
 * @since 0.1.0
 */
public final class MktmpResolver implements ParameterResolver,
    TestInstancePostProcessor {

    /**
     * Ctor.
     */
    public MktmpResolver() {
        // nothing to initialize
    }

    @Override
    public void postProcessTestInstance(final Object test,
        final ExtensionContext ext) {
        Class<?> type = test.getClass();
        while (!type.equals(Object.class)) {
            for (final Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Mktmp.class)) {
                    MktmpResolver.inject(test, field, ext);
                }
            }
            type = type.getSuperclass();
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext context,
        final ExtensionContext ext) {
        return MktmpResolver.supported(context.getParameter().getType())
            && context.isAnnotated(Mktmp.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext context,
        final ExtensionContext ext) {
        return MktmpResolver.make(
            context.getParameter().getType(),
            MktmpResolver.path(
                ext,
                context.getParameter().getDeclaringExecutable().getName(),
                context.getIndex() + 1
            )
        );
    }

    /**
     * Turn index into an ordinal number.
     *
     * <p>The teens 11, 12 and 13 (and every {@code ...11}, {@code ...12},
     * {@code ...13}) are an exception in English and always take "th", not
     * "st"/"nd"/"rd".</p>
     *
     * @param num The number
     * @return Ordinal one (1st, 2nd, 3rd, 8th, 11th, etc.)
     */
    static String ordinal(final int num) {
        final String tail;
        if (num % 100 / 10 == 1) {
            tail = "th";
        } else if (num % 10 == 1) {
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

    private static void inject(final Object test, final Field field,
        final ExtensionContext ext) {
        if (!MktmpResolver.supported(field.getType())) {
            throw new IllegalArgumentException(
                String.format(
                    "@Mktmp field \"%s\" must be Path or File",
                    field.getName()
                )
            );
        }
        try {
            final VarHandle handle = MethodHandles.privateLookupIn(
                field.getDeclaringClass(),
                MethodHandles.lookup()
            ).unreflectVarHandle(field);
            handle.set(
                test,
                MktmpResolver.make(
                    field.getType(),
                    MktmpResolver.path(ext, field.getName(), 1)
                )
            );
        } catch (final IllegalAccessException err) {
            throw new IllegalStateException(
                String.format(
                    "Failed to assign @Mktmp field \"%s\"",
                    field.getName()
                ),
                err
            );
        }
    }

    private static Object make(final Class<?> type, final Path path) {
        final Object ret;
        if (type.equals(File.class)) {
            ret = path.toFile();
        } else {
            ret = path;
        }
        return ret;
    }

    private static Path path(final ExtensionContext ext, final String name,
        final int index) {
        final Path target = Paths.get("target").toAbsolutePath();
        Path path = target.resolve("mktmp").resolve(
            ext.getTestClass().map(Class::getSimpleName).orElse(ext.getDisplayName())
        ).resolve(
            name
        );
        while (true) {
            final Path sub = path.resolve(
                String.format(
                    "%s-%s",
                    MktmpResolver.ordinal(index),
                    DateTimeFormatter.ofPattern("mm'm'ss's'SSS", Locale.ROOT)
                        .format(LocalDateTime.now(ZoneId.systemDefault()))
                )
            );
            if (sub.toFile().mkdirs()) {
                path = sub;
                break;
            }
        }
        return path;
    }

    private static boolean supported(final Class<?> type) {
        return type.equals(Path.class) || type.equals(File.class);
    }
}
