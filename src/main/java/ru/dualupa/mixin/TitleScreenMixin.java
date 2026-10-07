package ru.dualupa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dualupa.DuaLupaModClient;
import ru.dualupa.menu.DuaLupaButton;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) { super(title); }

    private static final String SERVER_IP = "176.108.245.214";
    private static final int SERVER_PORT = 25565;
    private static final String SERVER_NAME = "DUA LUPA";

    // ===== ФОН =====
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void dualupa$renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int w = this.width;
        int h = this.height;
        long time = System.currentTimeMillis();

        // 1. Градиент — 40 полос
        int topC = 0xFF0A0318;
        int midC = 0xFF1B0538;
        int botC = 0xFF3D0E6B;
        int half = h / 2;
        int STRIPES = 40;
        int stepH = Math.max(1, h / STRIPES);
        for (int i = 0; i < STRIPES; i++) {
            float t = i / (float)(STRIPES - 1);
            int c = t < 0.5f
                    ? lerp(topC, midC, t * 2)
                    : lerp(midC, botC, (t - 0.5f) * 2);
            int sy = i * stepH;
            int ey = (i == STRIPES - 1) ? h : sy + stepH;
            ctx.fill(0, sy, w, ey, c);
        }

        // 2. Орб 1 (большое свечение, медленно летает)
        double a1 = time / 6000.0;
        int ox1 = (int)(w * 0.5 + Math.cos(a1) * w * 0.35);
        int oy1 = (int)(h * 0.5 + Math.sin(a1) * h * 0.25);
        drawGlowCircle(ctx, ox1, oy1, 140, 0x0BA855F7);

        // 3. Орб 2
        double a2 = time / 8000.0 + Math.PI;
        int ox2 = (int)(w * 0.5 + Math.cos(a2) * w * 0.35);
        int oy2 = (int)(h * 0.5 + Math.sin(a2) * h * 0.25);
        drawGlowCircle(ctx, ox2, oy2, 120, 0x0AC026D3);

        // 4. Частицы — 80 маленьких точек
        for (int i = 0; i < 80; i++) {
            long seed = (long)i * 928371L;
            double speed = 0.0006 + (seed % 11) * 0.00005;
            double px = ((seed % w) + (time * speed * w * 0.01)) % w;
            double py = (((seed / 7) % h) - (time * speed * h * 0.005)) % h;
            if (py < 0) py += h;
            int alpha = (int)(60 + 80 * Math.abs(Math.sin(time / 900.0 + i)));
            int c = (alpha << 24) | 0xD9A8FF;
            ctx.fill((int)px, (int)py, (int)px + 2, (int)py + 2, c);
        }

        // 5. Тонкая светлая горизонтальная линия по центру
        int centerY = h / 2;
        ctx.fill(0, centerY, w, centerY + 1, 0x15FFFFFF);

        ci.cancel();
    }

    /** Псевдо-размытое свечение через уменьшающиеся круги */
    private void drawGlowCircle(DrawContext ctx, int cx, int cy, int radius, int color) {
        int steps = 12;
        for (int i = steps; i > 0; i--) {
            int r = radius * i / steps;
            int a = ((color >>> 24) & 0xFF) * (steps - i + 1) / steps;
            int c = (a << 24) | (color & 0x00FFFFFF);
            drawCircleApprox(ctx, cx, cy, r, c);
        }
    }

    /** Аппроксимация круга через горизонтальные полосы */
    private void drawCircleApprox(DrawContext ctx, int cx, int cy, int r, int color) {
        int step = Math.max(2, r / 20);
        for (int y = -r; y <= r; y += step) {
            int dx = (int)Math.sqrt(r * r - y * y);
            ctx.fill(cx - dx, cy + y, cx + dx, cy + y + step, color);
        }
    }

    private static int lerp(int a, int b, float t) {
        int aa = (a >>> 24) & 0xFF, ar = (a >>> 16) & 0xFF, ag = (a >>> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24) & 0xFF, br = (b >>> 16) & 0xFF, bg = (b >>> 8) & 0xFF, bb = b & 0xFF;
        int ra = (int)(aa + (ba - aa) * t);
        int rr = (int)(ar + (br - ar) * t);
        int rg = (int)(ag + (bg - ag) * t);
        int rb = (int)(ab + (bb - ab) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }

    // ===== ЛОГИКА КНОПОК =====
    private void connect(String targetServer) {
        MinecraftClient client = MinecraftClient.getInstance();
        ServerAddress addr = ServerAddress.parse(SERVER_IP + ":" + SERVER_PORT);
        ServerInfo info = new ServerInfo(SERVER_NAME, SERVER_IP + ":" + SERVER_PORT, ServerInfo.ServerType.OTHER);
        if (targetServer != null) DuaLupaModClient.pendingServer = targetServer;
        ConnectScreen.connect(this, client, addr, info, false, null);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void dualupa$init(CallbackInfo ci) {
        this.clearChildren();

        MinecraftClient client = MinecraftClient.getInstance();
        int cx = this.width / 2;
        int y = this.height / 4 + 60;
        int bw = 260;
        int bh = 46;
        int gap = 12;

        // ⚔ АНАРХИЯ
        this.addDrawableChild(new DuaLupaButton(
                cx - bw/2, y, bw, bh,
                Text.literal("АНАРХИЯ"),
                b -> connect(null),
                DuaLupaButton.Style.RED, "⚔ "
        ));

        // 🏆 МИНИ-ИГРЫ
        this.addDrawableChild(new DuaLupaButton(
                cx - bw/2, y + (bh + gap), bw, bh,
                Text.literal("МИНИ-ИГРЫ"),
                b -> connect("minigames"),
                DuaLupaButton.Style.BLUE, "🏆 "
        ));

        // ⭐ RPG
        DuaLupaButton rpg = new DuaLupaButton(
                cx - bw/2, y + (bh + gap) * 2, bw, bh,
                Text.literal("RPG · СКОРО"),
                b -> {}, DuaLupaButton.Style.GRAY, "★ "
        );
        rpg.active = false;
        this.addDrawableChild(rpg);

        // Настройки + Выход
        int rowY = y + (bh + gap) * 3 + 24;
        int halfW = (bw - gap) / 2;

        this.addDrawableChild(new DuaLupaButton(
                cx - bw/2, rowY, halfW, bh - 6,
                Text.literal("НАСТРОЙКИ"),
                b -> {
                    try {
                        client.setScreen(new net.minecraft.client.gui.screen.option.OptionsScreen(
                                (Screen)(Object)this, client.options));
                    } catch (Throwable ignored) {}
                },
                DuaLupaButton.Style.PURPLE, "⚙ "
        ));

        this.addDrawableChild(new DuaLupaButton(
                cx + gap/2, rowY, halfW, bh - 6,
                Text.literal("ВЫХОД"),
                b -> client.scheduleStop(),
                DuaLupaButton.Style.PURPLE, "✕ "
        ));
    }
}
