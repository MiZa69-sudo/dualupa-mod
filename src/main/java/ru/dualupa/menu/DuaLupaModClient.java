package ru.dualupa;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class DuaLupaModClient implements ClientModInitializer {

    /** Имя сервера на Velocity, к которому нужно перейти после подключения. */
    public static String pendingServer = null;
    private static int waitTicks = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pendingServer == null) return;
            if (client.player == null || client.getNetworkHandler() == null) {
                waitTicks = 0;
                return;
            }
            // ждём ~1 секунду, чтобы игрок точно вошёл в мир
            waitTicks++;
            if (waitTicks < 20) return;
            try {
                client.getNetworkHandler().sendChatCommand("server " + pendingServer);
            } catch (Throwable ignored) {}
            pendingServer = null;
            waitTicks = 0;
        });
    }
}
