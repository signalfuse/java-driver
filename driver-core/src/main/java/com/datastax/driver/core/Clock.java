/*
 * Copyright (C) 2012-2017 DataStax Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.driver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.*;

/**
 * A small abstraction around system clock that aims to provide microsecond precision with the best accuracy possible.
 */
interface Clock {

    /**
     * Returns the current time in microseconds.
     *
     * @return the difference, measured in microseconds, between the current time and and the Epoch
     * (that is, midnight, January 1, 1970 UTC).
     */
    long currentTimeMicros();
}

/**
 * Factory that returns the best Clock implementation depending on what native libraries are available in the system.
 * If LibC is available through JNR, and if the system property {@code com.datastax.driver.USE_NATIVE_CLOCK} is set to {@code true}
 * (which is the default value), then {@link NativeClock} is returned, otherwise {@link SystemClock} is returned.
 */
class ClockFactory {

    static Clock newInstance() {
        return new SystemClock();
    }
}

/**
 * Default implementation of a clock that delegates its calls to the system clock.
 *
 * @see System#currentTimeMillis()
 */
class SystemClock implements Clock {

    @Override
    public long currentTimeMicros() {
        return System.currentTimeMillis() * 1000;
    }

}
