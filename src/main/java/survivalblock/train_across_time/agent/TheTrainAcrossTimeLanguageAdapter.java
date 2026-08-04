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
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import survivalblock.train_across_time.agent.remap.*;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static survivalblock.train_across_time.TheTrainAcrossTimeConstants.LOGGER;

/**
 * @author Typho
 */
@SuppressWarnings("unused")
public class TheTrainAcrossTimeLanguageAdapter implements LanguageAdapter {
    public static final Path DEBUG_PATH = FabricLoader.getInstance().isDevelopmentEnvironment() ? FabricLoader.getInstance().getGameDir().toAbsolutePath().resolve(".wathe_port_debug") : null;
    public static final ClassOutputInfo.UsedMappingsOutput USED_MAPPINGS_OUTPUT;

    static {
        Log.info(LOGGER, "Committing sins");

        var mappingsOutputFile = System.getProperty("train_across_time:mappings_output_file");
        USED_MAPPINGS_OUTPUT = mappingsOutputFile == null ? ClassOutputInfo.UsedMappingsOutput.NONE : new ClassOutputInfo.UsedMappingsOutput() {
            public final WatheMappingsCache cache = WatheMappingsCache.create();
            public final Path outputFile = Paths.get(mappingsOutputFile);

            @Override
            public void useClass(String intermediary) {
                this.cache.classes.put(intermediary, WatheMappingsCache.INSTANCE.classes.get(intermediary));
            }

            @Override
            public void useMethod(String intermediary) {
                this.cache.methods.put(intermediary, WatheMappingsCache.INSTANCE.methods.get(intermediary));
            }

            @Override
            public void useField(String intermediary) {
                this.cache.fields.put(intermediary, WatheMappingsCache.INSTANCE.fields.get(intermediary));
            }

            @Override
            public void endClass() {
                try (OutputStream out = Files.newOutputStream(this.outputFile)) {
                    this.cache.save(new DataOutputStream(out));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        WatheMappingsCache.INSTANCE.reload();

        nukeAW("wathe");
        nukeAW("ratatouille");

        AgentLoader.loadAgent();

        Log.info(LOGGER, "Successfully loaded java agent " + AgentLoader.INSTRUMENTATION);

        AgentLoader.INSTRUMENTATION.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (className.equals("org/spongepowered/asm/mixin/transformer/MixinInfo")) {
                    var node = new ClassNode();
                    new ClassReader(classfileBuffer).accept(node, 0);

                    for (MethodNode method : node.methods) {
                        if (method.name.equals("loadMixinClass")) {
                            for (AbstractInsnNode insn : method.instructions) {
                                if (insn.getOpcode() == Opcodes.ARETURN) {
                                    var insns = new InsnList();
                                    insns.add(new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            Type.getInternalName(TheTrainAcrossTimeLanguageAdapter.class),
                                            "transformMixin",
                                            "(Lorg/objectweb/asm/tree/ClassNode;)Lorg/objectweb/asm/tree/ClassNode;"
                                    ));
                                    insns.add(new VarInsnNode(Opcodes.ASTORE, 2));
                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                    method.instructions.insertBefore(insn, insns);
                                }
                            }
                        }
                    }

                    var writer = new ClassWriter(0);
                    node.accept(writer);
                    return writer.toByteArray();
                }

                if (!(className.startsWith("dev/doctor4t/wathe") || className.startsWith("dev/doctor4t/ratatouille"))) {
                    return null;
                }

                try {
                    var info = new ClassOutputInfo(className, USED_MAPPINGS_OUTPUT);
                    var node = TheTrainAcrossTimeLanguageAdapter.transform(info, visitor -> new ClassReader(classfileBuffer).accept(visitor, 0));

                    byte[] bytes;
                    var writer = info.end();

                    if (writer == null) {
                        bytes = null;
                    } else {
                        node.accept(writer);
                        bytes = writer.toByteArray();
                    }

                    debugSaveClass(className, () -> bytes == null ? classfileBuffer : bytes);

                    return bytes;
                } catch (Exception e) {
                    Log.error(LOGGER, "Error while processing " + className, e);
                }

                return null;
            }
        });
    }

    public static ClassNode transform(
            ClassOutputInfo info,
            Consumer<ClassVisitor> visitor
    ) {
        var node = new ClassNode();
        visitor.accept(new MixinClassRemapper(new MixinClassRemapper(node, new WatheRemapper(Opcodes.ASM9, info)), WatheMappingsCache.INSTANCE.createRemapper(Opcodes.ASM9, info)));

        var patch = WatheClassPatches.PATCHES.get(info.className);

        if (patch != null) {
            info.markChanged();
            patch.accept(node, info);
        }

        return node;
    }

    public static void debugSaveClass(
            String className,
            Supplier<byte[]> bytes
    ) throws IOException {
        if (DEBUG_PATH != null) {
            var path = DEBUG_PATH.resolve(className + ".class");
            var folder = path.getParent().toFile();

            if (className.contains("GameConstants")) {
                System.out.println("Saving " + className + " to " + path);
            }

            if (folder.mkdirs() || folder.exists()) {
                Files.write(path, bytes.get());
            } else {
                throw new FileNotFoundException("File with path " + path + " could not be written to!");
            }
        }
    }

    public static ClassNode transformMixin(ClassNode oldNode) {
        if (!(oldNode.name.startsWith("dev/doctor4t/wathe") || oldNode.name.startsWith("dev/doctor4t/ratatouille"))) {
            return oldNode;
        }

        try {
            var info = new ClassOutputInfo(oldNode.name, USED_MAPPINGS_OUTPUT);
            var node = TheTrainAcrossTimeLanguageAdapter.transform(info, oldNode::accept);

            var writer = info.end();

            if (writer != null) {
                debugSaveClass(oldNode.name, () -> {
                    node.accept(writer);
                    return writer.toByteArray();
                });
            }

            return node;
        } catch (Exception e) {
            Log.error(LOGGER, "Error while processing mixin " + oldNode.name, e);
        }

        return oldNode;
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