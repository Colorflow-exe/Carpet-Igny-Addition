package com.liuyue.igny.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liuyue.igny.IGNYServer;
import com.liuyue.igny.network.packet.PacketUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomItemMaxStackSizeDataManager {
    //#if MC >= 12006
    public static final String JSON_FILE_NAME = "custom_item_max_stack_size.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, Integer> customStacks = new HashMap<>();
    private static MinecraftServer currentServer;

    public static void setServer(MinecraftServer server) {
        currentServer = server;
        customStacks.clear();
        load();
    }

    public static void load() {
        if (!getJsonPath().toFile().exists()) return;
        try (Reader reader = new FileReader(getJsonPath().toFile())) {
            customStacks = GSON.fromJson(reader, new TypeToken<Map<String, Integer>>(){}.getType());
        } catch (IOException e) {
            IGNYServer.LOGGER.error("Failed to load custom item stack config", e);
            customStacks.clear();
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(getJsonPath().toFile())) {
            GSON.toJson(customStacks, writer);
        } catch (IOException e) {
            IGNYServer.LOGGER.error("Failed to save custom item stack config", e);
        }
    }

    public static Map<String, Integer> getCustomStacks() {
        return Collections.unmodifiableMap(customStacks);
    }

    private static Path getJsonPath() {
        if (currentServer == null) {
            throw new IllegalStateException("Server not bound yet!");
        }
        return currentServer.getWorldPath(LevelResource.ROOT)
                .resolve(IGNYServer.MOD_ID)
                .resolve(JSON_FILE_NAME);
    }

    public static void set(Item item, int count) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        customStacks.put(id.toString(), count);
        currentServer.getPlayerList().getPlayers().forEach(PacketUtil::sendCustomStackSizeToClient);
    }

    public static void clientUpdateData(Map<String, Integer> newData) {
        customStacks = new HashMap<>(newData);
    }

    public static void remove(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        customStacks.remove(id.toString());
        save();
    }

    public static void clear() {
        customStacks.clear();
        save();
    }

    public static boolean hasCustomStack(Item item) {
        return customStacks.containsKey(BuiltInRegistries.ITEM.getKey(item).toString());
    }

    public static int getCustomStackSize(Item item) {
        return customStacks.getOrDefault(BuiltInRegistries.ITEM.getKey(item).toString(), item.getDefaultMaxStackSize());
    }
    //#endif
}