package fr.openmc.core.features.dimopener.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dimopener.DimensionOpenerManager;
import fr.openmc.core.features.dimopener.DimensionProgress;
import fr.openmc.core.features.dimopener.DimensionState;
import fr.openmc.core.features.dimopener.data.DimensionData;
import fr.openmc.core.features.dimopener.data.StepDimensionData;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DimensionContributeMenu extends Menu {

    // TODO: refaire ce menu en plus intuitife

    private static final int INPUT_SLOT = 22;
    private static final int INFO_SLOT = 4;

    private static final int POLL_PERIOD_TICKS = 4;

    private final String dimensionId;
    private BukkitTask inputWatcherTask;

    private final DimensionData data;

    public DimensionContributeMenu(Player owner, String dimensionId) {
        super(owner);
        this.dimensionId = dimensionId;
        data = DimensionOpenerManager.getDimension(dimensionId);
    }

    @Override
    public Component getName() {
        DimensionData dim = DimensionOpenerManager.getDimension(dimensionId);
        return Component.text(dim != null ? dim.getName() : "Dimension").color(NamedTextColor.DARK_PURPLE);
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = fill(Material.GRAY_STAINED_GLASS_PANE);

        DimensionData dim = DimensionOpenerManager.getDimension(dimensionId);
        DimensionProgress progress = DimensionOpenerManager.getProgress(dimensionId);

        if (dim == null || progress == null) return content;

        content.put(INFO_SLOT, infoItem(dim, progress));

        if (!DimensionOpenerManager.isPrerequisiteMet(dim)) {
            content.put(INPUT_SLOT, lockedItem(dim));
            stopInputWatcher();
            return content;
        }

        DimensionState state = progress.getState();

        if (!DimensionOpenerManager.isInInputPhase(dimensionId)) {
            content.put(INPUT_SLOT, waitingItem(progress, state));
            stopInputWatcher();
            return content;
        }

        StepDimensionData step = DimensionOpenerManager.getCurrentStep(dimensionId);
        if (step == null) return content;

        double remaining = step.getRequired() - progress.getCurrentAmount();

        if (step.getType().equals(StepDimensionData.Type.ITEMS)) {
            content.put(INPUT_SLOT, new ItemMenuBuilder(this, Material.AIR));
            startInputWatcher(step);
        } else if (step.getType().equals(StepDimensionData.Type.MONEY)) {
            stopInputWatcher();
            content.put(INPUT_SLOT, moneyItem(remaining));
        }

        return content;
    }

    private ItemMenuBuilder lockedItem(DimensionData data) {
        DimensionData required = DimensionOpenerManager.getDimension(data.getRequireDimension());
        String requiredName = required != null ? required.getName() : data.getRequireDimension();

        return new ItemMenuBuilder(this, Material.BARRIER, meta -> {
            meta.itemName(TranslationManager.translation("feature.dimopener.menu.locked.title"));
            meta.lore(List.of(
                    TranslationManager.translation("feature.dimopener.menu.locked.lore"),
                    Component.text(requiredName).color(NamedTextColor.YELLOW)
            ));
        });
    }

    private void startInputWatcher(StepDimensionData step) {
        if (inputWatcherTask != null) return;

        inputWatcherTask = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            if (!(getOwner().getOpenInventory().getTopInventory().getHolder(false) instanceof DimensionContributeMenu menu) || menu != this) {
                stopInputWatcher();
                return;
            }

            ItemStack current = getOwner().getOpenInventory().getTopInventory().getItem(INPUT_SLOT);
            if (current == null || current.getType() == Material.AIR) return;

            if (current.getType() != step.getMaterial()) {
                getOwner().getOpenInventory().getTopInventory().setItem(INPUT_SLOT, null);
                getOwner().getInventory().addItem(current);
                MessagesManager.sendMessage(
                        getOwner(),
                        TranslationManager.translation(
                                "feature.dimopener.contribute.wrong_item",
                                Component.text(step.getMaterial().toString())
                        ),
                        Prefix.OPENMC, MessageType.ERROR, false
                );
                return;
            }

            int amount = current.getAmount();
            getOwner().getOpenInventory().getTopInventory().setItem(INPUT_SLOT, null);

            var result = DimensionOpenerManager.contributeItems(getOwner(), dimensionId, amount);

            switch (result) {
                case SUCCESS -> MessagesManager.sendMessage(
                        getOwner(),
                        TranslationManager.translation("feature.dimopener.contribute.items_success", Component.text(amount)),
                        Prefix.OPENMC, MessageType.SUCCESS, false
                );
                case STEP_COMPLETED -> MessagesManager.sendMessage(
                        getOwner(),
                        TranslationManager.translation("feature.dimopener.contribute.step_completed"),
                        Prefix.OPENMC, MessageType.SUCCESS, false
                );
                case REQUIRED_DIMENSION_NOT_OPENED -> {
                    getOwner().getInventory().addItem(current);
                    MessagesManager.sendMessage(
                            getOwner(),
                            TranslationManager.translation("feature.dimopener.contribute.required_not_opened"),
                            Prefix.OPENMC, MessageType.ERROR, false
                    );
                }
                default -> getOwner().getInventory().addItem(current);
            }

            this.open();
        }, POLL_PERIOD_TICKS, POLL_PERIOD_TICKS);
    }

    private void stopInputWatcher() {
        if (inputWatcherTask != null) {
            inputWatcherTask.cancel();
            inputWatcherTask = null;
        }
    }

    private ItemMenuBuilder moneyItem(double remaining) {
        return new ItemMenuBuilder(this, Material.GOLD_INGOT, meta -> {
            meta.itemName(TranslationManager.translation("feature.dimopener.menu.money.title"));
            meta.lore(List.of(
                    TranslationManager.translation(
                            "feature.dimopener.menu.money.balance",
                            Component.text(EconomyManager.getMiniBalance(getOwner().getUniqueId()))
                    ),
                    TranslationManager.translation(
                            "feature.dimopener.menu.money.remaining",
                            Component.text(EconomyManager.getFormattedNumber(remaining))
                    ),
                    Component.empty(),
                    TranslationManager.translation("feature.dimopener.menu.money.click")
            ));
        }).setOnClick(_ -> openMoneyDialog(remaining));
    }

    private void openMoneyDialog(double remaining) {
        double balance = EconomyManager.getBalance(getOwner().getUniqueId());
        float max = (float) Math.max(1, Math.min(balance, remaining));
        float initial = Math.min(max, 1f);

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(TranslationManager.translation("feature.dimopener.dialog.money.title"))
                        .body(List.of(
                                DialogBody.plainMessage(
                                        TranslationManager.translation("feature.dimopener.dialog.money.body")
                                )
                        ))
                        .inputs(List.of(
                                DialogInput.numberRange("amount", TranslationManager.translation("feature.dimopener.dialog.money.amount"), 1f, max)
                                        .step(1f)
                                        .initial(initial)
                                        .width(300)
                                        .build()
                        ))
                        .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.create(
                                TranslationManager.translation("feature.dimopener.dialog.confirm"),
                                TranslationManager.translation("feature.dimopener.dialog.confirm.tooltip"),
                                150,
                                DialogAction.customClick(
                                        (view, _) -> {
                                            Float amount = view.getFloat("amount");
                                            if (amount == null || amount <= 0) return;

                                            var result = DimensionOpenerManager.contributeMoney(getOwner(), dimensionId, amount);

                                            switch (result) {
                                                case SUCCESS -> MessagesManager.sendMessage(
                                                        getOwner(),
                                                        TranslationManager.translation(
                                                                "feature.dimopener.contribute.money_success",
                                                                Component.text(EconomyManager.getFormattedNumber(amount))
                                                        ),
                                                        Prefix.DIMOPENER, MessageType.SUCCESS, false
                                                );
                                                case STEP_COMPLETED -> MessagesManager.sendMessage(
                                                        getOwner(),
                                                        TranslationManager.translation("feature.dimopener.contribute.step_completed"),
                                                        Prefix.DIMOPENER, MessageType.SUCCESS, false
                                                );
                                                case REQUIRED_DIMENSION_NOT_OPENED -> MessagesManager.sendMessage(
                                                        getOwner(),
                                                        TranslationManager.translation("feature.dimopener.contribute.required_not_opened"),
                                                        Prefix.DIMOPENER, MessageType.ERROR, false
                                                );
                                                default -> MessagesManager.sendMessage(
                                                        getOwner(),
                                                        TranslationManager.translation("feature.dimopener.contribute.impossible"),
                                                        Prefix.DIMOPENER, MessageType.ERROR, false
                                                );
                                            }

                                            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), this::open);
                                        },
                                        ClickCallback.Options.builder().uses(1).build()
                                )
                        ),
                        ActionButton.create(
                                TranslationManager.translation("feature.dimopener.dialog.cancel"),
                                TranslationManager.translation("feature.dimopener.dialog.cancel.tooltip"),
                                150,
                                null
                        )
                ))
        );

        getOwner().showDialog(dialog);
    }

    private ItemMenuBuilder infoItem(DimensionData dim, DimensionProgress progress) {
        StepDimensionData step = DimensionOpenerManager.getCurrentStep(dimensionId);
        return new ItemMenuBuilder(this, DimensionOpenerManager.resolveIcon(dim), meta -> {
            meta.itemName(TranslationManager.translation("feature.dimopener.menu.info.name", Component.text(dim.getName())));
            meta.lore(List.of(
                    TranslationManager.translation("feature.dimopener.menu.info.description", Component.text(dim.getDescription())),
                    Component.empty(),
                    TranslationManager.translation(
                            "feature.dimopener.menu.info.step",
                            Component.text(progress.getCurrentStepIndex() + 1),
                            Component.text(dim.getDimensionsStep().size())
                    ),
                    TranslationManager.translation("feature.dimopener.menu.info.step_name", Component.text(step != null ? step.getName() : "-")),
                    TranslationManager.translation("feature.dimopener.menu.info.step_description", Component.text(step != null ? step.getDescription() : ""))
            ));
        });
    }

    private ItemMenuBuilder waitingItem(DimensionProgress progress, DimensionState state) {
        if (state == DimensionState.OPENED) {
            return new ItemMenuBuilder(this, Material.END_PORTAL_FRAME, meta ->
                    meta.itemName(TranslationManager.translation("feature.dimopener.menu.opened")));
        }

        long remainingMs = Math.max(0, progress.getCooldownEndTimestamp() - System.currentTimeMillis());
        long remainingSeconds = remainingMs / 1000;

        long days = remainingSeconds / 86400;
        long hours = (remainingSeconds % 86400) / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;

        return new ItemMenuBuilder(this, Material.CLOCK, meta -> {
            meta.itemName(TranslationManager.translation("feature.dimopener.menu.waiting.title"));
            meta.lore(List.of(
                    TranslationManager.translation(
                            "feature.dimopener.menu.waiting.time",
                            Component.text(days), Component.text(hours), Component.text(minutes), Component.text(seconds)
                    )
            ));
        });
    }

    @Override
    public List<Integer> getTakableSlot() {
        List<Integer> slots = new ArrayList<>();

        boolean canDeposit = data != null
                && data.isEnabled()
                && DimensionOpenerManager.isPrerequisiteMet(data)
                && DimensionOpenerManager.isInInputPhase(dimensionId);

        if (canDeposit) {
            StepDimensionData step = DimensionOpenerManager.getCurrentStep(dimensionId);
            if (step != null && step.getType().equals(StepDimensionData.Type.ITEMS)) {
                slots.add(INPUT_SLOT);
                slots.addAll(MenuUtils.getInventoryItemSlots(this.getInventorySize().getSize()));
            }
        }
        return slots;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        stopInputWatcher();

        if (!DimensionOpenerManager.isInInputPhase(dimensionId)) return;

        StepDimensionData step = DimensionOpenerManager.getCurrentStep(dimensionId);
        if (step != null && step.getType().equals(StepDimensionData.Type.ITEMS)) {
            ItemStack leftover = event.getInventory().getItem(INPUT_SLOT);
            if (leftover != null && leftover.getType() != Material.AIR) {
                getOwner().getInventory().addItem(leftover);
            }
        }
    }
}