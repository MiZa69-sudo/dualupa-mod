package ru.dualupa.menu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

public class DuaLupaMenuMod implements ClientModInitializer {

    public static String pendingServer = null;

    @Override
    public void onInitializeClient() {
        System.out.println("[DUA LUPA] ===========================");
        System.out.println("[DUA LUPA] Menu mod INITIALIZED");
        System.out.println("[DUA LUPA] ===========================");

        // 🎯 Когда игрок заходит на прокси — отправляем /server <target>
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (pendingServer == null) return;
            final String target = pendingServer;
            pendingServer = null;

            System.out.println("[DUA LUPA] Player joined proxy. Target: " + target);

            new Thread(() -> {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {} // 🎯 ускорил с 3000
                client.execute(() -> {
                    if (client.player != null && client.player.networkHandler != null) {
                        System.out.println("[DUA LUPA] Sending: /server " + target);
                        client.player.networkHandler.sendChatCommand("server " + target);
                    }
                });
            }, "DUA-LUPA-Switch").start();
        });

        // 🎯 Когда игрок отключился от сервера — возвращаем в НАШЕ меню, не в ванильное
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            System.out.println("[DUA LUPA] Player disconnected — returning to custom menu");

            new Thread(() -> {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                client.execute(() -> {
                    try {
                        client.setScreen(new DuaLupaTitleScreen());
                        System.out.println("[DUA LUPA] Returned to DuaLupaTitleScreen");
                    } catch (Throwable t) {
                        System.err.println("[DUA LUPA] Failed to return: " + t.getMessage());
                    }
                });
            }, "DUA-LUPA-Return").start();
        });
    }
}
