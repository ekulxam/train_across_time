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

import org.jetbrains.annotations.Nullable;
import survivalblock.train_across_time.common.TATConstants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PlatformImpl implements TATConstants.Platform {
    @Override
    public @Nullable Path debugOutputPath() {
        return TATAgent.IS_DEV ? Paths.get(".wathe_port_debug").toAbsolutePath() : null;
    }

    @Override
    public boolean compileMappings() {
        return TATAgent.IS_DEV;
    }

    @Override
    public void info(String s) {
        System.out.println("[Train Across Time] " + s);
    }

    @Override
    public void warn(String s) {
        System.out.println("[Train Across Time] Warning: " + s);
    }

    @Override
    public void error(String s, Throwable throwable) {
        System.err.println("[Train Across Time] " + s);
        throwable.printStackTrace(System.err);
    }
}
