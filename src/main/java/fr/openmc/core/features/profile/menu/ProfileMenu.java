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
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

import static fr.openmc.core.features.mailboxes.utils.MailboxUtils.getHead;

public class ProfileMenu extends Menu {
    private static final int FRIENDS_SLOT = 9;
    private static final int MAILBOX_SLOT = 11;
    private static final int IDENTITY_SLOT = 13;
    private static final int CITY_SLOT = 15;
    private static final int PLAYTIME_SLOT = 17;
    private static final int CLOSE_SLOT = 35;

    private final OfflinePlayer target;

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.profile.menu.title",
                Component.text(getTargetName(), NamedTextColor.GOLD)
        );
    }

    @Override
    public String getTexture() {
        return "";
    }

    public ProfileMenu(Player owner, OfflinePlayer target) {
        super(owner);
        this.target = target;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        content.put(FRIENDS_SLOT, createFriendsItem());
        content.put(MAILBOX_SLOT, createMailboxItem());
        content.put(IDENTITY_SLOT, createIdentityItem());
        content.put(CITY_SLOT, createCityItem());
        content.put(PLAYTIME_SLOT, createPlaytimeItem());
        content.put(CLOSE_SLOT, createCloseItem());

        return content;
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
    private ItemMenuBuilder createIdentityItem() {
        Component status = TranslationManager.translation(
                target.isOnline()
                        ? "feature.profile.status.online"
                        : "feature.profile.status.offline"
        ).color(NamedTextColor.GRAY);

        return new ItemMenuBuilder(this, getHead(target), meta -> {
            meta.displayName(Component.text(getTargetName(), NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
            );
            meta.lore(TranslationManager.translationLore(
                    "feature.profile.item.identity.lore",
                    status
            ).stream().map(line -> line.color(NamedTextColor.GRAY)).toList());
        }).hide(DataComponentTypes.PROFILE);
    }

    private ItemMenuBuilder createFriendsItem() {
        boolean selfProfile = isSelfProfile();
        boolean friends = !selfProfile
                && FriendManager.areFriends(getOwner().getUniqueId(), target.getUniqueId());
        Component lore = getFriendsLore(selfProfile, friends);

        return new ItemMenuBuilder(
                this,
                friends ? Material.LIME_DYE : Material.PLAYER_HEAD,
                meta -> {
                    meta.displayName(TranslationManager.translation("feature.profile.item.friends.name")
                            .color(friends ? NamedTextColor.GREEN : NamedTextColor.DARK_AQUA)
                            .decorate(TextDecoration.BOLD)
                            .decoration(TextDecoration.ITALIC, false)
                    );
                    meta.lore(List.of(lore));
                }
        ).setOnClick(event -> openFriends(selfProfile, friends));
    }

    private Component getFriendsLore(boolean selfProfile, boolean friends) {
        if (selfProfile) {
            return TranslationManager.translation("feature.profile.item.friends.self_lore")
                    .color(NamedTextColor.GRAY);
        }

        String translationKey = friends
                ? "feature.profile.item.friends.already_lore"
                : "feature.profile.item.friends.add_lore";
        return TranslationManager.translation(
                translationKey,
                Component.text(getTargetName(), NamedTextColor.GRAY)
        ).color(NamedTextColor.GRAY);
    }

    private void openFriends(boolean selfProfile, boolean friends) {
        if (selfProfile || friends) {
            runCommand("friends list");
            return;
        }
        runCommand("friends add " + getTargetName());
    }

    private ItemMenuBuilder createMailboxItem() {
        boolean selfProfile = isSelfProfile();
        ItemStack icon = selfProfile
                ? new ItemStack(Material.CHEST)
                : OMCRegistry.CUSTOM_ITEMS.MAILBOX_SEND.getBest();
        Component lore = getMailboxLore(selfProfile);

        return new ItemMenuBuilder(this, icon, meta -> {
            meta.displayName(TranslationManager.translation("feature.profile.item.mailbox.name")
                    .color(NamedTextColor.DARK_AQUA)
                    .decorate(TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false)
            );
            meta.lore(List.of(lore));
        }).setOnClick(event ->openMailbox(selfProfile));
    }

    private Component getMailboxLore(boolean selfProfile) {
        String translationKey = selfProfile
                ? "feature.profile.item.mailbox.self_lore"
                : "feature.profile.item.mailbox.send_lore";
        if (selfProfile) {
            return TranslationManager.translation(translationKey)
                    .color(NamedTextColor.GRAY);
        }
        return TranslationManager.translation(
                translationKey,
                Component.text(getTargetName(), NamedTextColor.GRAY)
        ).color(NamedTextColor.GRAY);
    }

    private void openMailbox(boolean selfProfile) {
            if (selfProfile) {
                new PlayerMailbox(getOwner()).open();
            return;
                }
                new SendingLetter(getOwner(), target).open();
            }

    private ItemMenuBuilder createCityItem() {
        City city = CityManager.getPlayerCity(target.getUniqueId());
        if (city == null) {
            return createNoCityItem();
        }
        return createCityDetailsItem(city);
    }

    private ItemMenuBuilder createNoCityItem() {
        return new ItemMenuBuilder(
                    this,
                    OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_CHATEAU,
                    meta -> {
                        meta.displayName(TranslationManager.translation("feature.profile.item.city.name")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD)
                                .decoration(TextDecoration.ITALIC, false)
                        );
                        meta.lore(List.of(
                                TranslationManager.translation("feature.profile.item.city.none")
                                        .color(NamedTextColor.GRAY)
                        ));
                    }
            );}

    private ItemMenuBuilder createCityDetailsItem(City city) {
        return new ItemMenuBuilder(
                    this,
                    OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_CHATEAU,
                    meta -> {
                        meta.displayName(TranslationManager.translation("feature.profile.item.city.name")
                                .color(NamedTextColor.AQUA)
                                .decorate(TextDecoration.BOLD)
                                .decoration(TextDecoration.ITALIC, false)
                        );
                        meta.lore(TranslationManager.translationLore(
                                "feature.profile.item.city.lore",
                                Component.text(city.getName(), NamedTextColor.GRAY),
                                Component.text(
                                        city.getRankName(target.getUniqueId()),
                                        NamedTextColor.GRAY
                                )
                        ).stream().map(line -> line.color(NamedTextColor.GRAY)).toList());
                    }
            ).setOnClick(event -> new CityListDetailsMenu(getOwner(), city).open());
        }

    private ItemMenuBuilder createPlaytimeItem() {
        long ticksPlayed = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
        return new ItemMenuBuilder(
                this,
                OMCRegistry.CUSTOM_ITEMS.MAILBOX_HOURGLASS,
                meta -> {
                    meta.displayName(TranslationManager.translation("feature.profile.item.playtime.name")
                            .color(NamedTextColor.DARK_GRAY)
                            .decorate(TextDecoration.BOLD)
                            .decoration(TextDecoration.ITALIC, false)
                    );
                    meta.lore(List.of(
                            TranslationManager.translation(
                                    "feature.profile.item.playtime.lore",
                                    Component.text(
                                            DateUtils.convertTime(ticksPlayed),
                                            NamedTextColor.GRAY
                                    )
                            ).color(NamedTextColor.GRAY)
                    ));
                }
        );
    }

    private ItemMenuBuilder createCloseItem() {
        return new ItemMenuBuilder(
                this,
                OMCRegistry.CUSTOM_ITEMS.ICON_CANCEL,
                meta -> meta.displayName(TranslationManager.translation("feature.profile.item.close")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                        .decoration(TextDecoration.ITALIC, false)
                )
        ).setCloseButton();
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

    private String getTargetName() {
        String name = target.getName();
        if (name != null && !name.isBlank()) {
            return name;
        }
        return TranslationManager.translationString("feature.profile.player.unknown");
    }
}
