package fr.openmc.core.features.millestones.tutorial;

import fr.openmc.core.features.millestones.Millestone;
import fr.openmc.core.features.millestones.tutorial.quests.BreakAyweniteQuest;
import fr.openmc.core.features.millestones.tutorial.quests.CityCreateQuest;
import fr.openmc.core.features.quests.objects.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TutorialMillestone extends Millestone {
    @Override
    public String getName() {
        return "§7Tutoriel d'OpenMC";
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                Component.text("§7Découvrez §dOpenMC §7!"),
                Component.text("§7Passez en revue les §dFeatures"),
                Component.text("§8§oLes Villes, les Contests, l'Admin Shop, les Quêtes, ..."),
                Component.text("§7Idéal pour se lancer dans l'aventure !")
        );
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GRASS_BLOCK);
    }

    @Override
    public List<Quest> getSteps() {
        return List.of(
                new BreakAyweniteQuest(),
                new CityCreateQuest()
        );
    }
}
