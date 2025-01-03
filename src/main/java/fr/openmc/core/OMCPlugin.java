package fr.openmc.core;

import dev.xernas.menulib.MenuLib;
import fr.openmc.core.commands.CommandsManager;
import fr.openmc.core.features.ScoreboardManager;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.listeners.ListenersManager;
import fr.openmc.core.utils.LuckPermsAPI;
import fr.openmc.core.utils.PapiAPI;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.MotdUtils;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import fr.openmc.core.features.weatherwand.WeatherGUIListener;
import fr.openmc.core.features.weatherwand.MeteoWand;

import java.sql.SQLException;

public final class OMCPlugin extends JavaPlugin {
    @Getter static OMCPlugin instance;
    @Getter static FileConfiguration configs;
    private DatabaseManager dbManager;

    @Override
    public void onEnable() {
        instance = this;

        /* CONFIG */
        saveDefaultConfig();
        configs = this.getConfig();

        /* EXTERNALS */
        MenuLib.init(this);
        new LuckPermsAPI(this);
        new PapiAPI();

        /* MANAGERS */
        dbManager = new DatabaseManager();
        new CommandsManager();
        CustomItemRegistry.init();
        ContestManager contestManager = new ContestManager(this);
        ContestPlayerManager contestPlayerManager = new ContestPlayerManager();
        new SpawnManager(this);
        new CityManager();
        new ListenersManager();
        new EconomyManager();
        new MailboxManager();
        contestPlayerManager.setContestManager(contestManager); // else ContestPlayerManager crash because ContestManager is null
        contestManager.setContestPlayerManager(contestPlayerManager);
        new MotdUtils(this);

        getServer().getPluginManager().registerEvents(new WeatherGUIListener(), this);

        addCustomRecipes();

        getLogger().info("Plugin activé");
    }

    @Override
    public void onDisable() {
        ContestManager.getInstance().saveContestData();
        ContestManager.getInstance().saveContestPlayerData();
        if (dbManager != null) {
            try {
                dbManager.close();
            } catch (SQLException e) {
                getLogger().severe("Impossible de fermer la connexion à la base de données");
            }
        }

        getLogger().info("Plugin désactivé");
    }

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    /**
     * Méthode responsable d'ajouter les recettes personnalisées.
     */
    private void addCustomRecipes() {
        addMeteoWandRecipe();
    }

    /**
     * Ajoute la recette pour fabriquer le Meteo Wand.
     */
    private void addMeteoWandRecipe() {
        ItemStack meteoWand = new MeteoWand().getVanilla();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "meteowand"), meteoWand);
        recipe.shape(
                " S ",
                " B ",
                " D "
        );
        recipe.setIngredient('S', Material.NETHER_STAR);
        recipe.setIngredient('B', Material.STICK);
        recipe.setIngredient('D', Material.DIAMOND);

        Bukkit.addRecipe(recipe);
    }
}