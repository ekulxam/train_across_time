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
import survivalblock.train_across_time.common.WatheTransformer;
import survivalblock.train_across_time.common.remap.*;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Typho
 */
@SuppressWarnings("unused")
public class TATLanguageAdapter implements LanguageAdapter {
    public static final WatheTransformer TRANSFORMER = new WatheTransformer();

    static {
        TATConstants.PLATFORM.info("Committing sins");

        if (TRANSFORMER.debugPath != null) {
            try {
                try (var directoryStream = Files.walk(TRANSFORMER.debugPath)) {
                    //noinspection ResultOfMethodCallIgnored
                    directoryStream.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            } catch (IOException e) {
                TATConstants.PLATFORM.error("Unable to fully delete debug directory!", e);
            }
        }

        nukeAW(TATConstants.WATHE);
        nukeAW(TATConstants.RATATOUILLE);

        AgentLoader.loadAgent();

        TATConstants.PLATFORM.info("Successfully loaded java agent " + AgentLoader.INSTRUMENTATION);

        TATConstants.PLATFORM.info("Already loaded mixin classes: " + Arrays.stream(AgentLoader.INSTRUMENTATION.getAllLoadedClasses()).filter(c -> c.getName().contains("spongepowered")).toList());

        AgentLoader.INSTRUMENTATION.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (TATConstants.shouldTransformMixinClass(className)) {
                    var node = new ClassNode();
                    new ClassReader(classfileBuffer).accept(node, 0);
                    var writerFlags = 0;

                    switch (className) {
                        case TATConstants.MIXIN_PROCESSOR_CLASS -> {
                            writerFlags = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;

                            for (MethodNode method : node.methods) {
                                switch (method.name) {
                                    case "couldTransformClass" -> {
                                        var ordinal = 0;

                                        for (AbstractInsnNode insn : method.instructions) {
                                            if (insn.getOpcode() == Opcodes.POP && ordinal++ == 0) {
                                                var insns = new InsnList();

                                                insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                                insns.add(new MethodInsnNode(
                                                        Opcodes.INVOKESTATIC,
                                                        "survivalblock/train_across_time/agent/TATLanguageAdapter",
                                                        "couldTransformClass",
                                                        "(Ljava/lang/String;)Z"
                                                ));

                                                var label = new LabelNode();
                                                insns.add(new JumpInsnNode(Opcodes.IFEQ, label));

                                                insns.add(new InsnNode(Opcodes.ICONST_1));
                                                insns.add(new VarInsnNode(Opcodes.ISTORE, 3));

                                                insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                                insns.add(new FieldInsnNode(
                                                        Opcodes.GETFIELD,
                                                        "org/spongepowered/asm/mixin/transformer/MixinProcessor",
                                                        "lock",
                                                        "Lorg/spongepowered/asm/util/ReEntranceLock;"
                                                ));
                                                insns.add(new MethodInsnNode(
                                                        Opcodes.INVOKEVIRTUAL,
                                                        "org/spongepowered/asm/util/ReEntranceLock",
                                                        "pop",
                                                        "()Lorg/spongepowered/asm/util/ReEntranceLock;"
                                                ));
                                                insns.add(new InsnNode(Opcodes.POP));

                                                insns.add(new VarInsnNode(Opcodes.ILOAD, 3));
                                                insns.add(new InsnNode(Opcodes.IRETURN));

                                                insns.add(label);

                                                method.instructions.insert(insn, insns);
                                            }
                                        }
                                    }
                                    case "applyMixins" -> {
                                        var ordinal = 0;

                                        for (AbstractInsnNode insn : method.instructions) {
                                            if (insn instanceof VarInsnNode v && v.var == 0 && ordinal++ == 3) {
                                                var transformedVar = 6;
                                                var nodeVar = 3;

                                                var insns = new InsnList();

                                                insns.add(new VarInsnNode(Opcodes.ALOAD, nodeVar));
                                                insns.add(new MethodInsnNode(
                                                        Opcodes.INVOKESTATIC,
                                                        "survivalblock/train_across_time/agent/TATLanguageAdapter",
                                                        "transformUnary",
                                                        "(Lorg/objectweb/asm/tree/ClassNode;)Z"
                                                ));

                                                insns.add(new VarInsnNode(Opcodes.ILOAD, transformedVar));
                                                insns.add(new InsnNode(Opcodes.IOR));
                                                insns.add(new VarInsnNode(Opcodes.ISTORE, transformedVar));

                                                method.instructions.insertBefore(insn, insns);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        case TATConstants.CLASS_INFO_CLASS -> {
                            //writerFlags = ClassWriter.COMPUTE_MAXS;

                            for (MethodNode method : node.methods) {
                                if (method.name.equals("forName")) {
                                    var ordinal = 0;

                                    for (AbstractInsnNode insn : method.instructions) {
                                        if (insn instanceof MethodInsnNode m && m.name.equals("<init>") && ordinal++ == 1) {
                                            var insns = new InsnList();
                                            insns.add(new InsnNode(Opcodes.ICONST_0));
                                            insns.add(new MethodInsnNode(
                                                    Opcodes.INVOKESTATIC,
                                                    "survivalblock/train_across_time/agent/TATLanguageAdapter",
                                                    "transformStatic",
                                                    "(Lorg/objectweb/asm/tree/ClassNode;Z)Lorg/objectweb/asm/tree/ClassNode;"
                                            ));
                                            insns.add(new VarInsnNode(Opcodes.ASTORE, 3));
                                            insns.add(new VarInsnNode(Opcodes.ALOAD, 3));
                                            method.instructions.insertBefore(insn, insns);
                                        }
                                    }
                                }
                            }
                        }
                        case TATConstants.MIXIN_INFO_CLASS -> {
                            //writerFlags = ClassWriter.COMPUTE_MAXS;

                            for (MethodNode method : node.methods) {
                                if (method.name.equals("loadMixinClass")) {
                                    for (AbstractInsnNode insn : method.instructions) {
                                        if (insn.getOpcode() == Opcodes.ARETURN) {
                                            var insns = new InsnList();
                                            insns.add(new InsnNode(Opcodes.ICONST_1));
                                            insns.add(new MethodInsnNode(
                                                    Opcodes.INVOKESTATIC,
                                                    "survivalblock/train_across_time/agent/TATLanguageAdapter",
                                                    "transformStatic",
                                                    "(Lorg/objectweb/asm/tree/ClassNode;Z)Lorg/objectweb/asm/tree/ClassNode;"
                                            ));
                                            insns.add(new VarInsnNode(Opcodes.ASTORE, 2));
                                            insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                            method.instructions.insertBefore(insn, insns);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    var writer = new ClassWriter(writerFlags);
                    node.accept(writer);
                    var bytes = writer.toByteArray();
                    TRANSFORMER.debugSaveClass(className, () -> bytes);
                    return bytes;
                }

                return null;

                /*
                var transformed = TRANSFORMER.transform(Opcodes.ASM9, true, visitor -> {
                    new ClassReader(classfileBuffer).accept(visitor, 0);
                });

                if (transformed == null) {
                    return null;
                }

                var bytes = transformed.toByteArray();

                TRANSFORMER.debugSaveClass(className, () -> bytes == null ? classfileBuffer : bytes);

                return bytes;
                 */
            }
        });
    }

    public static boolean couldTransformClass(String name) {
        if (name.contains("LimitedHandledScreen")) {
            TATConstants.PLATFORM.info(name);
        }

        return TATConstants.shouldTransformClass(name.replace('.', '/'));
    }

    public static ClassNode transformStatic(ClassNode oldNode, boolean debug) {
        var transformed = TRANSFORMER.transform(Opcodes.ASM9, true, oldNode::accept);

        if (transformed != null) {
            if (debug) {
                TRANSFORMER.debugSaveClass(oldNode.name, transformed::toByteArray);
            }

            return transformed.node();
        }

        return oldNode;
    }

    public static boolean transformUnary(ClassNode node) {
        var newNode = transformStatic(node, true);

        if (newNode != node) {
            // we don't talk about it :aga: - typho
            node.version = 0;
            node.access = 0;
            node.name = null;
            node.signature = null;
            node.superName = null;
            node.sourceFile = null;
            node.sourceDebug = null;
            node.outerClass = null;
            node.outerMethod = null;
            node.outerMethodDesc = null;
            node.nestHostClass = null;
            node.module = null;
            node.nestMembers = null;
            node.permittedSubclasses = null;

            node.interfaces = new ArrayList<>();
            node.fields = new ArrayList<>();
            node.methods = new ArrayList<>();
            node.innerClasses = new ArrayList<>();
            node.recordComponents = null;

            node.visibleAnnotations = null;
            node.invisibleAnnotations = null;
            node.visibleTypeAnnotations = null;
            node.invisibleTypeAnnotations = null;
            node.attrs = null;

            newNode.accept(node);

            return true;
        } else {
            return false;
        }
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