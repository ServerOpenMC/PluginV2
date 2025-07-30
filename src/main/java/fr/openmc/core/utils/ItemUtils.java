package fr.openmc.core.utils;

import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.nonItalic;

public class ItemUtils {
    /**
     * Return a {@link TranslatableComponent} from a {@link ItemStack}
     *
     * @param stack ItemStack that get translate
     * @return a {@link TranslatableComponent} that can be translated by client
     */
    public static TranslatableComponent getItemTranslation(ItemStack stack) {
        return Component.translatable(Objects.requireNonNullElse(
                stack.getType().translationKey(),
                "block.minecraft.stone"
        ));
    }

    /**
     * Return a {@link TranslatableComponent} from a {@link Material}
     *
     * @param material Material that get translate
     * @return a {@link TranslatableComponent} that can be translated by client
     */
    public static TranslatableComponent getItemTranslation(Material material) {
        return getItemTranslation(new ItemStack(material));
    }

    /**
     * Découpe un nombre d'item en paquets
     *
     * @param items Votre ItemStack
     * @return Une Liste d'ItemStack
     */
    public static List<ItemStack> splitAmountIntoStack(ItemStack items) {
        int maxStackSize = items.getMaxStackSize();
        int amount = items.getAmount();

        List<ItemStack> stacks = new ArrayList<>();
        while (amount > maxStackSize) {
            ItemStack item = items.clone();
            item.setAmount(maxStackSize);
            stacks.add(item);

            amount -= maxStackSize;
        }

        if (amount > 0) {
            ItemStack item = items.clone();
            item.setAmount(amount);
            stacks.add(item);
        }

        return stacks;
    }

    /**
     * Retourne le nombre d'item qui peut aller dans un Stack
     *
     * @param player Joueur pour acceder a son inventaire
     * @param item   Item recherché pour completer un stack
     * @return Le nombre d'item qui peut completer un stack
     */
    public static int getNumberItemToStack(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();
        int numberitemtostack = 0;

        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack != null && stack.isSimilar(item)) {
                numberitemtostack = stack.getMaxStackSize() - stack.getAmount();
            }
        }
        return numberitemtostack;
    }


    /**
     * Retourne le nombre de slot vide
     *
     * @param player Joueur pour acceder a son inventaire
     */
    public static int getSlotNull(Player player) {
        Inventory inventory = player.getInventory();

        int slot = 0;

        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack == null) {
                slot++;
            }
        }

        return slot;
    }

    /**
     * Dire si le joueur a assez de place pour un objet
     *
     * @param player Joueur pour acceder a son inventaire
     * @param item   Objet concerné
     */
    public static int getFreePlacesForItem(Player player, ItemStack item) {
        int maxStackSize = item.getMaxStackSize();
        int freePlace = maxStackSize * getSlotNull(player);

        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack == null || !item.isSimilar(stack))
                continue;

            if (stack.getAmount() != maxStackSize)
                freePlace += maxStackSize - stack.getAmount();
        }

        return freePlace;
    }

    /**
     * Dire si le joueur a assez de place pour un type d'objet
     *
     * @param player Joueur pour acceder a son inventaire
     * @param item   Type d'bjet concerné
     */
    public static int getFreePlacesForItem(Player player, Material item) {
        int maxStackSize = item.getMaxStackSize();
        int freePlace = maxStackSize * getSlotNull(player);

        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack == null || stack.getType() != item)
                continue;

            if (stack.getAmount() != maxStackSize)
                freePlace += maxStackSize - stack.getAmount();
        }

        return freePlace;
    }

    // IMPORT FROM MAILBOX
    public static ItemStack getPlayerHead(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        String playerName;
        if (player != null) {
            playerName = player.getName();
            meta.setOwningPlayer(player);
        } else {
            OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);
            playerName = offlinePlayer.getName();
            meta.setOwningPlayer(offlinePlayer);
        }

        Component displayName = Component.text(playerName, NamedTextColor.GOLD, TextDecoration.BOLD);
        meta.displayName(nonItalic(displayName));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Check if the player has enough items in his inventory
     *
     * @param player the player to check
     * @param item the item to check {@link ItemStack}
     * @param amount the amount of items to check
     * @return {@code true} if the player has enough items, {@code false} otherwise
     */
    public static boolean hasEnoughItems(Player player, ItemStack item, int amount) {
        int totalItems = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (ItemStack is : contents) {
            if (is != null && is.isSimilar(item)) {
                totalItems += is.getAmount();
            }
        }

        if (amount == 0) return false;
        return totalItems >= amount;
    }
    /**
     * Check if the player has enough items in his inventory
     *
     * @param player the player to check
     * @param material the material to check {@link ItemStack}
     * @param amount the amount of items to check
     * @return {@code true} if the player has enough items, {@code false} otherwise
     */
    public static boolean hasEnoughItems(Player player, Material material, int amount) {
        int totalItems = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (ItemStack is : contents) {
            if (is != null && is.getType() == material) {
                totalItems += is.getAmount();
            }
        }

        if (amount == 0) return false;
        return totalItems >= amount;
    }


    /**
     * Dire si le joueur a des ou un slot de libre
     *
     * @param player Joueur pour acceder a son inventaire
     */
    public static boolean hasAvailableSlot(Player player) {
        Inventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();

        for (int i = 0; i < contents.length; i++) {
            // on ne vérifie pas la main secondaire et l'armure
            if (i >= 36 && i <= 40) continue;

            if (contents[i] == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retirer le nombre d'objet au joueur (vérification obligatoire avant execution)
     *
     * @param player   Joueur pour acceder a son inventaire
     * @param item     Objet a retirer
     * @param quantity Quantité a retirer
     */
    public static void removeItemsFromInventory(Player player, Material item, int quantity) {
        ItemStack[] contents = player.getInventory().getContents();
        int remaining = quantity;

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == item) {
                int stackAmount = stack.getAmount();
                if (stackAmount <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= stackAmount;
                } else {
                    stack.setAmount(stackAmount - remaining);
                    remaining = 0;
                }
            }
        }
    }

    public static void removeItemsFromInventory(Player player, ItemStack item, int quantity) {
        ItemStack[] contents = player.getInventory().getContents();
        int remaining = quantity;

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.isSimilar(item)) {
                int stackAmount = stack.getAmount();
                if (stackAmount <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= stackAmount;
                } else {
                    stack.setAmount(stackAmount - remaining);
                    remaining = 0;
                }
            }
        }
    }

    public static boolean takeAywenite(Player player, int amount) {
        ItemStack aywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
        if (aywenite == null) return false;

        if (!hasEnoughItems(player, aywenite, amount)) {
            MessagesManager.sendMessage(
                    player,
                    Component.text("Vous n'avez pas assez d'§dAywenite §f("+amount+ " nécessaires)"),
                    Prefix.OPENMC,
                    MessageType.ERROR,
                    true
            );
            return false;
        }

        removeItemsFromInventory(player, aywenite, amount);
        return true;
    }

    /**
     * Calcule le nombre maximal d'items pouvant être craftés.
     * <p>
     * Cette méthode récupère le résultat du craft. Si le résultat est nul, elle retourne 0.
     * Sinon, elle détermine le nombre minimal d'items présents dans la grille de craft et
     * renvoie le produit de la quantité du résultat par ce nombre minimal.
     *
     * @param inventory l'inventaire de crafting
     * @return le nombre maximal d'items pouvant être craftés
     */
    public static int getMaxCraftAmount(CraftingInventory inventory) {
        ItemStack result = inventory.getResult();
        if (result == null)
            return 0;

        int resultCount = result.getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack itemStack : inventory.getMatrix()) {
            if (itemStack != null && itemStack.getAmount() < materialCount)
                materialCount = itemStack.getAmount();
        }

        return resultCount * materialCount;
    }

    /**
     * Trouve le slot ou est l'item
     *
     * @param player le joueur pour l'inventaire
     * @param item   l'item a chercher
     * @return le slot de l'item (-1 si introuvable)
     */
    public static int getSlotOfItem(Player player, ItemStack item) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack current = inv.getItem(i);
            if (item.equals(current)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Vérifie si l'inventaire du joueur dispose d'assez d'espace pour ajouter l'objet.
     *
     * @param player le joueur dont l'inventaire est vérifié
     * @param item   l'objet à ajouter
     * @param amount la quantité d'objet à ajouter
     * @return true si l'espace disponible est suffisant, false sinon
     */
    public static boolean hasEnoughSpace(Player player, ItemStack item, int amount) {
        return getFreePlacesForItem(player, item) >= amount;
    }

    /**
     * Vérifie si l'inventaire du joueur dispose d'assez d'espace pour ajouter un objet du type sélectionné.
     *
     * @param player le joueur dont l'inventaire est vérifié
     * @param item   le type d'objet à ajouter
     * @param amount la quantité d'objet à ajouter
     * @return true si l'espace disponible est suffisant, false sinon
     */
    public static boolean hasEnoughSpace(Player player, Material item, int amount) {
        return getFreePlacesForItem(player, item) >= amount;
    }

    /**
     * Vérifie si l'inventaire du joueur dispose d'assez d'espace pour ajouter un objet.
     *
     * @param player le joueur dont l'inventaire est vérifié
     * @param item   l'objet à ajouter
     * @return true si l'espace disponible est suffisant, false sinon
     */
    public static boolean hasEnoughSpace(Player player, ItemStack item) {
        return hasEnoughSpace(player, item, 1);
    }

    /**
     * Vérifie si l'inventaire du joueur dispose d'assez d'espace pour ajouter un objet du type spécifié.
     *
     * @param player le joueur dont l'inventaire est vérifié
     * @param item   le type d'objet à ajouter
     * @return true si l'espace disponible est suffisant, false sinon
     */
    public static boolean hasEnoughSpace(Player player, Material item) {
        return hasEnoughSpace(player, item, 1);
    }

    /**
     * Donner le Type de Panneau en fonction du biome ou il se trouve
     *
     * @param player Joueur pour acceder au biome ou il est
     */
    public static Material getSignType(Player player) {
        HashMap<Biome, Material> biomeToSignType = new HashMap<>();
        biomeToSignType.put(Biome.BAMBOO_JUNGLE, Material.BAMBOO_SIGN);
        biomeToSignType.put(Biome.BIRCH_FOREST, Material.BIRCH_SIGN);
        biomeToSignType.put(Biome.OLD_GROWTH_BIRCH_FOREST, Material.BIRCH_SIGN);
        biomeToSignType.put(Biome.JUNGLE, Material.JUNGLE_SIGN);
        biomeToSignType.put(Biome.SPARSE_JUNGLE, Material.JUNGLE_SIGN);
        biomeToSignType.put(Biome.PALE_GARDEN, Material.PALE_OAK_SIGN);
        biomeToSignType.put(Biome.CHERRY_GROVE, Material.CHERRY_SIGN);
        biomeToSignType.put(Biome.CRIMSON_FOREST, Material.CRIMSON_SIGN);
        biomeToSignType.put(Biome.WARPED_FOREST, Material.WARPED_SIGN);
        biomeToSignType.put(Biome.MANGROVE_SWAMP, Material.MANGROVE_SIGN);
        biomeToSignType.put(Biome.SAVANNA, Material.ACACIA_SIGN);
        biomeToSignType.put(Biome.SAVANNA_PLATEAU, Material.ACACIA_SIGN);
        biomeToSignType.put(Biome.WINDSWEPT_SAVANNA, Material.ACACIA_SIGN);
        biomeToSignType.put(Biome.DARK_FOREST, Material.DARK_OAK_SIGN);
        biomeToSignType.put(Biome.TAIGA, Material.SPRUCE_SIGN);
        biomeToSignType.put(Biome.OLD_GROWTH_PINE_TAIGA, Material.SPRUCE_SIGN);
        biomeToSignType.put(Biome.SNOWY_TAIGA, Material.SPRUCE_SIGN);
        biomeToSignType.put(Biome.OLD_GROWTH_SPRUCE_TAIGA, Material.SPRUCE_SIGN);

        Biome playerBiome = player.getWorld().getBiome(player.getLocation());

        return biomeToSignType.getOrDefault(playerBiome, Material.OAK_SIGN);
    }
}
