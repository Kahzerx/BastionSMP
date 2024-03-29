package bastion.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.io.File;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.block.Block;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.level.ServerWorldProperties;


public class SBCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder = literal("sb")
				.then(literal("broken")
						.then(argument("item", ItemStackArgumentType.itemStack())
								.executes(ctx -> startThreadedShowSideBar(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item"), "broken"))))
				.then(literal("crafted")
						.then(argument("item", ItemStackArgumentType.itemStack())
								.executes(ctx -> startThreadedShowSideBar(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item"), "crafted"))))
				.then(literal("mined")
						.then(argument("item", ItemStackArgumentType.itemStack())
								.executes(ctx -> startThreadedShowSideBar(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item"), "mined"))))
				.then(literal("used")
						.then(argument("item", ItemStackArgumentType.itemStack())
								.executes(ctx -> startThreadedShowSideBar(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item"), "used"))))
				.then(literal("picked_up")
						.then(argument("item", ItemStackArgumentType.itemStack())
								.executes(ctx -> startThreadedShowSideBar(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item"), "picked_up"))))
				.then(literal("dropped")
						.then(argument("item", ItemStackArgumentType.itemStack())
								.executes(ctx -> startThreadedShowSideBar(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item"), "dropped"))))
				.then(literal("remove")
						.executes(ctx -> hideSidebar(ctx.getSource())));
		dispatcher.register(literalargumentbuilder);
	}

	private static int startThreadedShowSideBar(ServerCommandSource source, ItemStackArgument item, String type) {
		new Thread(() -> showSidebar(source, item, type)).start();
		return 1;
	}

	public static void showSidebar(ServerCommandSource source, ItemStackArgument item, String type) {
		Scoreboard scoreboard = source.getMinecraftServer().getScoreboard();
		Item minecraftItem = item.getItem();
		String objectiveName = type + "." + Item.getRawId(minecraftItem);
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(objectiveName);

		Entity entity = source.getEntity();
		Text text;

		if (scoreboardObjective != null) {
			if (scoreboard.getObjectiveForSlot(1) == scoreboardObjective) {
				text = new LiteralText("Ya se está mostrando ese scoreboard");
			} else {
				assert entity != null;
				text = new LiteralText(entity.getEntityName() + " ha seleccionado el scoreboard " + Formatting.GOLD + "[" + scoreboardObjective.getDisplayName().asString() + "]");
				scoreboard.setObjectiveSlot(1, scoreboardObjective);
			}
		} else {
			String criteriaName = "minecraft." + type + ":minecraft." + item.getItem().toString();
			String capitalize = type.substring(0, 1).toUpperCase() + type.substring(1);
			String displayName = capitalize + " " + minecraftItem.toString().replaceAll("_", " ");
			ScoreboardCriterion criteria = ScoreboardCriterion.createStatCriterion(criteriaName).get();

			scoreboard.addObjective(objectiveName, criteria, new LiteralText(displayName).formatted(Formatting.GOLD), criteria.getCriterionType());

			ScoreboardObjective newScoreboardObjective = scoreboardObjective = scoreboard.getNullableObjective(objectiveName);
			try {
				initialize(source, newScoreboardObjective, minecraftItem, type);
			} catch (Exception e) {
				scoreboard.removeObjective(newScoreboardObjective);
				text = new LiteralText("Ha ocurrido un error al momento de seleccionar un scoreboard, inténtelo de nuevo.").formatted(Formatting.RED);
				assert entity != null;
				source.getMinecraftServer().getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, entity.getUuid());
			}
			scoreboard.setObjectiveSlot(1, newScoreboardObjective);
			assert entity != null;
			assert scoreboardObjective != null;
			text = new LiteralText(entity.getEntityName() + " ha seleccionado el scoreboard " + Formatting.GOLD + "[" + scoreboardObjective.getDisplayName().asString() + "]");

		}
		assert entity != null;
		source.getMinecraftServer().getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, entity.getUuid());
	}

	public static int hideSidebar(ServerCommandSource source) {
		Scoreboard scoreboard = source.getMinecraftServer().getScoreboard();
		Entity entity = source.getEntity();
		Text text;

		if (scoreboard.getObjectiveForSlot(1) == null) {
			text = new LiteralText("No hay ningún scoreboard para remover");
			assert entity != null;
		} else {
			scoreboard.setObjectiveSlot(1, null);
			assert entity != null;
			text = new LiteralText(entity.getEntityName() + " ha eliminado el scoreboard");
		}
		source.getMinecraftServer().getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, entity.getUuid());
		return Command.SINGLE_SUCCESS;
	}

	public static void initialize(ServerCommandSource source, ScoreboardObjective scoreboardObjective, Item item, String type) {
		Scoreboard scoreboard = source.getMinecraftServer().getScoreboard();
		MinecraftServer server = source.getMinecraftServer();

		File file = new File(((ServerWorldProperties)server.getOverworld().getLevelProperties()).getLevelName(), "stats");
		File[] stats = file.listFiles();

		assert stats != null;
		for (File stat: stats) {
			String fileName = stat.getName();
			String uuidString = fileName.substring(0, fileName.lastIndexOf(".json"));

			UUID uuid = UUID.fromString(uuidString);
			ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

			Stat<?> finalStat = null;

			if (type.equalsIgnoreCase("broken")) {
				finalStat = Stats.BROKEN.getOrCreateStat(item);
			} else if (type.equalsIgnoreCase("crafted")) {
				finalStat = Stats.CRAFTED.getOrCreateStat(item);
			} else if (type.equalsIgnoreCase("mined")) {
				finalStat = Stats.MINED.getOrCreateStat(Block.getBlockFromItem(item));
			} else if (type.equalsIgnoreCase("used")) {
				finalStat = Stats.USED.getOrCreateStat(item);
			} else if (type.equalsIgnoreCase("picked_up")) {
				finalStat = Stats.PICKED_UP.getOrCreateStat(item);
			} else if (type.equalsIgnoreCase("dropped")) {
				finalStat = Stats.DROPPED.getOrCreateStat(item);
			}

			String playerName;
			int value;

			if (player != null) {
				value = player.getStatHandler().getStat(finalStat);
				playerName = player.getEntityName();
			} else {
				ServerStatHandler serverStatHandler = new ServerStatHandler(server, stat);
				value = serverStatHandler.getStat(finalStat);

				GameProfile gameProfile = server.getUserCache().getByUuid(uuid);

				if (gameProfile != null) {
					playerName = gameProfile.getName();
				} else {
					continue;
				}
			}

			if (value == 0) {
				continue;
			}
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(playerName, scoreboardObjective);
			scoreboardPlayerScore.setScore(value);
		}
	}
}