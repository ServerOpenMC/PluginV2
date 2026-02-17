package fr.openmc.core.utils;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.nonItalic;

public class ItemUtils {
    /**
     * Return a {@link TranslatableComponent} from a {@link ItemStack}
     *
     * @param material Material that get translate
     * @return a {@link TranslatableComponent} that can be translated by client
     */
    public static TranslatableComponent getItemTranslation(Material material) {
        return Component.translatable(Objects.requireNonNullElse(
                material.translationKey(),
                "block.minecraft.stone"
        ));
    }

    /**
     * Return a {@link TranslatableComponent} from a {@link Material}
     *
     * @param stack ItemStack that get translate
     * @return a {@link TranslatableComponent} that can be translated by client
     */
    public static TranslatableComponent getItemTranslation(ItemStack stack) {
        return getItemTranslation(stack.getType());
    }

    public static String getItemName(ItemStack stack) {
        CustomStack customItem = CustomStack.byItemStack(stack);
        if (customItem != null)
            return PlainTextComponentSerializer.plainText().serialize(stack.getItemMeta().customName());

        return PlainTextComponentSerializer.plainText().serialize(getItemTranslation(stack));
    }


    public static String getMaterialName(Material material) {
        return getItemName(new ItemStack(material));
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

    public static List<ItemStack> splitAmountIntoStack(ItemStack item, int totalAmount) {
        int maxStackSize = item.getMaxStackSize();
        List<ItemStack> stacks = new ArrayList<>();

        while (totalAmount > 0) {
            int stackAmount = Math.min(totalAmount, maxStackSize);
            ItemStack clone = item.clone();
            clone.setAmount(stackAmount);
            stacks.add(clone);
            totalAmount -= stackAmount;
        }

        return stacks;
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
            if (stack == null || !isSimilar(item, stack))
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
     * @param item   Type d'objet concerné
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
     * Check if the player has enough of a given item in their inventory.
     * Supports both vanilla items and custom ItemAdder items.
     *
     * @param player The player to check
     * @param item   The reference item
     * @param amount The required amount
     * @return true if the player has at least the required amount, false otherwise
     */
    public static boolean hasEnoughItems(Player player, ItemStack item, int amount) {
        if (amount <= 0) return false;

        int totalItems = 0;

        for (ItemStack is : player.getInventory().getContents()) {
            if (is == null) continue;

            if (isSimilar(is, item)) {
                totalItems += is.getAmount();
                if (totalItems >= amount) return true;
            }
        }

        return totalItems >= amount;
    }
    /**
     * Check if the player has enough items in his inventory
     *
     * @param player the player to check
     * @param material the material to check {@link ItemStack}
     * @param amount the number of items to check
     * @return {@code true} if the player has enough items, {@code false} otherwise
     */
    public static boolean hasEnoughItems(Player player, Material material, int amount) {
        return hasEnoughItems(player, new ItemStack(material), amount);
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
     * Retirer le nombre d'objets au joueur (vérification obligatoire avant execution)
     *
     * @param player         the player whose inventory will be modified
     * @param material       the item to remove, must be similar to the items in the inventory {@link Material}
     * @param amountToRemove the number of items to remove
     */
    public static int removeItemsFromInventory(Player player, Material material, int amountToRemove) {
        return removeItemsFromInventory(player, ItemStack.of(material), amountToRemove);
    }

    /**
     * Retirer le nombre d'objets au joueur (vérification obligatoire avant execution)
     *
     * @param player the player whose inventory will be modified
     * @param item the item to remove, must be similar to the items in the inventory {@link ItemStack}
     * @param amountToRemove the number of items to remove
     */
    public static int removeItemsFromInventory(Player player, ItemStack item, int amountToRemove) {
        if (player == null || item == null || amountToRemove <= 0) return 0;

        int removed = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length && removed < amountToRemove; i++) {
            ItemStack stack = contents[i];
            if (stack == null) continue;

            if (isSimilar(stack, item)) {
                int stackAmount = stack.getAmount();
                int toRemove = Math.min(amountToRemove - removed, stackAmount);

                removed += toRemove;

                if (stackAmount <= toRemove) {
                    player.getInventory().setItem(i, null);
                } else {
                    stack.setAmount(stackAmount - toRemove);
                }
            }
        }

        return removed;
    }

    public static boolean takeAywenite(Player player, int amount) {
        ItemStack aywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
        if (aywenite == null) return false;

        if (!hasEnoughItems(player, aywenite, amount)) {
            MessagesManager.sendMessage(
                    player,
                    Component.text("Vous n'avez pas assez d'§dAywenite §f(" + amount + " nécessaires)"),
                    Prefix.OPENMC,
                    MessageType.ERROR,
                    true
            );
            return false;
        }

        removeItemsFromInventory(player, aywenite, amount);
        return true;
    }

    public static boolean giveAywenite(Player player, int amount) {
        ItemStack aywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
        if (aywenite == null) return false;

        aywenite.setAmount(amount);

        if (!hasEnoughSpace(player, aywenite)) {
            MessagesManager.sendMessage(
                    player,
                    Component.text("Vous n'avez pas assez de place dans votre inventaire pour recevoir " + amount + " d'§dAywenite§f. L'aywenite est disponible dans votre mailbox"),
                    Prefix.OPENMC,
                    MessageType.ERROR,
                    true
            );
            MailboxManager.sendItems(player, player, new ItemStack[]{ aywenite });
            return false;
        }

        player.getInventory().addItem(aywenite);
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
     * Trouve le slot où est l'item
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

    public static int countItems(Player player, ItemStack item) {
        int count = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && isSimilar(item, stack)) {
                count += stack.getAmount();
            }
        }
        return count;
    }

    public static void setTag(ItemStack item, String key, String value) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey namespacedKey = new NamespacedKey(OMCPlugin.getInstance(), key);
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static void setTag(ItemStack item, NamespacedKey key, String value) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static String getTag(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType().isAir()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static String getTag(ItemStack item, String key) {
        if (item == null || item.getType().isAir()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        NamespacedKey namespacedKey = new NamespacedKey(OMCPlugin.getInstance(), key);
        return meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
    }
    /**
     * Compare deux {@link ItemStack} pour vérifier s'ils sont similaires.
     * Deux items sont considérés similaires s'ils ont le même type
     *
     * @param item1 le premier item à comparer
     * @param item2 le second item à comparer
     * @return true si les items sont similaires, false sinon
     */
    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        CustomStack customItem = CustomStack.byItemStack(item1);
        if (customItem != null) {
            CustomStack customIs = CustomStack.byItemStack(item2);
            return customIs != null && customIs.getId().equals(customItem.getId());
        }

        if (item1 == null || item2 == null) return false;
        return item1.getType() == item2.getType();
    }

    /**
     * Compare deux {@link ItemStack} pour vérifier s'ils sont similaires.
     * Deux items sont considérés semblables s'ils ont le même type, la même quantité,
     * et les mêmes métadonnées (nom, lore, etc.). Permet de ne pas vérifier le component TooltipDisplay.
     *
     * @param item1 le premier item à comparer
     * @param item2 le second item à comparer
     * @return true si les items sont similaires, false sinon
     */
    @SuppressWarnings("UnstableApiUsage")
    public static boolean isSimilarMenu(ItemStack item1, ItemStack item2) {
        CustomStack customItem = CustomStack.byItemStack(item1);
        if (customItem != null) {
            CustomStack customIs = CustomStack.byItemStack(item2);
            return customIs != null && customIs.getId().equals(customItem.getId());
        }

        if (item1 == null || item2 == null) return false;
        if (item1.getType() != item2.getType()) return false;
        if (item1.getAmount() != item2.getAmount()) return false;
        if (item1.hasItemMeta() != item2.hasItemMeta()) return false;
        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            if (!Objects.equals(item1.getItemMeta().displayName(), item2.getItemMeta().displayName())) return false;
            if (!Objects.equals(item1.getItemMeta().lore(), item2.getItemMeta().lore())) return false;
            if (!Objects.equals(item1.getItemMeta().getPersistentDataContainer(), item2.getItemMeta().getPersistentDataContainer()))
                return false;
            if (!Objects.equals(item1.getData(DataComponentTypes.ENCHANTMENTS), item2.getData(DataComponentTypes.ENCHANTMENTS)))
                return false;
        }

        return true;
    }

    public static ItemStack getInvisibleItem() {
        ItemStack item = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
        item.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft("air"));
            meta.setHideTooltip(true);
        });
        return item;
    }
}
