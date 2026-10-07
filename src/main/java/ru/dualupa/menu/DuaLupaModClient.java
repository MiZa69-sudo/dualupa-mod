package ru.dualupa;

import net.fabricmc.api.ClientModInitializer;

public class DuaLupaModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Всё делается через Mixin, тут пока пусто.
        System.out.println("[DUA LUPA] Client mod initialized");
    }
}
