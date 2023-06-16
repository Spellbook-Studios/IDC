package dk.sebsa.idc.knowledge;

import dk.sebsa.idc.IDC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerState extends PersistentState {
    public Map<UUID, PlayerState> playerStateMap = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // Putting the 'players' hashmap, into the 'nbt' which will be saved.
        NbtCompound playersNbtCompound = new NbtCompound();
        playerStateMap.forEach((UUID, playerState) -> {
            NbtCompound playerStateNbt = new NbtCompound();

            playerState.writeNbt(playerStateNbt);

            playersNbtCompound.put(UUID.toString(), playerStateNbt);
        });
        nbt.put("players", playersNbtCompound);

        return nbt;
    }

    public static ServerState createFromNbt(NbtCompound tag) {
        ServerState serverState = new ServerState();

        // Here we are basically reversing what we did in ''writeNbt'' and putting the data inside the tag back to our hashmap'
        NbtCompound playersTag = tag.getCompound("players");
        playersTag.getKeys().forEach(key -> {
            PlayerState playerState = new PlayerState();

            playerState.createFromNbt(playersTag.getCompound(key));

            UUID uuid = UUID.fromString(key);
            serverState.playerStateMap.put(uuid, playerState);
        });

        return serverState;
    }

    public static ServerState getServerState(MinecraftServer server) {
        // First we get the persistentStateManager for the OVERWORLD
        PersistentStateManager persistentStateManager = server
                .getWorld(World.OVERWORLD).getPersistentStateManager();

        // Calling this reads the file from the disk if it exists, or creates a new one and saves it to the disk
        return persistentStateManager.getOrCreate(
                ServerState::createFromNbt,
                ServerState::new,
                IDC.MODID);
    }

    public static PlayerState getPlayerState(LivingEntity player) {
        ServerState serverState = getServerState(player.getWorld().getServer());

        return serverState.playerStateMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerState());
    }
}
