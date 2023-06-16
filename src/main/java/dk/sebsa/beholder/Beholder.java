package dk.sebsa.beholder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Supplier;

public class Beholder {
    private final String modId;
    private final Map<Item, ItemInfo> itemStash = new HashMap<>();
    private final Map<ItemGroup.Builder, String> itemGroupStash = new HashMap<>();

    public Beholder(String modId) {
        this.modId = modId;
    }

    public void register() {
        Map<String, List<ItemStack>> groupEntries = new HashMap<>();

        for(Item item : itemStash.keySet()) {
            ItemInfo info = itemStash.get(item);
            Registry.register(Registries.ITEM, new Identifier(modId, info.id), item);
            if(info.itemGroup != null) groupEntries.computeIfAbsent(info.itemGroup, k ->  new ArrayList<>())
                    .add(item.getDefaultStack());
        }

        for(ItemGroup.Builder builder : itemGroupStash.keySet()) {
            String id = itemGroupStash.get(builder);
            if(groupEntries.containsKey(id)) builder.entries((enabledFeatures, entries) -> entries.addAll(groupEntries.get(id)));
            Registry.register(Registries.ITEM_GROUP, new Identifier("idc", "id"), builder.build());
        }
    }

    public Item collectItem(Item item, String id) {
        itemStash.put(item, new ItemInfo(item, id, ""));
        return item;
    }

    public Item collectItem(Item item, String id, String itemGroup) {
        itemStash.put(item, new ItemInfo(item, id, itemGroup));
        return item;
    }

    public void collectItemGroup(ItemGroup.Builder unBuildedBuilder, String identifier) {
        itemGroupStash.put(unBuildedBuilder, identifier);
    }

    private record ItemInfo(Item item, String id, String itemGroup) { }
}
