package skylands.event;

import eu.pb4.common.economy.api.CommonEconomy;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.MinecraftServer;
import skylands.SkylandsMain;
import skylands.logic.Island;
import skylands.logic.Skylands;

import java.util.Optional;

public class ServerStartEvent {

    public static void onStart(MinecraftServer server) {
        Skylands.init(server);
        CommonEconomy.register("skylands", Skylands.getInstance().economy.PROVIDER);
        registerPlaceholders();
    }

    //TODO: Add placeholders
    private static void registerPlaceholders() {
        Placeholders.register(SkylandsMain.id("locked"), (context, argument) -> {
            Optional<Island> island = Skylands.getInstance().islands.getByPlayer(context.player());
            return island.map(value -> PlaceholderResult.value(value.locked ? "Locked" : "Open")).orElseGet(() -> PlaceholderResult.invalid("Missing island!"));
        });

        Placeholders.register(SkylandsMain.id("size"), (context, argument) -> {
            Optional<Island> island = Skylands.getInstance().islands.getByPlayer(context.player());
            return island.map(value -> PlaceholderResult.value(String.valueOf(value.radius * 2 + 1))).orElseGet(() -> PlaceholderResult.invalid("Missing island!"));
        });
    }
}
