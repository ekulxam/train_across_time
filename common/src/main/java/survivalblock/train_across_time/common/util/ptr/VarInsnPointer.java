package survivalblock.train_across_time.common.util.ptr;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

@SuppressWarnings("RedundantIfStatement")
public final class VarInsnPointer extends InsnPointer<VarInsnNode, VarInsnPointer> {
    private int opcode = -1;
    private int var = -1;

    VarInsnPointer() {
        super(AbstractInsnNode.VAR_INSN);
        predicate = n -> {
            if (opcode != -1 && n.getOpcode() != opcode) {
                return false;
            }

            if (var != -1 && n.var != var) {
                return false;
            }

            return true;
        };
    }

    public VarInsnPointer opcode(int opcode) {
        this.opcode = opcode;
        return self();
    }

    public VarInsnPointer var(int var) {
        this.var = var;
        return self();
    }

    @Override
    public String toString() {
        return toString(
                "Var",
                ordinal == -1 ? null : "ordinal=" + ordinal,
                opcode == -1 ? null : "opcode=" + opcode,
                var == -1 ? null : "var=" + var
        );
    }
}
