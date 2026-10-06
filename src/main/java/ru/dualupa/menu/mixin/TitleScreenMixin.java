package ru.dualupa.menu.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dualupa.menu.DuaLupaTitleScreen;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.currentScreen instanceof TitleScreen) {
            System.out.println("[DUA LUPA] Mixin intercepted TitleScreen.init");
            client.execute(() -> {
                try {
                    client.setScreen(new DuaLupaTitleScreen());
                    System.out.println("[DUA LUPA] ========== CUSTOM MENU SET ==========");
                } catch (Throwable t) {
                    System.err.println("[DUA LUPA] FAILED to set screen: " + t.getMessage());
                }
            });
        }
    }
}
