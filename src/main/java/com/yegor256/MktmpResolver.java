/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 Yegor Bugayenko
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
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                    new SimpleDateFormat("mm'm'ss's'SSS", Locale.ROOT).format(new Date())
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
