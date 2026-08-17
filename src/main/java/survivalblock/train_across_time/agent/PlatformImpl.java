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
package survivalblock.train_across_time.agent;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.jetbrains.annotations.Nullable;
import survivalblock.train_across_time.common.TATConstants;

import java.nio.file.Path;

public class PlatformImpl implements TATConstants.Platform {
    public final LogCategory LOGGER = LogCategory.create("Train Across Time");

    @Override
    public @Nullable Path debugOutputPath() {
        return FabricLoader.getInstance().isDevelopmentEnvironment() ? FabricLoader.getInstance().getGameDir().toAbsolutePath().resolve(".wathe_port_debug") : null;
    }

    @Override
    public boolean compileMappings() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void info(String s) {
        Log.info(LOGGER, s);
    }

    @Override
    public void warn(String s) {
        Log.warn(LOGGER, s);
    }

    @Override
    public void error(String s, Throwable throwable) {
        Log.error(LOGGER, s, throwable);
    }
}
