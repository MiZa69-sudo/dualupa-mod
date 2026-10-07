package ru.dualupa.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class DuaLupaButton extends ButtonWidget {

    public enum Style {
        PURPLE(0x4A1F7A, 0x2E0D4F, 0xFF9633F9, 0xFFE6C4FF),
        RED   (0x6B1B1B, 0x3B0F0F, 0xFFFF4444, 0xFFFF9999),
        BLUE  (0x1B3B6B, 0x0F1F3B, 0xFF3BAAFF, 0xFFB0E0FF),
        GOLD  (0x6B5010, 0x3B2A05, 0xFFFFD700, 0xFFFFF3B0),
        GRAY  (0x2A2A38, 0x16161E, 0xFF6E6E7A, 0xFFB0B0C0);

        public final int bgLight, bgDark, glow, hi;
        Style(int l, int d, int g, int h) { bgLight=l; bgDark=d; glow=g; hi=h; }
    }

    private final Style style;
    private final String icon;

    public DuaLupaButton(int x, int y, int w, int h, Text msg, PressAction onPress, Style s, String icon) {
        super(x, y, w, h, msg, onPress, textSupplier -> textSupplier.get());
        this.style = s;
        this.icon = icon;
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int x = getX(), y = getY(), w = width, h = height;
        boolean hover = isHovered() && active;
        boolean pressed = hover && client.mouse.wasRightButtonClicked(); // не критично

        // ===== 1. СВЕЧЕНИЕ (за кнопкой, многослойное) =====
        if (hover && active) {
            long t = System.currentTimeMillis();
            float pulse = (float)(0.7 + 0.3 * Math.sin(t / 260.0));
            for (int i = 10; i > 0; i--) {
                int alpha = (int)(22 * pulse * (1 - i / 10.0f));
                int glowCol = (alpha << 24) | (style.glow & 0x00FFFFFF);
                ctx.fill(x - i, y - i, x + w + i, y + h + i, glowCol);
            }
        }

        // ===== 2. ТЕНЬ СНИЗУ =====
        ctx.fill(x + 2, y + h, x + w + 2, y + h + 4, 0x80000000);
        ctx.fill(x + 4, y + h + 4, x + w + 4, y + h + 6, 0x40000000);

        // ===== 3. ОСНОВНОЙ ГРАДИЕНТ =====
        int topC = style.bgLight;
        int botC = style.bgDark;
        if (hover && active) {
            topC = brighten(topC, 1.35f);
            botC = brighten(botC, 1.35f);
        }
        if (!active) {
            topC = 0xFF1A1A22;
            botC = 0xFF0E0E14;
        }

        int steps = 14;
        for (int i = 0; i < steps; i++) {
            float tt = i / (float)(steps - 1);
            int c = lerp(topC, botC, tt);
            int sy = y + i * h / steps;
            int ey = (i == steps - 1) ? y + h : y + (i + 1) * h / steps;
            ctx.fill(x, sy, x + w, ey, c);
        }

        // ===== 4. ВЕРХНИЙ БЛИК (2 строки) =====
        int hiCol = (style.hi & 0x00FFFFFF) | (active ? 0xAA000000 : 0x44000000);
        ctx.fill(x, y, x + w, y + 1, hiCol);
        ctx.fill(x, y + 1, x + w, y + 2, (hiCol & 0x00FFFFFF) | 0x44000000);

        // ===== 5. НИЖНЯЯ ТЕНЬ (внутренняя) =====
        ctx.fill(x, y + h - 1, x + w, y + h, 0x99000000);

        // ===== 6. БОКОВЫЕ АКЦЕНТЫ =====
        ctx.fill(x, y + 1, x + 1, y + h - 1, (style.hi & 0x00FFFFFF) | 0x22000000);
        ctx.fill(x + w - 1, y + 1, x + w, y + h - 1, 0x22000000);

        // ===== 7. ЛЁГКАЯ ПОДСВЕТКА ВНУТРИ при ховере =====
        if (hover && active) {
            ctx.fill(x + 1, y + 2, x + w - 1, y + 4, (style.glow & 0x00FFFFFF) | 0x33000000);
        }

        // ===== 8. ТЕКСТ =====
        int textColor = active ? 0xFFFFFFFF : 0xFF7A7A8A;
        int totalW = client.textRenderer.getWidth(icon) + 8 + client.textRenderer.getWidth(getMessage());
        int startX = x + (w - totalW) / 2;
        int textY = y + (h - 8) / 2;

        // Лёгкая тень текста
        ctx.drawText(client.textRenderer, icon, startX + 1, textY + 1, 0x60000000, false);
        ctx.drawText(client.textRenderer, getMessage(), startX + client.textRenderer.getWidth(icon) + 9, textY + 1, 0x60000000, false);
        // Сам текст
        ctx.drawText(client.textRenderer, icon, startX, textY, textColor, false);
        ctx.drawText(client.textRenderer, getMessage(), startX + client.textRenderer.getWidth(icon) + 8, textY, textColor, false);
    }

    private int brighten(int c, float f) {
        int a = (c >>> 24) & 0xFF;
        int r = Math.min(255, (int)(((c >>> 16) & 0xFF) * f));
        int g = Math.min(255, (int)(((c >>> 8) & 0xFF) * f));
        int b = Math.min(255, (int)((c & 0xFF) * f));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int lerp(int a, int b, float t) {
        int aa = (a >>> 24) & 0xFF, ar = (a >>> 16) & 0xFF, ag = (a >>> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24) & 0xFF, br = (b >>> 16) & 0xFF, bg = (b >>> 8) & 0xFF, bb = b & 0xFF;
        int ra = (int)(aa + (ba - aa) * t);
        int rr = (int)(ar + (br - ar) * t);
        int rg = (int)(ag + (bg - ag) * t);
        int rb = (int)(ab + (bb - ab) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }
}
