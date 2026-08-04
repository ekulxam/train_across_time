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
package survivalblock.train_across_time.common;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Predicate;

/**
 * Stores constants for The Train Across Time that can be loaded without fear of classloading something incorrectly.
 */
@ApiStatus.NonExtendable
public interface TATConstants {
    String MOD_ID = "train_across_time";

    String MIXIN_INFO_CLASS = "org/spongepowered/asm/mixin/transformer/MixinInfo";

    String WATHE = "wathe";
    String WATHE_PACKAGE = "dev/doctor4t/wathe";

    String RATATOUILLE = "ratatouille";
    String RATATOUILLE_PACKAGE = "dev/doctor4t/ratatouille";

    Platform PLATFORM = ServiceLoader.load(Platform.class).findFirst().orElseThrow();
    List<Predicate<String>> TRANSFORM_PREDICATES = new ArrayList<>(List.of(
            cls -> cls.startsWith(WATHE_PACKAGE),
            cls -> cls.startsWith(RATATOUILLE_PACKAGE)//,
            //cls -> cls.equals(MIXIN_INFO_CLASS)
    ));

    static boolean shouldTransformClass(String className) {
        for (Predicate<String> p : TRANSFORM_PREDICATES) {
            if (p.test(className)) {
                return true;
            }
        }

        return false;
    }

    static boolean isWatheClass(String className) {
        return className.startsWith(WATHE_PACKAGE);
    }

    static boolean isRatatouilleClass(String className) {
        return className.startsWith(RATATOUILLE_PACKAGE);
    }

    interface Platform {
        @Nullable Path debugOutputPath();

        boolean compileMappings();

        void info(String s);

        void error(String s, Throwable t);
    }
}
