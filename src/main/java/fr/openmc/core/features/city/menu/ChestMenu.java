package fr.openmc.core.features.city.menu;

public class ChestMenu {
//    private final City city;
//    @Getter private final int page;
//    private static ItemStack border;
//    @Getter @Setter private Inventory inventory;
//
//    public static final int UPGRADE_PER_MONEY = 3000;
//    public static final int UPGRADE_PER_AYWENITE = 5;
//
//
//    public ChestMenu(City city, int page) {
//        this.city = city;
//        this.page = page;
//
//        if (this.page < 1) {
//            throw new IllegalArgumentException("Page must be greater than 0");
//        }
//
//        if (this.page > this.city.getChestPages()) {
//            throw new IllegalArgumentException("Page must be less than or equal to " + this.city.getChestPages());
//        }
//    }
//
//    public boolean hasNextPage() {
//        return this.page < this.city.getChestPages();
//    }
//
//    public boolean hasPreviousPage() {
//        return this.page > 1;
//    }
//
//    public void open(Player player) {
//        if (Restart.isRestarting) {
//            MessagesManager.sendMessage(player, Component.text("§7Le coffre est inaccessible durant un rédémarrage programmé"), Prefix.OPENMC, MessageType.INFO, false);
//            return;
//        }
//
//        Inventory inventoryChest = Bukkit.createInventory(null, 54, Component.text("Coffre de " + this.city.getName() + " - Page " + this.page));
//
//        inventoryChest.setContents(this.city.getChestContent(this.page));
//
//        for (int i = 45; i < 54; i++) {
//            inventoryChest.setItem(i, getBorder());
//        }
//
//        if (hasPreviousPage()) {
//            ItemStack previous = CustomItemRegistry.getByName("menu:previous_page").getBest();
//            inventoryChest.setItem(45, previous);
//        }
//
//        if (hasNextPage()) {
//            ItemStack next = CustomItemRegistry.getByName("menu:next_page").getBest();
//            inventoryChest.setItem(53, next);
//        }
//
//        if (city.hasPermission(player.getUniqueId(), CPermission.CHEST_UPGRADE) && city.getChestPages() < 5) {
//            ItemStack upgrade = new ItemStack(Material.ENDER_CHEST);
//            ItemMeta meta = upgrade.getItemMeta();
//            meta.displayName(Component.text("§aAméliorer le coffre"));
//            meta.lore(List.of(
//                    Component.text("§7Votre ville doit avoir : "),
//                    Component.text("§8- §6"+ city.getChestPages()*UPGRADE_PER_MONEY).append(Component.text(EconomyManager.getEconomyIcon())).decoration(TextDecoration.ITALIC, false),
//                    Component.text("§8- §d"+ city.getChestPages()*UPGRADE_PER_AYWENITE + " d'Aywenite"),
//                    Component.text(""),
//                    Component.text("§e§lCLIQUEZ ICI POUR AMELIORER LE COFFRE")
//            ));
//            upgrade.setItemMeta(meta);
//            inventoryChest.setItem(48, upgrade);
//        }
//
//        ItemStack next = CustomItemRegistry.getByName("menu:close_button").getBest();
//        inventoryChest.setItem(49, next);
//
//        player.openInventory(inventoryChest);
//        city.setChestWatcher(player.getUniqueId());
//        city.setChestMenu(this);
//        this.inventory = inventoryChest;
//    }
}
