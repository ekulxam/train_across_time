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
package survivalblock.train_across_time.common.agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.Objects;

/**
 * @author Typho
 */
public class AgentLoader {
    public static Instrumentation INSTRUMENTATION = null;

    public static void loadAgent() {
        try (InputStream in = Objects.requireNonNull(AgentLoader.class.getResourceAsStream("/agent.jar"))) {
            var tempDir = Files.createTempDirectory("train_across_time");
            var agentPath = tempDir.resolve("agent.jar");

            try (OutputStream out = Files.newOutputStream(agentPath)) {
                in.transferTo(out);
            }

            var process = new ProcessBuilder(
                    "java",
                    "-jar",
                    agentPath.toAbsolutePath().toString(),
                    String.valueOf(ProcessHandle.current().pid()),
                    agentPath.toAbsolutePath().toString()
            )
                    .inheritIO()
                    .start();
            var exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new AssertionError("Unable to load Train Across Time agent, exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}