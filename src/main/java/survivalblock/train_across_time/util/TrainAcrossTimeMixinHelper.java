package survivalblock.train_across_time.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class TrainAcrossTimeMixinHelper {
    public static final Executable SHOP_ENTRIES_INITIALIZER = new Executable(1);
    public static boolean warnOnMissingPoisonDataComponent = false;
    public static boolean warnOnMissingPoisonBounds = false;

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
