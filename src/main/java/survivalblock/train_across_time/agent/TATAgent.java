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

import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.impl.metadata.ModDependencyImpl;
import net.fabricmc.loader.impl.metadata.NestedJarEntry;
import net.typho.asm_util.ClassTransformInfo;
import net.typho.asm_util.insn.InsnPointer;
import net.typho.asm_util.method.MethodPointer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import survivalblock.train_across_time.common.TATConstants;
import survivalblock.train_across_time.common.WatheTransformer;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Typho
 */
@SuppressWarnings({"unused"})
public class TATAgent {
    public static final WatheTransformer TRANSFORMER = new WatheTransformer();
    public static Set<String> TRANSFORMED = ConcurrentHashMap.newKeySet();

    public static void premain(String args, Instrumentation inst) {
        TATConstants.PLATFORM.info("I solemnly swear that I am up to no good >:3");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // add a breakpoint here if this is needed, it runs but doesn't actually print
            System.out.println("[Train Across Time] Transformed " + TRANSFORMED.size() + " classes");
        }));

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

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (TATConstants.shouldTransformEarlyClass(className)) {
                    var info = new ClassTransformInfo.AgentTransform(classfileBuffer);

                    switch (className) {
                        case TATConstants.V0_METADATA_PARSER_CLASS -> {
                            info.markChanged();
                            info.computeMaxStacks();

                            MethodPointer.method()
                                    .name("parse")
                                    .findOrThrow(info.getNode(), method -> {
                                        InsnPointer.type(TATConstants.V0_METADATA_CLASS)
                                                .lastOrdinal()
                                                .findOrThrow(method.instructions, insn -> {
                                                    var insns = new InsnList();
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 4));
                                                    insns.add(new MethodInsnNode(
                                                            Opcodes.INVOKESTATIC,
                                                            "survivalblock/train_across_time/agent/TATAgent",
                                                            "tweakModDependencies",
                                                            "(Ljava/lang/String;Ljava/util/List;)V"
                                                    ));
                                                    method.instructions.insertBefore(insn, insns);
                                                });
                                    });
                        }
                        case TATConstants.V1_METADATA_PARSER_CLASS -> {
                            info.markChanged();
                            info.computeMaxStacks();

                            MethodPointer.method()
                                    .name("parse")
                                    .findOrThrow(info.getNode(), method -> {
                                        InsnPointer.type(TATConstants.V1_METADATA_CLASS)
                                                .lastOrdinal()
                                                .findOrThrow(method.instructions, insn -> {
                                                    var insns = new InsnList();
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 10));
                                                    insns.add(new MethodInsnNode(
                                                            Opcodes.INVOKESTATIC,
                                                            "survivalblock/train_across_time/agent/TATAgent",
                                                            "tweakModDependencies",
                                                            "(Ljava/lang/String;Ljava/util/List;)V"
                                                    ));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 7));
                                                    insns.add(new MethodInsnNode(
                                                            Opcodes.INVOKESTATIC,
                                                            "survivalblock/train_across_time/agent/TATAgent",
                                                            "tweakNestedJars",
                                                            "(Ljava/lang/String;Ljava/util/List;)V"
                                                    ));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 9));
                                                    insns.add(new MethodInsnNode(
                                                            Opcodes.INVOKESTATIC,
                                                            "survivalblock/train_across_time/agent/TATAgent",
                                                            "tweakClassTweaker",
                                                            "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
                                                    ));
                                                    insns.add(new VarInsnNode(Opcodes.ASTORE, 9));
                                                    method.instructions.insertBefore(insn, insns);
                                                });
                                    });
                        }
                        case TATConstants.MIXIN_PROCESSOR_CLASS -> {
                            info.markChanged();
                            info.computeMaxStacks();
                            info.computeFrames();

                            MethodPointer.method()
                                    .name("couldTransformClass")
                                    .findOrThrow(info.getNode(), method -> {
                                        InsnPointer.simple()
                                                .opcode(Opcodes.POP)
                                                .findOrThrow(method.instructions, insn -> {
                                                    var insns = new InsnList();

                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                                    insns.add(new MethodInsnNode(
                                                            Opcodes.INVOKESTATIC,
                                                            "survivalblock/train_across_time/agent/TATAgent",
                                                            "couldTransformClass",
                                                            "(Ljava/lang/String;)Z"
                                                    ));

                                                    var label = new LabelNode();
                                                    insns.add(new JumpInsnNode(Opcodes.IFEQ, label));

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

                                                    insns.add(new InsnNode(Opcodes.ICONST_1));
                                                    insns.add(new InsnNode(Opcodes.IRETURN));

                                                    insns.add(label);

                                                    method.instructions.insert(insn, insns);
                                                });
                                    });
                            MethodPointer.method()
                                    .name("applyMixins")
                                    .findOrThrow(info.getNode(), method -> {
                                        InsnPointer.localOperation()
                                                .id(0)
                                                .ordinal(3)
                                                .findOrThrow(method.instructions, insn -> {
                                                    var transformedVar = 6;
                                                    var nodeVar = 3;

                                                    var insns = new InsnList();

                                                    insns.add(new VarInsnNode(Opcodes.ILOAD, transformedVar));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, nodeVar));
                                                    insns.add(new MethodInsnNode(
                                                            Opcodes.INVOKESTATIC,
                                                            "survivalblock/train_across_time/agent/TATAgent",
                                                            "transformUnary",
                                                            "(Lorg/objectweb/asm/tree/ClassNode;)Z"
                                                    ));

                                                    insns.add(new InsnNode(Opcodes.IOR));
                                                    insns.add(new VarInsnNode(Opcodes.ISTORE, transformedVar));

                                                    method.instructions.insertBefore(insn, insns);
                                                });
                                    });
                        }
                        case TATConstants.CLASS_INFO_CLASS -> {
                            info.markChanged();
                            info.computeMaxStacks();

                            MethodPointer.method()
                                    .name("forName")
                                    .findOrThrow(info.getNode(), method -> {
                                        InsnPointer.methodCall()
                                                .name("<init>")
                                                .ordinal(1)
                                                .findOrThrow(method.instructions, insn -> {
                                                    var insns = new InsnList();
                                                    insns.add(new InsnNode(Opcodes.ICONST_0));
                                                    insns.add(new MethodInsnNode(
                                                            Opcodes.INVOKESTATIC,
                                                            "survivalblock/train_across_time/agent/TATAgent",
                                                            "transformStatic",
                                                            "(Lorg/objectweb/asm/tree/ClassNode;Z)Lorg/objectweb/asm/tree/ClassNode;"
                                                    ));
                                                    insns.add(new VarInsnNode(Opcodes.ASTORE, 3));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 3));
                                                    method.instructions.insertBefore(insn, insns);
                                                });
                                    });
                        }
                        case TATConstants.MIXIN_INFO_CLASS -> {
                            info.markChanged();
                            info.computeMaxStacks();

                            MethodPointer.method()
                                    .name("loadMixinClass")
                                    .findOrThrow(info.getNode(), method -> {
                                        InsnPointer.simple()
                                                .opcode(Opcodes.ARETURN)
                                                .findOrThrow(method.instructions, insn -> {
                                                    var insns = new InsnList();
                                                    insns.add(new InsnNode(Opcodes.ICONST_1));
                                                    insns.add(new MethodInsnNode(
                                                            Opcodes.INVOKESTATIC,
                                                            "survivalblock/train_across_time/agent/TATAgent",
                                                            "transformStatic",
                                                            "(Lorg/objectweb/asm/tree/ClassNode;Z)Lorg/objectweb/asm/tree/ClassNode;"
                                                    ));
                                                    insns.add(new VarInsnNode(Opcodes.ASTORE, 2));
                                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                                    method.instructions.insertBefore(insn, insns);
                                                });
                                    });
                        }
                    }

                    return info.compile((name, bytes) -> TRANSFORMER.debugSaveClass(name, () -> bytes));
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
        }, true);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean couldTransformClass(String name) {
        return TATConstants.shouldTransformClass(name.replace('.', '/'));
    }

    public static ClassNode transformStatic(ClassNode node, boolean debug) {
        if (!couldTransformClass(node.name)) {
            return node;
        }

        TRANSFORMED.add(node.name);

        var info = new ClassTransformInfo.MutableWrapper(node);

        TRANSFORMER.transform(Opcodes.ASM9, true, info);

        if (info.changed && debug) {
            TRANSFORMER.debugSaveClass(node.name, () -> {
                var writer = new ClassWriter(0);
                info.getNode().accept(writer);
                return writer.toByteArray();
            });
        }

        return info.getNode();
    }

    public static boolean transformUnary(ClassNode node) {
        if (!couldTransformClass(node.name)) {
            return false;
        }

        TRANSFORMED.add(node.name);

        var info = new ClassTransformInfo.Wrapper(node);

        TRANSFORMER.transform(Opcodes.ASM9, true, info);

        if (info.changed) {
            TRANSFORMER.debugSaveClass(node.name, () -> {
                var writer = new ClassWriter(0);
                info.getNode().accept(writer);
                return writer.toByteArray();
            });
        }

        return info.changed;
    }

    public static void tweakModDependencies(String modId, List<ModDependency> dependencies) {
        if (TATConstants.MODS_TO_TWEAK.contains(modId)) {
            TATConstants.PLATFORM.info("Fixing dependencies for mod " + modId);

            dependencies.replaceAll(dep -> {
                if (dep.getModId().equals("minecraft")) {
                    try {
                        return new ModDependencyImpl(
                                dep.getKind(),
                                dep.getModId(),
                                List.of("~26.1")
                        );
                    } catch (VersionParsingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return dep;
                }
            });
        }
    }

    public static void tweakNestedJars(String modId, List<NestedJarEntry> jars) {
        if (TATConstants.MODS_TO_TWEAK.contains(modId)) {
            TATConstants.PLATFORM.info("Removing JiJs for mod " + modId);

            jars.clear();
        }
    }

    public static String tweakClassTweaker(String modId, String classTweaker) {
        return TATConstants.MODS_TO_TWEAK.contains(modId) ? null : classTweaker;
    }
}