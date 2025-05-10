package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.mascots.MascotsListener;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.mascots.MascotsManager;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.interactions.items.ItemInteraction;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.mascots.MascotsListener.futurCreateCity;
import static fr.openmc.core.features.city.mascots.MascotsListener.movingMascots;

public class CityTypeMenu extends Menu {
    String name;
    public CityTypeMenu(Player owner, String name) {
        super(owner);
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes - Type";
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
        Player player = getOwner();
        try {
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
                itemMeta.displayName(Component.text("§aVille en paix"));
                itemMeta.lore(peaceInfo);
            }).setOnClick(inventoryClickEvent -> {
                runChoiceType(player, "peace");
            }));

            map.put(15, new ItemBuilder(this, Material.DIAMOND_SWORD, itemMeta -> {
                itemMeta.displayName(Component.text("§cVille en guerre"));
                itemMeta.lore(warInfo);
            }).setOnClick(inventoryClickEvent -> {
                runChoiceType(player, "war");
            }));

            return map;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return map;
    }

    private void runChoiceType(Player player, String type) {
        futurCreateCity.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(name, type);

        ItemStack mascotsItem = CustomItemRegistry.getByName("omc_items:mascot_stick").getBest();
        ItemMeta meta = mascotsItem.getItemMeta();

        if (meta != null) {
            List<Component> info = new ArrayList<>();
            info.add(Component.text("§cVotre mascotte sera posé a l'emplacement du coffre et créera votre ville"));
            info.add(Component.text("§cCe coffre n'est pas retirable"));
            info.add(Component.text("§clors de votre déconnection la création sera annuler"));

            meta.displayName(Component.text("§lMascotte"));
            meta.lore(info);
        }

        mascotsItem.setItemMeta(meta);

        ItemInteraction.runLocationInteraction(
                player,
                mascotsItem,
                "Mascot:chest",
                300,
                "Vous avez reçu un coffre pour poser votre mascotte",
                "§cCréation annulée",
                mascotSpawn -> {
                    if (mascotSpawn == null) return true;

                    World world = Bukkit.getWorld("world");
                    World player_world = player.getWorld();

                    if (player_world!=world){
                        MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le coffre dans ce monde"), Prefix.CITY, MessageType.INFO, false);
                        return false;
                    }

                    if (mascotSpawn.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                        MessagesManager.sendMessage(player, Component.text("§cIl ne doit pas y avoir de block au dessus du coffre"), Prefix.CITY, MessageType.INFO, false);
                        return false;
                    }

                    if (!futurCreateCity.containsKey(player.getUniqueId())){
                        MessagesManager.sendMessage(player,MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                        return false;
                    }

                    Chunk chunk = mascotSpawn.getChunk();

                    String cityName = futurCreateCity.get(player.getUniqueId()).keySet().iterator().next();
                    boolean cityAdd = CityCommands.createCity(player, cityName, futurCreateCity.get(player.getUniqueId()).get(cityName), chunk);

                    // on return true maintenant pour eviter que createCity s'execute plusieurs fois
                    if (!cityAdd){
                        return true;
                    }

                    futurCreateCity.remove(player.getUniqueId());
                    City city = CityManager.getPlayerCity(player.getUniqueId());

                    if (city==null){
                        MessagesManager.sendMessage(player, Component.text("§cErreur : la ville n'a pas été reconnu"), Prefix.CITY, MessageType.ERROR, false);
                        return true;
                    }

                    String city_uuid = city.getUUID();

                    if (MascotUtils.mascotsContains(city_uuid) && !movingMascots.contains(city_uuid)){
                        MessagesManager.sendMessage(player, Component.text("§cVous possédez déjà une mascotte"), Prefix.CITY, MessageType.INFO, false);
                        return true;
                    }

                    player_world.getBlockAt(mascotSpawn).setType(Material.AIR);

                    MascotsManager.createMascot(city_uuid, player_world, mascotSpawn);
                    return true;
                }
        );
        getOwner().closeInventory();
    }
}
