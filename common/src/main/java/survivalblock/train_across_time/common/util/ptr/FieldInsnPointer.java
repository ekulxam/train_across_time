package survivalblock.train_across_time.common.util.ptr;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

@SuppressWarnings("RedundantIfStatement")
public final class FieldInsnPointer extends InsnPointer<FieldInsnNode, FieldInsnPointer> {
    private int opcode = -1;
    private String owner;
    private String name;
    private String desc;

    FieldInsnPointer() {
        super(AbstractInsnNode.FIELD_INSN);
        predicate = n -> {
            if (opcode != -1 && n.getOpcode() != opcode) {
                return false;
            }

            if (owner != null && !n.owner.equals(owner)) {
                return false;
            }

            if (name != null && !n.name.equals(name)) {
                return false;
            }

            if (desc != null && !n.desc.equals(desc)) {
                return false;
            }

            return true;
        };
    }

    public FieldInsnPointer opcode(int opcode) {
        this.opcode = opcode;
        return self();
    }

    public FieldInsnPointer owner(String owner) {
        this.owner = owner;
        return self();
    }

    public FieldInsnPointer name(String name) {
        this.name = name;
        return self();
    }

    public FieldInsnPointer desc(String desc) {
        this.desc = desc;
        return self();
    }

    @Override
    public String toString() {
        return toString(
                "Field",
                ordinal == -1 ? null : "ordinal=" + ordinal,
                opcode == -1 ? null : "opcode=" + opcode,
                owner == null ? null : "owner=" + owner,
                name == null ? null : "name=" + name,
                desc == null ? null : "desc=" + desc
        );
    }
}
