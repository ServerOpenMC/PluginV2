package fr.openmc.core.features.homes.utils;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.HomeIcons;
import fr.openmc.core.utils.WorldGuardApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class HomeUtil {

    private static final List<HomeIcons> HOME_ICONS = Arrays.stream(HomeIcons.values()).toList();

    public static HomeIcons getHomeIcon(Home home) {
        try {
            String homeName = home.getName();
            if(home.getIcon() == null) {
                return HomeIcons.DEFAULT;
            }
            if (home.getIcon() != HomeIcons.DEFAULT) {
                return home.getIcon();
            }

            for (HomeIcons icon : HOME_ICONS) {
                String[] usages = icon.getUsage().split("\\|");
                for (String usage : usages) {
                    if (homeName.contains(usage)) {
                        return icon;
                    }
                }
            }
            return HomeIcons.DEFAULT;
        } catch (Exception e) {
            OMCPlugin.getInstance().getLogger().severe("Error while getting home icon for home " + home.getName());
            return HomeIcons.DEFAULT;
        }
    }

    public static ItemStack getRandomsIcons() {
        String iconKey = String.valueOf(HOME_ICONS.get((int) (Math.random() * HOME_ICONS.size())).getId());
        return CustomStack.getInstance(iconKey).getItemStack();
    }

    public static HomeIcons getDefaultHomeIcon(String name) {
        return HOME_ICONS.stream()
                .filter(entry -> name.matches(".*" + entry.getUsage() + ".*"))
                .findFirst()
                .orElse(HomeIcons.DEFAULT);
    }

    public static HomeIcons getHomeIcon(String iconId) {
        return HOME_ICONS.stream()
                .filter(entry -> entry.getId().equals(iconId))
                .findFirst()
                .orElse(HomeIcons.DEFAULT);
    }

    public static ItemStack getHomeIconItem(Home home) {
        String iconKey = getHomeIcon(home).getId();
        if(iconKey == null) {
            OMCPlugin.getInstance().getLogger().severe("Error while getting home icon for home " + home.getName());
            return new ItemStack(Material.GRASS_BLOCK);
        }
        return CustomStack.getInstance(iconKey).getItemStack();
    }

    public static boolean checkName(Player player, MessagesManager msg, String name) {
        if(WorldGuardApi.isRegionConflict(player.getLocation())) {
            MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas ajouter un home dans une région protégée !"), Prefix.HOME, MessageType.ERROR, true);
            return true;
        }

        if(name.length() < 3) {
            MessagesManager.sendMessage(player, Component.text("§cLe nom de votre home doit contenir au moins 3 caractères !"), Prefix.HOME, MessageType.ERROR, true);
            return true;
        }

        if(name.length() > 16) {
            MessagesManager.sendMessage(player, Component.text("§cLe nom de votre home ne doit pas dépasser 16 caractères !"), Prefix.HOME, MessageType.ERROR, true);
            return true;
        }

        if(!name.matches("[a-zA-Z0-9]+")) {
            MessagesManager.sendMessage(player, Component.text("§cLe nom de votre home ne doit contenir que des caractères alphanumériques !"), Prefix.HOME, MessageType.ERROR, true);
            return true;
        }

        return false;
    }



}
