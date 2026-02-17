package fr.openmc.core.features.city.sub.mayor.perks;

import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Getter
public enum Perks {
    FOU_DE_RAGE(
            1,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "§e§lFou de Rage",
            List.of(
                    Component.text("§7Donne §3un effet de force I §7dans une ville adverse"),
                    Component.text("§7Et donne §3un effet de protection I§7 dans sa ville")
            ),
            ItemStack.of(Material.BLAZE_POWDER)
    ),
    IMPOT(
            2,
            PerkType.EVENT,
            PerkCategory.ECONOMIC,
            3 * 24 * 60 * 60 * 1000L, // 3 jours
            "§e§lPrévélement d'Impot",
            List.of(
		            Component.text("§7Possibilité de lancer un §3évènement §7pour prélever les impôts"),
                    Component.text("§7Limite de perte d'argent :§3 5k §8(Cooldown : 3j)")
            ),
            ItemStack.of(Material.GOLD_BLOCK)
    ),
    MINER(
            3,
            PerkType.BASIC,
            PerkCategory.AGRICULTURAL,
            0,
            "§e§lMineur Dévoué",
            List.of(
                    Component.text("§7Donne §3Haste I §7aux membres de la ville")
            ),
            ItemStack.of(Material.GOLDEN_PICKAXE),
            DataComponentTypes.ATTRIBUTE_MODIFIERS
    ),
    FRUIT_DEMON(
            4,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "§e§lFruit du Démon",
            List.of(
                    Component.text("§7Augmente §3la portée §7de tous les membres de la ville"),
                    Component.text("§7de §31 §7bloc")
            ),
            ItemStack.of(Material.CHORUS_FRUIT)
    ),
    BUSINESS_MAN(
            5,
            PerkType.BASIC,
            PerkCategory.ECONOMIC,
            0,
            "§e§lBuisness Man",
            List.of(
                    Component.text("§7Ajout §32% à l'intérêt de la banque"),
                    Component.text("§7de la ville et des joueurs!")
            ),
            ItemStack.of(Material.DIAMOND)
    ),
    IRON_BLOOD(
            6,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "§e§lFer dans le Sang",
            List.of(
		            Component.text("§7Fait apparaître un §3golem de fer §7lorsque la mascotte"),
                    Component.text("§7se fait taper par l'ennemi §8(Cooldown : 3 min)")
            ),
            ItemStack.of(Material.IRON_BLOCK)
    ),
    CITY_HUNTER(
            7,
            PerkType.BASIC,
            PerkCategory.STRATEGY,
            0,
            "§e§lChasseur Urbain",
            List.of(
                    Component.text("§7Augmente de §320 % §7les dégâts infligés aux"),
                    Component.text("§3monstres §7et §3joueurs §7dans sa propre ville.")
            ),
            ItemStack.of(Material.BOW)
    ),
    AYWENITER(
            8,
            PerkType.BASIC,
            PerkCategory.AGRICULTURAL,
            0,
            "§e§lAyweniteur",
            List.of(
                    Component.text("§7Casser une pierre donne 1% de chance d'avoir 2 d'Aywenites")
            ),
            CustomItemRegistry.getByName("omc_items:aywenite").getBest()
    ),
    GPS_TRACKER(
            9,
            PerkType.BASIC,
            PerkCategory.STRATEGY,
            0,
            "§e§lTraceur GPS",
            List.of(
                    Component.text("§7Lorsqu'un §3ennemi §7rentre dans votre ville,"),
                    Component.text("§7un §3effet de glowing §7lui est donné.")
            ),
            ItemStack.of(Material.COMPASS)
    ),
    SYMBIOSIS(
            10,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "§e§lSymbiose",
            List.of(
                    Component.text("§7Réduit les dégâts subis de §315%"),
		            Component.text("§7lorsque vous êtes autour de votre §3mascotte")
            ),
            ItemStack.of(Material.SCULK_CATALYST)
    ),
    AGRICULTURAL_ESSOR(
            11,
            PerkType.EVENT,
            PerkCategory.AGRICULTURAL,
            24 * 60 * 60 * 1000L, // 1 jour
            "§e§lEssor Agricole",
            List.of(
		            Component.text("§7La récolte est doublée pendant§3 30 min §7dans la ville §8(Cooldown : 1j)")
            ),
            ItemStack.of(Material.NETHERITE_HOE),
            DataComponentTypes.ATTRIBUTE_MODIFIERS
    ),
    MINERAL_RUSH(
            12,
            PerkType.EVENT,
            PerkCategory.AGRICULTURAL,
            24 * 60 * 60 * 1000L, // 1 jour
            "§e§lRuée Minière",
            List.of(
                    Component.text("§7Tous les minerais extraits pendant§3 5 §7minutes"),
		            Component.text("§7donnent le double de ressources §8(Cooldown : 1j)")
            ),
            ItemStack.of(Material.DIAMOND_PICKAXE),
            DataComponentTypes.ATTRIBUTE_MODIFIERS
    ),
    MILITARY_DISSUASION(
            13,
            PerkType.EVENT,
            PerkCategory.STRATEGY,
            25 * 60 * 1000L, // 25 minutes
            "§e§lDissuasion Militaire",
            List.of(
		            Component.text("§7Fait apparaître §310 golems de fer §7partout"),
                    Component.text("§7dans votre ville qui disparaissent dans §310 min §8(Cooldown : 25 min)")
            ),
            ItemStack.of(Material.IRON_GOLEM_SPAWN_EGG)
    ),
    IDYLLIC_RAIN(
            14,
            PerkType.EVENT,
            PerkCategory.ECONOMIC,
            24 * 60 * 60 * 1000L, // 1 jour
            "§e§lPluie idyllique",
            List.of(
		            Component.text("§7Fait apparaître de l'§3Aywenite §7dans votre ville pendant§3 1 §7min §8(Cooldown : 1j)")
            ),
            ItemStack.of(Material.GHAST_TEAR)
    ),
    MASCOTS_FRIENDLY(
            15,
            PerkType.BASIC,
            PerkCategory.MILITARY,
            0,
            "§e§lMascotte de Compagnie",
            List.of(
		            Component.text("§7A partir du §clevel 4 §7de la mascotte, vous"),
                    Component.text("§7aurez des §3effets bonus §7si la mascotte est en vie !")
            ),
            ItemStack.of(Material.SADDLE)
    ),
    GREAT_SLEEPER(
            16,
            PerkType.BASIC,
            PerkCategory.DREAM,
            0,
            "§e§lGrand Dormeur",
            List.of(
                    Component.text("§7Augmente de §340% la probabilité"),
                    Component.text("§7de faire un §3rêve.")
            ),
            ItemStack.of(Material.WHITE_BED)
    ),
    GREAT_DREAM(
            17,
            PerkType.BASIC,
            PerkCategory.DREAM,
            0,
            "§e§lGrand Rêveur",
            List.of(
                    Component.text("§7Augmente de §360% le temps dans"),
                    Component.text("§7les §3rêves.")
            ),
            DreamItemRegistry.getByName("somnifere").getBest()
    ),
    CHAOS_DREAM(
            18,
            PerkType.EVENT,
            PerkCategory.DREAM,
            24 * 60 * 60 * 1000L, // 1 jour
            "§e§lRêve Chaotique",
            List.of(
                    Component.text("§7Envoie tout les membres connectés dans"),
                    Component.text("§3les rêves §8(Cooldown : 1j)")
            ),
            DreamItemRegistry.getByName("singularity").getBest()
    )
    ;

    private final int id;
    private final PerkType type;
    private final PerkCategory category;
    private final long cooldown;
    private final String name;
    private final List<Component> lore;
    private final ItemStack itemStack;
    private final DataComponentType[] toHide;

    Perks(int id, PerkType type, PerkCategory category, long cooldown, String name, List<Component> lore, ItemStack itemStack, DataComponentType... toHide) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.cooldown = cooldown;
        this.name = name;
        this.lore = lore;
        this.itemStack = itemStack;
        this.toHide = toHide;
    }
}
