package me.bionicbeanie.mods.savecoords.impl;

import me.bionicbeanie.mods.savecoords.IDimensionAware;
import me.bionicbeanie.mods.savecoords.IFileStore;
import me.bionicbeanie.mods.savecoords.IKeyBinds;
import me.bionicbeanie.mods.savecoords.INetherCalculator;
import me.bionicbeanie.mods.savecoords.IPlayerLocator;
import net.minecraft.client.MinecraftClient;

public class Factory {

    public static IFileStore createFileStore(String baseDirectory) {
        return new FileStore(baseDirectory);
    }
    
    public static IPlayerLocator CreatePlayerLocator(MinecraftClient client, IDimensionAware dimensionAware) {
        return new PlayerLocator(client, dimensionAware);
    }
    
    public static IKeyBinds CreateKeyBinds(IFileStore fileStore) {
        return new KeyBinds(fileStore);
    }
    
    public static IDimensionAware CreateDimensionAware(MinecraftClient client) {
        return new DimensionAware(client);
    }
    
    public static INetherCalculator CreateNetherCalculator(IDimensionAware dimensionAware) {
        return new NetherCalculator(dimensionAware);
    }
}
