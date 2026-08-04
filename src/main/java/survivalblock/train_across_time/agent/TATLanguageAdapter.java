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
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import survivalblock.train_across_time.common.TATConstants;
import survivalblock.train_across_time.common.remap.*;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;

/**
 * @author Typho
 */
@SuppressWarnings("unused")
public class TATLanguageAdapter implements LanguageAdapter {
    public static final WatheTransformer TRANSFORMER = new WatheTransformer();

    static {
        TATConstants.PLATFORM.info("Committing sins");

        nukeAW(TATConstants.WATHE);
        nukeAW(TATConstants.RATATOUILLE);

        AgentLoader.loadAgent();

        TATConstants.PLATFORM.info("Successfully loaded java agent " + AgentLoader.INSTRUMENTATION);

        AgentLoader.INSTRUMENTATION.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                var transformed = TRANSFORMER.transform(Opcodes.ASM9, true, visitor -> {
                    new ClassReader(classfileBuffer).accept(visitor, 0);
                });

                if (transformed == null) {
                    TRANSFORMER.debugSaveClass(className, () -> classfileBuffer);
                    return null;
                }

                var bytes = transformed.toByteArray();

                TRANSFORMER.debugSaveClass(className, () -> bytes == null ? classfileBuffer : bytes);

                return bytes;
            }
        });
    }

    public static ClassNode transformMixin(ClassNode oldNode) {
        var transformed = TRANSFORMER.transform(Opcodes.ASM9, true, oldNode::accept);

        if (transformed == null) {
            TRANSFORMER.debugSaveClass(oldNode.name, () -> {
                var writer = new ClassWriter(0);
                oldNode.accept(writer);
                return writer.toByteArray();
            });
        } else {
            TRANSFORMER.debugSaveClass(oldNode.name, transformed::toByteArray);
            return transformed.node();
        }

        return oldNode;
    }

    public static void nukeAW(String modId) {
        ModContainer container = FabricLoader.getInstance().getModContainer(modId).orElseThrow();
        ModMetadata metadata = container.getMetadata();
        TATConstants.PLATFORM.info(modId + " metadata is an instance of " + metadata.getClass());

        try {
            Field classTweaker = metadata.getClass().getDeclaredField("classTweaker");
            classTweaker.setAccessible(true);
            classTweaker.set(metadata, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        TATConstants.PLATFORM.info("Successfully nuked access widener of mod " + modId);
    }

    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) throws LanguageAdapterException {
        throw new LanguageAdapterException("Do not use the language adapter 'wathe_port', it is merely a cursed method of running code before access wideners are loaded.");
    }
}