package survivalblock.train_across_time.common.remap;

import org.objectweb.asm.ClassVisitor;

import java.util.function.Consumer;

public class ClassNameVisitor extends ClassVisitor {
    public final Consumer<String> out;

    public ClassNameVisitor(int api, Consumer<String> out) {
        super(api);
        this.out = out;
    }

    public ClassNameVisitor(int api, ClassVisitor classVisitor, Consumer<String> out) {
        super(api, classVisitor);
        this.out = out;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        out.accept(name);
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
