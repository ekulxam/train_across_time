package survivalblock.train_across_time;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class TrainAcrossTime {
    public static final Executable SHOP_ENTRIES_INITIALIZER = new Executable(1);

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
            if (this.maxExecutionCount != null && this.maxExecutionCount < 0) {
                return;
            }

            Objects.requireNonNull(this.delegate).run();

            if (this.maxExecutionCount != null) {
                this.maxExecutionCount--;
            }
        }
    }
}
