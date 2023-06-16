package dk.sebsa.idc.knowledge;

import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerState {
    private final Map<String, Boolean> knowledgeKnown = new HashMap<>();

    public void writeNbt(NbtCompound playerStateNbt) {
        NbtCompound knowledgeKnownNbt = new NbtCompound();
        knowledgeKnown.forEach(knowledgeKnownNbt::putBoolean);
        playerStateNbt.put("knowledgeKnown", knowledgeKnownNbt);
    }

    public void createFromNbt(NbtCompound playerStateNbt) {
        NbtCompound knowledgeKnownNbt = playerStateNbt.getCompound("knowledgeKnown");
        knowledgeKnownNbt.getKeys().forEach(key -> {
            knowledgeKnown.put(key, knowledgeKnownNbt.getBoolean(key));
        });
    }

    /**
     * Learns knowledeg
     * @param knowledge The knowledge to learn
     * @return True if the knowledge was learned, false otherwise (e.g. knowledge already known)
     */
    public boolean learn(Archive.Knowledge knowledge) {
        return !Boolean.TRUE.equals(knowledgeKnown.put(knowledge.id(), true));
    }

    /**
     * Forgets knowledge
     * @param knowledge The knowledge to learn
     * @return True if the knowledge was forgotten, false otherwise (e.g. didn't known knowledge in the first place)
     */
    public boolean forget(Archive.Knowledge knowledge) {
        return Boolean.TRUE.equals(knowledgeKnown.put(knowledge.id(), false));
    }

    /**
     * Gets all known knowledge from player state
     * @return A list of strings each string being the id of a knowledge known to the player
     */
    public List<String> knownKnowledge() {
        List<String> knowledgeStrings = new ArrayList<>();
        knowledgeKnown.forEach((knowledge, bool) -> {
            if(bool) knowledgeStrings.add(knowledge);
        });
        return knowledgeStrings;
    }

    /**
     * Checks wether a knowledge is known to player
     * @param knowledge The knowledge to check for
     * @return True if the player knows the knowledge, false otherwise
     */
    public boolean knowsKnowledge(Archive.Knowledge knowledge) {
        return Boolean.TRUE.equals(knowledgeKnown.get(knowledge.id()));
    }
}
