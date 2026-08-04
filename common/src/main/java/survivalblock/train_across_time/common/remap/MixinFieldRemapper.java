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
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.FieldRemapper;
import org.objectweb.asm.commons.Remapper;

import java.util.Set;

/**
 * @author Typho
 */
public class MixinFieldRemapper extends FieldRemapper {
    public final Set<Type> mixinTargets;

    public MixinFieldRemapper(FieldVisitor fieldVisitor, Remapper remapper, Set<Type> mixinTargets) {
        super(fieldVisitor, remapper);
        this.mixinTargets = mixinTargets;
    }

    public MixinFieldRemapper(int api, FieldVisitor fieldVisitor, Remapper remapper, Set<Type> mixinTargets) {
        super(api, fieldVisitor, remapper);
        this.mixinTargets = mixinTargets;
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
