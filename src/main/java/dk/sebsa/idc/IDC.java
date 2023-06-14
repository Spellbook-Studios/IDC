package dk.sebsa.idc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class IDC implements ModInitializer {
    public static final String MODID = "IDC";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info("IDC Works!");

        // IDC Command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("idc")
        .requires(source -> source.hasPermissionLevel(4))
        .executes(context -> {
            // For versions since 1.20, please use the following, which is intended to avoid creating Text objects if no feedback is needed.
            context.getSource().sendMessage(Text.literal("I truly don't care either"));

            return 1;
        }).then(literal("knowledge").executes(context -> {
            context.getSource().sendMessage(Text.literal("I don't know none????"));
            return 1;
        }))));
    }
}
