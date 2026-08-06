package survivalblock.train_across_time.common.util.ptr;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import survivalblock.train_across_time.common.TATConstants;

public final class TypeInsnPointer extends InsnPointer<FieldInsnNode, TypeInsnPointer> {
    private String desc;

    TypeInsnPointer() {
        super(AbstractInsnNode.TYPE_INSN);
        predicate = (self, n) -> {
            if (desc != null && !n.desc.equals(desc)) {
                if (self.debug) {
                    TATConstants.PLATFORM.info("\t\tExpected desc " + desc + " but desc " + n.desc);
                }

                return false;
            }

            return true;
        };
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
