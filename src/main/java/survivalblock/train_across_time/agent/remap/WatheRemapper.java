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

import org.objectweb.asm.commons.Remapper;

/**
 * @author Typho
 */
@SuppressWarnings({"SwitchStatementWithTooFewBranches", "DuplicateBranchesInSwitch"})
public class WatheRemapper extends Remapper {
    public final String className;

    public WatheRemapper(int api, String className) {
        super(api);
        this.className = className;
    }

    @Override
    public String map(String internalName) {
        // WHY FABRIC WHY DID YOU STOP MAINTAINING INTERMEDIARY THATS LITERALLY AGAINST ITS WHOLE POINT
        if (internalName.startsWith("net/minecraft/class_")) {
            try {
                internalName = switch (Integer.parseInt(internalName.substring(internalName.indexOf('_') + 1))) {
                    case 9331 -> "net/minecraft/core/component/DataComponentType";

                    case 9285 -> "net/minecraft/world/item/component/ItemAttributeModifiers";

                    case 9143 -> "net/minecraft/network/codec/StreamMemberEncoder";
                    case 9141 -> "net/minecraft/network/codec/StreamDecoder";
                    case 9139 -> "net/minecraft/network/codec/StreamCodec";
                    case 9135 -> "net/minecraft/network/codec/ByteBufCodecs";
                    case 9129 -> "net/minecraft/network/RegistryFriendlyByteBuf";

                    case 8710 -> "net/minecraft/network/protocol/common/custom/CustomPacketPayload";

                    case 8002 -> "net/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil";

                    case 7924 -> "net/minecraft/core/registries/Registries";
                    case 7923 -> "net/minecraft/core/registries/BuiltInRegistries";
                    case 7922 -> "net/minecraft/core/DefaultedRegistry";

                    case 7706 -> "net/minecraft/world/item/CreativeModeTabs";

                    case 7225 -> "net/minecraft/core/HolderLookup";

                    case 6885 -> "net/minecraft/core/HolderSet";
                    case 6880 -> "net/minecraft/core/Holder";

                    case 5558 -> "net/minecraft/world/level/block/entity/BlockEntityTicker";

                    case 5321 -> "net/minecraft/resources/ResourceKey";

                    case 5250 -> "net/minecraft/network/chat/MutableComponent";

                    case 4970 -> "net/minecraft/world/level/block/state/BlockBehaviour";

                    case 4538 -> "net/minecraft/world/level/LevelReader";

                    case 4284 -> "net/minecraft/util/datafix/DataFixTypes";

                    case 4184 -> "net/minecraft/client/Camera";
                    case 4176 -> "net/minecraft/world/food/Foods";
                    case 4174 -> "net/minecraft/world/food/FoodProperties";

                    case 3965 -> "net/minecraft/world/phys/BlockHitResult";

                    case 3857 -> "net/minecraft/world/entity/projectile/throwableitemprojectile/ThrowableItemProjectile";

                    case 3737 -> "net/minecraft/world/level/block/SimpleWaterloggedBlock";
                    case 3726 -> "net/minecraft/world/phys/shapes/CollisionContext";

                    case 3612 -> "net/minecraft/world/level/material/Fluids";
                    case 3611 -> "net/minecraft/world/level/material/Fluid";
                    case 3610 -> "net/minecraft/world/level/material/FluidState";
                    case 3609 -> "net/minecraft/world/level/material/FlowingFluid";

                    case 3545 -> "net/minecraft/util/Tuple";

                    case 3419 -> "net/minecraft/sounds/SoundSource";
                    case 3417 -> "net/minecraft/sounds/SoundEvents";
                    case 3414 -> "net/minecraft/sounds/SoundEvent";

                    case 3324 -> "net/minecraft/server/players/PlayerList";

                    case 3244 -> "net/minecraft/server/network/ServerGamePacketListenerImpl";
                    case 3222 -> "net/minecraft/server/level/ServerPlayer";
                    case 3218 -> "net/minecraft/server/level/ServerLevel";

                    case 3174 -> "net/minecraft/server/dedicated/DedicatedPlayerList";

                    case 2960 -> "net/minecraft/resources/Identifier";

                    case 2769 -> "net/minecraft/world/level/block/state/properties/Property";
                    case 2767 -> "net/minecraft/network/protocol/game/ClientboundSoundPacket";
                    case 2758 -> "net/minecraft/world/level/block/state/properties/IntegerProperty";
                    case 2754 -> "net/minecraft/world/level/block/state/properties/EnumProperty";
                    case 2753 -> "net/minecraft/world/level/block/state/properties/EnumProperty"; // This might cause some problems, since DirectionProperty was removed
                    case 2746 -> "net/minecraft/world/level/block/state/properties/BooleanProperty";
                    case 2741 -> "net/minecraft/world/level/block/state/properties/BlockStateProperties";

                    case 2689 -> "net/minecraft/world/level/block/state/StateDefinition";
                    case 2680 -> "net/minecraft/world/level/block/state/BlockState";
                    case 2622 -> "net/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket";
                    case 2602 -> "net/minecraft/network/protocol/game/ClientGamePacketListener";

                    case 2596 -> "net/minecraft/network/protocol/Packet";
                    case 2591 -> "net/minecraft/world/level/block/entity/BlockEntityType";
                    case 2586 -> "net/minecraft/world/level/block/entity/BlockEntity";
                    case 2561 -> "net/minecraft/network/chat/Component";
                    case 2540 -> "net/minecraft/network/FriendlyByteBuf";
                    case 2520 -> "net/minecraft/nbt/Tag";

                    case 2499 -> "net/minecraft/nbt/ListTag";
                    case 2498 -> "net/minecraft/world/level/block/SoundType";
                    case 2487 -> "net/minecraft/nbt/CompoundTag";
                    case 2470 -> "net/minecraft/world/level/block/Rotation";
                    case 2464 -> "net/minecraft/world/level/block/RenderShape";
                    case 2415 -> "net/minecraft/world/level/block/Mirror";

                    case 2382 -> "net/minecraft/core/Vec3i";
                    case 2378 -> "net/minecraft/core/Registry";
                    case 2374 -> "net/minecraft/core/Position";
                    case 2350 -> "net/minecraft/core/Direction";
                    case 2338 -> "net/minecraft/core/BlockPos";
                    case 2314 -> "net/minecraft/commands/synchronization/ArgumentTypeInfo";

                    case 2248 -> "net/minecraft/world/level/block/Block";
                    case 2246 -> "net/minecraft/world/level/block/Blocks";
                    case 2237 -> "net/minecraft/world/level/block/BaseEntityBlock";

                    case 2168 -> "net/minecraft/commands/CommandSourceStack";

                    case 1937 -> "net/minecraft/world/level/Level";
                    case 1936 -> "net/minecraft/world/level/LevelAccessor";
                    case 1935 -> "net/minecraft/world/level/ItemLike";
                    case 1922 -> "net/minecraft/world/level/BlockGetter";

                    case 1834 -> "net/minecraft/world/item/Tiers";
                    case 1832 -> "net/minecraft/world/item/Tier";
                    case 1802 -> "net/minecraft/world/item/Items";

                    case 1799 -> "net/minecraft/world/item/ItemStack";
                    case 1792 -> "net/minecraft/world/item/Item";
                    case 1761 -> "net/minecraft/world/item/CreativeModeTab";
                    case 1750 -> "net/minecraft/world/item/context/BlockPlaceContext";
                    case 1747 -> "net/minecraft/world/item/BlockItem";
                    case 1743 -> "net/minecraft/world/item/AxeItem";
                    case 1740 -> "net/minecraft/world/item/ArmorMaterials";
                    case 1738 -> "net/minecraft/world/item/ArmorItem";
                    case 1702 -> "net/minecraft/world/food/FoodData";

                    case 1661 -> "net/minecraft/world/entity/player/Inventory";
                    case 1657 -> "net/minecraft/world/entity/player/Player";

                    case 1542 -> "net/minecraft/world/entity/item/ItemEntity";

                    case 1311 -> "net/minecraft/world/entity/MobCategory";
                    case 1309 -> "net/minecraft/world/entity/LivingEntity";

                    case 1299 -> "net/minecraft/world/entity/EntityType";
                    case 1297 -> "net/minecraft/world/entity/Entity";
                    case 1269 -> "net/minecraft/world/InteractionResult";
                    case 1263 -> "net/minecraft/world/Container";

                    case 1049 -> "net/minecraft/client/renderer/texture/SimpleTexture";
                    case 1007 -> "net/minecraft/client/renderer/entity/player/PlayerRenderer";

                    case 989 -> "net/minecraft/client/renderer/entity/layers/ItemInHandLayer";
                    case 922 -> "net/minecraft/client/renderer/entity/LivingEntityRenderer";

                    case 898 -> "net/minecraft/client/renderer/entity/EntityRenderDispatcher";
                    case 897 -> "net/minecraft/client/renderer/entity/EntityRenderer";

                    case 765 -> "net/minecraft/client/renderer/LightTexture";
                    case 761 -> "net/minecraft/client/renderer/LevelRenderer";
                    case 759 -> "net/minecraft/client/renderer/ItemInHandRenderer";
                    case 758 -> "net/minecraft/client/renderer/FogRenderer";
                    case 757 -> "net/minecraft/client/renderer/GameRenderer";
                    case 746 -> "net/minecraft/client/player/LocalPlayer";
                    case 742 -> "net/minecraft/client/player/AbstractClientPlayer";

                    case 638 -> "net/minecraft/client/multiplayer/ClientLevel";

                    case 572 -> "net/minecraft/client/model/HumanoidModel";

                    case 481 -> "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen";
                    case 437 -> "net/minecraft/client/gui/screens/Screen";
                    case 429 -> "net/minecraft/client/gui/screens/options/OptionsScreen";
                    case 423 -> "net/minecraft/client/gui/screens/InBedChatScreen";

                    case 340 -> "net/minecraft/client/gui/components/DebugScreenOverlay";
                    case 338 -> "net/minecraft/client/gui/screens/ChatScreen"; // unsure if this is correct
                    case 329 -> "net/minecraft/client/gui/Gui";
                    case 315 -> "net/minecraft/client/Options";
                    case 310 -> "net/minecraft/client/Minecraft";
                    case 309 -> "net/minecraft/client/KeyboardHandler";
                    case 304 -> "net/minecraft/client/KeyMapping";

                    case 269 -> "net/minecraft/world/scores/Scoreboard";
                    case 265 -> "net/minecraft/world/phys/shapes/VoxelShape";
                    case 243 -> "net/minecraft/world/phys/Vec3";

                    case 156 -> "net/minecraft/util/Util";
                    case 124 -> "net/minecraft/ChatFormatting";

                    default -> internalName;
                };
            } catch (NumberFormatException ignored) {
                internalName = switch (internalName) {
                    case "net/minecraft/class_9331$class_9332" -> "net/minecraft/core/component/DataComponentType$Builder";

                    case "net/minecraft/class_8710$class_9155" -> "net/minecraft/network/protocol/common/custom/CustomPacketPayload$TypeAndCodec";
                    case "net/minecraft/class_8710$class_9154" -> "net/minecraft/network/protocol/common/custom/CustomPacketPayload$Type";

                    case "net/minecraft/class_7225$class_7874" -> "net/minecraft/core/HolderLookup$Provider";

                    case "net/minecraft/class_6885$class_6887" -> "net/minecraft/core/HolderSet$ListBacked";

                    case "net/minecraft/class_4970$class_2251" -> "net/minecraft/world/level/block/state/BlockBehaviour$Properties";

                    case "net/minecraft/class_2689$class_2690" -> "net/minecraft/world/level/block/state/StateDefinition$Builder";
                    case "net/minecraft/class_2591$class_5559" -> "net/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder$Factory";
                    case "net/minecraft/class_2591$class_2592" -> "net/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder";
                    case "net/minecraft/class_2338$class_2339" -> "net/minecraft/core/BlockPos$MutableBlockPos";

                    case "net/minecraft/class_1792$class_1793" -> "net/minecraft/world/item/Item$Properties";
                    case "net/minecraft/class_1738$class_8051" -> "net/minecraft/world/item/ArmorItem$Type";
                    case "net/minecraft/class_1299$class_4049" -> "net/minecraft/world/entity/EntityType$EntityFactory";
                    case "net/minecraft/class_1299$class_1300" -> "net/minecraft/world/entity/EntityType$Builder";
                    case "net/minecraft/class_1049$class_4006" -> "net/minecraft/client/renderer/texture/SimpleTexture$TextureImage";

                    default -> internalName;
                };
            }
        } else {
            internalName = switch (internalName) {
                case "org/ladysnake/cca/api/v3/world/WorldComponentInitializer" -> "org/ladysnake/cca/api/v3/level/LevelComponentInitializer";

                case "net/fabricmc/fabric/api/itemgroup/v1/ItemGroupEvents" -> "net/fabricmc/fabric/api/creativetab/v1/CreativeModeTabEvents";
                case "net/fabricmc/fabric/api/itemgroup/v1/ItemGroupEvents$ModifyEntries" -> "net/fabricmc/fabric/api/creativetab/v1/CreativeModeTabEvents$ModifyOutput";
                case "net/fabricmc/fabric/api/itemgroup/v1/FabricItemGroupEntries" -> "net/fabricmc/fabric/api/creativetab/v1/FabricCreativeModeTabOutput";

                default -> internalName;
            };
        }

        internalName = switch (this.className) {
            case "dev/doctor4t/wathe/index/WatheItems" -> switch (internalName) {
                case "net/minecraft/world/item/Tiers" -> "net/minecraft/world/item/ToolMaterial";

                default -> internalName;
            };

            default -> internalName;
        };

        return internalName;
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        name = switch (owner) {
            case "net/fabricmc/fabric/api/networking/v1/PayloadTypeRegistry" -> switch (name) {
                case "configurationC2S" -> "serverboundConfiguration";
                case "configurationS2C" -> "clientboundConfiguration";
                case "playC2S" -> "serverboundPlay";
                case "playS2C" -> "clientboundPlay";

                default -> name;
            };
            case "net/fabricmc/fabric/api/itemgroup/v1/ItemGroupEvents" -> switch (name) {
                case "modifyEntriesEvent" -> "modifyOutputEvent";

                default -> name;
            };

            default -> name;
        };

        if (name.startsWith("method_") || name.startsWith("comp_")) {
            try {
                name = switch (Integer.parseInt(name.substring(name.indexOf('_') + 1))) {
                    case 60655 -> "fromNamespaceAndPath";

                    case 59922 -> "getRandom";
                    case 57882 -> "networkSynchronized";
                    case 57881 -> "persistent";
                    case 57880 -> "build";
                    case 57873 -> "builder";
                    case 57379 -> "set";
                    case 57348 -> "attributes";
                    case 57346 -> "createAttributes";
                    case 56690 -> "getDurability";
                    case 56673 -> "registryAccess";
                    case 56438 -> "ofMember";
                    case 55766 -> "useWithoutItem";
                    case 54663 -> "withColor";
                    case 54094 -> "simpleCodec";
                    case 53969 -> "codec";

                    case 47983 -> "wrapAsHolder";
                    case 47908 -> "createVariableRangeEvent";
                    case 46758 -> "asGetterLookup";
                    case 46751 -> "lookupOrThrow";
                    case 46733 -> "get";
                    case 45421 -> "accept";
                    case 45420 -> "accept";
                    case 43470 -> "literal";
                    case 43128 -> "playSound";
                    case 43057 -> "nextFloat";
                    case 43055 -> "nextLong";
                    case 43048 -> "nextInt";
                    case 40239 -> "stream";

                    case 39360 -> "is";
                    case 39281 -> "scheduleTick";
                    case 38585 -> "create";
                    case 38244 -> "saveWithoutMetadata";
                    case 38235 -> "getUpdatePacket";
                    case 37908 -> "level";
                    case 33614 -> "spawnDestroyParticles";
                    case 32309 -> "getRandom";
                    case 31645 -> "getTicker";
                    case 31618 -> "createTickerHelper";
                    case 31574 -> "is";
                    case 31548 -> "getInventory";

                    case 29281 -> "getCraftSlots";
                    case 29280 -> "clearOrCountMatchingItems";
                    case 29179 -> "create";
                    case 28498 -> "hasProperty";
                    case 27693 -> "append";
                    case 27692 -> "withStyle";
                    case 27661 -> "copy";
                    case 27299 -> "clientTrackingRange";
                    case 26204 -> "getBlock";
                    case 26186 -> "rotate";
                    case 25927 -> "putUUID";
                    case 25926 -> "getUUID";
                    case 24953 -> "atCenterOf";
                    case 23317 -> "getX";
                    case 23318 -> "getY";
                    case 23321 -> "getZ";
                    case 22488 -> "noOcclusion";
                    case 20528 -> "create"; // technically it's 'of' but we replace it with FabricBlockEntityTypeBuilder which has 'create'

                    case 19265 -> "food";
                    case 18470 -> "getPlayerByUUID";
                    case 18456 -> "players";
                    case 17687 -> "sized";
                    case 17356 -> "playNotifySound";
                    case 16887 -> "getUpdateTag";
                    case 15789 -> "getTickDelay";
                    case 15729 -> "getSource";
                    case 15441 -> "getB";
                    case 14364 -> "send";
                    case 12832 -> "getPath";
                    case 11667 -> "add";
                    case 11657 -> "setValue";
                    case 11654 -> "getValue";
                    case 11014 -> "loadAdditional";
                    case 11010 -> "getBlockState";
                    case 11007 -> "saveAdditional";
                    case 10852 -> "append";
                    case 10583 -> "getFloat";
                    case 10582 -> "putString";
                    case 10577 -> "getBoolean";
                    case 10574 -> "getDouble";
                    case 10573 -> "contains";
                    case 10569 -> "putInt";
                    case 10566 -> "put";
                    case 10558 -> "getString";
                    case 10556 -> "putBoolean";
                    case 10554 -> "getList";
                    case 10550 -> "getInt";
                    case 10549 -> "putDouble";
                    case 10548 -> "putFloat";
                    case 10545 -> "contains";
                    case 10503 -> "rotate";
                    case 10345 -> "getRotation";
                    case 10230 -> "register";
                    case 10216 -> "x";
                    case 10215 -> "z";
                    case 10214 -> "y";
                    case 10153 -> "getOpposite";
                    case 10123 -> "newBlockEntity";
                    case 10074 -> "below";

                    case 9630 -> "ofFullCopy";
                    case 9606 -> "attack";
                    case 9605 -> "getStateForPlacement";
                    case 9604 -> "getRenderShape";
                    case 9598 -> "rotate";
                    case 9569 -> "mirror";
                    case 9564 -> "defaultBlockState";
                    case 9559 -> "updateShape";
                    case 9545 -> "getFluidState";
                    case 9541 -> "box";
                    case 9530 -> "getShape";
                    case 9526 -> "useShapeForLightOcclusion";
                    case 9515 -> "createBlockStateDefinition";
                    case 9226 -> "sendSuccess";
                    case 9225 -> "getLevel";

                    case 8413 -> "sendBlockUpdated";
                    case 8409 -> "getRandom";
                    case 8321 -> "getBlockEntity";
                    case 8320 -> "getBlockState";
                    case 8316 -> "getFluidState";
                    case 8045 -> "getLevel";
                    case 8042 -> "getHorizontalDirection";
                    case 8037 -> "getClickedPos";

                    case 7972 -> "copy";
                    case 7960 -> "isEmpty";
                    case 7909 -> "getItem";
                    case 7906 -> "addCooldown";
                    case 7904 -> "isOnCooldown";
                    case 7895 -> "durability";
                    case 7889 -> "stacksTo";
                    case 7854 -> "getDefaultInstance";
                    case 7357 -> "getCooldowns";
                    case 7353 -> "displayClientMessage";
                    case 7270 -> "addItem";

                    case 6047 -> "getMainHandItem";

                    case 5903 -> "of";
                    case 5901 -> "noSummon";
                    case 5845 -> "getStringUUID";
                    case 5667 -> "getUUID";
                    case 5476 -> "getDisplayName";
                    case 5447 -> "setItem";
                    case 5438 -> "getItem";
                    case 5431 -> "setChanged";

                    case 654 -> "make";
                    case 349 -> "value";
                    case 327 -> "identifier";

                    default -> name;
                };
            } catch (NumberFormatException ignored) {
            }
        }

        // TODO
        name = switch (this.className) {
            case "dev/doctor4t/wathe/index/WatheItems" -> switch (name) {
                case "attributes" -> "axe";

                default -> name;
            };

            default -> name;
        };

        return name;
    }

    @Override
    public String mapMethodDesc(String methodDescriptor) {
        methodDescriptor = super.mapMethodDesc(methodDescriptor);

        // TODO
        methodDescriptor = switch (this.className) {
            case "dev/doctor4t/wathe/index/WatheItems" -> switch (methodDescriptor) {
                case "(Lnet/minecraft/world/item/component/ItemAttributeModifiers;)Lnet/minecraft/world/item/Item$Properties;" -> "(Lnet/minecraft/world/item/ToolMaterial;FF)Lnet/minecraft/world/item/Item$Properties;";

                default -> methodDescriptor;
            };

            default -> methodDescriptor;
        };

        return methodDescriptor;
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        if (name.startsWith("field_")) {
            try {
                name = switch (Integer.parseInt(name.substring(name.indexOf('_') + 1))) {
                    case 49658 -> "DATA_COMPONENT_TYPE";
                    case 48846 -> "ARMADILLO";
                    case 48554 -> "STRING_UTF8";
                    case 48547 -> "BOOL";
                    case 44688 -> "CREATIVE_MODE_TAB";
                    case 41934 -> "HELMET";
                    case 41935 -> "CHESTPLATE";
                    case 41936 -> "LEGGINGS";
                    case 41937 -> "BOOTS";
                    case 41197 -> "ITEM";
                    case 41181 -> "BLOCK_ENTITY_TYPE";
                    case 41178 -> "ITEM";
                    case 41177 -> "ENTITY_TYPE";
                    case 41175 -> "BLOCK";
                    case 41172 -> "SOUND_EVENT";
                    case 40202 -> "COMBAT";
                    case 40197 -> "FUNCTIONAL_BLOCKS";

                    case 38068 -> "WARDEN_HEARTBEAT";

                    case 25139 -> "CODEC";
                    case 22030 -> "NETHERITE_BOOTS";
                    case 20381 -> "HONEY_BOTTLE";

                    case 17715 -> "MISC";
                    case 15910 -> "WATER";
                    case 15248 -> "PLAYERS";
                    case 15245 -> "BLOCKS";
                    case 14628 -> "WOOL_HIT";
                    case 13987 -> "connection";
                    case 12524 -> "NOTE";
                    case 12508 -> "WATERLOGGED";
                    case 12481 -> "HORIZONTAL_FACING";
                    case 11867 -> "worldPosition";
                    case 11863 -> "level";
                    case 10446 -> "WHITE_WOOL";
                    case 10423 -> "GRAY_WOOL";
                    case 10215 -> "MAGENTA_WOOL";

                    case 9236 -> "isClientSide";
                    case 9229 -> "random";

                    case 8922 -> "WOOD";

                    case 7545 -> "selected";
                    case 7498 -> "inventoryMenu";

                    case 5812 -> "SUCCESS";

                    case 1079 -> "DARK_RED";

                    default -> name;
                };
            } catch (NumberFormatException ignored) {
            }
        }

        return name;
    }
}
