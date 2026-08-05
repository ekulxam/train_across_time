package survivalblock.train_across_time.common.util;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;

import java.util.*;

public class ClassOutputInfo {
    public String className;
    public final UsedMappingsOutput usedMappingsOutput;

    private boolean changed = false;
    private int writerFlags = 0;
    private final Set<String> errors = new HashSet<>();

    public ClassOutputInfo(UsedMappingsOutput usedMappingsOutput) {
        this.usedMappingsOutput = usedMappingsOutput;
    }

    public void markChanged() {
        changed = true;
    }

    public void computeFrames() {
        writerFlags |= ClassWriter.COMPUTE_FRAMES;
    }

    public void computeMaxStackSizes() {
        writerFlags |= ClassWriter.COMPUTE_MAXS;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public @Nullable ClassWriter end() {
        if (!errors.isEmpty()) {
            var errors = new ArrayList<>(this.errors);
            Collections.sort(errors);
            throw new IllegalStateException((errors.size() == 1 ? "Error" : "Errors") + " while transforming class " + className + ":\n" + String.join("\n", errors));
        }

        if (changed) {
            if (usedMappingsOutput != null) {
                usedMappingsOutput.endClass();
            }

            return new ClassWriter(writerFlags);
        }

        return null;
    }

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

        default void endClass() {
        }
    }
}
