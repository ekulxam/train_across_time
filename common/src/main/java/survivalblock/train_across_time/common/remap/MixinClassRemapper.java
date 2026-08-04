/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package survivalblock.train_across_time.common.remap;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Typho
 */
public class MixinClassRemapper extends ClassRemapper {
    public final Set<Type> mixinTargets = new HashSet<>();

    public MixinClassRemapper(ClassVisitor classVisitor, Remapper remapper) {
        super(classVisitor, remapper);
    }

    public MixinClassRemapper(int api, ClassVisitor classVisitor, Remapper remapper) {
        super(api, classVisitor, remapper);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
            return new AnnotationVisitor(api, super.visitAnnotation(descriptor, visible)) {
                @Override
                public AnnotationVisitor visitArray(String name) {
                    return switch (name) {
                        case "value", "targets" -> new AnnotationVisitor(api, super.visitArray(name)) {
                            @Override
                            public void visit(String name, Object value) {
                                super.visit(name, value);

                                mixinTargets.add((Type) remapper.mapValue(value instanceof Type type ? type : Type.getType((String) value)));
                            }
                        };
                        default -> super.visitArray(name);
                    };
                }
            };
        }

        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    protected FieldVisitor createFieldRemapper(FieldVisitor fieldVisitor) {
        return new MixinFieldRemapper(this.api, fieldVisitor, this.remapper, mixinTargets);
    }

    @Override
    protected MethodVisitor createMethodRemapper(MethodVisitor methodVisitor) {
        return new MixinMethodRemapper(this.api, methodVisitor, this.remapper, mixinTargets);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected AnnotationVisitor createAnnotationRemapper(AnnotationVisitor annotationVisitor) {
        return new MixinAnnotationRemapper(this.api, null, annotationVisitor, this.remapper, mixinTargets);
    }

    @Override
    protected AnnotationVisitor createAnnotationRemapper(String descriptor, AnnotationVisitor annotationVisitor) {
        return new MixinAnnotationRemapper(this.api, descriptor, annotationVisitor, this.remapper, mixinTargets);
    }
}
