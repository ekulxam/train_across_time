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
        internalName = switch (internalName) {
            case "org/ladysnake/cca/api/v3/world/WorldComponentInitializer" -> "org/ladysnake/cca/api/v3/level/LevelComponentInitializer";

            // WHY FABRIC WHY DID YOU STOP MAINTAINING INTERMEDIARY THATS LITERALLY AGAINST ITS WHOLE POINT
            case "net/minecraft/class_9285" -> "net/minecraft/world/item/component/ItemAttributeModifiers";

            case "net/minecraft/class_9143" -> "net/minecraft/network/codec/StreamMemberEncoder";
            case "net/minecraft/class_9141" -> "net/minecraft/network/codec/StreamDecoder";
            case "net/minecraft/class_9139" -> "net/minecraft/network/codec/StreamCodec";
            case "net/minecraft/class_9129" -> "net/minecraft/network/RegistryFriendlyByteBuf";

            case "net/minecraft/class_8710" -> "net/minecraft/network/protocol/common/custom/CustomPacketPayload";
            case "net/minecraft/class_8710$class_9155" -> "net/minecraft/network/protocol/common/custom/CustomPacketPayload$TypeAndCodec";
            case "net/minecraft/class_8710$class_9154" -> "net/minecraft/network/protocol/common/custom/CustomPacketPayload$Type";

            case "net/minecraft/class_1738$class_8051" -> "net/minecraft/world/item/ArmorItem$Type";
            case "net/minecraft/class_8002" -> "net/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil";

            case "net/minecraft/class_7924" -> "net/minecraft/core/registries/Registries";
            case "net/minecraft/class_7923" -> "net/minecraft/core/registries/BuiltInRegistries";
            case "net/minecraft/class_7922" -> "net/minecraft/core/DefaultedRegistry";

            case "net/minecraft/class_7706" -> "net/minecraft/world/item/CreativeModeTabs";

            case "net/minecraft/class_7225" -> "net/minecraft/core/HolderLookup";
            case "net/minecraft/class_7225$class_7874" -> "net/minecraft/core/HolderLookup$Provider";

            case "net/minecraft/class_6885" -> "net/minecraft/core/HolderSet";
            case "net/minecraft/class_6885$class_6887" -> "net/minecraft/core/HolderSet$ListBacked";
            case "net/minecraft/class_6880" -> "net/minecraft/core/Holder";

            case "net/minecraft/class_5558" -> "net/minecraft/world/level/block/entity/BlockEntityTicker";

            case "net/minecraft/class_5321" -> "net/minecraft/resources/ResourceKey";

            case "net/minecraft/class_4970" -> "net/minecraft/world/level/block/state/BlockBehaviour";
            case "net/minecraft/class_4970$class_2251" -> "net/minecraft/world/level/block/state/BlockBehaviour$Properties";

            case "net/minecraft/class_4538" -> "net/minecraft/world/level/LevelReader";

            case "net/minecraft/class_4284" -> "net/minecraft/util/datafix/DataFixTypes";

            case "net/minecraft/class_4184" -> "net/minecraft/client/Camera";
            case "net/minecraft/class_4176" -> "net/minecraft/world/food/Foods";
            case "net/minecraft/class_4174" -> "net/minecraft/world/food/FoodProperties";

            case "net/minecraft/class_3965" -> "net/minecraft/world/phys/BlockHitResult";

            case "net/minecraft/class_3857" -> "net/minecraft/world/entity/projectile/throwableitemprojectile/ThrowableItemProjectile";

            case "net/minecraft/class_3737" -> "net/minecraft/world/level/block/SimpleWaterloggedBlock";
            case "net/minecraft/class_3726" -> "net/minecraft/world/phys/shapes/CollisionContext";

            case "net/minecraft/class_3612" -> "net/minecraft/world/level/material/Fluids";
            case "net/minecraft/class_3611" -> "net/minecraft/world/level/material/Fluid";
            case "net/minecraft/class_3610" -> "net/minecraft/world/level/material/FluidState";
            case "net/minecraft/class_3609" -> "net/minecraft/world/level/material/FlowingFluid";

            case "net/minecraft/class_3545" -> "net/minecraft/util/Tuple";

            case "net/minecraft/class_3419" -> "net/minecraft/sounds/SoundSource";
            case "net/minecraft/class_3417" -> "net/minecraft/sounds/SoundEvents";
            case "net/minecraft/class_3414" -> "net/minecraft/sounds/SoundEvent";

            case "net/minecraft/class_3324" -> "net/minecraft/server/players/PlayerList";

            case "net/minecraft/class_3244" -> "net/minecraft/server/network/ServerGamePacketListenerImpl";
            case "net/minecraft/class_3222" -> "net/minecraft/server/level/ServerPlayer";
            case "net/minecraft/class_3218" -> "net/minecraft/server/level/ServerLevel";

            case "net/minecraft/class_3174" -> "net/minecraft/server/dedicated/DedicatedPlayerList";

            case "net/minecraft/class_2960" -> "net/minecraft/resources/Identifier";

            case "net/minecraft/class_2769" -> "net/minecraft/world/level/block/state/properties/Property";
            case "net/minecraft/class_2767" -> "net/minecraft/network/protocol/game/ClientboundSoundPacket";
            case "net/minecraft/class_2758" -> "net/minecraft/world/level/block/state/properties/IntegerProperty";
            case "net/minecraft/class_2754" -> "net/minecraft/world/level/block/state/properties/EnumProperty";
            case "net/minecraft/class_2753" -> "net/minecraft/world/level/block/state/properties/EnumProperty"; // This might cause some problems, since DirectionProperty was removed
            case "net/minecraft/class_2746" -> "net/minecraft/world/level/block/state/properties/BooleanProperty";
            case "net/minecraft/class_2741" -> "net/minecraft/world/level/block/state/properties/BlockStateProperties";

            case "net/minecraft/class_2689" -> "net/minecraft/world/level/block/state/StateDefinition";
            case "net/minecraft/class_2689$class_2690" -> "net/minecraft/world/level/block/state/StateDefinition$Builder";
            case "net/minecraft/class_2680" -> "net/minecraft/world/level/block/state/BlockState";
            case "net/minecraft/class_2622" -> "net/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket";
            case "net/minecraft/class_2602" -> "net/minecraft/network/protocol/game/ClientGamePacketListener";

            case "net/minecraft/class_2596" -> "net/minecraft/network/protocol/Packet";
            case "net/minecraft/class_2591" -> "net/minecraft/world/level/block/entity/BlockEntityType";
            case "net/minecraft/class_2586" -> "net/minecraft/world/level/block/entity/BlockEntity";
            case "net/minecraft/class_2561" -> "net/minecraft/network/chat/Component";
            case "net/minecraft/class_2540" -> "net/minecraft/network/FriendlyByteBuf";
            case "net/minecraft/class_2520" -> "net/minecraft/nbt/Tag";

            case "net/minecraft/class_2499" -> "net/minecraft/nbt/ListTag";
            case "net/minecraft/class_2487" -> "net/minecraft/nbt/CompoundTag";
            case "net/minecraft/class_2470" -> "net/minecraft/world/level/block/Rotation";
            case "net/minecraft/class_2464" -> "net/minecraft/world/level/block/RenderShape";
            case "net/minecraft/class_2415" -> "net/minecraft/world/level/block/Mirror";

            case "net/minecraft/class_2382" -> "net/minecraft/core/Vec3i";
            case "net/minecraft/class_2378" -> "net/minecraft/core/Registry";
            case "net/minecraft/class_2374" -> "net/minecraft/core/Position";
            case "net/minecraft/class_2350" -> "net/minecraft/core/Direction";
            case "net/minecraft/class_2338" -> "net/minecraft/core/BlockPos";
            case "net/minecraft/class_2338$class_2339" -> "net/minecraft/core/BlockPos$MutableBlockPos";
            case "net/minecraft/class_2314" -> "net/minecraft/commands/synchronization/ArgumentTypeInfo";

            case "net/minecraft/class_2248" -> "net/minecraft/world/level/block/Block";
            case "net/minecraft/class_2246" -> "net/minecraft/world/level/block/Blocks";
            case "net/minecraft/class_2237" -> "net/minecraft/world/level/block/BaseEntityBlock";

            case "net/minecraft/class_1937" -> "net/minecraft/world/level/Level";
            case "net/minecraft/class_1936" -> "net/minecraft/world/level/LevelAccessor";
            case "net/minecraft/class_1935" -> "net/minecraft/world/level/ItemLike";
            case "net/minecraft/class_1922" -> "net/minecraft/world/level/BlockGetter";

            case "net/minecraft/class_1834" -> "net/minecraft/world/item/Tiers";
            case "net/minecraft/class_1832" -> "net/minecraft/world/item/Tier";
            case "net/minecraft/class_1802" -> "net/minecraft/world/item/Items";

            case "net/minecraft/class_1799" -> "net/minecraft/world/item/ItemStack";
            case "net/minecraft/class_1792" -> "net/minecraft/world/item/Item";
            case "net/minecraft/class_1792$class_1793" -> "net/minecraft/world/item/Item$Properties";
            case "net/minecraft/class_1761" -> "net/minecraft/world/item/CreativeModeTab";
            case "net/minecraft/class_1750" -> "net/minecraft/world/item/context/BlockPlaceContext";
            case "net/minecraft/class_1747" -> "net/minecraft/world/item/BlockItem";
            case "net/minecraft/class_1743" -> "net/minecraft/world/item/AxeItem";
            case "net/minecraft/class_1740" -> "net/minecraft/world/item/ArmorMaterials";
            case "net/minecraft/class_1738" -> "net/minecraft/world/item/ArmorItem";
            case "net/minecraft/class_1702" -> "net/minecraft/world/food/FoodData";

            case "net/minecraft/class_1661" -> "net/minecraft/world/entity/player/Inventory";
            case "net/minecraft/class_1657" -> "net/minecraft/world/entity/player/Player";

            case "net/minecraft/class_1542" -> "net/minecraft/world/entity/item/ItemEntity";

            case "net/minecraft/class_1309" -> "net/minecraft/world/entity/LivingEntity";

            case "net/minecraft/class_1297" -> "net/minecraft/world/entity/Entity";
            case "net/minecraft/class_1269" -> "net/minecraft/world/InteractionResult";
            case "net/minecraft/class_1263" -> "net/minecraft/world/Container";

            case "net/minecraft/class_1049" -> "net/minecraft/client/renderer/texture/SimpleTexture";
            case "net/minecraft/class_1049$class_4006" -> "net/minecraft/client/renderer/texture/SimpleTexture$TextureImage";
            case "net/minecraft/class_1007" -> "net/minecraft/client/renderer/entity/player/PlayerRenderer";

            case "net/minecraft/class_989" -> "net/minecraft/client/renderer/entity/layers/ItemInHandLayer";
            case "net/minecraft/class_922" -> "net/minecraft/client/renderer/entity/LivingEntityRenderer";

            case "net/minecraft/class_898" -> "net/minecraft/client/renderer/entity/EntityRenderDispatcher";
            case "net/minecraft/class_897" -> "net/minecraft/client/renderer/entity/EntityRenderer";

            case "net/minecraft/class_765" -> "net/minecraft/client/renderer/LightTexture";
            case "net/minecraft/class_761" -> "net/minecraft/client/renderer/LevelRenderer";
            case "net/minecraft/class_759" -> "net/minecraft/client/renderer/ItemInHandRenderer";
            case "net/minecraft/class_758" -> "net/minecraft/client/renderer/FogRenderer";
            case "net/minecraft/class_757" -> "net/minecraft/client/renderer/GameRenderer";
            case "net/minecraft/class_746" -> "net/minecraft/client/player/LocalPlayer";
            case "net/minecraft/class_742" -> "net/minecraft/client/player/AbstractClientPlayer";

            case "net/minecraft/class_638" -> "net/minecraft/client/multiplayer/ClientLevel";

            case "net/minecraft/class_572" -> "net/minecraft/client/model/HumanoidModel";

            case "net/minecraft/class_481" -> "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen";
            case "net/minecraft/class_437" -> "net/minecraft/client/gui/screens/Screen";
            case "net/minecraft/class_429" -> "net/minecraft/client/gui/screens/options/OptionsScreen";
            case "net/minecraft/class_423" -> "net/minecraft/client/gui/screens/InBedChatScreen";

            case "net/minecraft/class_340" -> "net/minecraft/client/gui/components/DebugScreenOverlay";
            case "net/minecraft/class_338" -> "net/minecraft/client/gui/screens/ChatScreen"; // unsure if this is correct
            case "net/minecraft/class_329" -> "net/minecraft/client/gui/Gui";
            case "net/minecraft/class_315" -> "net/minecraft/client/Options";
            case "net/minecraft/class_310" -> "net/minecraft/client/Minecraft";
            case "net/minecraft/class_309" -> "net/minecraft/client/KeyboardHandler";
            case "net/minecraft/class_304" -> "net/minecraft/client/KeyMapping";

            case "net/minecraft/class_269" -> "net/minecraft/world/scores/Scoreboard";
            case "net/minecraft/class_265" -> "net/minecraft/world/phys/shapes/VoxelShape";
            case "net/minecraft/class_243" -> "net/minecraft/world/phys/Vec3";

            case "net/minecraft/class_156" -> "net/minecraft/util/Util";
            case "net/minecraft/class_124" -> "net/minecraft/ChatFormatting";

            default -> internalName;
        };

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
            default -> name;
        };

        name = switch (name) {
            case "method_60655" -> "fromNamespaceAndPath";

            case "method_59922" -> "getRandom";
            case "method_57379" -> "set";
            case "method_57348" -> "attributes";
            case "method_57346" -> "createAttributes";
            case "method_56690" -> "getDurability";
            case "method_56673" -> "registryAccess";
            case "method_56438" -> "ofMember";
            case "method_55766" -> "useWithoutItem";
            case "method_54094" -> "simpleCodec";
            case "method_53969" -> "codec";

            case "method_47983" -> "wrapAsHolder";
            case "method_46758" -> "asGetterLookup";
            case "method_46751" -> "lookupOrThrow";
            case "method_46733" -> "get";
            case "method_45421" -> "accept";
            case "method_45420" -> "accept";
            case "method_43470" -> "literal";
            case "method_43128" -> "playSound";
            case "method_43057" -> "nextFloat";
            case "method_43055" -> "nextLong";
            case "method_43048" -> "nextInt";
            case "method_40239" -> "stream";

            case "method_39360" -> "is";
            case "method_39281" -> "scheduleTick";
            case "method_38585" -> "create";
            case "method_38244" -> "saveWithoutMetadata";
            case "method_38235" -> "getUpdatePacket";
            case "method_37908" -> "level";
            case "method_33614" -> "spawnDestroyParticles";
            case "method_32309" -> "getRandom";
            case "method_31645" -> "getTicker";
            case "method_31618" -> "createTickerHelper";
            case "method_31574" -> "is";
            case "method_31548" -> "getInventory";

            case "method_29281" -> "getCraftSlots";
            case "method_29280" -> "clearOrCountMatchingItems";
            case "method_29179" -> "create";
            case "method_28498" -> "hasProperty";
            case "method_27692" -> "withStyle";
            case "method_26204" -> "getBlock";
            case "method_26186" -> "rotate";
            case "method_25927" -> "putUUID";
            case "method_25926" -> "getUUID";
            case "method_24953" -> "atCenterOf";
            case "method_23317" -> "getX";
            case "method_23318" -> "getY";
            case "method_23321" -> "getZ";
            case "method_22488" -> "noOcclusion";

            case "method_19265" -> "food";
            case "method_18470" -> "getPlayerByUUID";
            case "method_18456" -> "players";
            case "method_17356" -> "playNotifySound";
            case "method_16887" -> "getUpdateTag";
            case "method_15789" -> "getTickDelay";
            case "method_15729" -> "getSource";
            case "method_15441" -> "getB";
            case "method_14364" -> "send";
            case "method_12832" -> "getPath";
            case "method_11667" -> "add";
            case "method_11657" -> "setValue";
            case "method_11654" -> "getValue";
            case "method_11014" -> "loadAdditional";
            case "method_11010" -> "getBlockState";
            case "method_11007" -> "saveAdditional";
            case "method_10583" -> "getFloat";
            case "method_10582" -> "putString";
            case "method_10577" -> "getBoolean";
            case "method_10574" -> "getDouble";
            case "method_10573" -> "contains";
            case "method_10569" -> "putInt";
            case "method_10566" -> "put";
            case "method_10558" -> "getString";
            case "method_10556" -> "putBoolean";
            case "method_10554" -> "getList";
            case "method_10550" -> "getInt";
            case "method_10549" -> "putDouble";
            case "method_10548" -> "putFloat";
            case "method_10545" -> "contains";
            case "method_10503" -> "rotate";
            case "method_10345" -> "getRotation";
            case "method_10230" -> "register";
            case "method_10216" -> "x";
            case "method_10215" -> "z";
            case "method_10214" -> "y";
            case "method_10153" -> "getOpposite";
            case "method_10123" -> "newBlockEntity";
            case "method_10074" -> "below";

            case "method_9630" -> "ofFullCopy";
            case "method_9606" -> "attack";
            case "method_9605" -> "getStateForPlacement";
            case "method_9604" -> "getRenderShape";
            case "method_9598" -> "rotate";
            case "method_9569" -> "mirror";
            case "method_9564" -> "defaultBlockState";
            case "method_9559" -> "updateShape";
            case "method_9545" -> "getFluidState";
            case "method_9541" -> "box";
            case "method_9530" -> "getShape";
            case "method_9526" -> "useShapeForLightOcclusion";
            case "method_9515" -> "createBlockStateDefinition";

            case "method_8413" -> "sendBlockUpdated";
            case "method_8409" -> "getRandom";
            case "method_8321" -> "getBlockEntity";
            case "method_8320" -> "getBlockState";
            case "method_8316" -> "getFluidState";
            case "method_8045" -> "getLevel";
            case "method_8042" -> "getHorizontalDirection";
            case "method_8037" -> "getClickedPos";

            case "method_7972" -> "copy";
            case "method_7909" -> "getItem";
            case "method_7904" -> "isOnCooldown";
            case "method_7895" -> "durability";
            case "method_7889" -> "stacksTo";
            case "method_7854" -> "getDefaultInstance";
            case "method_7357" -> "getCooldowns";
            case "method_7353" -> "displayClientMessage";

            case "method_6047" -> "getMainHandItem";

            case "method_5845" -> "getStringUUID";
            case "method_5667" -> "getUUID";
            case "method_5438" -> "getItem";
            case "method_5431" -> "setChanged";

            case "method_654" -> "make";
            case "comp_349" -> "value";
            case "comp_327" -> "identifier";

            default -> name;
        };

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
        return switch (name) {
            case "field_48846" -> "ARMADILLO";
            case "field_44688" -> "CREATIVE_MODE_TAB";
            case "field_41934" -> "HELMET";
            case "field_41935" -> "CHESTPLATE";
            case "field_41936" -> "LEGGINGS";
            case "field_41937" -> "BOOTS";
            case "field_41197" -> "ITEM";
            case "field_41178" -> "ITEM";
            case "field_41175" -> "BLOCK";
            case "field_41172" -> "SOUND_EVENT";
            case "field_40202" -> "COMBAT";
            case "field_40197" -> "FUNCTIONAL_BLOCKS";

            case "field_38068" -> "WARDEN_HEARTBEAT";

            case "field_25139" -> "CODEC";
            case "field_22030" -> "NETHERITE_BOOTS";
            case "field_20381" -> "HONEY_BOTTLE";

            case "field_15910" -> "WATER";
            case "field_15248" -> "PLAYERS";
            case "field_15245" -> "BLOCKS";
            case "field_14628" -> "WOOL_HIT";
            case "field_13987" -> "connection";
            case "field_12524" -> "NOTE";
            case "field_12508" -> "WATERLOGGED";
            case "field_12481" -> "HORIZONTAL_FACING";
            case "field_11867" -> "worldPosition";
            case "field_11863" -> "level";
            case "field_10446" -> "WHITE_WOOL";
            case "field_10423" -> "GRAY_WOOL";
            case "field_10215" -> "MAGENTA_WOOL";

            case "field_9236" -> "isClientSide";
            case "field_9229" -> "random";

            case "field_8922" -> "WOOD";

            case "field_7545" -> "selected";
            case "field_7498" -> "inventoryMenu";

            case "field_5812" -> "SUCCESS";

            case "field_1079" -> "DARK_RED";

            default -> name;
        };
    }
}
