/*
 * SPDX-FileCopyrightText: Copyright (c) 2024-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used in unit tests in order to signal
 * to JUnit that a certain method argument must be generated
 * through the {@link MktmpResolver}.
 *
 * <p>Use it like this:</p>
 *
 * <code><pre> import com.yegor256.Mktmp;
 * import com.yegor256.MktmpResolver;
 * import org.junit.jupiter.api.Test;
 * import org.junit.jupiter.api.extension.ExtendWith;
 * &#64;ExtendWith(MktmpResolver.class)
 * class FooTest {
 *   &#64;Test
 *   void worksFine(@Mktmp Path tmp) {
 *     // The "tmp" directory is a subdirectory of
 *     // the "target/mktmp/" directory, where all
 *     // temporary directories of all tests will
 *     // be kept, in order to help you review the
 *     // leftovers after failed (or passed) tests.
 *   }
 * }</pre></code>
 *
 * @since 0.1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mktmp {
}
