package fr.openmc.core.features.city.menu;

import fr.openmc.api.hooks.WorldGuardHook;
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
import fr.openmc.core.features.city.actions.CityUnclaimAction;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ChunkInfo;
import fr.openmc.core.utils.ChunkPos;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
    public @NotNull String getName() {
	    return "Menu des villes - La carte";
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
                itemMeta.displayName(Component.text("§eChargement..."));
                itemMeta.lore(List.of(Component.text("§7Chargement des données de chunks en cours")));
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
                itemMeta.displayName(Component.text("§aRetour"));
                itemMeta.lore(List.of(Component.text("§7Retourner au menu précédent")));
            }, true));

            if (hasFreeClaimAvailable) {
                inventory.put(49, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
	                itemMeta.displayName(Component.text("§6Claim gratuit"));
                    itemMeta.lore(List.of(Component.text("§7Vous avez §6" + freeClaims + " claim gratuit !")));
                }));
            }
        }

        inventory.put(53, new ItemBuilder(this, Material.MAP, itemMeta -> {
            itemMeta.displayName(Component.text("§6Rafraîchir la carte"));
	        itemMeta.lore(List.of(Component.text("§7Mettre à jour §6les claims affichés§7")));
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
            itemMeta.displayName(Component.text("§cClaim dans une région protégée"));
            itemMeta.lore(List.of(
                    Component.text("§cCette zone est protégée par une région WorldGuard"),
                    Component.text("§7Position : §f" + chunkX + ", " + chunkZ)
            ));
        });
    }

    private ItemBuilder createPlayerCityChunkItem(Material material, City city, int chunkX, int chunkZ) {
        return new ItemBuilder(this, material, itemMeta -> {
            itemMeta.displayName(Component.text("§9Claim de votre ville"));
            itemMeta.lore(List.of(
                    Component.text("§7Ville : §d" + city.getName()),
                    Component.text("§7Position : §f" + chunkX + ", " + chunkZ),
                    Component.empty(),
                    Component.text("§cVous rapporte :"),
                    Component.text("§8- §6" + CityUnclaimAction.calculatePrice(playerCity.getChunks().size())).append(Component.text(EconomyManager.getEconomyIcon())).decoration(TextDecoration.ITALIC, false),
                    Component.text("§8- §d" + CityUnclaimAction.calculateAywenite(playerCity.getChunks().size()) + " d'Aywenite"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ POUR UNCLAIM")
            ));
        }).setOnClick(event -> handleChunkUnclaimClick(player, chunkX, chunkZ, hasPermissionClaim));
    }

    private ItemBuilder createOtherCityChunkItem(Material material, City city, int chunkX, int chunkZ) {
        return new ItemBuilder(this, material, itemMeta -> {
            itemMeta.displayName(Component.text("§cClaim d'une ville adverse"));
            itemMeta.lore(List.of(
                    Component.text("§7Ville : §d" + city.getName()),
                    Component.text("§7Position : §f" + chunkX + ", " + chunkZ)
            ));
        });
    }

    private ItemBuilder createUnclaimedChunkItem(Material material, int chunkX, int chunkZ) {
        List<Component> lore;
        if (hasFreeClaimAvailable) {
            lore = List.of(
                    Component.text("§7Position : §f" + chunkX + ", " + chunkZ),
                    Component.empty(),
                    Component.text("§cCoûte :"),
		            Component.text("§8- §6Claim gratuit"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ POUR CLAIM")
            );
        } else {
            lore = List.of(
                    Component.text("§7Position : §f" + chunkX + ", " + chunkZ),
                    Component.empty(),
                    Component.text("§cCoûte :"),
                    Component.text("§8- §6" + price).append(Component.text(EconomyManager.getEconomyIcon())).decoration(TextDecoration.ITALIC, false),
                    Component.text("§8- §d" + aywenite + " d'Aywenite"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ POUR CLAIM")
            );
        }

        return new ItemBuilder(this, material, itemMeta -> {
            itemMeta.displayName(Component.text("§cClaim libre"));
            itemMeta.lore(lore);
        }).setOnClick(event -> handleChunkClaimClick(player, chunkX, chunkZ, hasPermissionClaim));
    }

    private void handleChunkClaimClick(Player player, int chunkX, int chunkZ, boolean hasPermissionClaim) {
        City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

        if (cityCheck == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!hasPermissionClaim) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_CLAIM.getMessage(), Prefix.CITY, MessageType.ERROR, false);
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
                List.of(Component.text("§7Voulez vous vraiment claim ce chunk ?")),
                List.of(Component.text("§7Annuler la procédure de claim")));
        menu.open();
    }

    private void handleChunkUnclaimClick(Player player, int chunkX, int chunkZ, boolean hasPermissionClaim) {
        City cityCheck = CityManager.getPlayerCity(player.getUniqueId());

        if (cityCheck == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!hasPermissionClaim) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_CLAIM.getMessage(), Prefix.CITY, MessageType.ERROR, false);
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
                List.of(Component.text("§7Voulez vous vraiment unclaim ce chunk ?")),
                List.of(Component.text("§7Annuler la procédure de unclaim")));
        menu.open();
    }
}
