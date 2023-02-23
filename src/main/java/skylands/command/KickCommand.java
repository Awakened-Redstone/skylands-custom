package skylands.command;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import skylands.logic.Skylands;
import skylands.util.Texts;
import skylands.util.Worlds;

import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static skylands.command.utils.CommandUtils.node;
import static skylands.command.utils.CommandUtils.register;

public class KickCommand {

    static void init(CommandDispatcher<ServerCommandSource> dispatcher) {
        register(dispatcher, node().then(literal("kick").requires(Permissions.require("skylands.command.kick", true))
                .then(argument("player", player()).executes(context -> {
                    var player = context.getSource().getPlayer();
                    var kickedPlayer = EntityArgumentType.getPlayer(context, "player");
                    if (player != null && kickedPlayer != null) {
                        KickCommand.run(player, kickedPlayer);
                    }
                    return 1;
                }))
        ));
    }

    static void run(ServerPlayerEntity player, ServerPlayerEntity kicked) {
        Skylands.instance.islands.get(player).ifPresentOrElse(island -> {
            if (player.getName().getString().equals(kicked.getName().getString())) {
                player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.yourself"));
            } else {
                if (island.isMember(kicked)) {
                    player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.member"));
                } else {
                    Worlds.getIsland(kicked.getWorld()).ifPresent(isl -> {
                        if (isl.owner.uuid.equals(island.owner.uuid)) {
                            player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.success", map -> map.put("%player%", kicked.getName().getString())));

                            kicked.sendMessage(Texts.prefixed("message.skylands.kick_visitor.kick", map -> map.put("%owner%", player.getName().getString())));
                            kicked.sendMessage(Texts.prefixed("message.skylands.hub_visit"));
                            FabricDimensions.teleport(kicked, Skylands.instance.server.getOverworld(), new TeleportTarget(Skylands.instance.hub.pos, new Vec3d(0, 0, 0), 0, 0));
                        } else {
                            player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.fail", map -> map.put("%player%", kicked.getName().getString())));
                        }
                    });
                }
            }
        }, () -> player.sendMessage(Texts.prefixed("message.skylands.kick_visitor.no_island")));
    }
}
