package survivalblock.train_across_time;

import org.jspecify.annotations.Nullable;

public class TrainAcrossTime {
    public static final Executable SHOP_ENTRIES_INITIALIZER = new Executable();

    public static class Executable implements Runnable {
        private boolean executed = false;
        private Runnable delegate;

        public Executable() {
        }

        public void set(Runnable runnable) {
            this.delegate = runnable;
        }

        @Override
        public void run() {
            if (this.executed) {
                return;
            }

            this.delegate.run();
            this.executed = true;
        }
    }
}
