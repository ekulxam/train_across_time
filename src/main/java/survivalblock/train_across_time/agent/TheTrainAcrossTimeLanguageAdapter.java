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
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.LanguageAdapterException;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.util.log.Log;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import survivalblock.train_across_time.agent.remap.ClassOutputInfo;
import survivalblock.train_across_time.agent.remap.MixinClassRemapper;
import survivalblock.train_across_time.agent.remap.WatheClassPatches;
import survivalblock.train_across_time.agent.remap.WatheRemapper;

import java.io.FileNotFoundException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.security.ProtectionDomain;

import static survivalblock.train_across_time.TheTrainAcrossTimeConstants.LOGGER;

/**
 * @author Typho
 */
public class TheTrainAcrossTimeLanguageAdapter implements LanguageAdapter {
    static {
        Log.info(LOGGER, "Committing sins");

        nukeAW("wathe");
        nukeAW("ratatouille");

        AgentLoader.loadAgent();

        Log.info(LOGGER, "Successfully loaded java agent " + AgentLoader.INSTRUMENTATION);

        var debugPath = FabricLoader.getInstance().isDevelopmentEnvironment() ? FabricLoader.getInstance().getGameDir().toAbsolutePath().resolve(".wathe_port_debug") : null;

        AgentLoader.INSTRUMENTATION.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (!(className.startsWith("dev/doctor4t/wathe") || className.startsWith("dev/doctor4t/ratatouille"))) {
                    return null;
                }

                try {
                    var node = new ClassNode();
                    var info = new ClassOutputInfo(className);
                    new ClassReader(classfileBuffer).accept(new MixinClassRemapper(node, new WatheRemapper(Opcodes.ASM9, info)), 0);

                    var patch = WatheClassPatches.PATCHES.get(className);

                    if (patch != null) {
                        info.markChanged();
                        patch.accept(node, info);
                    }

                    byte[] bytes = null;
                    var writer = info.end();

                    if (writer != null) {
                        node.accept(writer);
                        bytes = writer.toByteArray();
                    }

                    if (debugPath != null) {
                        var path = debugPath.resolve(className + ".class");
                        var folder = path.getParent().toFile();

                        if (folder.mkdirs() || folder.exists()) {
                            Files.write(path, bytes == null ? classfileBuffer : bytes);
                        } else {
                            throw new FileNotFoundException("File with path " + path + " could not be written to!");
                        }
                    }

                    return bytes;
                } catch (Exception e) {
                    Log.error(LOGGER, "Error while processing " + className, e);
                }

                return null;
            }
        });
    }

    public static void nukeAW(String modId) {
        ModContainer container = FabricLoader.getInstance().getModContainer(modId).orElseThrow();
        ModMetadata metadata = container.getMetadata();
        Log.info(LOGGER, modId + " metadata is an instance of " + metadata.getClass());

        try {
            Field classTweaker = metadata.getClass().getDeclaredField("classTweaker");
            classTweaker.setAccessible(true);
            classTweaker.set(metadata, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Log.info(LOGGER, "Successfully nuked access widener of mod " + modId);
    }

    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) throws LanguageAdapterException {
        throw new LanguageAdapterException("Do not use the language adapter 'wathe_port', it is merely a cursed method of running code before access wideners are loaded.");
    }
}