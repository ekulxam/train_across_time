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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnnotationRemapper;
import org.objectweb.asm.commons.Remapper;

import java.util.List;
import java.util.Set;

/**
 * @author Typho
 */
public class MixinAnnotationRemapper extends AnnotationRemapper {
    public static final List<String> ACCESSOR_TYPES = List.of(
            "Lorg/spongepowered/asm/mixin/gen/Accessor;",
            "Lorg/spongepowered/asm/mixin/gen/Invoker;"
    );
    public final Set<Type> mixinTargets;

    public MixinAnnotationRemapper(String descriptor, AnnotationVisitor annotationVisitor, Remapper remapper, Set<Type> mixinTargets) {
        super(descriptor, annotationVisitor, remapper);
        this.mixinTargets = mixinTargets;
    }

    public MixinAnnotationRemapper(int api, String descriptor, AnnotationVisitor annotationVisitor, Remapper remapper, Set<Type> mixinTargets) {
        super(api, descriptor, annotationVisitor, remapper);
        this.mixinTargets = mixinTargets;
    }

    @Override
    public void visit(String name, Object value) {
        if (!mixinTargets.isEmpty()) {
            if (value instanceof String string) {
                if (descriptor != null && ACCESSOR_TYPES.contains(descriptor)) {
                    var presumedOwner = mixinTargets.stream().findAny().orElseThrow().getInternalName();
                    string = remapper.mapMethodName(presumedOwner, remapper.mapFieldName(presumedOwner, string, "Ljava/lang/Object;"), "()V");
                } else if (string.contains("(")) { // method
                    var index = string.indexOf('(');
                    var methodName = string.substring(0, index);
                    var methodDesc = string.substring(index);

                    if (methodName.isEmpty()) {
                        string = remapper.mapMethodDesc(methodDesc);
                    } else if (methodName.contains(";")) {
                        var index1 = methodName.indexOf(';') + 1;

                        var methodOwner = Type.getType(methodName.substring(0, index1));
                        methodName = methodName.substring(index1);

                        string = remapper.mapValue(methodOwner) + remapper.mapMethodName(methodOwner.getInternalName(), methodName, methodDesc) + remapper.mapMethodDesc(methodDesc);
                    } else {
                        var presumedOwner = mixinTargets.stream().findAny().orElseThrow().getInternalName();
                        string = remapper.mapMethodName(presumedOwner, methodName, methodDesc) + remapper.mapMethodDesc(methodDesc);
                    }
                } else if (string.contains(":")) { // field
                    var index = string.indexOf(':');
                    var fieldName = string.substring(0, index);
                    var fieldDesc = string.substring(index + 1);

                    if (fieldName.contains(";")) {
                        var index1 = fieldName.indexOf(';');
                        var fieldOwner = Type.getType(fieldName.substring(0, index1 + 1));
                        string = remapper.mapValue(fieldOwner) + remapper.mapFieldName(fieldOwner.getInternalName(), fieldName.substring(index1 + 1), fieldDesc) + ":" + remapper.mapValue(Type.getType(fieldDesc));
                    } else {
                        var presumedOwner = mixinTargets.stream().findAny().orElseThrow().getInternalName();
                        string = remapper.mapFieldName(presumedOwner, fieldName, fieldDesc) + ":" + remapper.map(fieldDesc);
                    }
                } else if (string.startsWith("L") && string.endsWith(";")) {
                    string = remapper.mapValue(Type.getType(string)).toString();
                }

                value = switch (string) {
                    case "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V" -> "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V";
                    case "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V" -> "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V";
                    default -> string;
                };
            }
        }

        super.visit(name, value);
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
