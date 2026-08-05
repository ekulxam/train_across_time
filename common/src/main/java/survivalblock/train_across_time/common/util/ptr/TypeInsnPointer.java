package survivalblock.train_across_time.common.util.ptr;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

public final class TypeInsnPointer extends InsnPointer<FieldInsnNode, TypeInsnPointer> {
    private String desc;

    TypeInsnPointer() {
        super(AbstractInsnNode.TYPE_INSN);
        predicate = n -> desc == null || n.desc.equals(desc);
    }

    public TypeInsnPointer desc(String desc) {
        this.desc = desc;
        return self();
    }

    public TypeInsnPointer desc(Type type) {
        return desc(type.getInternalName());
    }

    public TypeInsnPointer desc(Class<?> cls) {
        return desc(Type.getType(cls));
    }

    @Override
    public String toString() {
        return toString(
                "Type",
                ordinal == -1 ? null : "ordinal=" + ordinal,
                desc == null ? null : "desc=" + desc
        );
    }
}
