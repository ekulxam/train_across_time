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
package survivalblock.train_across_time.agent.remap;

import net.fabricmc.loader.impl.util.log.Log;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.function.Consumer;

import static survivalblock.train_across_time.TheTrainAcrossTimeConstants.LOGGER;

/**
 * @author Typho
 */
@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class WatheClassPatches {
    private WatheClassPatches() {
    }

    public static final Map<String, Consumer<ClassNode>> PATCHES = new HashMap<>();

    public static void register(String className, Consumer<ClassNode> patch) {
        PATCHES.merge(className, patch, (a, b) -> node -> {
            a.accept(node);
            b.accept(node);
        });
    }

    public static void register(List<String> classes, Consumer<ClassNode> patch) {
        for (String cls : classes) {
            register(cls, patch);
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

    public static void applyToMethod(ClassNode node, String name, Consumer<MethodNode> action) {
        for (MethodNode methodNode : node.methods) {
            if (methodNode.name.equals(name)) {
                action.accept(methodNode);
            }
        }
    }

    public static void applyToField(ClassNode node, String name, Consumer<FieldNode> action) {
        for (FieldNode fieldNode : node.fields) {
            if (fieldNode.name.equals(name)) {
                action.accept(fieldNode);
            }
        }
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
        ), node -> {
            node.interfaces.add("org/ladysnake/cca/api/v3/component/Component");
        });

        register("dev/doctor4t/wathe/index/WatheItems", node -> {
            for (MethodNode method : node.methods) {
                if (method.name.equals("<clinit>")) {
                    method.instructions.remove(
                            Arrays.stream(method.instructions.toArray())
                                    .filter(insn -> insn instanceof MethodInsnNode m && m.name.equals("createAttributes"))
                                    .findFirst()
                                    .orElseThrow()
                    );
                }
            }
        });

        register("dev/doctor4t/ratatouille/util/registrar/BlockRegistrar", node -> {
            for (MethodNode method : node.methods) {
                if (method.name.equals("createWithItem")) {
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
                }
            }
        });

        register(List.of(
                "dev/doctor4t/ratatouille/index/RatatouilleItems"
        ), node -> {
            for (MethodNode method : node.methods) {
                if (method.name.equals("<clinit>")) {
                    Log.info(LOGGER, "Injecting item ids into RatatouilleItems");
                    applyItemIds(method, Map.of());
                }
            }
        });
        register(List.of(
                "dev/doctor4t/ratatouille/index/RatatouilleBlocks"
        ), node -> {
            for (MethodNode method : node.methods) {
                if (method.name.equals("<clinit>")) {
                    Log.info(LOGGER, "Injecting block ids into RatatouilleBlocks");
                    applyBlockIds(method, Map.of());
                }
            }
        });
        register(List.of(
                "dev/doctor4t/wathe/index/WatheItems"
        ), node -> {
            for (MethodNode method : node.methods) {
                if (method.name.equals("<clinit>")) {
                    Log.info(LOGGER, "Injecting item ids into WatheItems");
                    applyItemIds(method, Map.of(
                            2, "knife"
                    ));
                }
            }
        });
        register(List.of(
                "dev/doctor4t/wathe/index/WatheBlocks"
        ), node -> {
            for (MethodNode method : node.methods) {
                if (method.name.equals("<clinit>")) {
                    Log.info(LOGGER, "Injecting block ids into WatheBlocks");
                    applyBlockIds(method, Map.of());
                }
            }
        });
        register("dev/doctor4t/wathe/util/ShopEntry", node -> {
            applyToField(node, "stack", field -> {
                field.desc = field.desc.replace("Lnet/minecraft/world/item/ItemStack", "Lnet/minecraft/world/item/ItemStackTemplate");
            });
            for (MethodNode method : node.methods) {
                method.desc = method.desc.replace("Lnet/minecraft/world/item/ItemStack", "Lnet/minecraft/world/item/ItemStackTemplate");
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
        register("dev/doctor4t/wathe/game/GameConstants", node -> {
            for (MethodNode method : node.methods) {
                if (method.name.contains("lambda$static") && method.desc.contains("Ljava/util/ArrayList")) {
                    for (var instruction : method.instructions) {
                        if (instruction instanceof MethodInsnNode methodInsn && methodInsn.owner.equals("net/minecraft/world/item/ItemStack") && methodInsn.name.equals("<init>")) {
                            methodInsn.owner = "net/minecraft/world/item/ItemStackTemplate";
                            methodInsn.desc = methodInsn.desc.replace("Lnet/minecraft/world/level/ItemLike", "Lnet/minecraft/world/item/Item");
                        }
                    }
                }
            }
        });
    }
}
