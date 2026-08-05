package survivalblock.train_across_time.common.util.ptr;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.Objects;

public final class ConstantInsnPointer extends InsnPointer<LdcInsnNode, ConstantInsnPointer> {
    private Object value;

    ConstantInsnPointer() {
        super(AbstractInsnNode.LDC_INSN);
        predicate = n -> Objects.equals(n.cst, value);
    }

    public ConstantInsnPointer value(int value) {
        this.value = value;
        return self();
    }

    public ConstantInsnPointer value(float value) {
        this.value = value;
        return self();
    }

    public ConstantInsnPointer value(long value) {
        this.value = value;
        return self();
    }

    public ConstantInsnPointer value(double value) {
        this.value = value;
        return self();
    }

    public ConstantInsnPointer value(String value) {
        this.value = value;
        return self();
    }

    public ConstantInsnPointer value(Type value) {
        this.value = value;
        return self();
    }

    public ConstantInsnPointer value(Handle value) {
        this.value = value;
        return self();
    }

    public ConstantInsnPointer value(ConstantDynamic value) {
        this.value = value;
        return self();
    }

    @Override
    public String toString() {
        return toString(
                "Constant",
                ordinal == -1 ? null : "ordinal=" + ordinal,
                value == null ? null : "ordinal=" + value
        );
    }
}
