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
package survivalblock.train_across_time.common.patch;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;import survivalblock.train_across_time.common.TATConstants;
import survivalblock.train_across_time.common.util.ClassOutputInfo;
import survivalblock.train_across_time.common.util.TransformedClass;
import survivalblock.train_across_time.common.util.ptr.InsnPointer;

import java.io.IOException;
import java.io.PrintWriter;import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Typho
 */
@SuppressWarnings({"SwitchStatementWithTooFewBranches", "unchecked"})
public class WatheClassPatches {
    private WatheClassPatches() {
    }

    public static final String AT = "Lorg/spongepowered/asm/mixin/injection/At;";
    public static final String MODIFY_EXPRESSION_VALUE = "Lcom/llamalad7/mixinextras/injector/ModifyExpressionValue;";
    public static final String MODIFY_RETURN_VALUE = "Lcom/llamalad7/mixinextras/injector/ModifyReturnValue;";
    public static final String WRAP_OPERATION = "Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;";
    public static final String INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;";

    public static final Map<String, BiConsumer<ClassNode, ClassOutputInfo>> PATCHES = new HashMap<>();

    public static void register(List<String> classes, BiConsumer<ClassNode, ClassOutputInfo> patch) {
        for (String cls : classes) {
            PATCHES.merge(cls, patch, (a, b) -> (node, info) -> {
                a.accept(node, info);
                b.accept(node, info);
            });
        }
    }

    public static void applyItemIds(MethodNode method, Map<Integer, String> fallbacks) {
        String[] namespace = new String[1];
        String[] path = new String[1];
        int[] index = new int[1];

        method.instructions.forEach(node -> {
            if (node instanceof LdcInsnNode ldc && path[0] == null && ldc.cst instanceof String p) {
                path[0] = p;
            } else if (node instanceof MethodInsnNode insn) {
                if (insn.name.equals("<init>")) {
                    switch (insn.owner) {
                        case "dev/doctor4t/ratatouille/util/registrar/ItemRegistrar" -> {
                            namespace[0] = path[0];
                            path[0] = null;
                        }
                        case "net/minecraft/world/item/Item$Properties" -> {
                            InsnList insns = new InsnList();
                            insns.add(new FieldInsnNode(
                                    Opcodes.GETSTATIC,
                                    "net/minecraft/core/registries/Registries",
                                    "ITEM",
                                    "Lnet/minecraft/resources/ResourceKey;"
                            ));
                            insns.add(new LdcInsnNode(Objects.requireNonNull(namespace[0], "Couldn't find a namespace")));
                            insns.add(new LdcInsnNode(Objects.requireNonNull(path[0] == null ? fallbacks.get(index[0]) : path[0], "Couldn't find a path")));
                            insns.add(new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "net/minecraft/resources/Identifier",
                                    "fromNamespaceAndPath",
                                    "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/Identifier;"
                            ));
                            insns.add(new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "net/minecraft/resources/ResourceKey",
                                    "create",
                                    "(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/ResourceKey;"
                            ));
                            insns.add(new MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "net/minecraft/world/item/Item$Properties",
                                    "setId",
                                    "(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item$Properties;"
                            ));
                            method.instructions.insert(node, insns);
                            path[0] = null;
                            index[0]++;
                        }
                    }
                }
            }
        });
    }

    public static <T extends AbstractInsnNode> boolean transmuteInsn(
            ClassNode node,
            String name,
            InsnPointer<T, ?> at,
            Consumer<T> transmuter
    ) {
        return applyToMethod(node, name, method -> {
            transmuter.accept(at.findOrThrow(method.instructions));
        });
    }

    public static boolean spliceMethod(
            ClassNode node,
            String name,
            InsnPointer<?, ?> at
    ) {
        return applyToMethod(node, name, method -> {
            method.instructions.remove(at.findOrThrow(method.instructions));
        });
    }

    public static boolean spliceMethod(
            ClassNode node,
            String name,
            InsnPointer<?, ?> at,
            BiConsumer<MethodNode, InsnList> replacement
    ) {
        return applyToMethod(node, name, method -> {
            var atNode = at.findOrThrow(method.instructions);

            var insns = new InsnList();
            replacement.accept(method, insns);

            method.instructions.insertBefore(atNode, insns);
            method.instructions.remove(atNode);
        });
    }

    public static boolean spliceMethod(
            ClassNode node,
            String name,
            InsnPointer<?, ?> from,
            InsnPointer<?, ?> to
    ) {
        return applyToMethod(node, name, method -> {
            var fromNode = from.findOrThrow(method.instructions);
            var toNode = to.findOrThrow(method.instructions);

            if (fromNode == toNode) {
                method.instructions.remove(fromNode);
            } else {
                while (fromNode.getNext() != toNode) {
                    method.instructions.remove(fromNode.getNext());

                    if (fromNode.getNext() == null) {
                        throw new IllegalStateException();
                    }
                }

                method.instructions.remove(fromNode);
                method.instructions.remove(toNode);
            }
        });
    }

    public static boolean spliceMethod(
            ClassNode node,
            String name,
            InsnPointer<?, ?> from,
            InsnPointer<?, ?> to,
            BiConsumer<MethodNode, InsnList> replacement
    ) {
        return applyToMethod(node, name, method -> {
            var fromNode = from.findOrThrow(method.instructions);
            var toNode = to.findOrThrow(method.instructions);

            var insns = new InsnList();
            replacement.accept(method, insns);

            method.instructions.insertBefore(fromNode, insns);

            if (fromNode == toNode) {
                method.instructions.remove(fromNode);
            } else {
                while (fromNode.getNext() != toNode) {
                    method.instructions.remove(fromNode.getNext());

                    if (fromNode.getNext() == null) {
                        throw new IllegalStateException();
                    }
                }

                method.instructions.remove(fromNode);
                method.instructions.remove(toNode);
            }
        });
    }

    public static boolean applyToMethod(ClassNode node, String name, Consumer<MethodNode> action) {
        var found = false;

        for (MethodNode methodNode : node.methods) {
            if ((methodNode.access & Opcodes.ACC_BRIDGE) == 0 && methodNode.name.equals(name)) {
                action.accept(methodNode);
                found = true;
            }
        }

        return found;
    }

    public static boolean applyToField(ClassNode node, String name, Consumer<FieldNode> action) {
        var found = false;

        for (FieldNode fieldNode : node.fields) {
            if (fieldNode.name.equals(name)) {
                action.accept(fieldNode);
                found = true;
            }
        }

        return found;
    }

    public static void applyBlockIds(MethodNode method, Map<Integer, String> fallbacks) {
        String[] namespace = new String[1];
        String[] path = new String[1];
        int[] index = new int[1];

        method.instructions.forEach(node -> {
            if (node instanceof LdcInsnNode ldc && path[0] == null && ldc.cst instanceof String p) {
                path[0] = p;
            } else if (node instanceof MethodInsnNode insn) {
                if (insn.name.equals("<init>")) {
                    switch (insn.owner) {
                        case "dev/doctor4t/ratatouille/util/registrar/BlockRegistrar" -> {
                            namespace[0] = path[0];
                            path[0] = null;
                        }
                    }
                } else if (insn.name.equals("of") || insn.name.equals("ofFullCopy") || insn.name.equals("ofLegacyCopy")) {
                    switch (insn.owner) {
                        case "net/minecraft/world/level/block/state/BlockBehaviour$Properties" -> {
                            InsnList insns = new InsnList();
                            insns.add(new FieldInsnNode(
                                    Opcodes.GETSTATIC,
                                    "net/minecraft/core/registries/Registries",
                                    "BLOCK",
                                    "Lnet/minecraft/resources/ResourceKey;"
                            ));
                            insns.add(new LdcInsnNode(Objects.requireNonNull(namespace[0], "Couldn't find a namespace")));
                            insns.add(new LdcInsnNode(Objects.requireNonNull(path[0] == null ? fallbacks.get(index[0]) : path[0], "Couldn't find a path")));
                            insns.add(new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "net/minecraft/resources/Identifier",
                                    "fromNamespaceAndPath",
                                    "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/Identifier;"
                            ));
                            insns.add(new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "net/minecraft/resources/ResourceKey",
                                    "create",
                                    "(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/ResourceKey;"
                            ));
                            insns.add(new MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    "net/minecraft/world/level/block/state/BlockBehaviour$Properties",
                                    "setId",
                                    "(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;"
                            ));
                            method.instructions.insert(node, insns);
                            path[0] = null;
                            index[0]++;
                        }
                    }
                }
            }
        });
    }

    public static void tweakNBTSaveMethod(
            ClassNode node,
            String methodName,
            boolean nuke
    ) {
        tweakNBTMethod(node, methodName, "net/minecraft/world/level/storage/ValueOutput", nuke);
    }

    public static void tweakNBTLoadMethod(
            ClassNode node,
            String methodName,
            boolean nuke
    ) {
        tweakNBTMethod(node, methodName, "net/minecraft/world/level/storage/ValueInput", nuke);
    }

    public static void tweakNBTMethod(
            ClassNode node,
            String methodName,
            String replacementClass,
            boolean nuke
    ) {
        String descriptor = "L" + replacementClass + ";";

        applyToMethod(node, methodName, method -> {
            method.desc = method.desc.replace("Lnet/minecraft/nbt/CompoundTag;", descriptor);

            if (nuke) {
                method.instructions.clear();
                method.instructions.add(new InsnNode(Opcodes.RETURN));
                method.localVariables.removeIf(localVariableNode -> !localVariableNode.name.equals("this") && !localVariableNode.name.equals("nbt"));
            } else {
                for (var insn : method.instructions) {
                    if (insn instanceof MethodInsnNode methodInsnNode) {
                        methodInsnNode.owner = methodInsnNode.owner.replace("net/minecraft/nbt/CompoundTag", replacementClass);
                        methodInsnNode.desc = methodInsnNode.desc.replace("Lnet/minecraft/nbt/CompoundTag;", descriptor);
                    }
                }
                for (var local : method.localVariables) {
                    local.desc = local.desc.replace("Lnet/minecraft/nbt/CompoundTag;", descriptor);
                }
            }
        });
    }

    public static void changeInjectionMethod(
            ClassNode node,
            String methodName,
            String injectionDesc,
            String... newMethod
    ) {
        applyToMethod(node, methodName, method -> {
            for (AnnotationNode anno : method.visibleAnnotations) {
                if (anno.desc.equals(injectionDesc)) {
                    var iterator = anno.values.listIterator();

                    while (iterator.hasNext()) {
                        var name = (String) iterator.next();
                        var value = iterator.next();

                        if (name.equals("method")) {
                            iterator.set(new ArrayList<>(Arrays.asList(newMethod)));
                            break;
                        }
                    }
                }
            }
        });
    }

    public static void changeInjectionAt(
            ClassNode node,
            String methodName,
            String injectionDesc,
            Object... newAt
    ) {
        applyToMethod(node, methodName, method -> {
            for (AnnotationNode anno : method.visibleAnnotations) {
                if (anno.desc.equals(injectionDesc)) {
                    var iterator = anno.values.listIterator();

                    while (iterator.hasNext()) {
                        var name = (String) iterator.next();
                        var value = iterator.next();

                        if (name.equals("at")) {
                            var newAnno = new AnnotationNode(Opcodes.ASM9, AT);
                            newAnno.values = new ArrayList<>(Arrays.asList(newAt));
                            iterator.set(newAnno);
                            break;
                        }
                    }
                }
            }
        });
    }

    private static int COUNTER = 0;

    static {
        register(List.of(
                "dev/doctor4t/wathe/cca/AutoStartComponent",
                "dev/doctor4t/wathe/cca/GameRoundEndComponent",
                "dev/doctor4t/wathe/cca/GameTimeComponent",
                "dev/doctor4t/wathe/cca/GameWorldComponent",
                "dev/doctor4t/wathe/cca/MapVariablesWorldComponent",
                "dev/doctor4t/wathe/cca/PlayerMoodComponent",
                "dev/doctor4t/wathe/cca/PlayerNoteComponent",
                "dev/doctor4t/wathe/cca/PlayerPoisonComponent",
                "dev/doctor4t/wathe/cca/PlayerPsychoComponent",
                "dev/doctor4t/wathe/cca/PlayerShopComponent",
                "dev/doctor4t/wathe/cca/ScoreboardRoleSelectorComponent",
                "dev/doctor4t/wathe/cca/TrainWorldComponent",
                "dev/doctor4t/wathe/cca/WatheComponents",
                "dev/doctor4t/wathe/cca/WorldBlackoutComponent"
        ), (node, info) -> {
            node.interfaces.add("org/ladysnake/cca/api/v3/component/Component");
        });

        register(List.of(
                "dev/doctor4t/wathe/index/WatheItems"
        ), (node, info) -> {
            applyToMethod(node, "<clinit>", method -> {
                method.instructions.remove(
                        Arrays.stream(method.instructions.toArray())
                                .filter(insn -> insn instanceof MethodInsnNode m && m.name.equals("createAttributes"))
                                .findFirst()
                                .orElseThrow()
                );
            });
        });

        register(List.of(
                "dev/doctor4t/ratatouille/util/registrar/BlockRegistrar"
        ), (node, info) -> {
            applyToMethod(node, "createWithItem", method -> {
                switch (method.desc) {
                    case "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;",
                         "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;[Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/block/Block;" -> {
                        boolean hasVarargs = method.desc.equals("(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;[Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/block/Block;");
                        InsnList insns = new InsnList();

                        insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        insns.add(new VarInsnNode(Opcodes.ALOAD, 2));

                        insns.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/world/item/Item$Properties"));
                        insns.add(new InsnNode(Opcodes.DUP));
                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKESPECIAL,
                                "net/minecraft/world/item/Item$Properties",
                                "<init>",
                                "()V",
                                false
                        ));

                        if (hasVarargs) {
                            insns.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        }

                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKEVIRTUAL,
                                "dev/doctor4t/ratatouille/util/registrar/BlockRegistrar",
                                "createWithItem",
                                hasVarargs ? "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/item/Item$Properties;[Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/block/Block;" : "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/level/block/Block;",
                                false
                        ));

                        insns.add(new InsnNode(Opcodes.ARETURN));

                        method.instructions = insns;
                        method.tryCatchBlocks.clear();
                        method.localVariables.clear();
                        method.maxLocals = hasVarargs ? 4 : 3;
                    }
                    case "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/level/block/Block;",
                         "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/item/Item$Properties;[Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/block/Block;" -> {
                        InsnList insns = new InsnList();

                        insns.add(new VarInsnNode(Opcodes.ALOAD, 3));

                        insns.add(new FieldInsnNode(
                                Opcodes.GETSTATIC,
                                "net/minecraft/core/registries/Registries",
                                "ITEM",
                                "Lnet/minecraft/resources/ResourceKey;"
                        ));

                        insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insns.add(new FieldInsnNode(
                                Opcodes.GETFIELD,
                                "dev/doctor4t/ratatouille/util/registrar/Registrar",
                                "namespace",
                                "Ljava/lang/String;"
                        ));

                        insns.add(new VarInsnNode(Opcodes.ALOAD, 1));

                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "net/minecraft/resources/Identifier",
                                "fromNamespaceAndPath",
                                "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/Identifier;",
                                false
                        ));

                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "net/minecraft/resources/ResourceKey",
                                "create",
                                "(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/ResourceKey;",
                                false
                        ));

                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKEVIRTUAL,
                                "net/minecraft/world/item/Item$Properties",
                                "setId",
                                "(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item$Properties;",
                                false
                        ));

                        insns.add(new InsnNode(Opcodes.POP));

                        method.instructions.insert(insns);
                    }
                }
            });
        });
        register(List.of(
                "dev/doctor4t/ratatouille/util/registrar/EntityTypeRegistrar"
        ), (node, info) -> {
            applyToMethod(node, "create", method -> {
                List<MethodInsnNode> buildCalls = new ArrayList<>();
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof MethodInsnNode methodInsnNode && methodInsnNode.owner.equals("net/minecraft/world/entity/EntityType$Builder") && methodInsnNode.name.equals("build")) {
                        methodInsnNode.desc = "(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/entity/EntityType;";
                        buildCalls.add(methodInsnNode);
                    }
                }

                for (MethodInsnNode build : buildCalls) {
                    InsnList insns = new InsnList();

                    insns.add(new FieldInsnNode(
                            Opcodes.GETSTATIC,
                            "net/minecraft/core/registries/Registries",
                            "ENTITY_TYPE",
                            "Lnet/minecraft/resources/ResourceKey;"
                    ));

                    insns.add(new VarInsnNode(Opcodes.ALOAD, 0));

                    insns.add(new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "dev/doctor4t/ratatouille/util/registrar/Registrar",
                            "namespace",
                            "Ljava/lang/String;"
                    ));
                    insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    insns.add(new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/minecraft/resources/Identifier",
                            "fromNamespaceAndPath",
                            "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/Identifier;",
                            false
                    ));
                    insns.add(new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/minecraft/resources/ResourceKey",
                            "create",
                            "(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/ResourceKey;",
                            false
                    ));

                    method.instructions.insertBefore(build, insns);
                    info.computeMaxStackSizes();
                }
            });
        });

        register(List.of(
                "dev/doctor4t/ratatouille/index/RatatouilleItems"
        ), (node, info) -> {
            applyToMethod(node, "<clinit>", method -> {
                TATConstants.PLATFORM.info("Injecting item ids into RatatouilleItems");
                applyItemIds(method, Map.of());
                info.computeMaxStackSizes();
            });
        });
        register(List.of(
                "dev/doctor4t/ratatouille/index/RatatouilleBlocks"
        ), (node, info) -> {
            applyToMethod(node, "<clinit>", method -> {
                TATConstants.PLATFORM.info("Injecting block ids into RatatouilleBlocks");
                applyBlockIds(method, Map.of());
                info.computeMaxStackSizes();
            });
        });
        register(List.of(
                "dev/doctor4t/wathe/index/WatheItems"
        ), (node, info) -> {
            applyToMethod(node, "<clinit>", method -> {
                TATConstants.PLATFORM.info("Injecting item ids into WatheItems");
                applyItemIds(method, Map.of(
                        2, "knife"
                ));
                info.computeMaxStackSizes();
            });
        });
        register(List.of(
                "dev/doctor4t/wathe/index/WatheBlocks"
        ), (node, info) -> {
            applyToMethod(node, "<clinit>", method -> {
                TATConstants.PLATFORM.info("Injecting block ids into WatheBlocks");
                applyBlockIds(method, Map.of());
                info.computeMaxStackSizes();
            });

            applyToMethod(node, "createBranch", method -> {
                method.instructions.forEach(n -> {
                    if (n instanceof MethodInsnNode insn) {
                        if (insn.name.equals("of") || insn.name.equals("ofFullCopy") || insn.name.equals("ofLegacyCopy")) {
                            switch (insn.owner) {
                                case "net/minecraft/world/level/block/state/BlockBehaviour$Properties" -> {
                                    InsnList insns = new InsnList();
                                    insns.add(new FieldInsnNode(
                                            Opcodes.GETSTATIC,
                                            "net/minecraft/core/registries/Registries",
                                            "BLOCK",
                                            "Lnet/minecraft/resources/ResourceKey;"
                                    ));
                                    insns.add(new LdcInsnNode(TATConstants.WATHE));
                                    insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    insns.add(new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            "net/minecraft/resources/Identifier",
                                            "fromNamespaceAndPath",
                                            "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/Identifier;"
                                    ));
                                    insns.add(new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            "net/minecraft/resources/ResourceKey",
                                            "create",
                                            "(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/ResourceKey;"
                                    ));
                                    insns.add(new MethodInsnNode(
                                            Opcodes.INVOKEVIRTUAL,
                                            "net/minecraft/world/level/block/state/BlockBehaviour$Properties",
                                            "setId",
                                            "(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;"
                                    ));
                                    method.instructions.insert(n, insns);
                                }
                            }
                        }
                    }
                });
                info.computeMaxStackSizes();
            });
        });
        register(List.of(
                "dev/doctor4t/wathe/util/ShopEntry",
                "dev/doctor4t/wathe/game/GameConstants$1",
                "dev/doctor4t/wathe/game/GameConstants$2"
        ), (node, info) -> {
            applyToField(node, "stack", field -> {
                field.desc = field.desc.replace("Lnet/minecraft/world/item/ItemStack", "Lnet/minecraft/world/item/ItemStackTemplate");
            });

            for (MethodNode method : node.methods) {
                if ((method.access & Opcodes.ACC_STATIC) == 0) {
                    method.desc = method.desc.replace("Lnet/minecraft/world/item/ItemStack", "Lnet/minecraft/world/item/ItemStackTemplate");
                }

                for (var instruction : method.instructions) {
                    if (instruction instanceof FieldInsnNode field && field.owner.equals(node.name) && field.name.equals("stack")) {
                        field.desc = field.desc.replace("Lnet/minecraft/world/item/ItemStack", "Lnet/minecraft/world/item/ItemStackTemplate");
                    } else if (instruction instanceof MethodInsnNode m && m.name.equals("<init>") && method.name.equals("<init>")) {
                        m.desc = m.desc.replace("Lnet/minecraft/world/item/ItemStack", "Lnet/minecraft/world/item/ItemStackTemplate");
                    }
                }

                if (method.name.equals("onBuy")) {
                    for (var instruction : method.instructions) {
                        if (instruction instanceof MethodInsnNode methodInsn && methodInsn.owner.equals("net/minecraft/world/item/ItemStack") && methodInsn.name.equals("copy")) {
                            methodInsn.owner = "net/minecraft/world/item/ItemStackTemplate";
                            methodInsn.name = "create";
                        }
                    }
                }
            }
        });
        register(List.of(
                "dev/doctor4t/wathe/game/GameConstants"
        ), (node, info) -> {
            for (MethodNode methodNode : node.methods) {
                if (methodNode.name.contains("lambda$static") && methodNode.desc.contains("Ljava/util/ArrayList")) {
                    List<MethodInsnNode> getDefaultInstanceNodes = new ArrayList<>();
                    for (var instruction : methodNode.instructions) {
                        if (instruction instanceof TypeInsnNode typeInsnNode) {
                            typeInsnNode.desc = typeInsnNode.desc.replace("net/minecraft/world/item/ItemStack", "net/minecraft/world/item/ItemStackTemplate");
                        } else if (instruction instanceof MethodInsnNode methodInsn) {
                            if (methodInsn.owner.equals("net/minecraft/world/item/Item") && methodInsn.name.equals("getDefaultInstance")) {
                                getDefaultInstanceNodes.add(methodInsn);
                            } else if (methodInsn.owner.equals("net/minecraft/world/item/ItemStack") && methodInsn.name.equals("<init>")) {
                                methodInsn.owner = "net/minecraft/world/item/ItemStackTemplate";
                                methodInsn.desc = methodInsn.desc.replace("Lnet/minecraft/world/level/ItemLike", "Lnet/minecraft/world/item/Item");
                            } else if ((methodInsn.owner.equals("dev/doctor4t/wathe/util/ShopEntry") || methodInsn.owner.startsWith("dev/doctor4t/wathe/game/GameConstants$")) && methodInsn.name.equals("<init>")) {
                                methodInsn.desc = methodInsn.desc.replace("net/minecraft/world/item/ItemStack", "net/minecraft/world/item/ItemStackTemplate");
                            }
                        }
                    }
                    for (var methodInsn : getDefaultInstanceNodes) {
                        MethodInsnNode ctor = new MethodInsnNode(
                                Opcodes.INVOKESPECIAL,
                                "net/minecraft/world/item/ItemStackTemplate",
                                "<init>",
                                "(Lnet/minecraft/world/item/Item;)V"
                        );

                        AbstractInsnNode getStaticItem = methodInsn.getPrevious();

                        methodNode.instructions.insertBefore(getStaticItem, new TypeInsnNode(Opcodes.NEW, "net/minecraft/world/item/ItemStackTemplate"));
                        methodNode.instructions.insertBefore(getStaticItem, new InsnNode(Opcodes.DUP));
                        methodNode.instructions.set(methodInsn, ctor);
                    }
                }
            }
        });
        register(List.of(
                "dev/doctor4t/wathe/block/entity/SeatEntity",
                "dev/doctor4t/wathe/entity/FirecrackerEnttiy",
                "dev/doctor4t/wathe/entity/GrenadeEntity",
                "dev/doctor4t/wathe/entity/NoteEntity",
                "dev/doctor4t/wathe/entity/PlayerBodyEntity"
        ), (node, info) -> {
            tweakNBTSaveMethod(node, "addAdditionalSaveData", !node.name.contains("NoteEntity"));
            tweakNBTLoadMethod(node, "readAdditionalSaveData", true);

            if (node.name.contains("PlayerBodyEntity")) {
                for (MethodNode methodNode : node.methods) {
                    if (methodNode.name.equals("<clinit>")) {
                        for (var insn : methodNode.instructions) {
                            if (insn instanceof FieldInsnNode fieldInsnNode && fieldInsnNode.owner.equals("net/minecraft/network/syncher/EntityDataSerializers") && fieldInsnNode.name.equals("OPTIONAL_UUID")) {
                                fieldInsnNode.owner = "survivalblock/train_across_time/TrainAcrossTime";
                            }
                        }
                    }
                }
            }
        });
        register(List.of(
                "dev/doctor4t/wathe/block/BarrierPanelBlock"
        ), (node, info) -> {
            for (FieldNode field : node.fields) {
                if (field.name.equals("SHAPES")) {
                    if (field.signature != null) {
                        field.signature = field.signature.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                    }

                    field.desc = field.desc.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                }
            }

            applyToMethod(node, "<init>", method -> {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof MethodInsnNode m) {
                        if (m.name.equals("getShapeForEachState")) {
                            m.desc = m.desc.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                        }
                    } else if (insn instanceof FieldInsnNode field) {
                        if (field.name.equals("SHAPES")) {
                            field.desc = field.desc.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                        }
                    }
                }
            });

            applyToMethod(node, "getShape", method -> {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof MethodInsnNode m) {
                        if (m.name.equals("get")) {
                            method.instructions.set(m, new MethodInsnNode(
                                    Opcodes.INVOKEINTERFACE,
                                    "java/util/function/Function",
                                    "apply",
                                    "(Ljava/lang/Object;)Ljava/lang/Object;"
                            ));
                        }
                    } else if (insn instanceof FieldInsnNode field) {
                        if (field.name.equals("SHAPES")) {
                            field.desc = field.desc.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                        }
                    }
                }
            });
        });
        register(List.of(
                "dev/doctor4t/wathe/client/render/entity/FirecrackerEntityRenderer"
        ), (node, info) -> {
            if (node.signature != null) {
                node.signature = node.signature.substring(0, node.signature.indexOf("<")) + "Ldev/doctor4t/wathe/entity/FirecrackerEntity;Lsurvivalblock/train_across_time/provided/client/FirecrackerEntityRenderState;>";
            }
        });
        register(List.of(
                "dev/doctor4t/wathe/client/render/entity/NoteEntityRenderer"
        ), (node, info) -> {
            if (node.signature != null) {
                node.signature = node.signature.substring(0, node.signature.indexOf("<")) + "Ldev/doctor4t/wathe/entity/NoteEntity;Lsurvivalblock/train_across_time/provided/client/NoteEntityRenderState;>";
            }
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/PlayerEntityMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("wathe$poisonedFoodEffect") || method.name.equals("wathe$eat"));
            changeInjectionAt(
                    node,
                    "wathe$cancelWakingUpPlayers",
                    MODIFY_EXPRESSION_VALUE,
                    "value", "INVOKE",
                    "target", "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
            );

            tweakNBTSaveMethod(node, "wathe$saveData", false);
            tweakNBTLoadMethod(node, "wathe$readData", false);
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/MinecraftClientMixin"
        ), (node, info) -> {
            changeInjectionAt(
                    node,
                    "wathe$invalid",
                    WRAP_OPERATION,
                    "value", "INVOKE",
                    "target", "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"
            );
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/items/ClientPlayerEntityMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("wathe$disableItemSlowdown"));
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/AbstractClientPlayerEntityMixin"
        ), (node, info) -> {
            changeInjectionMethod(
                    node,
                    "wathe$fovPulse",
                    INJECT,
                    "getFieldOfViewModifier(ZF)F"
            );
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/ItemEntityMixin"
        ), (node, info) -> {
            // unused @Shadow that changed so just remove it
            node.fields.removeIf(field -> field.name.equals("thrower"));
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/ServerPlayerEntityMixin"
        ), (node, info) -> {
            changeInjectionAt(
                    node,
                    "wathe$disableSleepMessage",
                    WRAP_OPERATION,
                    "value", "INVOKE",
                    "target", "Lnet/minecraft/server/level/ServerPlayer;sendOverlayMessage(Lnet/minecraft/network/chat/Component;)V"
            );

            applyToMethod(node, "wathe$disableSleepMessage", method -> {
                method.desc = "(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/Component;Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation;)V";
                method.signature = "(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/Component;Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation<Ljava/lang/Void;>;)V";
                method.parameters.remove(2);
                method.localVariables.remove(3);
                method.maxLocals--;
            });

            node.methods.removeIf(method -> method.name.equals("wathe$disableSetSpawnpoint"));

            changeInjectionAt(
                    node,
                    "wathe$allowSleepingAtAnyTime",
                    MODIFY_EXPRESSION_VALUE,
                    "value", "INVOKE",
                    "target", "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
            );
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/ItemMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("arsenal$setTridentOwner"));
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/PlayerInventoryMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("wathe$invalid"));
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/restrictions/KeyBindingMixin"
        ), (node, info) -> {
            changeInjectionMethod(
                    node,
                    "wathe$restrainMatchesKey",
                    MODIFY_RETURN_VALUE,
                    "matches(Lnet/minecraft/client/input/KeyEvent;)Z"
            );
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/scenery/ClientWorldMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("wathe$addCustomBlockMarkers"));
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/self/NoteItemMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("useClient"));
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/self/TrimmedBedBlockEntityMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("tickOnClientSide"));
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/self/BeveragePlateBlockEntityMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("tickWithoutFearOfCrashing"));
        });
        register(List.of(
                "dev/doctor4t/wathe/mixin/client/restrictions/EntityRendererMixin"
        ), (node, info) -> {
            node.methods.removeIf(method -> method.name.equals("renderLabelIfPresent"));
        });
        register(List.of(
                "dev/doctor4t/ratatouille/client/RatatouilleClient"
        ), (node, info) -> {
            spliceMethod(
                    node,
                    "onInitializeClient",
                    InsnPointer.fieldGetStatic()
                            .owner("net/fabricmc/fabric/api/blockrenderlayer/v1/BlockRenderLayerMap")
                            .name("INSTANCE"),
                    InsnPointer.methodCallInterface()
                            .owner("net/fabricmc/fabric/api/blockrenderlayer/v1/BlockRenderLayerMap")
                            .name("putBlocks")
                    // TODO I'm pretty sure we need to put code here to register the block render layer, but I can't find what to call? It might be auto detected in modern, not sure. - Typho
            );
        });
        register(List.of(
                "dev/doctor4t/wathe/client/render/block_entity/AnimatableBlockEntityRenderer"
        ), (node, info) -> {
            info.computeMaxStackSizes();
            info.computeFrames();

            applyToMethod(node, "<init>", method -> {
                for (LocalVariableNode var : method.localVariables) {
                    if (var.index >= 1) {
                        var.index++;
                    }
                }

                method.desc = method.desc.replace("(", "(Lnet/minecraft/client/model/geom/ModelPart;");

                if (method.signature != null) {
                    method.signature = method.signature.replace("(", "(Lnet/minecraft/client/model/geom/ModelPart;");
                }

                var thisVar = method.localVariables.getFirst();
                method.localVariables.add(1, new LocalVariableNode(
                        "root",
                        "Lnet/minecraft/client/model/geom/ModelPart;",
                        null,
                        thisVar.start,
                        thisVar.end,
                        1
                ));

                if (method.parameters != null) {
                    method.parameters.addFirst(new ParameterNode(
                            "root",
                            0
                    ));
                }

                InsnPointer.methodCallDirect()
                        .name("<init>")
                        .findOrThrow(method.instructions, insn -> {
                            insn.desc = insn.desc.replace("(", "(Lnet/minecraft/client/model/geom/ModelPart;");
                            method.instructions.insertBefore(insn, new VarInsnNode(Opcodes.ALOAD, method.maxLocals));
                        });
            });
        });
        register(List.of(
                "dev/doctor4t/wathe/client/render/block_entity/SmallDoorBlockEntityRenderer",
                "dev/doctor4t/wathe/client/render/block_entity/WheelBlockEntityRenderer"
        ), (node, info) -> {
            info.computeFrames();

            node.fields.removeIf(m -> m.name.equals("part"));
            node.methods.removeIf(m -> m.name.equals("root"));

            spliceMethod(
                    node,
                    "<init>",
                    InsnPointer.localOperation()
                            .id(0),
                    InsnPointer.methodCallDirect()
                            .owner("dev/doctor4t/wathe/client/render/block_entity/AnimatableBlockEntityRenderer")
                            .name("<init>"),
                    (method, insns) -> {
                        insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
                        insns.add(InsnPointer.fieldGetStatic()
                                .desc("Lnet/minecraft/client/model/geom/ModelLayerLocation;")
                                .findOrThrow(method.instructions)
                                .clone(Map.of()));
                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKEVIRTUAL,
                                "net/minecraft/client/renderer/blockentity/BlockEntityRendererProvider$Context",
                                "bakeLayer",
                                "(Lnet/minecraft/client/model/geom/ModelLayerLocation;)Lnet/minecraft/client/model/geom/ModelPart;"
                        ));
                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKESPECIAL,
                                "dev/doctor4t/wathe/client/render/block_entity/AnimatableBlockEntityRenderer",
                                "<init>",
                                "(Lnet/minecraft/client/model/geom/ModelPart;)V"
                        ));
                    }
            );
            spliceMethod(
                    node,
                    "<init>",
                    InsnPointer.localOperation()
                            .id(0)
                            .ordinal(3),
                    InsnPointer.fieldSet()
                            .owner(node.name)
                            .name("part")
            );
            transmuteInsn(
                    node,
                    "setAngles",
                    InsnPointer.fieldGet()
                            .owner(node.name)
                            .name("part"),
                    field -> {
                        field.owner = "net/minecraft/client/model/Model";
                    }
            );
        });
        /*
        register(List.of(
                "dev/doctor4t/wathe/client/render/entity/Player"
        ), (node, info) -> {
            if (node.signature != null) {
                node.signature = node.signature.substring(0, node.signature.indexOf("<")) + "Ldev/doctor4t/wathe/entity/NoteEntity;Lsurvivalblock/train_across_time/provided/client/NoteEntityRenderState;>";
            }
        });
        */
    }
}
