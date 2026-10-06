package ru.dualupa.menu;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class DuaLupaTitleScreen extends Screen {

    private static final String SERVER_IP = "176.108.245.214";
    private static final int PORT = 25565;
    private static final int PURPLE = 0xFFB57EEC;
    private static final int PURPLE_DARK = 0xFF9633F9;
    private static final int GRAY = 0xFF888888;

    public DuaLupaTitleScreen() {
        super(Text.literal("DUA LUPA"));
    }

    @Override
    protected void init() {
        System.out.println("[DUA LUPA] Menu init, size " + this.width + "x" + this.height);

        int centerX = this.width / 2;
        int w = 220, h = 30;
        int y = this.height / 2 - 50;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("⚔  АНАРХИЯ"), btn -> {
            DuaLupaMenuMod.pendingServer = "anarchy";
            connect();
        }).dimensions(centerX - w / 2, y, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("🎮  МИНИ-ИГРЫ"), btn -> {
            DuaLupaMenuMod.pendingServer = "minigames";
            connect();
        }).dimensions(centerX - w / 2, y + 40, w, h).build());

        ButtonWidget rpg = ButtonWidget.builder(Text.literal("✨  RPG — СКОРО 😴"), b -> {})
            .dimensions(centerX - w / 2, y + 80, w, h).build();
        rpg.active = false;
        this.addDrawableChild(rpg);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("⚙ Настройки"), b -> {
            if (this.client != null) this.client.setScreen(new OptionsScreen(this, this.client.options));
        }).dimensions(centerX - w / 2 - 60, y + 140, 140, 25).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("❌ Выход"), b -> {
            if (this.client != null) this.client.scheduleStop();
        }).dimensions(centerX + w / 2 - 80, y + 140, 140, 25).build());
    }

    private void connect() {
        if (this.client == null) return;
        String addr = SERVER_IP + ":" + PORT;
        System.out.println("[DUA LUPA] Connecting to " + addr);
        ConnectScreen.connect(this, this.client,
            ServerAddress.parse(addr),
            new ServerInfo("DUA LUPA", addr, ServerInfo.ServerType.OTHER),
            false, null);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float d) {
        this.renderBackground(ctx, mx, my, d);
        int cx = this.width / 2;
        ctx.drawCenteredTextWithShadow(this.textRenderer, "DUA LUPA", cx, this.height / 2 - 140, PURPLE);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "✦ Выбери свой мир ✦", cx, this.height / 2 - 115, PURPLE_DARK);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "© 2026 DUA LUPA", cx, this.height - 30, GRAY);
        super.render(ctx, mx, my, d);
    }

    @Override public boolean shouldCloseOnEsc() { return false; }
    @Override public boolean shouldPause() { return false; }
}
