package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.ChunkDataCache;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.actions.CityClaimAction;
import fr.openmc.core.features.city.actions.CityCreateAction;
import fr.openmc.core.features.city.actions.CityUnclaimAction;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.hooks.WorldGuardHook;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.chunk.ChunkInfo;
import fr.openmc.core.utils.world.chunk.ChunkPos;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CityChunkMenu extends Menu {
    public static final Map<String, ChunkDataCache> CHUNK_CACHE = new ConcurrentHashMap<>();

    private final Player player;
    private final int playerChunkX, playerChunkZ, startX, startZ;
    private final City playerCity;
    private final UUID playerCityUUID;
    private final boolean hasPermissionClaim, hasFreeClaimAvailable;
    private final int freeClaims;
    private final double price;
    private final int aywenite;
    private Map<ChunkPos, ChunkInfo> chunkInfoMap;

    public CityChunkMenu(Player owner) {
        super(owner);
        this.player = owner;

        this.playerChunkX = player.getLocation().getChunk().getX();
        this.playerChunkZ = player.getLocation().getChunk().getZ();
        this.startX = playerChunkX - 4;
        this.startZ = playerChunkZ - 2;
        this.playerCity = CityManager.getPlayerCity(player.getUniqueId());

        boolean tempHasPermissionClaim = false;
        boolean tempHasFreeClaimAvailable = false;
        int tempFreeClaims = 0;
        double tempPrice = 0;
        int tempAywenite = 0;
        UUID tempPlayerCityUUID = null;

        if (playerCity != null) {
            tempHasPermissionClaim = playerCity.hasPermission(player.getUniqueId(), CityPermission.CLAIM);
            tempPlayerCityUUID = playerCity.getUniqueId();

            int nbChunk = playerCity.getChunks().size();
            tempPrice = CityClaimAction.calculatePrice(nbChunk);
            tempAywenite = CityClaimAction.calculateAywenite(nbChunk);

            tempFreeClaims = playerCity.getFreeClaims();
            tempHasFreeClaimAvailable = tempFreeClaims > 0;
        }

        this.playerCityUUID = tempPlayerCityUUID;
        this.hasPermissionClaim = tempHasPermissionClaim;
        this.hasFreeClaimAvailable = tempHasFreeClaimAvailable;
        this.freeClaims = tempFreeClaims;
        this.price = tempPrice;
        this.aywenite = tempAywenite;

        loadChunkData();
    }

    private void loadChunkData() {
        String cacheKey = player.getWorld().getName() + ":" + startX + "," + startZ;
        ChunkDataCache cache = CHUNK_CACHE.get(cacheKey);

        if (cache != null && !cache.isExpired()) {
            this.chunkInfoMap = cache.chunkInfoMap;
            return;
        }

        Map<ChunkPos, ChunkInfo> newChunkInfoMap = new ConcurrentHashMap<>();
        this.chunkInfoMap = newChunkInfoMap;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 9; col++) {
                    int chunkX = startX + col;
                    int chunkZ = startZ + row;
                    ChunkPos pos = new ChunkPos(chunkX, chunkZ);

                    City city = CityManager.getCityFromChunk(chunkX, chunkZ);
                    if (city != null) {
                        newChunkInfoMap.put(pos, new ChunkInfo(city, false));
                    }
                }
            }

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 9; col++) {
                        int chunkX = startX + col;
                        int chunkZ = startZ + row;
                        ChunkPos pos = new ChunkPos(chunkX, chunkZ);

                        if (!newChunkInfoMap.containsKey(pos)) {
                            Chunk chunk = player.getWorld().getChunkAt(chunkX, chunkZ);
                            boolean isProtected = WorldGuardHook.doesChunkContainWGRegion(chunk);
                            if (isProtected) {
                                newChunkInfoMap.put(pos, new ChunkInfo(null, true));
                            } else {
                                newChunkInfoMap.put(pos, new ChunkInfo(null, false));
                            }
                        }
                    }
                }

                CHUNK_CACHE.put(cacheKey, new ChunkDataCache(newChunkInfoMap));

                Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), this::open);
                });
            });
        });
    }

    @Override
    public @NotNull Component getName() {
	    return TranslationManager.translation("feature.city.menus.chunks.name");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        // empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        long startTime = System.currentTimeMillis();

        addNavigationButtons(inventory);

        if (chunkInfoMap == null || chunkInfoMap.isEmpty()) {
            inventory.put(22, new ItemBuilder(this, Material.CLOCK, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("feature.city.menus.chunks.loading.title"));
                itemMeta.lore(TranslationManager.translationLore("feature.city.menus.chunks.loading.lore"));
            }));
            return inventory;
        }

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int chunkX = startX + col;
                int chunkZ = startZ + row;
                ChunkPos pos = new ChunkPos(chunkX, chunkZ);
                ChunkInfo info = chunkInfoMap.getOrDefault(pos, new ChunkInfo(null, false));

                int slotIndex = row * 9 + col;
                inventory.put(slotIndex, createChunkItem(chunkX, chunkZ, info));
            }
        }

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    private void addNavigationButtons(Map<Integer, ItemBuilder> inventory) {
        if (playerCity != null) {
            inventory.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
                itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
                itemMeta.lore(List.of(TranslationManager.translation("messages.menus.back_lore")));
            }, true));

            if (hasFreeClaimAvailable) {
                inventory.put(49, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
	                itemMeta.displayName(TranslationManager.translation("feature.city.menus.chunks.free_claim.title"));
                    itemMeta.lore(TranslationManager.translationLore(
                            "feature.city.menus.chunks.free_claim.lore",
                            Component.text(freeClaims).color(NamedTextColor.GOLD)
                    ));
                }));
            }
        }

        inventory.put(53, new ItemBuilder(this, Material.MAP, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.chunks.refresh.title"));
	        itemMeta.lore(TranslationManager.translationLore("feature.city.menus.chunks.refresh.lore"));
        }).setOnClick(event -> {
            String refreshCacheKey = player.getWorld().getName() + ":" + startX + "," + startZ;
            CHUNK_CACHE.remove(refreshCacheKey);

            CityChunkMenu newMenu = new CityChunkMenu(player);
            newMenu.open();
        }));
    }

    private ItemBuilder createChunkItem(int chunkX, int chunkZ, ChunkInfo info) {
        Material material = Material.GRAY_STAINED_GLASS_PANE;
        City city = info.city();
        boolean isProtected = info.isProtected();

        if (chunkX == playerChunkX && chunkZ == playerChunkZ) {
            material = Material.LIME_STAINED_GLASS_PANE;
        } else if (isProtected) {
            material = Material.ORANGE_STAINED_GLASS_PANE;
        } else if (city != null) {
            boolean isPlayerCity = playerCityUUID != null && playerCityUUID.equals(city.getUniqueId());
            material = isPlayerCity ? Material.BLUE_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        }

        if (isProtected) {
            return createProtectedChunkItem(material, chunkX, chunkZ);
        } else if (city != null) {
            boolean isPlayerCity = playerCityUUID != null && playerCityUUID.equals(city.getUniqueId());
            return isPlayerCity
                    ? createPlayerCityChunkItem(material, city, chunkX, chunkZ)
                    : createOtherCityChunkItem(material, city, chunkX, chunkZ);
        } else {
            return createUnclaimedChunkItem(material, chunkX, chunkZ);
        }
    }

    private ItemBuilder createProtectedChunkItem(Material material, int chunkX, int chunkZ) {
        return new ItemBuilder(this, material, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.chunks.protected.title"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.city.menus.chunks.protected.lore",
                    Component.text(chunkX + ", " + chunkZ).color(NamedTextColor.WHITE)
            ));
        });
    }

    private ItemBuilder createPlayerCityChunkItem(Material material, City city, int chunkX, int chunkZ) {
        List<Component> lore;
        Component cityName = Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE);
        Component position = Component.text(chunkX + ", " + chunkZ).color(NamedTextColor.WHITE);
        if (city.getChunks().size() > CityCreateAction.FREE_CLAIMS+1) {
            Component moneyValue = Component.text(CityUnclaimAction.calculatePrice(playerCity.getChunks().size())).color(NamedTextColor.GOLD);
            Component moneyIcon = Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD);
            Component ayweniteValue = Component.text(CityUnclaimAction.calculateAywenite(playerCity.getChunks().size())).color(NamedTextColor.LIGHT_PURPLE);
            lore = TranslationManager.translationLore(
                    "feature.city.menus.chunks.player_claim.lore.reward",
                    cityName,
                    position,
                    moneyValue,
                    moneyIcon,
                    ayweniteValue
            );
        } else {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.chunks.player_claim.lore.basic",
                    cityName,
                    position
            );
        }

        return new ItemBuilder(this, material, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.chunks.player_claim.title"));
            itemMeta.lore(lore);
        }).setOnClick(event -> handleChunkUnclaimClick(player, chunkX, chunkZ, hasPermissionClaim));
    }

    private ItemBuilder createOtherCityChunkItem(Material material, City city, int chunkX, int chunkZ) {
        return new ItemBuilder(this, material, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.chunks.other_claim.title"));
            itemMeta.lore(TranslationManager.translationLore(
                    "feature.city.menus.chunks.other_claim.lore",
                    Component.text(city.getName()).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(chunkX + ", " + chunkZ).color(NamedTextColor.WHITE)
            ));
        });
    }

    private ItemBuilder createUnclaimedChunkItem(Material material, int chunkX, int chunkZ) {
        List<Component> lore;
        Component position = Component.text(chunkX + ", " + chunkZ).color(NamedTextColor.WHITE);
        if (hasFreeClaimAvailable) {
            lore = TranslationManager.translationLore(
                    "feature.city.menus.chunks.unclaimed.lore.free",
                    position
            );
        } else {
            Component moneyValue = Component.text(price).color(NamedTextColor.GOLD);
            Component moneyIcon = Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD);
            Component ayweniteValue = Component.text(aywenite).color(NamedTextColor.LIGHT_PURPLE);
            lore = TranslationManager.translationLore(
                    "feature.city.menus.chunks.unclaimed.lore.cost",
                    position,
                    moneyValue,
                    moneyIcon,
                    ayweniteValue
            );
        }

        return new ItemBuilder(this, material, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.menus.chunks.unclaimed.title"));
            itemMeta.lore(lore);
        }).setOnClick(event -> handleChunkClaimClick(player, chunkX, chunkZ, hasPermissionClaim));
    }

    private void handleChunkClaimClick(Player player, int chunkX, int chunkZ, boolean hasPermissionClaim) {
        City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

        if (cityCheck == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!hasPermissionClaim) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.player_cannot_claim"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        ConfirmMenu menu = new ConfirmMenu(
                player,
                () -> {
                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                        CityClaimAction.startClaim(player, chunkX, chunkZ);
                    });
                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                        String refreshCacheKey = player.getWorld().getName() + ":" + startX + "," + startZ;
                        CHUNK_CACHE.remove(refreshCacheKey);

                        new CityChunkMenu(player).open();
                    }, 2);
                },
                () -> {
                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                        new CityChunkMenu(player).open();
                    }, 2);
                },
                List.of(TranslationManager.translation("feature.city.menus.chunks.confirm_claim.accept")),
                List.of(TranslationManager.translation("feature.city.menus.chunks.confirm_claim.deny")));
        menu.open();
    }

    private void handleChunkUnclaimClick(Player player, int chunkX, int chunkZ, boolean hasPermissionClaim) {
        City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

        if (cityCheck == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!hasPermissionClaim) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.player_cannot_claim"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        ConfirmMenu menu = new ConfirmMenu(
                player,
                () -> {
                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                        CityUnclaimAction.startUnclaim(player, chunkX, chunkZ);
                    });
                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                        String refreshCacheKey = player.getWorld().getName() + ":" + startX + "," + startZ;
                        CHUNK_CACHE.remove(refreshCacheKey);

                        new CityChunkMenu(player).open();
                    }, 2);
                },
                () -> {
                    Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                        new CityChunkMenu(player).open();
                    }, 2);
                },
                List.of(TranslationManager.translation("feature.city.menus.chunks.confirm_unclaim.accept")),
                List.of(TranslationManager.translation("feature.city.menus.chunks.confirm_unclaim.deny")));
        menu.open();
    }
}
