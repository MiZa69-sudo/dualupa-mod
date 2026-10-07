package ru.dualupa.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class DuaLupaButton extends ButtonWidget {

    public enum Style {
        //   bg-light     bg-dark       border      glow          top-hi
        PURPLE(0xFF9633F9, 0xFF5B1FA8, 0xFF000000, 0xFFFF5FFF, 0xFFE6C4FF),
        RED   (0xFFFF4444, 0xFF8B1010, 0xFF000000, 0xFFFF5555, 0xFFFF9999),
        BLUE  (0xFF3BAAFF, 0xFF125C99, 0xFF000000, 0xFF55CCFF, 0xFFB0E0FF),
        GOLD  (0xFFFFD700, 0xFF9A7A00, 0xFF000000, 0xFFFFEE88, 0xFFFFF3B0),
        GRAY  (0xFF6E6E7A, 0xFF2A2A38, 0xFF000000, 0xFF9999AA, 0xFFB0B0C0),
        DARK  (0xFF1E1E26, 0xFF0E0E14, 0xFF000000, 0xFF555566, 0xFF888899);

        public final int bgLight;
        public final int bgDark;
        public final int border;
        public final int glow;
        public final int topHi;

        Style(int bgLight, int bgDark, int border, int glow, int topHi) {
            this.bgLight = bgLight;
            this.bgDark = bgDark;
            this.border = border;
            this.glow = glow;
            this.topHi = topHi;
        }
    }

    private final Style style;
    private final String iconText;

    public DuaLupaButton(int x, int y, int width, int height, Text message,
                         PressAction onPress, Style style, String iconText) {
        super(x, y, width, height, message, onPress, null);
        this.style = style;
        this.iconText = iconText;
    }

    public DuaLupaButton(int x, int y, int width, int height, Text message,
                         PressAction onPress, Style style) {
        this(x, y, width, height, message, onPress, style, null);
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int x = this.getX();
        int y = this.getY();
        int w = this.width;
        int h = this.height;

        boolean hover = this.isHovered() && this.active;

        // --- 1. ВНЕШНЕЕ СВЕЧЕНИЕ (ховер) ---
        if (hover) {
            int glowOuter = (this.style.glow & 0x00FFFFFF) | 0x40000000;
            int glowInner = (this.style.glow & 0x00FFFFFF) | 0x70000000;
            ctx.fill(x - 4, y - 4, x + w + 4, y + h + 4, glowOuter);
            ctx.fill(x - 3, y - 3, x + w + 3, y + h + 3, glowOuter);
            ctx.fill(x - 2, y - 2, x + w + 2, y + h + 2, glowInner);
        }

        // --- 2. ЧЁРНАЯ РАМКА (2px) ---
        int border = 0xFF000000;
        ctx.fill(x - 2, y - 2, x + w + 2, y - 1, border);
        ctx.fill(x - 2, y + h + 1, x + w + 2, y + h + 2, border);
        ctx.fill(x - 2, y - 2, x - 1, y + h + 2, border);
        ctx.fill(x + w + 1, y - 2, x + w + 2, y + h + 2, border);

        // --- 3. ЗАЛИВКА (градиент bgLight -> bgDark) ---
        int top    = hover ? brighten(this.style.bgLight, 1.15f) : this.style.bgLight;
        int bottom = hover ? brighten(this.style.bgDark,  1.15f) : this.style.bgDark;
        drawPixelGradient(ctx, x, y, x + w, y + h, top, bottom);

        // --- 4. ВЕРХНИЙ БЛИК ---
        int hi = hover ? brighten(this.style.topHi, 1.2f) : this.style.topHi;
        ctx.fill(x, y, x + w, y + 1, hi);
        ctx.fill(x, y + 1, x + w, y + 2, (hi & 0x00FFFFFF) | 0x55000000);

        // --- 5. НИЖНЯЯ ТЕНЬ ---
        int sh = darken(bottom, 0.5f);
        ctx.fill(x, y + h - 1, x + w, y + h, sh);

        // --- 6. БОКОВЫЕ АКЦЕНТЫ ---
        int sideLight = (this.style.topHi & 0x00FFFFFF) | 0x33FFFFFF;
        int sideDark  = 0x33000000;
        ctx.fill(x, y + 1, x + 1, y + h - 1, sideLight);
        ctx.fill(x + w - 1, y + 1, x + w, y + h - 1, sideDark);

        // --- 7. ТЕКСТ ---
        int textColor = this.active ? 0xFFFFFFFF : 0xFF8A8A9A;
        int textY = y + (h - 8) / 2;

        if (this.iconText != null && !this.iconText.isEmpty()) {
            int iconWidth = client.textRenderer.getWidth(this.iconText);
            int totalWidth = iconWidth + 6 + client.textRenderer.getWidth(this.getMessage());
            int startX = x + (w - totalWidth) / 2;

            ctx.drawTextWithShadow(client.textRenderer, this.iconText,
                    startX, textY, textColor);
            ctx.drawTextWithShadow(client.textRenderer, this.getMessage(),
                    startX + iconWidth + 6, textY, textColor);
        } else {
            ctx.drawCenteredTextWithShadow(client.textRenderer, this.getMessage(),
                    x + w / 2, textY, textColor);
        }

        // --- 8. INNER GLOW при ховере ---
        if (hover) {
            int inGlow = (this.style.glow & 0x00FFFFFF) | 0x33000000;
            ctx.fill(x, y + 1, x + w, y + 3, inGlow);
        }
    }

    private void drawPixelGradient(DrawContext ctx, int x1, int y1, int x2, int y2,
                                   int topColor, int bottomColor) {
        int height = y2 - y1;
        if (height <= 0) return;
        int steps = 8;
        int stepH = Math.max(1, height / steps);
        for (int s = 0; s < steps; s++) {
            float t = (float) s / (steps - 1);
            int c = lerpColor(topColor, bottomColor, t);
            int sy = y1 + s * stepH;
            int ey = (s == steps - 1) ? y2 : sy + stepH;
            ctx.fill(x1, sy, x2, ey, c);
        }
    }

    private int lerpColor(int a, int b, float t) {
        int aa = (a >>> 24) & 0xFF, ar = (a >>> 16) & 0xFF, ag = (a >>> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24) & 0xFF, br = (b >>> 16) & 0xFF, bg = (b >>> 8) & 0xFF, bb = b & 0xFF;
        int ra = (int) (aa + (ba - aa) * t);
        int rr = (int) (ar + (br - ar) * t);
        int rg = (int) (ag + (bg - ag) * t);
        int rb = (int) (ab + (bb - ab) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }

    private int brighten(int color, float f) {
        int a = (color >>> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >>> 16) & 0xFF) * f));
        int g = Math.min(255, (int) (((color >>> 8) & 0xFF) * f));
        int b = Math.min(255, (int) ((color & 0xFF) * f));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int darken(int color, float f) {
        return brighten(color, f);
    }
}
