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

import net.fabricmc.loader.impl.util.log.LogCategory;
import org.jetbrains.annotations.ApiStatus;

/**
 * Stores constants for The Train Across Time that can be loaded without fear of classloading something incorrectly.
 */
@ApiStatus.NonExtendable
public interface TheTrainAcrossTimeConstants {
    String MOD_ID = "train_across_time";
    String WATHE = "wathe";
    String WATHE_PACKAGE = "dev.doctor4t.wathe";
    LogCategory LOGGER = LogCategory.create("The Train Across Time");

    static String id(String path) {
        return MOD_ID + "." + path;
    }

    static boolean isWatheClass(String className) {
        return className.replace('/', '.').startsWith(WATHE_PACKAGE);
    }
}
