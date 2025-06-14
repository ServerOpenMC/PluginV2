package fr.openmc.core.features.settings.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.settings.PlayerSettings;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import fr.openmc.core.features.settings.policy.CityJoinPolicy;
import fr.openmc.core.features.settings.policy.FriendRequestPolicy;
import fr.openmc.core.features.settings.policy.VisibilityLevel;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSettingsMenu extends Menu {

    private final PlayerSettings settings;

    public PlayerSettingsMenu(Player player) {
        super(player);
        try {
            this.settings = PlayerSettingsManager.getInstance().getPlayerSettings(player);
        } catch (Exception e) {
            player.sendMessage(Component.text("Erreur lors de l'ouverture des paramètres. Veuillez réessayer plus tard.", NamedTextColor.RED));
            throw new RuntimeException("Failed to initialize PlayerSettingsMenu", e);
        }
    }

    @Override
    public @NotNull String getName() {
        return "§r§fParamètres de " + getOwner().getName();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> content = new HashMap<>();

        // Paramètre : Demandes d'amis activées
        boolean friendRequestsEnabled = settings.getSetting(SettingType.FRIEND_REQUESTS_ENABLED);
        content.put(10, new ItemBuilder(this, friendRequestsEnabled ? Material.LIME_DYE : Material.GRAY_DYE, meta -> {
            meta.displayName(Component.text("Demandes d'amis", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(friendRequestsEnabled ? "Activées" : "Désactivées", friendRequestsEnabled ? NamedTextColor.GREEN : NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            boolean currentValue = settings.getSetting(SettingType.FRIEND_REQUESTS_ENABLED);
            settings.setSetting(SettingType.FRIEND_REQUESTS_ENABLED, !currentValue);
            this.refresh();
            MessagesManager.sendMessage(getOwner(), Component.text("Demandes d'amis " + (currentValue ?
                    "désactivées" : "activées"), NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false),
                    Prefix.SETTINGS, MessageType.SUCCESS, true);

            this.refresh();
        }));

        // Politique des demandes d'amis
        FriendRequestPolicy friendPolicy = settings.getSetting(SettingType.FRIEND_REQUESTS_POLICY);
        content.put(12, new ItemBuilder(this, Material.PLAYER_HEAD, meta -> {
            meta.displayName(Component.text("Politique demandes d'amis", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Qui peut t'envoyer des demandes d'amis:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));

            for (FriendRequestPolicy policy : FriendRequestPolicy.values()) {
                Component prefix = policy == friendPolicy
                        ? Component.text(" → ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                        : Component.text("    ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

                lore.add(prefix.append(Component.text(policy.getDisplayName(),
                                policy == friendPolicy ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
            }

            lore.add(Component.empty());
            lore.add(Component.text(friendPolicy.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            FriendRequestPolicy[] values = FriendRequestPolicy.values();
            int next = (friendPolicy.ordinal() + 1) % values.length;
            settings.setSetting(SettingType.FRIEND_REQUESTS_POLICY, values[next]);
            this.refresh();
            getOwner().sendMessage(Component.text("Visibilité du temps de jeu mise à jour.", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }));

        // Visibilité de l'argent
        VisibilityLevel moneyVisibility = settings.getSetting(SettingType.FRIEND_VISIBILITY_MONEY);
        content.put(14, new ItemBuilder(this, Material.GOLD_INGOT, meta -> {
            meta.displayName(Component.text("Visibilité de l'argent", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Qui peut voir ton argent:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));

            for (VisibilityLevel level : VisibilityLevel.values()) {
                Component prefix = level == moneyVisibility
                        ? Component.text(" → ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                        : Component.text("    ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

                lore.add(prefix.append(Component.text(level.getDisplayName(),
                                level == moneyVisibility ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
            }

            lore.add(Component.empty());
            lore.add(Component.text(moneyVisibility.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            VisibilityLevel[] values = VisibilityLevel.values();
            int next = (moneyVisibility.ordinal() + 1) % values.length;
            settings.setSetting(SettingType.FRIEND_VISIBILITY_MONEY, values[next]);
            this.refresh();
            getOwner().sendMessage(Component.text("Visibilité du temps de jeu mise à jour.", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }));

        // Politique des demandes de rejoindre une ville
        CityJoinPolicy cityJoinPolicy = settings.getSetting(SettingType.CITY_JOIN_REQUESTS_POLICY);
        content.put(16, new ItemBuilder(this, Material.PAPER, meta -> {
            meta.displayName(Component.text("Demandes de rejoindre une ville", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Qui peut te demander à rejoindre une ville:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));

            for (CityJoinPolicy policy : CityJoinPolicy.values()) {
                Component prefix = policy == cityJoinPolicy
                        ? Component.text(" → ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                        : Component.text("    ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

                lore.add(prefix.append(Component.text(policy.getDisplayName(),
                                policy == cityJoinPolicy ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
            }

            lore.add(Component.empty());
            lore.add(Component.text(cityJoinPolicy.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            CityJoinPolicy[] values = CityJoinPolicy.values();
            int next = (cityJoinPolicy.ordinal() + 1) % values.length;
            settings.setSetting(SettingType.CITY_JOIN_REQUESTS_POLICY, values[next]);
            this.refresh();
            getOwner().sendMessage(Component.text("Visibilité du temps de jeu mise à jour.", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }));

        // Invitations de ville activées
        boolean cityInvitationsEnabled = settings.getSetting(SettingType.CITY_INVITATIONS_ENABLED);
        content.put(28, new ItemBuilder(this, cityInvitationsEnabled ? Material.LIME_DYE : Material.GRAY_DYE, meta -> {
            meta.displayName(Component.text("Invitations de ville", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(cityInvitationsEnabled ? "Activées" : "Désactivées",
                    cityInvitationsEnabled ? NamedTextColor.GREEN : NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            settings.setSetting(SettingType.CITY_INVITATIONS_ENABLED, !cityInvitationsEnabled);
            this.refresh();
            getOwner().sendMessage(Component.text("Visibilité du temps de jeu mise à jour.", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }));

        // Visibilité de la ville
        VisibilityLevel cityVisibility = settings.getSetting(SettingType.FRIEND_VISIBILITY_CITY);
        content.put(30, new ItemBuilder(this, Material.BRICKS, meta -> {
            meta.displayName(Component.text("Visibilité de la ville", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Qui peut voir ta ville:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));

            for (VisibilityLevel level : VisibilityLevel.values()) {
                Component prefix = level == cityVisibility
                        ? Component.text(" → ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                        : Component.text("    ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

                lore.add(prefix.append(Component.text(level.getDisplayName(),
                                level == cityVisibility ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
            }

            lore.add(Component.empty());
            lore.add(Component.text(cityVisibility.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            VisibilityLevel[] values = VisibilityLevel.values();
            int next = (cityVisibility.ordinal() + 1) % values.length;
            settings.setSetting(SettingType.FRIEND_VISIBILITY_CITY, values[next]);
            this.refresh();
            getOwner().sendMessage(Component.text("Visibilité du temps de jeu mise à jour.", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }));

        // Visibilité du statut en ligne
        VisibilityLevel statusVisibility = settings.getSetting(SettingType.FRIEND_VISIBILITY_STATUS);
        content.put(32, new ItemBuilder(this, Material.ENDER_EYE, meta -> {
            meta.displayName(Component.text("Visibilité du statut", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Qui peut voir ton statut en ligne:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));

            for (VisibilityLevel level : VisibilityLevel.values()) {
                Component prefix = level == statusVisibility
                        ? Component.text(" → ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                        : Component.text("    ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

                lore.add(prefix.append(Component.text(level.getDisplayName(),
                                level == statusVisibility ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
            }

            lore.add(Component.empty());
            lore.add(Component.text(statusVisibility.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            VisibilityLevel[] values = VisibilityLevel.values();
            int next = (statusVisibility.ordinal() + 1) % values.length;
            settings.setSetting(SettingType.FRIEND_VISIBILITY_STATUS, values[next]);
            this.refresh();
            getOwner().sendMessage(Component.text("Visibilité du temps de jeu mise à jour.", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }));

        // Visibilité du temps de jeu
        VisibilityLevel playtimeVisibility = settings.getSetting(SettingType.FRIEND_VISIBILITY_PLAYTIME);
        content.put(34, new ItemBuilder(this, Material.CLOCK, meta -> {
            meta.displayName(Component.text("Visibilité du temps de jeu", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Qui peut voir ton temps de jeu:", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));

            for (VisibilityLevel level : VisibilityLevel.values()) {
                Component prefix = level == playtimeVisibility
                        ? Component.text(" → ", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                        : Component.text("    ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

                lore.add(prefix.append(Component.text(level.getDisplayName(),
                                level == playtimeVisibility ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)));
            }

            lore.add(Component.empty());
            lore.add(Component.text(playtimeVisibility.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Clique pour changer", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }).setOnClick(e -> {
            VisibilityLevel[] values = VisibilityLevel.values();
            int next = (playtimeVisibility.ordinal() + 1) % values.length;
            settings.setSetting(SettingType.FRIEND_VISIBILITY_PLAYTIME, values[next]);
            this.refresh();
            getOwner().sendMessage(Component.text("Visibilité du temps de jeu mise à jour.", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }));

        // Bouton de fermeture
        content.put(40, new ItemBuilder(this, Material.BARRIER, meta -> {
            meta.displayName(Component.text("Fermer", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        }).setCloseButton());

        return content;
    }

    private void refresh() {
        new PlayerSettingsMenu(getOwner()).open();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {}

    @Override
    public void onClose(InventoryCloseEvent event) {}

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}