package bastion.utils;

import com.google.common.collect.Sets;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Set;

public class BastionUtils {
    public static String getDimensionWithColor(World world) {
        Identifier dimensionType = world.getRegistryKey().getValue();
        String msg = world.getDimension().toString();
        if (dimensionType.equals(World.OVERWORLD.getValue())) msg = Formatting.GREEN + "[Overworld]";
        else if (dimensionType.equals(World.NETHER.getValue())) msg = Formatting.RED + "[Nether]";
        else if (dimensionType.equals(World.END.getValue())) msg = Formatting.DARK_PURPLE + "[End]";
        return msg;
    }

    public static String getDimensionWithColor(ServerPlayerEntity player) {
        Identifier dimensionType = player.world.getRegistryKey().getValue();
        String msg = player.world.getDimension().toString();
        if (dimensionType.equals(World.OVERWORLD.getValue())) msg = Formatting.GREEN + "[Overworld]";
        else if (dimensionType.equals(World.NETHER.getValue())) msg = Formatting.RED + "[Nether]";
        else if (dimensionType.equals(World.END.getValue())) msg = Formatting.DARK_PURPLE + "[End]";
        return msg;
    }

    public static String formatCoords(double x, double y, double z){
        return Formatting.WHITE + String.format(" [x: %d, y: %d, z: %d]", (int) x, (int) y, (int) z);
    }

    public static Collection<String> getPlayers(ServerCommandSource source) {
        Set<String> players = Sets.newLinkedHashSet();
        players.addAll(source.getPlayerNames());
        return players;
    }
}
