package me.neophyte.mods.savecoords.gui.impl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import me.neophyte.mods.savecoords.IDimensionAware;
import me.neophyte.mods.savecoords.IFileStore;
import me.neophyte.mods.savecoords.IKeyBinds;
import me.neophyte.mods.savecoords.IModGui;
import me.neophyte.mods.savecoords.INetherCalculator;
import me.neophyte.mods.savecoords.IPlayerLocator;
import me.neophyte.mods.savecoords.impl.Factory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

//All of this works because single-threaded initialization!! Sed lyf :(
public class DIContainer {

    private static boolean initialized = false;
    private static IFileStore fileStore;
    private static GuiController guiController;
    private static IPlayerLocator playerLocator;
    private static IModGui modGui;
    private static IKeyBinds keyBinds;
    private static ConfigScreenFactory<Screen> modMenuScreenFactory;
    private static CurrentPositionPingOperation pingPositionOperation;
    private static IDimensionAware dimensionAware;
    private static INetherCalculator netherCalculator;

    public static IModGui getModGui() {
        initialize();
        return modGui;
    }

    public static IKeyBinds getKeyBinds() {
        initialize();
        return keyBinds;
    }

    public static ConfigScreenFactory<Screen> getModMenuScreenFactory() {
        initialize();

        if (modMenuScreenFactory == null) {
            modMenuScreenFactory = (parent) -> {
                ConfigViewHandler handler = new ConfigViewHandler();

                handler.onSaveButtonClick(() -> {
                    new SaveConfigsOperation(keyBinds, fileStore, handler::getState).call();
                    guiController.closeScreen();
                });

                handler.onBackButtonClick(() -> guiController.closeScreen());

                return handler.createView(keyBinds.getAllBinds());
            };
        }
        return modMenuScreenFactory;
    }

    public static Runnable getPingPositionOperation() {
        initialize();
        return () -> {
            try {
                pingPositionOperation.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public static boolean togglePingBehavior() {
        return pingPositionOperation.toggleEnabled();
    }

    private static void initialize() {
        if (!initialized) {
            initialized = true;

            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            fileStore = Factory.createFileStore(minecraftClient.runDirectory.getAbsolutePath());
            guiController = new GuiController(minecraftClient);
            dimensionAware = Factory.CreateDimensionAware(minecraftClient);
            playerLocator = Factory.CreatePlayerLocator(minecraftClient, dimensionAware);
            keyBinds = Factory.CreateKeyBinds(fileStore);
            netherCalculator = Factory.CreateNetherCalculator(dimensionAware);
            modGui = new SaveCoordinatesGui(fileStore, playerLocator, dimensionAware, keyBinds, guiController,
                    netherCalculator);

            pingPositionOperation = new CurrentPositionPingOperation(fileStore, () -> playerLocator.locate());
        }
    }
}
