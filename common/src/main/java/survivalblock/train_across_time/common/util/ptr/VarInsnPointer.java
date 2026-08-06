package survivalblock.train_across_time.common.util.ptr;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import survivalblock.train_across_time.common.TATConstants;

public final class VarInsnPointer extends InsnPointer<VarInsnNode, VarInsnPointer> {
    private int opcode = -1;
    private int id = -1;

    VarInsnPointer() {
        super(AbstractInsnNode.VAR_INSN);
        predicate = (self, n) -> {
            if (opcode != -1 && n.getOpcode() != opcode) {
                if (self.debug) {
                    TATConstants.PLATFORM.info("\t\tExpected opcode " + opcode + " but got " + n.getOpcode());
                }

                return false;
            }

            if (id != -1 && n.var != id) {
                if (self.debug) {
                    TATConstants.PLATFORM.info("\t\tExpected id " + id + " but got " + n.var);
                }

                return false;
            }

            return true;
        };
    }

    public VarInsnPointer opcode(int opcode) {
        this.opcode = opcode;
        return self();
    }

    public VarInsnPointer id(int id) {
        this.id = id;
        return self();
    }

    @Override
    public String toString() {
        return toString(
                "Var",
                ordinal == -1 ? null : "ordinal=" + ordinal,
                opcode == -1 ? null : "opcode=" + opcode,
                id == -1 ? null : "id=" + id
        );
    }
}
