package com.alrex.parcool.server.limitation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.info.CompiledLimitation;
import com.alrex.parcool.common.network.LimitationPacket;
import com.alrex.parcool.config.ParCoolConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class LimitationRegistry {
    public static final Limitation.ID GLOBAL_ID = new Limitation.ID(ParCool.MOD_ID, "global");
    public static final Limitation.ID INDIVIDUAL_ID = new Limitation.ID(ParCool.MOD_ID, "individual");
    private static final LevelResource SERVER_CONFIG = new LevelResource("serverconfig");
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();

    public LimitationRegistry() {
    }

    @Nullable
    private Path limitationFolderRootPath;
    private final SortedMap<UUID, SortedMap<Limitation.ID, Limitation>> loaded = new TreeMap<>();
    private final SortedSet<Limitation.ID> registeredID = new TreeSet<>();
    @Nullable
    private Limitation globalLimitation;

    private SortedMap<Limitation.ID, Limitation> getLimitationMapOf(UUID playerID) {
        SortedMap<Limitation.ID, Limitation> map = loaded.get(playerID);
        if (map == null) {
            map = load(playerID);
        }
        return map;
    }

    private Collection<Limitation> getLimitationsOf(UUID playerID) {
        return getLimitationMapOf(playerID).values();
    }

    public CompiledLimitation getLimitationSet(UUID playerID) {
        return CompiledLimitation.compile(getLimitationsOf(playerID));
    }

    public Limitation createLimitationOf(UUID playerID, Limitation.ID id) {
        Limitation limitation = getLimitationOf(playerID, id);
        if (limitation != null) return limitation;
        limitation = Limitation.newEmptyInstance(id);
        registeredID.add(id);
        getLimitationMapOf(playerID).put(id, limitation);
        return limitation;
    }

    public boolean delete(Limitation.ID id) {
        if (limitationFolderRootPath == null) return false;
        for (SortedMap<Limitation.ID, Limitation> limitationMap : loaded.values()) {
            limitationMap.remove(id);
        }
        try {
            FileUtils.deleteDirectory(getFolderPath(id).toFile());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public Limitation getGlobalLimitation() {
        return globalLimitation;
    }

    public Collection<Limitation.ID> getRegisteredIDs() {
        return registeredID;
    }

    @Nullable
    public Limitation getLimitationOf(UUID playerID, Limitation.ID id) {
        if (id.equals(GLOBAL_ID)) {
            return globalLimitation;
        }
        return getLimitationMapOf(playerID).get(id);
    }

    public void updateServerLimitation(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        var limitation = CompiledLimitation.compile(getLimitationsOf(player.getUUID()));
        parkourability.getActionInfo().setServerLimitation(limitation);
        ParCool.CONNECTION.send(PacketDistributor.ALL.noArg(), new LimitationPacket(player.getUUID(), true, limitation));
    }

    public SortedMap<Limitation.ID, Limitation> load(UUID playerID) {
        if (limitationFolderRootPath == null) {
            throw new IllegalStateException(String.format("When loading Limitation Player:%s, Initialization is not completed yet", playerID));
        }
        File limitationFolder = limitationFolderRootPath.toFile();
        File[] directories = limitationFolder.listFiles(File::isDirectory);
        if (directories == null) {
            LOGGER.error("Cannot get Limitation folders");
            return null;
        }
        SortedMap<Limitation.ID, Limitation> playerData = loaded.computeIfAbsent(playerID, k -> new TreeMap<>());
        for (File dir : directories) {
            File[] limitationGroups = dir.listFiles(File::isDirectory);
            if (limitationGroups == null) {
                LOGGER.error("Cannot get Limitation folders of '{}'", dir.getName());
                continue;
            }
            for (File limitationGroup : limitationGroups) {
                File[] limitationFiles = limitationGroup.listFiles((file) -> file.isFile() && file.canRead() && file.getName().endsWith(".json"));
                if (limitationFiles == null) {
                    LOGGER.error("Cannot get Limitation files of '{}'", dir.getName());
                    continue;
                }
                Limitation.ID limitationID = new Limitation.ID(dir.getName(), limitationGroup.getName());
                registeredID.add(limitationID);
                for (File limitationFile : limitationFiles) {
                    String limitationFilename = limitationFile.getName();
                    String uuidString = limitationFilename.substring(0, limitationFilename.length() - ".json".length());
                    UUID playerUUID = UUID.fromString(uuidString);
                    if (!playerUUID.equals(playerID)) {
                        continue;
                    }
                    try (var reader = new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(limitationFile)
                            ),
                            StandardCharsets.UTF_8
                    )) {
                        var json = JsonParser.parseReader(reader);
                        if (json instanceof JsonObject object) {
                            var limitation = Limitation.readFrom(limitationID, object, ParCool.getActionRegistry(), ParCool.getStaminaTypeRegistry());
                            playerData.put(limitation.getID(), limitation);
                        } else {
                            throw new IOException("Root object of limitation must be Json Object");
                        }
                    } catch (FileNotFoundException e) {
                        LOGGER.error("Could not read '{}', skipped.", limitationFile.getAbsolutePath());
                    } catch (IOException e) {
                        LOGGER.error("Failed to read '{}':{}", limitationFile.getAbsolutePath(), e.getMessage());
                    }
                }
            }
        }
        LOGGER.info("Limitation of {} was loaded", playerID);
        return playerData;
    }

    public void unload(UUID playerID) {
        if (limitationFolderRootPath == null) {
            throw new IllegalStateException(String.format("When loading Limitation Player:%s, Initialization is not completed yet", playerID));
        }
        SortedMap<Limitation.ID, Limitation> map = loaded.remove(playerID);
        if (map == null) {
            LOGGER.warn("Limitation entry is not loaded for UUID:{}. Skipped.", playerID);
            return;
        }
        for (Limitation limitation : map.values()) {
            File limitationFile = getActualFilePath(playerID, limitation.getID()).toFile();
            if (limitationFile.getParentFile().exists() || limitationFile.getParentFile().mkdirs()) {
                saveToFile(limitation, limitationFile);
            } else {
                LOGGER.error("Failed to save limitation:{} for {}", limitation.getID(), playerID);
            }
        }
        LOGGER.info("Limitation of {} was unloaded", playerID);
    }

    public void saveToFile(Limitation limitation, File file) {
        try (JsonWriter writer =
                     GSON.newJsonWriter(
                             new OutputStreamWriter(
                                     new BufferedOutputStream(
                                             Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                                     ),
                                     StandardCharsets.UTF_8
                             )
                     )
        ) {
            GSON.toJson(limitation.save(), writer);
        } catch (IOException e) {
            LOGGER.error("IOException during saving limitation : {}", e.getMessage());
        }
    }

    public void save() {
        for (Map.Entry<UUID, SortedMap<Limitation.ID, Limitation>> limitationEntry : loaded.entrySet()) {
            UUID playerID = limitationEntry.getKey();
            for (Limitation limitation : limitationEntry.getValue().values()) {
                File limitationFile = getFolderPath(limitation.getID())
                        .resolve(playerID.toString() + ".json").toFile();
                if (limitationFile.getParentFile().exists() || limitationFile.getParentFile().mkdirs()) {
                    saveToFile(limitation, limitationFile);
                } else {
                    LOGGER.error("Failed to save limitation:{} for {}", limitation.getID(), playerID);
                }
            }
        }
    }

    private Path getFolderPath(Limitation.ID id) {
        return limitationFolderRootPath
                .resolve(id.group())
                .resolve(id.name());
    }

    @Nullable
    public Path getActualFilePath(UUID playerID, Limitation.ID id) {
        Path folderPath = getFolderPath(id);
        return folderPath.resolve(playerID + ".json");
    }

    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event) {
        Path configPath = getServerConfigPath(event.getServer());
        var limitationFolderRootPath = configPath.resolve("parcool").resolve("limitations");
        globalLimitation = Limitation.readFromConfig(ParCoolConfig.getServerConfigLimitation(), ParCool.getActionRegistry(), ParCool.getStaminaTypeRegistry());
        File limitationFolder = limitationFolderRootPath.toFile();
        if (!limitationFolder.exists()) {
            limitationFolder.mkdirs();
        }
        this.limitationFolderRootPath = limitationFolderRootPath;
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        save();
    }

    private static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVER_CONFIG);
        net.minecraftforge.fml.loading.FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
        return serverConfig;
    }
}
