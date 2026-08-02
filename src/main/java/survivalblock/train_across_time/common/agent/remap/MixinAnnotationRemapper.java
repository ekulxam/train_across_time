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
package survivalblock.train_across_time.common.agent.remap;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.AnnotationRemapper;
import org.objectweb.asm.commons.Remapper;

/**
 * @author Typho
 */
public class MixinAnnotationRemapper extends AnnotationRemapper {
    public MixinAnnotationRemapper(String descriptor, AnnotationVisitor annotationVisitor, Remapper remapper) {
        super(descriptor, annotationVisitor, remapper);
    }

    public MixinAnnotationRemapper(int api, String descriptor, AnnotationVisitor annotationVisitor, Remapper remapper) {
        super(api, descriptor, annotationVisitor, remapper);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected AnnotationVisitor createAnnotationRemapper(AnnotationVisitor annotationVisitor) {
        return new MixinAnnotationRemapper(api, null, annotationVisitor, remapper);
    }

    @Override
    protected AnnotationVisitor createAnnotationRemapper(String descriptor, AnnotationVisitor annotationVisitor) {
        return new MixinAnnotationRemapper(api, descriptor, annotationVisitor, remapper);
    }
}
