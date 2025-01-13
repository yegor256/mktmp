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
