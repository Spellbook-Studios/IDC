package dk.sebsa.idc.knowledge;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dk.sebsa.idc.IDC;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class KnowledgeArgument implements ArgumentType<Archive.Knowledge> {
    public UUID player = null;
    public static KnowledgeArgument knowledge() {
        return new KnowledgeArgument();
    }

    @Override
    public Archive.Knowledge parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor(); // The starting position of the cursor is at the beginning of the argument.
        if (!reader.canRead()) {
            reader.skip();
        }
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }

        Archive.Knowledge k = IDC.KNOWLEDGE.knowledge.get(reader.getString().substring(argBeginning, reader.getCursor()));
        if (k != null) return k;
        else throw new SimpleCommandExceptionType(Text.literal("Unknown knowledge")).createWithContext(reader);
    }
}
