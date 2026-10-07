package ru.dualupa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Если игрок каким-то образом попадает в ванильный список серверов —
 * мгновенно возвращаем его в наше меню DUA LUPA.
 * Это скрывает IP и не даёт добавлять свои сервера.
 */
@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    protected MultiplayerScreenMixin(Text title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void dualupa$redirectToTitle(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        // Возвращаем в наше меню, где работает кастомный фон и кнопки
        client.setScreen(new TitleScreen());
    }
}
