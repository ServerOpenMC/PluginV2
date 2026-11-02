package fr.openmc.core.features.milestones.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.NoCityMenu;
import fr.openmc.core.features.city.sub.milestone.menu.CityMilestoneMenu;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.milestones.commands.autocomplete.PlayerMilestoneAutocomplete;
import fr.openmc.core.features.milestones.menus.MainMilestonesMenu;
import fr.openmc.core.features.milestones.menus.MilestoneMenu;
import fr.openmc.core.features.milestones.models.MilestoneType;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Command("milestones")
@CommandPermission("omc.commands.milestones")
public class MilestoneCommand {

    /**
     * Nom, dans la commande, du milestone de ville.
     * Il n'est pas un {@link MilestoneType} (il dépend de la ville du joueur), mais il est
     * accessible depuis le menu principal des milestones au même titre que les autres.
     */
    public static final String CITY_MILESTONE = "city";

    @CommandPlaceholder()
    void mainCommand(
            Player player,
            @Named("milestone") @Optional @SuggestWith(PlayerMilestoneAutocomplete.class) String milestoneName
    ) {
        if (milestoneName == null || milestoneName.isBlank()) {
            if (DreamUtils.isInDreamWorld(player)) new MilestoneMenu(player, MilestoneType.DREAM.getMilestone()).open();
            else new MainMilestonesMenu(player).open();
            return;
        }

        if (milestoneName.equalsIgnoreCase(CITY_MILESTONE)) {
            // Même comportement que le bouton "Ville" du menu principal
            City city = CityManager.getPlayerCity(player.getUniqueId());

            if (city == null) new NoCityMenu(player).open();
            else new CityMilestoneMenu(player, city).open();
            return;
        }

        for (MilestoneType type : MilestoneType.values()) {
            if (type.name().equalsIgnoreCase(milestoneName)) {
                type.getMilestone().getMenu(player).open();
                return;
            }
        }

        MessagesManager.sendMessage(player,
                TranslationManager.translation(
                        "feature.milestones.command.not_found",
                        Component.text(milestoneName).color(NamedTextColor.YELLOW),
                        Component.text(String.join(", ", getMilestoneNames())).color(NamedTextColor.YELLOW)
                ),
                Prefix.MILLESTONE, MessageType.ERROR, true);
    }

    /**
     * @return Les noms (en minuscules) des milestones ouvrables via {@code /milestones <milestone>}
     */
    public static List<String> getMilestoneNames() {
        List<String> names = new ArrayList<>();

        for (MilestoneType type : MilestoneType.values()) {
            names.add(type.name().toLowerCase(Locale.ROOT));
        }

        names.add(CITY_MILESTONE);

        return names;
    }
}
