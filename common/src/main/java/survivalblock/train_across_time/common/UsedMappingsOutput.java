package survivalblock.train_across_time.common;

public interface UsedMappingsOutput {
    UsedMappingsOutput NONE = new UsedMappingsOutput() {
        @Override
        public void useClass(String intermediary) {
        }

        @Override
        public void useMethod(String intermediary) {
        }

        @Override
        public void useField(String intermediary) {
        }
    };

    void useClass(String intermediary);

    void useMethod(String intermediary);

    void useField(String intermediary);

    default void save() {
    }
}
