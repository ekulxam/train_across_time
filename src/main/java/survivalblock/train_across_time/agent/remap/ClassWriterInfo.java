package survivalblock.train_across_time.agent.remap;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;

public class ClassWriterInfo {
    private boolean changed = false;
    private int writerFlags = 0;

    public void markChanged() {
        changed = true;
    }

    public void computeFrames() {
        writerFlags |= ClassWriter.COMPUTE_FRAMES;
    }

    public void computeMaxStackSizes() {
        writerFlags |= ClassWriter.COMPUTE_MAXS;
    }

    public @Nullable ClassWriter createWriter() {
        return changed ? new ClassWriter(writerFlags) : null;
    }
}
