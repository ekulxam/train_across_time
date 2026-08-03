package survivalblock.train_across_time.agent.remap;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;

import java.util.*;

public class ClassOutputInfo {
    public final String className;
    private boolean changed = false;
    private int writerFlags = 0;
    private final Set<String> errors = new HashSet<>();

    public ClassOutputInfo(String className) {
        this.className = className;
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

        return changed ? new ClassWriter(writerFlags) : null;
    }
}
