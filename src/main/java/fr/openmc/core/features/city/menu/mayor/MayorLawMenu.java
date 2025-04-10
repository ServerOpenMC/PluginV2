package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.CityLaw;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.chronometer.Chronometer;
import fr.openmc.core.utils.cooldown.DynamicCooldownManager;
import fr.openmc.core.utils.menu.MenuUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MayorLawMenu extends Menu {

    private static final long COOLDOWN_TIME_ANNOUNCE = 3 * 60 * 60 * 1000L; // 3 heures en ms
    private static final long COOLDOWN_TIME_WARP = 60 * 60 * 1000L; // 1 heure en ms
    private static final long COOLDOWN_TIME_PVP = 4 * 60 * 60 * 1000L; // 4 heures en ms

    public MayorLawMenu(Player owner) {
        super(owner);
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
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();



        try {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            Mayor mayor = city.getMayor();

            CityLaw law = city.getLaw();

            String nameLawPVP = law.isPvp() ? "§cDésactiver §7le PVP" : "§4Activer §7le PVP";
            List<Component> loreLawPVP = List.of(
                        Component.text("§7Cette §1loi " + (law.isPvp() ? "§4active" : "§cdésactive") +  " §7le PVP dans toute la §dVille"),
                        Component.text("§7entre les membres !"),
                        Component.text(""),
                        Component.text("§e§lCLIQUEZ ICI POUR " + (law.isPvp() ? "ACTIVER" : "DESACTIVER") +  " LE PVP")
                );
            inventory.put(9, new ItemBuilder(this, Material.IRON_SWORD, itemMeta -> {
                itemMeta.itemName(Component.text(nameLawPVP));
                itemMeta.lore(loreLawPVP);
            }).setOnClick(inventoryClickEvent -> {
                if (law.isPvp()) {
                    law.setPvp(false);
                } else {
                    law.setPvp(true);
                }
                String messageLawPVP = law.isPvp() ? "§7Vous avez §cdésactivé §7le PVP dans votre ville" : "§7Vous avez §4activé §7le PVP dans votre ville";
                MessagesManager.sendMessage(player, Component.text(messageLawPVP), Prefix.MAYOR, MessageType.SUCCESS, false);
            }));

            Location warpLoc = law.getWarp();
            List<Component> loreLawWarp;
            if (warpLoc == null) {
                loreLawWarp = List.of(
                        Component.text("§7Cette §1loi §7n'est pas effective!"),
                        Component.text("§7Vous devez choisir un endroit où les membres pourront"),
                        Component.text("§7arriver"),
                        Component.text(""),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR UN ENDROIT")
                );
            } else {
                loreLawWarp = List.of(
                        Component.text("§7Les membres peuvent se téléporter à votre warp!"),
                        Component.text("§7Voici la position du warp : "),
                        Component.text("§8- §7x=§6" + warpLoc.getX()),
                        Component.text("§8- §7y=§6" + warpLoc.getY()),
                        Component.text("§8- §7z=§6" + warpLoc.getZ()),
                        Component.text(""),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR UN ENDROIT")
                );
            }
            inventory.put(11, new ItemBuilder(this, Material.ENDER_PEARL, itemMeta -> {
                itemMeta.itemName(Component.text("§7Changer son warp"));
                itemMeta.lore(loreLawWarp);
            }).setOnClick(inventoryClickEvent -> {
              // le bail de donner un coffre et tt
            }));

            List<Component> loreLawAnnounce = new ArrayList<>(List.of(
                    Component.text("§7Cette §1loi §7permet d'émettre un message dans toute la ville!")
            ));

            if (!DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:city-announce")) {
                loreLawAnnounce.addAll(
                        List.of(
                                Component.text(""),
                                Component.text("§cCooldown §7: " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(mayor.getUUID().toString(), "mayor:city-announce")))
                        )
                );
            }

            loreLawAnnounce.addAll(
                    List.of(
                            Component.text(""),
                            Component.text("§e§lCLIQUEZ ICI POUR ECRIRE LE MESSAGE")
                    )
            );


            ItemStack itemAnnounce =  new ItemBuilder(this, Material.BELL, itemMeta -> {
                itemMeta.itemName(Component.text("§7Faire une annonce"));
                itemMeta.lore(loreLawAnnounce);
            }).setOnClick(inventoryClickEvent -> {
                if (DynamicCooldownManager.isReady(mayor.getUUID().toString(), "mayor:city-announce")) {
                    System.out.println("input from user");
                    DynamicCooldownManager.use(mayor.getUUID().toString(), "mayor:city-announce", COOLDOWN_TIME_ANNOUNCE);
                }
            });

            MenuUtils.runDynamicItem(player, this, itemAnnounce, 12)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

            Perks perkEvent = PerkManager.getPerkEvent(mayor);
            if (PerkManager.getPerkEvent(mayor) != null) {
                ItemStack iaPerkEvent = perkEvent.getItemStack();
                String namePerkEvent = perkEvent.getName();
                List<Component> lorePerkEvent = perkEvent.getLore();
                inventory.put(14, new ItemBuilder(this, iaPerkEvent, itemMeta -> {
                    itemMeta.itemName(Component.text(namePerkEvent));
                    itemMeta.lore(lorePerkEvent);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }));
            }

            return inventory;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return null;
    }
}
