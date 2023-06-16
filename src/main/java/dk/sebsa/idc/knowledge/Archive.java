package dk.sebsa.idc.knowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Archive {
    public Map<String, Knowledge> knowledge = new HashMap<>();

    public void archive(String id) {
        knowledge.put(id, new Knowledge(id));
    }

    public record Knowledge(String id) {}
}
