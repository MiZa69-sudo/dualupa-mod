package ru.dualupa.menu;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class DuaLupaTitleScreen extends Screen {

    private static final String SERVER_IP = "176.108.245.214";
    private static final int PORT = 25565;

    public DuaLupaTitleScreen() {
        super(Text.literal("DUA LUPA"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int buttonWidth = 260;
        int buttonHeight = 36;
        int gap = 12;
        int startY = this.height / 2 - 30;

        // ⚔ АНАРХИЯ
        this.addDrawableChild(new DuaLupaButton(
            centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight,
            Text.literal("⚔  АНАРХИЯ"),
            btn -> {
                System.out.println("[DUA LUPA] Clicked ANARCHY");
                DuaLupaMenuMod.pendingServer = "anarchy";
                connectToProxy();
            },
            DuaLupaButton.Style.RED
        ));

        // 🎮 МИНИ-ИГРЫ
        this.addDrawableChild(new DuaLupaButton(
            centerX - buttonWidth / 2, startY + buttonHeight + gap, buttonWidth, buttonHeight,
            Text.literal("🎮  МИНИ-ИГРЫ"),
            btn -> {
                System.out.println("[DUA LUPA] Clicked MINIGAMES");
                DuaLupaMenuMod.pendingServer = "minigames";
                connectToProxy();
            },
            DuaLupaButton.Style.BLUE
        ));

        // ✨ RPG (заглушка)
        DuaLupaButton rpgBtn = new DuaLupaButton(
            centerX - buttonWidth / 2, startY + (buttonHeight + gap) * 2, buttonWidth, buttonHeight,
            Text.literal("✨  RPG — СКОРО 😴"),
            btn -> {},
            DuaLupaButton.Style.GOLD
        );
        rpgBtn.active = false;
        this.addDrawableChild(rpgBtn);

        // Нижние мелкие кнопки
        int smallBtnW = 120;
        int smallBtnH = 26;
        int smallY = startY + (buttonHeight + gap) * 3 + 15;

        this.addDrawableChild(new DuaLupaButton(
            centerX - smallBtnW - 5, smallY, smallBtnW, smallBtnH,
            Text.literal("⚙ Настройки"),
            btn -> {
                if (this.client != null) this.client.setScreen(new OptionsScreen(this, this.client.options));
            },
            DuaLupaButton.Style.GRAY
        ));

        this.addDrawableChild(new DuaLupaButton(
            centerX + 5, smallY, smallBtnW, smallBtnH,
            Text.literal("✕ Выход"),
            btn -> {
                if (this.client != null) this.client.scheduleStop();
            },
            DuaLupaButton.Style.DARK
        ));
    }

    private void connectToProxy() {
        if (this.client == null) return;
        String addr = SERVER_IP + ":" + PORT;
        System.out.println("[DUA LUPA] Connecting to " + addr);
        ConnectScreen.connect(this, this.client,
            ServerAddress.parse(addr),
            new ServerInfo("DUA LUPA", addr, ServerInfo.ServerType.OTHER),
            false, null);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 1. Ванильный фон (панорама)
        this.renderBackground(ctx, mouseX, mouseY, delta);

        // 2. Тёмный оверлей с фиолетовым оттенком
        ctx.fill(0, 0, this.width, this.height, 0xC00A0818);

        // 3. Дополнительный градиент сверху и снизу
        for (int i = 0; i < 150; i++) {
            int alpha = (int) (0x60 * (1.0f - i / 150.0f));
            int color = (alpha << 24) | 0x000000;
            ctx.fill(0, i, this.width, i + 1, color);
            ctx.fill(0, this.height - i - 1, this.width, this.height - i, color);
        }

        int cx = this.width / 2;

        // 4. Логотип DUA LUPA (большой, с свечением)
        drawBigLogo(ctx, cx, 70);

        // 5. Подзаголовок
        ctx.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§d✦ §7МЕСТО, ГДЕ ТЕБЯ НЕ ЗАБУДУТ §d✦"),
            cx, 145, 0xFFFFFFFF);

        // 6. Разделительная линия
        int lineY = 165;
        ctx.fill(cx - 200, lineY, cx + 200, lineY + 1, 0x889633F9);

        // 7. Онлайн внизу
        ctx.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§a● §fСервер онлайн   §8|   §fВерсия: §d1.21.4"),
            cx, this.height - 55, 0xFFFFFFFF);

        // 8. Подпись
        ctx.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§8© 2026 DUA LUPA · v1.0.0 BETA"),
            cx, this.height - 30, 0xFF555555);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawBigLogo(DrawContext ctx, int cx, int y) {
        // Текст с многослойным свечением
        String logo = "DUA LUPA";

        // Свечение (фиолетовые обводки)
        int[][] offsets = {
            {-2, 0}, {2, 0}, {0, -2}, {0, 2},
            {-1, -1}, {1, -1}, {-1, 1}, {1, 1}
        };
        for (int[] o : offsets) {
            for (int layer = 0; layer < 3; layer++) {
                int alpha = 0x60 - layer * 0x18;
                int color = (alpha << 24) | 0x9633F9;
                ctx.getMatrices().push();
                ctx.getMatrices().translate(cx + o[0] * (layer + 1), y + o[1] * (layer + 1), 0);
                ctx.getMatrices().scale(3.0f, 3.0f, 1.0f);
                ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(logo), 0, 0, color);
                ctx.getMatrices().pop();
            }
        }

        // Основной текст — белый с градиентом (имитация)
        ctx.getMatrices().push();
        ctx.getMatrices().translate(cx, y, 0);
        ctx.getMatrices().scale(3.0f, 3.0f, 1.0f);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(logo), 0, 0, 0xFFE6C4FF);
        ctx.getMatrices().pop();
    }

    @Override public boolean shouldCloseOnEsc() { return false; }
    @Override public boolean shouldPause() { return false; }
}
