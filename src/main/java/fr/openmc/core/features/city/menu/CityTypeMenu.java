package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.utils.BlockVector2;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.chronometer.ChronometerType;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.database.DatabaseManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CityTypeMenu extends Menu {

    Player player;
    String name;
    public CityTypeMenu(Player owner, String name) {
        super(owner);
        this.player = owner;
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> map = new HashMap<>();

        List<Component> peaceInfo = new ArrayList<>();
        peaceInfo.add(Component.text("§aLa sécurité est assurée"));
        peaceInfo.add(Component.text("§fObjectif : relaxez vous et construisez la"));
        peaceInfo.add(Component.text("§fville de vos rêves"));

        List<Component> warInfo = new ArrayList<>();
        warInfo.add(Component.text("§cLa guerre vous attend"));
        warInfo.add(Component.text("§fObjectif : devenir la ville la plus puissante"));
        warInfo.add(Component.text("§cATTENTION : les autres villes en situation de guerre"));
        warInfo.add(Component.text("§cpeuvent tuer votre mascotte et détruire les constructions"));

        map.put(11, new ItemBuilder(this, Material.POPPY, itemMeta -> {
            itemMeta.setDisplayName("§aVille en paix");
            itemMeta.lore(peaceInfo);
        }).setOnClick(inventoryClickEvent -> {
            createCity("peace");
        }));

        map.put(15, new ItemBuilder(this, Material.DIAMOND_SWORD, itemMeta -> {
            itemMeta.setDisplayName("§cVille en guerre");
            itemMeta.lore(warInfo);
        }).setOnClick(inventoryClickEvent -> {
            createCity("war");
        }));

        return map;
    }

    private void createCity(String type) {

        UUID uuid = player.getUniqueId();

        MessagesManager.sendMessage(player, Component.text("Votre ville est en cours de création..."), Prefix.CITY, MessageType.INFO, false);

        String cityUUID = UUID.randomUUID().toString().substring(0, 8);

        Chunk origin = player.getChunk();
        AtomicBoolean isClaimed = new AtomicBoolean(false);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (CityManager.isChunkClaimed(origin.getX() + x, origin.getZ() + z)) {
                    isClaimed.set(true);
                    break;
                }
            }
        }

        if (isClaimed.get()) {
            MessagesManager.sendMessage(player, Component.text("Cette parcelle est déjà claim"), Prefix.CITY, MessageType.ERROR, false);
            player.closeInventory();
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = DatabaseManager.getConnection().prepareStatement("INSERT INTO city_regions (city_uuid, x, z) VALUES (?, ?, ?)");
                statement.setString(1, cityUUID);

                statement.setInt(2, origin.getX());
                statement.setInt(3, origin.getZ());
                statement.addBatch();

                statement.executeBatch();
                statement.close();
            } catch (SQLException e) {
                MessagesManager.sendMessage(player, Component.text("Une erreur est survenue, réessayez plus tard"), Prefix.CITY, MessageType.ERROR, false);
                player.closeInventory();
                throw new RuntimeException(e);
            }
        });

        City city = CityManager.createCity(player, cityUUID, name, type);
        city.addPlayer(uuid);
        city.addPermission(uuid, CPermission.OWNER);

        CityManager.claimedChunks.put(BlockVector2.at(origin.getX(), origin.getZ()), city);
        MascotsManager.addFreeClaim(15, player);

        player.closeInventory();

        MessagesManager.sendMessage(player, Component.text("Votre ville a été créée : " + name), Prefix.CITY, MessageType.SUCCESS, true);
        MessagesManager.sendMessage(player, Component.text("Vous disposez de 15 claims gratuits"), Prefix.CITY, MessageType.SUCCESS, false);

        DynamicCooldownManager.use(uuid, "city:big", 60000); //1 minute

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        Chronometer.startChronometer(player, "Mascot:chest", 300, ChronometerType.ACTION_BAR, null, ChronometerType.ACTION_BAR, "Mascote posé en " + x +" " + y + " " + z);
        MascotsManager.giveChest(player);

    }
}
