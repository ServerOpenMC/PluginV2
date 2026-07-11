package fr.openmc.core.features.profile.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.list.CityListDetailsMenu;
import fr.openmc.core.features.friend.FriendManager;
import fr.openmc.core.features.mailboxes.menu.PlayerMailbox;
import fr.openmc.core.features.mailboxes.menu.letter.SendingLetter;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileMenu extends Menu {
    private final OfflinePlayer target;

    public ProfileMenu(Player owner) {
        this(owner, owner);
    }

    public ProfileMenu(Player owner, OfflinePlayer target) {
        super(owner);
        this.target = target;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.profile.menu.title",
                PlayerNameCache.name(target.getUniqueId()).color(NamedTextColor.GOLD)
        );
    }

    @Override
    public String getTexture() {
        return "";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        addFriendsItem(inventory);
        addMailboxItem(inventory);
        addIdentityItem(inventory);
        addCityItem(inventory);
        addPlaytimeItem(inventory);
        addCloseItem(inventory);

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @SuppressWarnings("UnstableApiUsage")
    private void addIdentityItem(Map<Integer, ItemMenuBuilder> inventory) {
        String statusKey = target.isOnline()
                ? "feature.profile.status.online"
                : "feature.profile.status.offline";

        inventory.put(13, new ItemMenuBuilder(
                this,
                ItemUtils.getPlayerHead(target.getUniqueId()),
                meta -> {
                    meta.displayName(TranslationManager.translation(
                            "feature.profile.item.identity.name",
                            PlayerNameCache.name(target.getUniqueId()).color(NamedTextColor.GOLD)
                    ));
                    meta.lore(TranslationManager.translationLore(
                            "feature.profile.item.identity.lore",
                            TranslationManager.translation(statusKey)
                                    .color(NamedTextColor.GRAY)
                    ));
                }
        ).hide(DataComponentTypes.PROFILE));
    }

    private void addFriendsItem(Map<Integer, ItemMenuBuilder> inventory) {
        boolean selfProfile = isSelfProfile();
        boolean friends = !selfProfile
                && FriendManager.areFriends(getOwner().getUniqueId(), target.getUniqueId());
        String nameKey = friends
                ? "feature.profile.item.friends.name.friend"
                : "feature.profile.item.friends.name";

        inventory.put(9, new ItemMenuBuilder(
                this,
                friends ? Material.LIME_DYE : Material.PLAYER_HEAD,
                meta -> {
                    meta.displayName(TranslationManager.translation(nameKey));
                    meta.lore(List.of(getFriendsLore(selfProfile, friends)));
                }
        ).setOnClick(event -> openFriends(selfProfile, friends)));
    }

    private Component getFriendsLore(boolean selfProfile, boolean friends) {
        if (selfProfile) {
            return TranslationManager.translation("feature.profile.item.friends.self_lore");
        }

        String translationKey = friends
                ? "feature.profile.item.friends.already_lore"
                : "feature.profile.item.friends.add_lore";
        return TranslationManager.translation(
                translationKey,
                PlayerNameCache.name(target.getUniqueId()).color(NamedTextColor.GOLD)
        );
    }

    private void openFriends(boolean selfProfile, boolean friends) {
        if (selfProfile || friends) {
            runCommand("friends list");
            return;
        }
        runCommand("friends add " + PlayerNameCache.getName(target.getUniqueId()));
    }

    private void addMailboxItem(Map<Integer, ItemMenuBuilder> inventory) {
        boolean selfProfile = isSelfProfile();
        ItemStack icon = selfProfile
                ? new ItemStack(Material.CHEST)
                : OMCRegistry.CUSTOM_ITEMS.MAILBOX_SEND.getBest();

        inventory.put(11, new ItemMenuBuilder(this, icon, meta -> {
            meta.displayName(TranslationManager.translation("feature.profile.item.mailbox.name"));
            meta.lore(List.of(getMailboxLore(selfProfile)));
        }).setOnClick(event -> openMailbox(selfProfile)));
    }

    private Component getMailboxLore(boolean selfProfile) {
        if (selfProfile) {
            return TranslationManager.translation("feature.profile.item.mailbox.self_lore");
        }
        return TranslationManager.translation(
                "feature.profile.item.mailbox.send_lore",
                PlayerNameCache.name(target.getUniqueId()).color(NamedTextColor.GOLD)
        );
    }

    private void openMailbox(boolean selfProfile) {
        if (selfProfile) {
            new PlayerMailbox(getOwner()).open();
            return;
        }
        new SendingLetter(getOwner(), target).open();
    }

    private void addCityItem(Map<Integer, ItemMenuBuilder> inventory) {
        City city = CityManager.getPlayerCity(target.getUniqueId());
        if (city == null) {
            inventory.put(15, new ItemMenuBuilder(
                    this,
                    OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_CHATEAU,
                    meta -> {
                        meta.displayName(TranslationManager.translation(
                                "feature.profile.item.city.name.none"
                        ));
                        meta.lore(List.of(
                                TranslationManager.translation("feature.profile.item.city.none")
                        ));
                    }
            ));
            return;
        }

        inventory.put(15, new ItemMenuBuilder(
                this,
                OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_CHATEAU,
                meta -> {
                    meta.displayName(TranslationManager.translation(
                            "feature.profile.item.city.name"
                    ));
                    meta.lore(TranslationManager.translationLore(
                            "feature.profile.item.city.lore",
                            Component.text(city.getName(), NamedTextColor.GRAY),
                            Component.text(
                                    city.getRankName(target.getUniqueId()),
                                    NamedTextColor.GRAY
                            )
                    ));
                }
        ).setOnClick(event -> new CityListDetailsMenu(getOwner(), city).open()));
    }

    private void addPlaytimeItem(Map<Integer, ItemMenuBuilder> inventory) {
        long ticksPlayed = target.getStatistic(Statistic.PLAY_ONE_MINUTE);

        inventory.put(17, new ItemMenuBuilder(
                this,
                OMCRegistry.CUSTOM_ITEMS.MAILBOX_HOURGLASS,
                meta -> {
                    meta.displayName(TranslationManager.translation(
                            "feature.profile.item.playtime.name"
                    ));
                    meta.lore(List.of(
                            TranslationManager.translation(
                                    "feature.profile.item.playtime.lore",
                                    Component.text(
                                            DateUtils.convertTime(ticksPlayed),
                                            NamedTextColor.GRAY
                                    )
                            )
                    ));
                }
        ));
    }

    private void addCloseItem(Map<Integer, ItemMenuBuilder> inventory) {
        inventory.put(35, new ItemMenuBuilder(
                this,
                OMCRegistry.CUSTOM_ITEMS.ICON_CANCEL,
                meta -> meta.displayName(
                        TranslationManager.translation("feature.profile.item.close")
                )
        ).setCloseButton());
    }

    private void runCommand(String command) {
        getOwner().closeInventory();
        Bukkit.getScheduler().runTask(
                OMCPlugin.getInstance(),
                () -> getOwner().performCommand(command)
        );
    }

    private boolean isSelfProfile() {
        return getOwner().getUniqueId().equals(target.getUniqueId());
    }

}
