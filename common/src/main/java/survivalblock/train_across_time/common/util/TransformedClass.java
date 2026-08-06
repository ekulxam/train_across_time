package survivalblock.train_across_time.common.util;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

public record TransformedClass(
        ClassNode node,
        WatheClassOutputInfo info
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
