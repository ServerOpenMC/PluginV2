package fr.openmc.core.features.city.sub.mayor.perks;

import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.items.CustomItemRegistry;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
                    Component.text("Donne", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("un effet de force I", NamedTextColor.DARK_AQUA)
                        .appendSpace()
                        .append(Component.text("dans une ville adverse", NamedTextColor.GRAY))),
                    Component.text("Et donne", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("un effet de protection I", NamedTextColor.DARK_AQUA)
                        .appendSpace()
                        .append(Component.text("dans sa ville", NamedTextColor.GRAY)))
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
                    Component.text("Possibilité de lancer un", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("évènement", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("pour prélever les impôts", NamedTextColor.GRAY)),
                    Component.text("§7Limite de perte d'argent :")
                        .appendSpace()
                        .append(Component.text("5k", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("(§7Cooldown : 3j§8)"))
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
                    Component.text("Donne", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("Haste I", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("aux membres de la ville", NamedTextColor.GRAY))
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
                    Component.text("Augmente", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("la portée", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("de tous les membres de la ville", NamedTextColor.GRAY)),
                    Component.text("de", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("1", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("bloc", NamedTextColor.GRAY))
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
                    Component.text("Ajout", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("2", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("à l'intérêt de la banque", NamedTextColor.GRAY))
                        .appendNewline()
                        .append(Component.text("de la ville et des joueurs!", NamedTextColor.GRAY))
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
                    Component.text("Fait apparaître", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("un", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("golem de fer", NamedTextColor.GRAY))
                        .appendSpace()
                        .append(Component.text("lorsque la mascotte")),
                    Component.text("se fait taper par l'ennemi", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("(Cooldown : 3 min)", NamedTextColor.DARK_GRAY))
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
                    Component.text("Augmente de", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("20 %", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("les dégâts infligés aux", NamedTextColor.GRAY)),
                    Component.text("monstres", NamedTextColor.DARK_AQUA)
                        .appendSpace()
                        .append(Component.text("et"))
                        .appendSpace()
                        .append(Component.text("joueurs", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("dans sa propre ville.", NamedTextColor.GRAY))
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
                    Component.text("Casser", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("une pierre", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("donne 1% de chance d'avoir 2 d'Aywenites", NamedTextColor.GRAY))
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
                    Component.text("Lorsqu'un")
                    .appendSpace()
                    .append(Component.text("ennemi", NamedTextColor.DARK_AQUA))
                    .appendSpace()
                    .append(Component.text("rentre dans votre ville,", NamedTextColor.GRAY)),
                    Component.text("un", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("effet de glowing", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("lui est donné.", NamedTextColor.GRAY))
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
                    Component.text("Réduit les dégâts subis de", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("15%", NamedTextColor.DARK_AQUA)),
		            Component.text("lorsque vous êtes autour de votre", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("mascotte", NamedTextColor.DARK_AQUA))
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
                    Component.text("La récolte est doublée pendant", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("30 min", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("dans la ville", NamedTextColor.GRAY))
                        .appendSpace()
                        .append(Component.text("(Cooldown : 1j)", NamedTextColor.DARK_GRAY))
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
                    Component.text("Tous les minerais extraits pendant", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("5 minutes", NamedTextColor.DARK_AQUA))
                        .appendSpace(),
                    Component.text("donnent le double de ressources", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("(Cooldown : 1j)", NamedTextColor.DARK_GRAY))
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
		            Component.text("Fait apparaître", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("10 golems de fer", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("partout", NamedTextColor.GRAY)),
                    Component.text("dans votre ville qui disparaissent dans", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("10 min", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("(Cooldown : 25 min)", NamedTextColor.DARK_GRAY))
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
                Component.text("Fait apparaître de l'Aywenite dans votre ville pendant", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("1 min", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("(Cooldown : 1j)", NamedTextColor.DARK_GRAY))
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
                    Component.text("§7A partir du §clevel 4 §7de la mascotte, vous")
                    .appendSpace()
                    .append(Component.text("level 4", NamedTextColor.RED))
                    .appendSpace()
                    .append(Component.text("de la mascotte, vous", NamedTextColor.GRAY)),
                    Component.text("aurez des", NamedTextColor.GRAY)
                    .appendSpace()
                    .append(Component.text("effets bonus", NamedTextColor.DARK_AQUA))
                    .appendSpace()
                    .append(Component.text("si la mascotte est en vie !", NamedTextColor.GRAY))
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
                    Component.text("Augmente de", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("40%", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("la probabilité", NamedTextColor.GRAY)),
                    Component.text("de faire un", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("rêve", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text(".", NamedTextColor.GRAY))
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
                    Component.text("Augmente de", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("60%", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text("le temps dans", NamedTextColor.GRAY)),
                    Component.text("les", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(Component.text("rêves", NamedTextColor.DARK_AQUA))
                        .appendSpace()
                        .append(Component.text(".", NamedTextColor.GRAY))
            ),
            DreamItemRegistry.getByName("omc_dream:somnifere").getBest()
    ),
    CHAOS_DREAM(
            18,
            PerkType.EVENT,
            PerkCategory.DREAM,
            24 * 60 * 60 * 1000L, // 1 jour
            "§e§lRêve Chaotique",
            List.of(
                    Component.text("Envoie tout les membres connectés dans", NamedTextColor.GRAY),
                    Component.text("les rêves", NamedTextColor.DARK_AQUA)
                        .appendSpace()
                        .append(Component.text("(Cooldown : 1j)", NamedTextColor.DARK_GRAY))
            ),
            DreamItemRegistry.getByName("omc_dream:singularity").getBest()
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
