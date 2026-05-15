package fr.openmc.core.features.city;

import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

@Getter
public enum CityPermission {
    OWNER(TranslationManager.translation("feature.city.permission.owner"), Material.NETHERITE_BLOCK), //Impossible à donner sauf avec un transfert
    INVITE(TranslationManager.translation("feature.city.permission.invite"), Material.OAK_DOOR),
    KICK(TranslationManager.translation("feature.city.permission.kick"), Material.IRON_DOOR),
    PLACE(TranslationManager.translation("feature.city.permission.place"), Material.OAK_LOG),
    BREAK(TranslationManager.translation("feature.city.permission.break"), Material.STONE_PICKAXE),
    OPEN_CHEST(TranslationManager.translation("feature.city.permission.open_chest"), Material.CHEST),
    INTERACT(TranslationManager.translation("feature.city.permission.interact"), Material.LEVER),
    CLAIM(TranslationManager.translation("feature.city.permission.claim"), Material.GRASS_BLOCK),
    SEE_CHUNKS(TranslationManager.translation("feature.city.permission.see_claim"), Material.MAP),
    RENAME(TranslationManager.translation("feature.city.permission.rename"), Material.NAME_TAG),
    MONEY_DEPOSIT(TranslationManager.translation("feature.city.permission.money_deposit"), Material.EMERALD),
    MONEY_BALANCE(TranslationManager.translation("feature.city.permission.money_see"), Material.GOLD_INGOT),
    MONEY_WITHDRAW(TranslationManager.translation("feature.city.permission.money_withdraw"), Material.DIAMOND),
    MANAGE_PERMS(TranslationManager.translation("feature.city.permission.manage_perm"), Material.DIAMOND_BLOCK), // Cette permission est donnée seulement par l'owner
    ACCESS_CITY_CHEST(TranslationManager.translation("feature.city.permission.access_city_chest"), Material.ENDER_CHEST),
    UPGRADE_CHEST(TranslationManager.translation("feature.city.permission.upgrade_chest"), Material.OAK_CHEST_BOAT),
    CHANGE_TYPE(TranslationManager.translation("feature.city.permission.change_type"), Material.BIRCH_SIGN),
    MASCOT_MOVE(TranslationManager.translation("feature.city.permission.move_mascot"), Material.LEAD),
    MASCOT_CHANGE_SKIN(TranslationManager.translation("feature.city.permission.change_mascot_skin"), Material.ZOMBIE_SPAWN_EGG),
    MASCOT_UPGRADE(TranslationManager.translation("feature.city.permission.upgrade_mascot"), Material.BONE),
    MASCOT_HEAL(TranslationManager.translation("feature.city.permission.heal_mascot"), Material.POTION),
    LAUNCH_WAR(TranslationManager.translation("feature.city.permission.launch_war"), Material.IRON_SWORD),
    MANAGE_RANKS(TranslationManager.translation("feature.city.permission.manage_grade"), Material.PAPER),
    ASSIGN_RANKS(TranslationManager.translation("feature.city.permission.assign_grade"), Material.BOOK)
    ;

    private final Component displayName;
    private final Material icon;
    
    CityPermission(Component displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
}