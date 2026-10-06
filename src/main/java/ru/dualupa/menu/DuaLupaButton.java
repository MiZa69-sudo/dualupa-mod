package ru.dualupa.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class DuaLupaButton extends ButtonWidget {

    public enum Style {
        RED(0xFF6B1B1B, 0xFFFF4444, 0xFFFF8888),
        BLUE(0xFF1B3B6B, 0xFF00CCFF, 0xFF55FFFF),
        GOLD(0xFF6B5010, 0xFFFFD700, 0xFFFFF0A0),
        GRAY(0xFF1E1E26, 0xFF555566, 0xFF888899),
        DARK(0xFF0E0E14, 0xFF333340, 0xFF555566),
        PURPLE(0xFF3D1B6B, 0xFF9633F9, 0xFFB57EEC);

        public final int bg;
        public final int border;
        public final int glow;

        Style(int bg, int border, int glow) {
            this.bg = bg;
            this.border = border;
            this.glow = glow;
        }
    }

    private final Style style;

    public DuaLupaButton(int x, int y, int width, int height, Text message, PressAction onPress, Style style) {
        super(x, y, width, height, message, onPress, (button) -> button.getMessage());
        this.style = style;
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();

        int bgColor;
        int borderColor;
        int textColor;
        int glowColor;

        if (!this.active) {
            bgColor = 0x33FFFFFF;
            borderColor = 0xFF444444;
            textColor = 0xFF666666;
            glowColor = 0x00000000;
        } else if (this.isHovered()) {
            bgColor = this.style.border | 0xFF000000;
            borderColor = 0xFFFFFFFF;
            textColor = 0xFF000000;
            glowColor = (this.style.glow & 0x00FFFFFF) | 0x80000000;
        } else {
            bgColor = this.style.bg | 0xFF000000;
            borderColor = this.style.border | 0xFF000000;
            textColor = 0xFFFFFFFF;
            glowColor = (this.style.border & 0x00FFFFFF) | 0x40000000;
        }

        int x = this.getX();
        int y = this.getY();
        int w = this.width;
        int h = this.height;

        // Внешнее свечение (если hover)
        if (this.isHovered() && this.active) {
            ctx.fill(x - 3, y - 3, x + w + 3, y + h + 3, glowColor);
            ctx.fill(x - 2, y - 2, x + w + 2, y + h + 2, glowColor);
            ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, glowColor);
        }

        // Основной фон (градиент сверху вниз)
        drawVerticalGradient(ctx, x, y, x + w, y + h, bgColor, darken(bgColor, 0.6f));

        // Верхняя светлая полоска (блик)
        if (this.active && !this.isHovered()) {
            ctx.fill(x + 1, y + 1, x + w - 1, y + 2, 0x33FFFFFF);
        }

        // Рамка
        ctx.fill(x, y, x + w, y + 1, borderColor);
        ctx.fill(x, y + h - 1, x + w, y + h, borderColor);
        ctx.fill(x, y, x + 1, y + h, borderColor);
        ctx.fill(x + w - 1, y, x + w, y + h, borderColor);

        // Текст по центру
        ctx.drawCenteredTextWithShadow(client.textRenderer, this.getMessage(),
            x + w / 2, y + (h - 8) / 2, textColor);
    }

    private void drawVerticalGradient(DrawContext ctx, int x1, int y1, int x2, int y2, int colorTop, int colorBottom) {
        int height = y2 - y1;
        if (height <= 0) return;
        for (int i = 0; i < height; i++) {
            float t = (float) i / height;
            int c = lerpColor(colorTop, colorBottom, t);
            ctx.fill(x1, y1 + i, x2, y1 + i + 1, c);
        }
    }

    private int lerpColor(int a, int b, float t) {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int ra = (int) (aa + (ba - aa) * t);
        int rr = (int) (ar + (br - ar) * t);
        int rg = (int) (ag + (bg - ag) * t);
        int rb = (int) (ab + (bb - ab) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }

    private int darken(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int) ((color & 0xFF) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
