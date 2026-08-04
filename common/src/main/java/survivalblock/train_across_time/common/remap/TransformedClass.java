package survivalblock.train_across_time.common.remap;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

public record TransformedClass(
        ClassNode node,
        ClassOutputInfo info
) {
    public byte @Nullable [] toByteArray() {
        var writer = info.end();

        if (writer == null) {
            return null;
        } else {
            node.accept(writer);
            return writer.toByteArray();
        }
    }
}
