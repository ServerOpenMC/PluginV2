package fr.openmc.api.packetmenulib.menu;

public enum ClickType {
    LEFT_CLICK,
    RIGHT_CLICK,
    SHIFT_LEFT_CLICK,
    SHIFT_RIGHT_CLICK,
    DOUBLE_CLICK,
    CLICK_OUTSIDE,
    OTHER // We find the other buttons inconvenient for users, and we won’t ask a player to press a keyboard key just to click a button.
}
