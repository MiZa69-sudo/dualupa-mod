package ru.dualupa.menu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class DuaLupaMenuMod implements ClientModInitializer {

    public static String pendingServer = null;

    @Override
    public void onInitializeClient() {
        System.out.println("[DUA LUPA] ===========================");
        System.out.println("[DUA LUPA] Menu mod INITIALIZED");
        System.out.println("[DUA LUPA] ===========================");

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (pendingServer == null) return;
            final String target = pendingServer;
            pendingServer = null;
            System.out.println("[DUA LUPA] Player joined. Sending /server " + target);
            new Thread(() -> {
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                client.execute(() -> {
                    if (client.player != null && client.player.networkHandler != null) {
                        client.player.networkHandler.sendChatCommand("server " + target);
                    }
                });
            }, "DUA-LUPA-Switch").start();
        });
    }
}
