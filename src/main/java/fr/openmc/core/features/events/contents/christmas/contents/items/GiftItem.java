package fr.openmc.core.features.events.contents.christmas.contents.items;

import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.GiftType;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.UsableItem;
import fr.openmc.core.registry.loottable.LootReward;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiftItem extends CustomItem implements UsableItem {
    private final GiftType type;

    public GiftItem(GiftType type) {
        super(type.getId());
        this.type = type;
    }

    @Override
    public @NotNull ItemStack getVanilla() {
        return ItemStack.of(Material.PAPER);
    }

    @Override
    public void onClickOnEntity(Player p, PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player p2)) return;

        ItemStack itemHand = p.getEquipment().getItemInMainHand();
        if (p.hasCooldown(itemHand)) return;
        p.setCooldown(itemHand, 2 * 20);

        LootReward reward = type.getGiftTable().rollLoots();
        ItemUtils.removeItemsFromPlayerInventory(p, this.getBest(), 1);

        for (CustomLoot loot : reward.loots()) {
            loot.run(p);
            loot.run(p2);
        }

        OMCPlayer player = OMCPlayer.of(p);
        OMCPlayer player2 = OMCPlayer.of(p2);

        Component lootComponent = reward.buildComponent();
        player.message().sendInfo(
                TranslationManager.translation("feature.events.christmas.gift.gift_gived",
                        this.getBest().displayName(),
                        player2.getNameWithHead(),
                        lootComponent
                ),
                Prefix.CHRISTMAS,
                false
        );
        player2.message().sendInfo(
                TranslationManager.translation("feature.events.christmas.gift.player_gave",
                        player.getNameWithHead(),
                        this.getBest().displayName(),
                        lootComponent
                ),
                Prefix.CHRISTMAS,
                false
        );

        player.playSound(Sound.sound(Key.key("minecraft:entity.firework_rocket.twinkle"), Sound.Source.AMBIENT, 1, 1.3f));
        player2.playSound(Sound.sound(Key.key("minecraft:entity.firework_rocket.twinkle"), Sound.Source.AMBIENT, 1, 1.3f));
    }
}
