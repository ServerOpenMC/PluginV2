package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.leaderboards.LeaderboardManager;
import fr.openmc.core.utils.PlayerNameCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class CityTopMenu extends PaginatedMenu {

    // Constants for the menu
    private static final Component SORT_HEADER = Component.text("§7Cliquez pour trier par");
    private static final String SELECTED_PREFIX = "§6➢ ";
    private static final String UNSELECTED_PREFIX = "§b  ";

    private final List<City> cities;
    private SortType sortType;

    /**
     * Constructor for CityListMenu.
     *
     * @param owner The player who opens the menu.
     */
    public CityTopMenu(Player owner) {
        this(owner, SortType.GLOBAL);
    }

    /**
     * Constructor for CityListMenu with a specified sort type.
     *
     * @param owner    The player who opens the menu.
     * @param sortType The initial sort type.
     */
    public CityTopMenu(Player owner, SortType sortType) {
        super(owner);
        this.cities = new ArrayList<>(CityManager.getCities());
        setSortType(sortType);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return IntStream.rangeClosed(0, 53)
                .filter(i -> i != 13 && i != 21 && i != 23 && i != 29 &&
                        i != 30 && i != 31 && i != 32 && i != 33)
                .boxed()
                .toList();
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        AtomicInteger rank = new AtomicInteger(1);

        cities.forEach(city -> {
            UUID ownerUUID = city.getPlayerWithPermission(CityPermission.OWNER);

            if (ownerUUID != null) {
                String ownerName = PlayerNameCache.getName(ownerUUID);

                List<Component> cityLore = new ArrayList<>();

                cityLore.add(Component.text("§7Propriétaire : " + ownerName));
                if (MayorManager.phaseMayor == 2 && FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
                    String mayorCity = city.getMayor() == null ? "§7Aucun" : city.getMayor().getName();
                    NamedTextColor mayorColor = (city.getMayor() == null || city.getMayor().getMayorColor() == null)
                            ? NamedTextColor.WHITE
                            : city.getMayor().getMayorColor();
                    cityLore.add(Component.text("§7Maire : ")
                            .append(Component.text(mayorCity)
                                    .color(mayorColor)
                                    .decoration(TextDecoration.ITALIC, false)));
                }
                cityLore.add(Component.text("§7Niveau: §3" + city.getLevel()));
                cityLore.add(Component.text("§7Membres : §a" + city.getMembers().size() + "/" + MemberLimitRewards.getMemberLimit(city.getLevel()) + " membres"));
                cityLore.add(Component.text("§7Superficie : §6" + city.getChunks().size() + " chunks"));
                cityLore.add(Component.text("§7Richesses : §6"
                        + EconomyManager.getFormattedSimplifiedNumber(city.getBalance())
                        + EconomyManager.getEconomyIcon()));
	            cityLore.add(Component.text("§7Points de puissances : §c" + city.getPowerPoints()));

                int currentRank = rank.getAndIncrement();

                items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(ownerUUID), itemMeta -> {
                    itemMeta.displayName(Component.text("n°" + currentRank + " " + city.getName())
                            .color(LeaderboardManager.getRankColor(currentRank))
                            .decoration(TextDecoration.ITALIC, false)
                    );
                    itemMeta.lore(cityLore);
                }));
            }
        });

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();

        map.put(49, new ItemBuilder(this, Material.HOPPER, itemMeta -> {
            itemMeta.displayName(Component.text("Trier"));
            itemMeta.lore(generateSortLoreText());
        }).setOnClick(inventoryClickEvent -> {
            changeSortType(inventoryClickEvent.isLeftClick());
            new CityTopMenu(getOwner(), sortType).open();
        }));

        return map;
    }

    @Override
    public @NotNull String getName() {
	    return "Menu des classement des villes";
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    /**
     * Generates the lore text for the sorting options.
     *
     * @return A list of strings representing the lore text.
     */
    private List<Component> generateSortLoreText() {
        return List.of(
                SORT_HEADER,
                formatSortOption(SortType.GLOBAL, "Global"),
                formatSortOption(SortType.POWER, "Puissances"),
                formatSortOption(SortType.MONEY, "Richesses"),
                formatSortOption(SortType.CLAIM, "Superficie"),
                formatSortOption(SortType.POPULATION, "Population")
        );
    }

    /**
     * Formats the sorting option string.
     *
     * @param type  The sorting type.
     * @param label The label for the sorting option.
     * @return A formatted string representing the sorting option.
     */
    private Component formatSortOption(SortType type, String label) {
        return Component.text((sortType == type ? SELECTED_PREFIX : UNSELECTED_PREFIX) + label);
    }

    /**
     * Sets the sorting type and sorts the cities accordingly.
     *
     * @param sortType The sorting type to set.
     */
    private void setSortType(SortType sortType) {
        this.sortType = sortType;
        switch (this.sortType) {
            case GLOBAL -> sortByGlobal(cities);
            case POWER -> sortByPower(cities);
            case MONEY -> sortByMoney(cities);
            case CLAIM -> sortByClaim(cities);
            case POPULATION -> sortByPopulation(cities);
        }
    }

    /**
     * Changes the sorting type to the next one in the enum and sorts the cities accordingly.
     */
    private void changeSortType(boolean isLeftClick) {
        sortType = isLeftClick
                ? SortType.values()[(sortType.ordinal() + 1) % SortType.values().length]
                : SortType.values()[(sortType.ordinal() - 1 + SortType.values().length) % SortType.values().length];

        switch (sortType) {
            case POWER -> sortByPower(cities);
            case MONEY -> sortByMoney(cities);
            case CLAIM -> sortByClaim(cities);
            case POPULATION -> sortByPopulation(cities);
            default -> sortByGlobal(cities);
        }
    }


    /**
     * Sorts the cities by their money.
     *
     * @param cities The list of cities to sort.
     */
    private void sortByMoney(List<City> cities) {
        if (cities.size() <= 1) return;
        cities.sort((o1, o2) -> Double.compare(o2.getBalance(), o1.getBalance()));
    }

    /**
     * Sorts the cities by their population.
     *
     * @param cities The list of cities to sort.
     */
    private void sortByPopulation(List<City> cities) {
        if (cities.size() <= 1) return;
        cities.sort((o1, o2) -> Integer.compare(o2.getMembers().size(), o1.getMembers().size()));
    }

    /**
     * Sorts the cities by their chunks.
     *
     * @param cities The list of cities to sort.
     */
    private void sortByClaim(List<City> cities) {
        if (cities.size() <= 1) return;
        cities.sort((o1, o2) -> Integer.compare(o2.getChunks().size(), o1.getChunks().size()));
    }

    /**
     * Sorts the cities by a global score combining multiple factors.
     *
     * @param cities The list of cities to sort.
     */
    private void sortByGlobal(List<City> cities) {
        if (cities.size() <= 1) return;

        cities.sort((c1, c2) -> {
            double score1 = getGlobalScore(c1);
            double score2 = getGlobalScore(c2);
            return Double.compare(score2, score1); // Descending order
        });
    }

    /**
     * Calculates the global score for a city based on money, claims, population, and power.
     *
     * @param city The city to evaluate.
     * @return The calculated score.
     */
    private double getGlobalScore(City city) {
        double moneyScore = city.getBalance() / 1000.0;
        double claimScore = city.getChunks().size() * 5;
        double populationScore = city.getMembers().size() * 10;
        double powerScore = city.getPowerPoints() * 2;

        return moneyScore + claimScore + populationScore + powerScore;
    }

    /**
     * Sorts the cities by their power.
     *
     * @param cities The list of cities to sort.
     */
    private void sortByPower(List<City> cities) {
        if (cities.size() <= 1) return;
        cities.sort((o1, o2) -> Integer.compare(o2.getPowerPoints(), o1.getPowerPoints()));
    }


    /**
     * Enum representing the sorting types for the city top.
     */
    public enum SortType {
        GLOBAL,
        POWER,
        MONEY,
        CLAIM,
        POPULATION,
    }
}
