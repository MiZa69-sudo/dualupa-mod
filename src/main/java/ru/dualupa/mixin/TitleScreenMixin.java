package ru.dualupa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dualupa.menu.DuaLupaButton;

import java.util.Random;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) { super(title); }

    private static int[][] STAR_CACHE = null;
    private static final int STAR_COUNT = 140;
    private static final Random RNG = new Random(20261007L);

    private static void ensureStars(int w, int h) {
        if (STAR_CACHE != null && STAR_CACHE.length == STAR_COUNT) return;
        STAR_CACHE = new int[STAR_COUNT][4];
        for (int i = 0; i < STAR_COUNT; i++) {
            STAR_CACHE[i][0] = RNG.nextInt(Math.max(1, w));
            STAR_CACHE[i][1] = RNG.nextInt(Math.max(1, h));
            STAR_CACHE[i][2] = 2 + RNG.nextInt(2);
            STAR_CACHE[i][3] = RNG.nextInt(2000);
        }
    }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void dualupa$customBackground(DrawContext ctx, int mouseX, int mouseY,
                                          float delta, CallbackInfo ci) {
        int w = this.width;
        int h = this.height;

        int topColor    = 0xFF0A0318;
        int midColor    = 0xFF1B0538;
        int bottomColor = 0xFF3D0E6B;
        int half = h / 2;
        for (int y = 0; y < half; y++) {
            float t = (float) y / half;
            ctx.fill(0, y, w, y + 1, lerp(topColor, midColor, t));
        }
        for (int y = half; y < h; y++) {
            float t = (float) (y - half) / (h - half);
            ctx.fill(0, y, w, y + 1, lerp(midColor, bottomColor, t));
        }

        for (int y = 0; y < 80; y++) {
            int alpha = (int) (60 * (1f - y / 80f));
            int c = (alpha << 24) | 0x9633F9;
            ctx.fill(0, h - 80 + y, w, h - 79 + y, c);
        }

        ensureStars(w, h);
        long time = System.currentTimeMillis();
        for (int[] s : STAR_CACHE) {
            int x = s[0], y = s[1], size = s[2], phase = s[3];
            double twinkle = 0.6 + 0.4 * Math.sin((time / 700.0) + phase);
            int alpha = (int) (255 * twinkle);
            int c = (alpha << 24) | 0xD9A8FF;
            ctx.fill(x, y, x + size, y + size, c);
            if (size >= 3) {
                int halo = (alpha / 4) << 24 | 0x9633F9;
                ctx.fill(x - 1, y - 1, x + size + 1, y + size + 1, halo);
            }
        }

        ci.cancel();
    }

    private static int lerp(int a, int b, float t) {
        int aa = (a >>> 24) & 0xFF, ar = (a >>> 16) & 0xFF, ag = (a >>> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24) & 0xFF, br = (b >>> 16) & 0xFF, bg = (b >>> 8) & 0xFF, bb = b & 0xFF;
        int ra = (int) (aa + (ba - aa) * t);
        int rr = (int) (ar + (br - ar) * t);
        int rg = (int) (ag + (bg - ag) * t);
        int rb = (int) (ab + (bb - ab) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void dualupa$replaceButtons(CallbackInfo ci) {
        this.clearChildren();

        final Screen self = this;
        final MinecraftClient client = MinecraftClient.getInstance();

        int cx = this.width / 2;
        int y = this.height / 4 + 60;
        int bw = 240;
        int bh = 44;
        int gap = 10;

        // АНАРХИЯ — открывает список серверов (там уже наш сервер)
        this.addDrawableChild(new DuaLupaButton(
                cx - bw / 2, y, bw, bh,
                Text.literal("АНАРХИЯ"),
                b -> client.setScreen(new MultiplayerScreen(self)),
                DuaLupaButton.Style.RED, "⚔"
        ));

        // МИНИ-ИГРЫ — тоже список серверов
        this.addDrawableChild(new DuaLupaButton(
                cx - bw / 2, y + (bh + gap), bw, bh,
                Text.literal("МИНИ-ИГРЫ"),
                b -> client.setScreen(new MultiplayerScreen(self)),
                DuaLupaButton.Style.BLUE, "🏆"
        ));

        // RPG — пока неактивна
        DuaLupaButton rpg = new DuaLupaButton(
                cx - bw / 2, y + (bh + gap) * 2, bw, bh,
                Text.literal("RPG · СКОРО"),
                b -> {}, DuaLupaButton.Style.GRAY, "★"
        );
        rpg.active = false;
        this.addDrawableChild(rpg);

        int rowY = y + (bh + gap) * 3 + 20;
        int halfW = (bw - gap) / 2;

        // НАСТРОЙКИ — открывает список серверов тоже (пока безопасно)
        // В будущем заменим на реальный экран настроек
        this.addDrawableChild(new DuaLupaButton(
                cx - bw / 2, rowY, halfW, bh - 6,
                Text.literal("НАСТРОЙКИ"),
                b -> client.setScreen(new MultiplayerScreen(self)),
                DuaLupaButton.Style.PURPLE, "⚙"
        ));

        // ВЫХОД
        this.addDrawableChild(new DuaLupaButton(
                cx + gap / 2, rowY, halfW, bh - 6,
                Text.literal("ВЫХОД"),
                b -> client.scheduleStop(),
                DuaLupaButton.Style.PURPLE, "✕"
        ));
    }
}
