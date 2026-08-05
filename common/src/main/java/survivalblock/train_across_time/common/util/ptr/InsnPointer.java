package survivalblock.train_across_time.common.util.ptr;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class InsnPointer<T extends AbstractInsnNode, S extends InsnPointer<T, S>> extends ASMPointer<T, InsnList, S> {
    protected final int type;
    protected int ordinal = -1;

    protected InsnPointer(int type) {
        this.type = type;
    }

    private InsnPointer(int type, Predicate<T> predicate) {
        this.type = type;
        this.predicate = predicate;
    }

    public S ordinal(int ordinal) {
        this.ordinal = ordinal;
        return self();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> find(InsnList insns) {
        var i = 0;

        for (AbstractInsnNode insn : insns) {
            if ((insn.getType() == type || type == -1) && predicate.test((T) insn) && (i++ == ordinal || ordinal == -1)) {
                return Optional.of((T) insn);
            }
        }

        return Optional.empty();
    }

    /**
     * Method call
     */
    public static MethodInsnPointer methodCall() {
        return new MethodInsnPointer();
    }

    /**
     * Non-final instance method call
     */
    public static MethodInsnPointer methodCallInherited() {
        return new MethodInsnPointer().opcode(Opcodes.INVOKEVIRTUAL);
    }

    /**
     * Final instance method call or constructor call
     */
    public static MethodInsnPointer methodCallDirect() {
        return new MethodInsnPointer().opcode(Opcodes.INVOKESPECIAL);
    }

    /**
     * Static method call
     */
    public static MethodInsnPointer methodCallStatic() {
        return new MethodInsnPointer().opcode(Opcodes.INVOKESTATIC);
    }

    /**
     * Interface method call
     */
    public static MethodInsnPointer methodCallInterface() {
        return new MethodInsnPointer().opcode(Opcodes.INVOKEINTERFACE);
    }

    /**
     * I have no clue what this one does
     */
    public static MethodInsnPointer methodCallDynamic() {
        return new MethodInsnPointer().opcode(Opcodes.INVOKEDYNAMIC);
    }

    /**
     * Static or instance field get or set
     */
    public static FieldInsnPointer fieldOperation() {
        return new FieldInsnPointer();
    }

    /**
     * Instance field get
     */
    public static FieldInsnPointer fieldGetInstance() {
        return fieldOperation().opcode(Opcodes.GETFIELD);
    }

    /**
     * Static field get
     */
    public static FieldInsnPointer fieldGetStatic() {
        return fieldOperation().opcode(Opcodes.GETSTATIC);
    }

    /**
     * Instance field set
     */
    public static FieldInsnPointer fieldSetInstance() {
        return fieldOperation().opcode(Opcodes.PUTFIELD);
    }

    /**
     * Static field set
     */
    public static FieldInsnPointer fieldSetStatic() {
        return fieldOperation().opcode(Opcodes.PUTSTATIC);
    }

    /**
     * Constant value
     */
    public static ConstantInsnPointer constant() {
        return new ConstantInsnPointer();
    }

    /**
     * Constant boolean, byte, short, or integer value
     */
    public static ConstantInsnPointer constant(int value) {
        return new ConstantInsnPointer().value(value);
    }

    /**
     * Constant long value
     */
    public static ConstantInsnPointer constant(long value) {
        return new ConstantInsnPointer().value(value);
    }

    /**
     * Constant float value
     */
    public static ConstantInsnPointer constant(float value) {
        return new ConstantInsnPointer().value(value);
    }

    /**
     * Constant double value
     */
    public static ConstantInsnPointer constant(double value) {
        return new ConstantInsnPointer().value(value);
    }

    /**
     * Constant string value
     */
    public static ConstantInsnPointer constant(String value) {
        return new ConstantInsnPointer().value(value);
    }

    /**
     * Constant type value
     */
    public static ConstantInsnPointer constant(Type value) {
        return new ConstantInsnPointer().value(value);
    }

    /**
     * Handles
     */
    public static ConstantInsnPointer constant(Handle value) {
        return new ConstantInsnPointer().value(value);
    }

    /**
     * ConstantDynamics
     */
    public static ConstantInsnPointer constant(ConstantDynamic value) {
        return new ConstantInsnPointer().value(value);
    }

    public static TypeInsnPointer type(String desc) {
        return new TypeInsnPointer().desc(desc);
    }

    public static TypeInsnPointer type(Type type) {
        return new TypeInsnPointer().desc(type);
    }

    public static TypeInsnPointer type(Class<?> cls) {
        return new TypeInsnPointer().desc(cls);
    }

    /**
     * Local variable operation
     */
    public static VarInsnPointer localOperation() {
        return new VarInsnPointer();
    }

    /**
     * No-argument instruction with a specific opcode (constants, returns, pops, etc.)
     */
    public static InsnPointer<InsnNode, ?> opcodeSimple(int opcode) {
        return new InsnPointer<>(AbstractInsnNode.INSN, n -> n.getOpcode() == opcode);
    }

    /**
     * Any instruction with a specific opcode
     */
    public static InsnPointer<AbstractInsnNode, ?> opcodeAny(int opcode) {
        return new InsnPointer<>(-1, n -> n.getOpcode() == opcode);
    }
}
