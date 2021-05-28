package me.bionicbeanie.mods;

import org.lwjgl.glfw.GLFW;

import me.bionicbeanie.mods.api.IFileStore;
import me.bionicbeanie.mods.api.IGui;
import me.bionicbeanie.mods.api.IViewHandler;
import me.bionicbeanie.mods.api.IPlayerLocator;
import me.bionicbeanie.mods.api.IScreenController;
import me.bionicbeanie.mods.gui.SaveCoordinatesGui;
import me.bionicbeanie.mods.gui.ScreenController;
import me.bionicbeanie.mods.gui.view.DefaultViewHandler;
import me.bionicbeanie.mods.gui.view.ListViewHandler;
import me.bionicbeanie.mods.impl.FileStore;
import me.bionicbeanie.mods.impl.PlayerLocator;
import me.bionicbeanie.mods.model.PlayerPosition;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class SaveCoordinatesClient implements ClientModInitializer {

    private static IPlayerLocator locator = new PlayerLocator();

    @Override
    public void onInitializeClient() {

        KeyBinding keyBinding = registerKeyBinding();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                IGui gui = CreateModGui(client);
                gui.showDefaultView();
            }
        });
    }

    private KeyBinding registerKeyBinding() {
        String translationKey = "key.savecoords.coords";
        String category = "category.savecoords.generic";
        int keyBind = GLFW.GLFW_KEY_H;

        KeyBinding keyBinding = new KeyBinding(translationKey, InputUtil.Type.KEYSYM, keyBind, category);
        
        return KeyBindingHelper.registerKeyBinding(keyBinding);
    }

    private IGui CreateModGui(MinecraftClient client) {
        IGui gui = new SaveCoordinatesGui();
        IFileStore fileStore = new FileStore(client.runDirectory.getAbsolutePath());
        IViewHandler<PlayerPosition> defaultHandler = new DefaultViewHandler(fileStore, locator, client, gui);
        IViewHandler<Void> listHandler = new ListViewHandler(fileStore, gui, client);
        IScreenController screenController = new ScreenController(client);

        gui.init(defaultHandler, listHandler, screenController);

        return gui;
    }
}
