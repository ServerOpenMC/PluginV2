package fr.openmc.core.features.city;

import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

@Getter
public enum CityPermission {
    OWNER(TranslationManager.translation("feature.city.permission.owner"), Material.NETHERITE_BLOCK, false), //Impossible à donner sauf avec un transfert
    INVITE(TranslationManager.translation("feature.city.permission.invite"), Material.OAK_DOOR, false),
    KICK(TranslationManager.translation("feature.city.permission.kick"), Material.IRON_DOOR, false),
    PLACE(TranslationManager.translation("feature.city.permission.place"), Material.OAK_LOG, true),
    BREAK(TranslationManager.translation("feature.city.permission.break"), Material.STONE_PICKAXE, true),
    OPEN_CHEST(TranslationManager.translation("feature.city.permission.open_chest"), Material.CHEST, true),
    INTERACT(TranslationManager.translation("feature.city.permission.interact"), Material.LEVER, true),
    CLAIM(TranslationManager.translation("feature.city.permission.claim"), Material.GRASS_BLOCK, false),
    SEE_CHUNKS(TranslationManager.translation("feature.city.permission.see_claim"), Material.MAP, false),
    RENAME(TranslationManager.translation("feature.city.permission.rename"), Material.NAME_TAG, false),
    MONEY_DEPOSIT(TranslationManager.translation("feature.city.permission.money_deposit"), Material.EMERALD, false),
    MONEY_BALANCE(TranslationManager.translation("feature.city.permission.money_see"), Material.GOLD_INGOT, false),
    MONEY_WITHDRAW(TranslationManager.translation("feature.city.permission.money_withdraw"), Material.DIAMOND, false),
    MANAGE_PERMS(TranslationManager.translation("feature.city.permission.manage_perm"), Material.DIAMOND_BLOCK, false), // Cette permission est donnée seulement par l'owner
    ACCESS_CITY_CHEST(TranslationManager.translation("feature.city.permission.access_city_chest"), Material.ENDER_CHEST, false),
    UPGRADE_CHEST(TranslationManager.translation("feature.city.permission.upgrade_chest"), Material.OAK_CHEST_BOAT, false),
    CHANGE_TYPE(TranslationManager.translation("feature.city.permission.change_type"), Material.BIRCH_SIGN, false),
    MASCOT_MOVE(TranslationManager.translation("feature.city.permission.move_mascot"), Material.LEAD, false),
    MASCOT_CHANGE_SKIN(TranslationManager.translation("feature.city.permission.change_mascot_skin"), Material.ZOMBIE_SPAWN_EGG, false),
    MASCOT_UPGRADE(TranslationManager.translation("feature.city.permission.upgrade_mascot"), Material.BONE, false),
    MASCOT_HEAL(TranslationManager.translation("feature.city.permission.heal_mascot"), Material.POTION, false),
    LAUNCH_WAR(TranslationManager.translation("feature.city.permission.launch_war"), Material.IRON_SWORD, false),
    MANAGE_RANKS(TranslationManager.translation("feature.city.permission.manage_grade"), Material.PAPER, false),
    ASSIGN_RANKS(TranslationManager.translation("feature.city.permission.assign_grade"), Material.BOOK, false)
    ;

    private final Component displayName;
    private final Material icon;
    private final boolean externalPermission;
    
    CityPermission(Component displayName, Material icon, boolean externalPermission) {
        this.displayName = displayName;
        this.icon = icon;
        this.externalPermission = externalPermission;
    }
}