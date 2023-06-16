package dk.sebsa.idc;

import dk.sebsa.beholder.Beholder;
import dk.sebsa.idc.knowledge.Archive;
import dk.sebsa.idc.knowledge.KnowledgeArgument;
import dk.sebsa.idc.knowledge.PlayerState;
import dk.sebsa.idc.knowledge.ServerState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class IDC implements ModInitializer {
    public static final String MODID = "idc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final Item PORTAL_GUN = new Item(new FabricItemSettings().maxCount(1));
    public static final Archive KNOWLEDGE = new Archive();

    @Override
    public void onInitialize() {
        LOGGER.info("IDC Works!");
        Beholder beholder = new Beholder(MODID);

        beholder.collectItem(PORTAL_GUN, "portal_gun", "idc");
        beholder.collectItemGroup(FabricItemGroup.builder()
                .icon(() -> new ItemStack(PORTAL_GUN))
                .displayName(Text.translatable("itemGroup.idc.item_group")), "idc");

        ArgumentTypeRegistry.registerArgumentType(new Identifier(MODID, "knowledge"), KnowledgeArgument.class, ConstantArgumentSerializer.of(KnowledgeArgument::knowledge));

        beholder.register();

        // KNOWLEDGE
        KNOWLEDGE.archive("1");
        KNOWLEDGE.archive("abc");

        // IDC Command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("idc")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(context -> {
                    // For versions since 1.20, please use the following, which is intended to avoid creating Text objects if no feedback is needed.
                    context.getSource().sendMessage(Text.literal("I truly don't care either"));

                    return 1;
                }).then(literal("knowledge").then(argument("player", EntityArgumentType.player())
                                .then(literal("list").executes(context -> {
                                    EntitySelector selector = context.getArgument("player", EntitySelector.class);
                                    LivingEntity player = selector.getPlayer(context.getSource());
                                    PlayerState playerState = ServerState.getPlayerState(player);
                                    List<String> knowledgeKnown = playerState.knownKnowledge();

                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Player knows "); sb.append(knowledgeKnown.size()); sb.append(" knowledge");
                                    knowledgeKnown.forEach((knowledge -> { sb.append("\n * "); sb.append(knowledge); }));

                                    context.getSource().sendMessage(Text.literal(sb.toString()));
                                    return 1;
                                }))
                                .then(literal("learn").then(argument("knowledge", new KnowledgeArgument()).executes(context -> {
                                    EntitySelector selector = context.getArgument("player", EntitySelector.class);
                                    LivingEntity player = selector.getPlayer(context.getSource());
                                    PlayerState playerState = ServerState.getPlayerState(player);
                                    Archive.Knowledge knowledge = context.getArgument("knowledge", Archive.Knowledge.class);

                                    if(playerState.learn(knowledge)) {
                                        context.getSource().sendMessage(Text.literal("Player learned " + knowledge.id()));
                                        ServerState.getServerState(player.getServer()).markDirty();
                                    } else { context.getSource().sendMessage(Text.literal("Failed, maybe the player already has this knowledge")); }

                                    return 1;
                                })))
                                .then(literal("forget").then(argument("knowledge", KnowledgeArgument.knowledge()).executes(context -> {
                                    EntitySelector selector = context.getArgument("player", EntitySelector.class);
                                    LivingEntity player = selector.getPlayer(context.getSource());
                                    PlayerState playerState = ServerState.getPlayerState(player);
                                    Archive.Knowledge knowledge = context.getArgument("knowledge", Archive.Knowledge.class);

                                    if(playerState.forget(knowledge)) {
                                        context.getSource().sendMessage(Text.literal("Player forgot " + knowledge.id()));
                                        ServerState.getServerState(player.getServer()).markDirty();
                                    } else { context.getSource().sendMessage(Text.literal("Failed, maybe the player didn't have this knowledge")); }

                                    return 1;
                                })))
                        ))));
    }
}
