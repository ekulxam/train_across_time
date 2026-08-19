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

import net.typho.asm_util.ASMUtil;
import net.typho.asm_util.ClassTransformInfo;
import net.typho.asm_util.Modifier;
import net.typho.asm_util.field.FieldPointer;
import net.typho.asm_util.insn.InsnPointer;
import net.typho.asm_util.method.MethodPointer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Typho
 */
@SuppressWarnings({"SwitchStatementWithTooFewBranches"})
public class WatheClassPatches {
    private WatheClassPatches() {
    }

    public static final String AT = "Lorg/spongepowered/asm/mixin/injection/At;";
    public static final String MODIFY_EXPRESSION_VALUE = "Lcom/llamalad7/mixinextras/injector/ModifyExpressionValue;";
    public static final String MODIFY_RETURN_VALUE = "Lcom/llamalad7/mixinextras/injector/ModifyReturnValue;";
    public static final String WRAP_OPERATION = "Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;";
    public static final String INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;";

    public static final Map<String, Consumer<ClassTransformInfo>> PATCHES = new HashMap<>();

    public static void register(Set<String> classes, Consumer<ClassTransformInfo> patch) {
        for (String cls : classes) {
            PATCHES.merge(cls, patch, (a, b) -> info -> {
                a.accept(info);
                b.accept(info);
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

    public static <T extends AbstractInsnNode> void transmuteInsn(
            ClassNode node,
            String name,
            InsnPointer<T, ?> at,
            Consumer<T> transmuter
    ) {
        MethodPointer.method()
                .name(name)
                .modifier(Modifier.BRIDGE, false)
                .findOrThrow(node, method -> {
                    at.forEach(method.instructions, transmuter);
                });
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

        MethodPointer.method()
                .name(methodName)
                .findOrThrow(node, method -> {
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
        MethodPointer.method()
                .name(methodName)
                .findOrThrow(node, method -> {
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
        MethodPointer.method()
                .name(methodName)
                .findOrThrow(node, method -> {
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

    static {
        register(Set.of(
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
        ), info -> {
            info.getNode().interfaces.add("org/ladysnake/cca/api/v3/component/Component");
        });

        register(Set.of(
                "dev/doctor4t/wathe/index/WatheItems"
        ), info -> {
            MethodPointer.method()
                    .name("<clinit>")
                    .findOrThrow(info.getNode(), method -> {
                        method.instructions.remove(
                                Arrays.stream(method.instructions.toArray())
                                        .filter(insn -> insn instanceof MethodInsnNode m && m.name.equals("createAttributes"))
                                        .findFirst()
                                        .orElseThrow()
                        );
                    });
        });

        register(Set.of(
                "dev/doctor4t/ratatouille/util/registrar/BlockRegistrar"
        ), info -> {
            MethodPointer.method()
                    .name("createWithItem")
                    .forEach(info.getNode(), method -> {
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
        register(Set.of(
                "dev/doctor4t/ratatouille/util/registrar/EntityTypeRegistrar"
        ), info -> {
            MethodPointer.method()
                    .name("create")
                    .findOrThrow(info.getNode(), method -> {
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
                            info.computeMaxStacks();
                        }
                    });
        });

        register(Set.of(
                "dev/doctor4t/ratatouille/index/RatatouilleItems"
        ), info -> {
            info.computeMaxStacks();

            MethodPointer.method()
                    .name("<clinit>")
                    .findOrThrow(info.getNode(), method -> {
                        TATConstants.PLATFORM.info("Injecting item ids into RatatouilleItems");
                        applyItemIds(method, Map.of());
                    });
        });
        register(Set.of(
                "dev/doctor4t/ratatouille/index/RatatouilleBlocks"
        ), info -> {
            info.computeMaxStacks();

            MethodPointer.method()
                    .name("<clinit>")
                    .findOrThrow(info.getNode(), method -> {
                        TATConstants.PLATFORM.info("Injecting block ids into RatatouilleBlocks");
                        applyBlockIds(method, Map.of());
                    });
        });
        register(Set.of(
                "dev/doctor4t/wathe/index/WatheItems"
        ), info -> {
            info.computeMaxStacks();

            MethodPointer.method()
                    .name("<clinit>")
                    .findOrThrow(info.getNode(), method -> {
                        TATConstants.PLATFORM.info("Injecting item ids into WatheItems");
                        applyItemIds(method, Map.of(
                                2, "knife"
                        ));
                    });
        });
        register(Set.of(
                "dev/doctor4t/wathe/index/WatheBlocks"
        ), info -> {
            info.computeMaxStacks();

            info.getNode().fields.add(
                    info.getNode().fields.indexOf(FieldPointer.field()
                            .name("BAMBOO_POLE")
                            .findOrThrow(info.getNode())),
                    new FieldNode(
                            Opcodes.ASM9,
                            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                            "PALE_OAK_BRANCH",
                            "Lnet/minecraft/world/level/block/Block;",
                            null,
                            null
                    )
            );
            info.getNode().fields.add(
                    info.getNode().fields.indexOf(FieldPointer.field()
                            .name("STRIPPED_BAMBOO_POLE")
                            .findOrThrow(info.getNode())),
                    new FieldNode(
                            Opcodes.ASM9,
                            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                            "STRIPPED_PALE_OAK_BRANCH",
                            "Lnet/minecraft/world/level/block/Block;",
                            null,
                            null
                    )
            );

            MethodPointer.method()
                    .name("<clinit>")
                    .findOrThrow(info.getNode(), method -> {
                        TATConstants.PLATFORM.info("Injecting block ids into WatheBlocks");
                        applyBlockIds(method, Map.of());

                        InsnPointer.constant("bamboo_pole")
                                .findOrThrow(method.instructions, insn -> {
                                    var insns = new InsnList();

                                    insns.add(new LdcInsnNode("pale_oak_branch"));
                                    insns.add(new FieldInsnNode(
                                            Opcodes.GETSTATIC,
                                            "net/minecraft/world/level/block/Blocks",
                                            "PALE_OAK_WOOD",
                                            "Lnet/minecraft/world/level/block/Block;"
                                    ));
                                    insns.add(new FieldInsnNode(
                                            Opcodes.GETSTATIC,
                                            info.getNode().name,
                                            "registrar",
                                            "Ldev/doctor4t/ratatouille/util/registrar/BlockRegistrar;"
                                    ));
                                    insns.add(new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            info.getNode().name,
                                            "createBranch",
                                            "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;Ldev/doctor4t/ratatouille/util/registrar/BlockRegistrar;)Lnet/minecraft/world/level/block/Block;",
                                            true
                                    ));
                                    insns.add(new FieldInsnNode(
                                            Opcodes.PUTSTATIC,
                                            info.getNode().name,
                                            "PALE_OAK_BRANCH",
                                            "Lnet/minecraft/world/level/block/Block;"
                                    ));

                                    method.instructions.insertBefore(insn, insns);
                                });

                        InsnPointer.constant("stripped_bamboo_pole")
                                .findOrThrow(method.instructions, insn -> {
                                    var insns = new InsnList();

                                    insns.add(new LdcInsnNode("stripped_pale_oak_branch"));
                                    insns.add(new FieldInsnNode(
                                            Opcodes.GETSTATIC,
                                            "net/minecraft/world/level/block/Blocks",
                                            "STRIPPED_PALE_OAK_WOOD",
                                            "Lnet/minecraft/world/level/block/Block;"
                                    ));
                                    insns.add(new FieldInsnNode(
                                            Opcodes.GETSTATIC,
                                            info.getNode().name,
                                            "registrar",
                                            "Ldev/doctor4t/ratatouille/util/registrar/BlockRegistrar;"
                                    ));
                                    insns.add(new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            info.getNode().name,
                                            "createBranch",
                                            "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;Ldev/doctor4t/ratatouille/util/registrar/BlockRegistrar;)Lnet/minecraft/world/level/block/Block;",
                                            true
                                    ));
                                    insns.add(new FieldInsnNode(
                                            Opcodes.PUTSTATIC,
                                            info.getNode().name,
                                            "STRIPPED_PALE_OAK_BRANCH",
                                            "Lnet/minecraft/world/level/block/Block;"
                                    ));

                                    method.instructions.insertBefore(insn, insns);
                                });
                    });

            MethodPointer.method()
                    .name("createBranch")
                    .findOrThrow(info.getNode(), method -> {
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
                        info.computeMaxStacks();
                    });
        });
        register(Set.of(
                "dev/doctor4t/wathe/util/ShopEntry",
                "dev/doctor4t/wathe/game/GameConstants$1",
                "dev/doctor4t/wathe/game/GameConstants$2"
        ), info -> {
            if (info.getNode().name.equals("dev/doctor4t/wathe/util/ShopEntry")) {
                FieldPointer.field()
                        .name("stack")
                        .findOrThrow(info.getNode(), field -> {
                            field.desc = field.desc.replace("Lnet/minecraft/world/item/ItemStack", "Lnet/minecraft/world/item/ItemStackTemplate");
                        });
            }

            for (MethodNode method : info.getNode().methods) {
                if ((method.access & Opcodes.ACC_STATIC) == 0) {
                    method.desc = method.desc.replace("Lnet/minecraft/world/item/ItemStack", "Lnet/minecraft/world/item/ItemStackTemplate");
                }

                for (var instruction : method.instructions) {
                    if (instruction instanceof FieldInsnNode field && field.owner.equals(info.getNode().name) && field.name.equals("stack")) {
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
        register(Set.of(
                "dev/doctor4t/wathe/game/GameConstants"
        ), info -> {
            for (MethodNode methodNode : info.getNode().methods) {
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
        register(Set.of(
                "dev/doctor4t/wathe/block/entity/SeatEntity",
                "dev/doctor4t/wathe/entity/FirecrackerEntity",
                "dev/doctor4t/wathe/entity/NoteEntity",
                "dev/doctor4t/wathe/entity/PlayerBodyEntity"
        ), info -> {
            tweakNBTSaveMethod(info.getNode(), "addAdditionalSaveData", !info.getNode().name.contains("NoteEntity"));
            tweakNBTLoadMethod(info.getNode(), "readAdditionalSaveData", true);

            if (info.getNode().name.contains("PlayerBodyEntity")) {
                for (MethodNode methodNode : info.getNode().methods) {
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
        register(Set.of(
                "dev/doctor4t/wathe/block/BarrierPanelBlock"
        ), info -> {
            for (FieldNode field : info.getNode().fields) {
                if (field.name.equals("SHAPES")) {
                    if (field.signature != null) {
                        field.signature = field.signature.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                    }

                    field.desc = field.desc.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                }
            }

            MethodPointer.method()
                    .name("<init>")
                    .findOrThrow(info.getNode(), method1 -> {
                        for (AbstractInsnNode insn1 : method1.instructions) {
                            if (insn1 instanceof MethodInsnNode m1) {
                                if (m1.name.equals("getShapeForEachState")) {
                                    m1.desc = m1.desc.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                                }
                            } else if (insn1 instanceof FieldInsnNode field1) {
                                if (field1.name.equals("SHAPES")) {
                                    field1.desc = field1.desc.replace("com/google/common/collect/ImmutableMap", "java/util/function/Function");
                                }
                            }
                        }
                    });

            MethodPointer.method()
                    .name("getShape")
                    .findOrThrow(info.getNode(), method -> {
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
        register(Set.of(
                "dev/doctor4t/wathe/client/render/entity/FirecrackerEntityRenderer"
        ), info -> {
            if (info.getNode().signature != null) {
                info.getNode().signature = info.getNode().signature.substring(0, info.getNode().signature.indexOf("<")) + "Ldev/doctor4t/wathe/entity/FirecrackerEntity;Lsurvivalblock/train_across_time/provided/client/FirecrackerEntityRenderState;>";
            }
        });
        register(Set.of(
                "dev/doctor4t/wathe/client/render/entity/NoteEntityRenderer"
        ), info -> {
            if (info.getNode().signature != null) {
                info.getNode().signature = info.getNode().signature.substring(0, info.getNode().signature.indexOf("<")) + "Ldev/doctor4t/wathe/entity/NoteEntity;Lsurvivalblock/train_across_time/provided/client/NoteEntityRenderState;>";
            }
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/PlayerEntityMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("wathe$poisonedFoodEffect") || method.name.equals("wathe$eat"));
            changeInjectionAt(
                    info.getNode(),
                    "wathe$cancelWakingUpPlayers",
                    MODIFY_EXPRESSION_VALUE,
                    "value", "INVOKE",
                    "target", "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
            );
            changeInjectionMethod(
                    info.getNode(),
                    "wathe$saveData",
                    INJECT,
                    "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V"
            );
            changeInjectionMethod(
                    info.getNode(),
                    "wathe$readData",
                    INJECT,
                    "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V"
            );

            tweakNBTSaveMethod(info.getNode(), "wathe$saveData", false);
            tweakNBTLoadMethod(info.getNode(), "wathe$readData", false);
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/MinecraftClientMixin"
        ), info -> {
            changeInjectionAt(
                    info.getNode(),
                    "wathe$invalid",
                    WRAP_OPERATION,
                    "value", "INVOKE",
                    "target", "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"
            );
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/items/ClientPlayerEntityMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("wathe$disableItemSlowdown"));
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/AbstractClientPlayerEntityMixin"
        ), info -> {
            changeInjectionMethod(
                    info.getNode(),
                    "wathe$fovPulse",
                    INJECT,
                    "getFieldOfViewModifier(ZF)F"
            );
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/ItemEntityMixin"
        ), info -> {
            // unused @Shadow that changed so just remove it
            info.getNode().fields.removeIf(field -> field.name.equals("thrower"));
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/ServerPlayerEntityMixin"
        ), info -> {
            changeInjectionAt(
                    info.getNode(),
                    "wathe$disableSleepMessage",
                    WRAP_OPERATION,
                    "value", "INVOKE",
                    "target", "Lnet/minecraft/server/level/ServerPlayer;sendOverlayMessage(Lnet/minecraft/network/chat/Component;)V"
            );

            MethodPointer.method()
                    .name("wathe$disableSleepMessage")
                    .findOrThrow(info.getNode(), method1 -> {
                        method1.desc = "(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/Component;Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation;)V";
                        method1.signature = "(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/Component;Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation<Ljava/lang/Void;>;)V";
                        method1.parameters.remove(2);
                        method1.localVariables.remove(3);
                        method1.maxLocals--;
                    });

            info.getNode().methods.removeIf(method -> method.name.equals("wathe$disableSetSpawnpoint"));

            changeInjectionAt(
                    info.getNode(),
                    "wathe$allowSleepingAtAnyTime",
                    MODIFY_EXPRESSION_VALUE,
                    "value", "INVOKE",
                    "target", "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
            );
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/ItemMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("arsenal$setTridentOwner"));
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/PlayerInventoryMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("wathe$invalid"));
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/restrictions/KeyBindingMixin"
        ), info -> {
            changeInjectionMethod(
                    info.getNode(),
                    "wathe$restrainMatchesKey",
                    MODIFY_RETURN_VALUE,
                    "matches(Lnet/minecraft/client/input/KeyEvent;)Z"
            );
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/scenery/ClientWorldMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("wathe$addCustomBlockMarkers"));
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/self/NoteItemMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("useClient"));
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/self/TrimmedBedBlockEntityMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("tickOnClientSide"));
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/self/BeveragePlateBlockEntityMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("tickWithoutFearOfCrashing"));
        });
        register(Set.of(
                "dev/doctor4t/wathe/mixin/client/restrictions/EntityRendererMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("renderLabelIfPresent"));
        });
        register(Set.of(
                "dev/doctor4t/ratatouille/client/RatatouilleClient",
                "dev/doctor4t/wathe/client/WatheClient"
        ), info -> {
            ASMUtil.splice(
                    MethodPointer.method()
                            .name("onInitializeClient")
                            .findOrThrow(info.getNode())
                            .instructions,
                    InsnPointer.fieldGetStatic()
                            .owner("net/fabricmc/fabric/api/blockrenderlayer/v1/BlockRenderLayerMap")
                            .name("INSTANCE")
                            .ordinal(0),
                    InsnPointer.methodCallInterface()
                            .lastOrdinal()
                            .owner("net/fabricmc/fabric/api/blockrenderlayer/v1/BlockRenderLayerMap")
                            .name("putBlocks")
            );
        });
        register(Set.of(
                "dev/doctor4t/wathe/client/render/block_entity/AnimatableBlockEntityRenderer"
        ), info -> {
            info.computeMaxStacks();
            info.computeFrames();

            MethodPointer.method()
                    .name("<init>")
                    .forEach(info.getNode(), method -> {
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
        register(Set.of(
                "dev/doctor4t/wathe/client/render/block_entity/SmallDoorBlockEntityRenderer",
                "dev/doctor4t/wathe/client/render/block_entity/WheelBlockEntityRenderer"
        ), info -> {
            info.computeFrames();

            info.getNode().fields.removeIf(m -> m.name.equals("part"));
            info.getNode().methods.removeIf(m -> m.name.equals("root"));
            var init = MethodPointer.method()
                    .name("<init>")
                    .findOrThrow(info.getNode());

            var replacement = new InsnList();
            replacement.add(new VarInsnNode(Opcodes.ALOAD, 0));
            replacement.add(new VarInsnNode(Opcodes.ALOAD, 0));
            replacement.add(new VarInsnNode(Opcodes.ALOAD, 2));
            replacement.add(InsnPointer.fieldGetStatic()
                    .desc("Lnet/minecraft/client/model/geom/ModelLayerLocation;")
                    .findOrThrow(init.instructions)
                    .clone(Map.of()));
            replacement.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "net/minecraft/client/renderer/blockentity/BlockEntityRendererProvider$Context",
                    "bakeLayer",
                    "(Lnet/minecraft/client/model/geom/ModelLayerLocation;)Lnet/minecraft/client/model/geom/ModelPart;"
            ));
            replacement.add(new MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "dev/doctor4t/wathe/client/render/block_entity/AnimatableBlockEntityRenderer",
                    "<init>",
                    "(Lnet/minecraft/client/model/geom/ModelPart;)V"
            ));
            ASMUtil.splice(
                    init.instructions,
                    InsnPointer.localOperation()
                            .id(0)
                            .ordinal(0),
                    InsnPointer.methodCallDirect()
                            .owner("dev/doctor4t/wathe/client/render/block_entity/AnimatableBlockEntityRenderer")
                            .name("<init>"),
                    replacement
            );
            ASMUtil.splice(
                    init.instructions,
                    InsnPointer.localOperation()
                            .id(0)
                            .ordinal(3),
                    InsnPointer.fieldSet()
                            .owner(info.getNode().name)
                            .name("part")
            );
            transmuteInsn(
                    info.getNode(),
                    "setAngles",
                    InsnPointer.fieldGet()
                            .owner(info.getNode().name)
                            .name("part"),
                    field -> {
                        field.owner = "net/minecraft/client/model/Model";
                    }
            );
        });
        register(Set.of(
                "dev/doctor4t/wathe/client/model/item/KnifeModelLoadingPlugin"
        ), info -> {
            info.getNode().fields.removeIf(f -> f.name.equals("KNIFE_MODEL_ID"));
            ASMUtil.splice(
                    MethodPointer.method()
                            .name("<clinit>")
                            .findOrThrow(info.getNode())
                            .instructions,
                    InsnPointer.fieldGetStatic()
                            .owner("dev/doctor4t/wathe/item/KnifeItem")
                            .name("ITEM_ID"),
                    InsnPointer.fieldSetStatic()
                            .owner(info.getNode().name)
                            .name("KNIFE_MODEL_ID")
            );

            ASMUtil.splice(
                    MethodPointer.method()
                            .name("getModelLocation")
                            .findOrThrow(info.getNode())
                            .instructions,
                    InsnPointer.methodCallInherited()
                            .owner("net/minecraft/client/resources/model/ModelResourceLocation")
                            .name("comp_2875")
            );
            transmuteInsn(
                    info.getNode(),
                    "getModelLocation",
                    InsnPointer.fieldGetStatic()
                            .owner(info.getNode().name)
                            .name("KNIFE_MODEL_ID"),
                    field -> {
                        field.owner = "dev/doctor4t/wathe/item/KnifeItem";
                        field.name = "ITEM_ID";
                        field.desc = "Lnet/minecraft/resources/Identifier;";
                    }
            );

            transmuteInsn(
                    info.getNode(),
                    "lambda$onInitializeModelLoader$1",
                    InsnPointer.fieldGetStatic()
                            .owner(info.getNode().name)
                            .name("KNIFE_MODEL_ID"),
                    field -> {
                        field.owner = "dev/doctor4t/wathe/item/KnifeItem";
                        field.name = "ITEM_ID";
                        field.desc = "Lnet/minecraft/resources/Identifier;";
                    }
            );
            transmuteInsn(
                    info.getNode(),
                    "lambda$onInitializeModelLoader$1",
                    InsnPointer.methodCallInterface()
                            .owner("net/fabricmc/fabric/api/client/model/loading/v1/ModelModifier$OnLoad$Context")
                            .name("topLevelId"),
                    m -> {
                        m.desc = "()Lnet/minecraft/resources/Identifier;";
                    }
            );
            transmuteInsn(
                    info.getNode(),
                    "lambda$onInitializeModelLoader$1",
                    InsnPointer.methodCallInherited()
                            .owner("net/minecraft/client/resources/model/ModelResourceLocation")
                            .name("equals"),
                    m -> {
                        m.owner = "net/minecraft/resources/Identifier";
                    }
            );
        });
        register(Set.of(
                "dev/doctor4t/wathe/client/WatheClient"
        ), info -> {
            info.getNode().fields.addFirst(new FieldNode(
                    Opcodes.ASM9,
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                    "KEY_MAPPING_CATEGORY",
                    "Lnet/minecraft/client/KeyMapping$Category;",
                    null,
                    null
            ));

            MethodPointer.method()
                    .name("<clinit>")
                    .findOrThrow(info.getNode(), method -> {
                        var insns = new InsnList();
                        insns.add(new LdcInsnNode(TATConstants.WATHE));
                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "dev/doctor4t/wathe/Wathe",
                                "id",
                                "(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;"
                        ));
                        insns.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "net/minecraft/client/KeyMapping$Category",
                                "register",
                                "(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/KeyMapping$Category;"
                        ));
                        insns.add(new FieldInsnNode(
                                Opcodes.PUTSTATIC,
                                info.getNode().name,
                                "KEY_MAPPING_CATEGORY",
                                "Lnet/minecraft/client/KeyMapping$Category;"
                        ));
                        method.instructions.insert(insns);
                    });

            MethodPointer.method()
                    .name("onInitializeClient")
                    .findOrThrow(info.getNode(), method -> {
                        var insns = new InsnList();

                        insns.add(new FieldInsnNode(
                                Opcodes.GETSTATIC,
                                info.getNode().name,
                                "KEY_MAPPING_CATEGORY",
                                "Lnet/minecraft/client/KeyMapping$Category;"
                        ));

                        ASMUtil.splice(
                                method.instructions,
                                InsnPointer.constant("category.wathe.keybinds"),
                                insns
                        );
                        InsnPointer.methodCall()
                                .owner("net/minecraft/client/KeyMapping")
                                .name("<init>")
                                .forEach(method.instructions, field -> {
                                    field.desc = field.desc.replace("Ljava/lang/String;)V", "Lnet/minecraft/client/KeyMapping$Category;)V");
                                });
                    });
        });
        register(Set.of(
                "dev/doctor4t/ratatouille/mixin/client/optionlock/SliderWidgetMixin"
        ), info -> {
            info.getNode().methods.removeIf(method -> method.name.equals("ratatouille$disableSliderHandleIfInactive"));
        });
        /*
        register(Set.of(
                "dev/doctor4t/wathe/client/render/entity/Player"
        ), info -> {
            if (info.getNode().signature != null) {
                info.getNode().signature = info.getNode().signature.substring(0, info.getNode().signature.indexOf("<")) + "Ldev/doctor4t/wathe/entity/NoteEntity;Lsurvivalblock/train_across_time/provided/client/NoteEntityRenderState;>";
            }
        });
        */
    }
}
