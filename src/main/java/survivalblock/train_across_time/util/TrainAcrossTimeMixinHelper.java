/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package survivalblock.train_across_time.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class TrainAcrossTimeMixinHelper {
    public static final Executable SHOP_ENTRIES_INITIALIZER = new Executable(1);

    /**
     * A wrapper class for {@link Runnable} that can be run a certain amount of times.
     */
    public static class Executable implements Runnable {
        @Nullable
        private Integer maxExecutionCount;
        @Nullable
        private Runnable delegate = null;

        public Executable(@Nullable Integer maxExecutionCount) {
            this.maxExecutionCount = maxExecutionCount;
        }

        public Executable() {
            this(null);
        }

        public void set(@NonNull Runnable runnable) {
            this.delegate = runnable;
        }

        @Override
        public void run() {
            if (this.maxExecutionCount != null && this.maxExecutionCount <= 0) {
                return;
            }

            Objects.requireNonNull(this.delegate).run();

            if (this.maxExecutionCount != null) {
                this.maxExecutionCount--;
            }
        }

        public void runLast() {
            this.run();
            this.throwIfIncomplete();
        }

        public void throwIfIncomplete() {
            if (this.maxExecutionCount == null) {
                return;
            }
            if (this.maxExecutionCount > 0) {
                throw new IllegalStateException("Executable should have finished by now, but it still has " + this.maxExecutionCount + " more execution(s) remaining!");
            }
        }
    }
}
