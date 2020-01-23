package land.pvp.core.managers;

import java.util.HashMap;
import java.util.Map;
import land.pvp.core.CorePlugin;
import land.pvp.core.inventory.menu.Menu;
import land.pvp.core.inventory.menu.impl.ReportMenu;
import org.bukkit.inventory.Inventory;

public class MenuManager {
    private final Map<Class<? extends Menu>, Menu> menus = new HashMap<>();

    public MenuManager(CorePlugin plugin) {
        registerMenus(
                new ReportMenu(plugin)
        );
    }

    public Menu getMenu(Class<? extends Menu> clazz) {
        return menus.get(clazz);
    }

    public Menu getMatchingMenu(Inventory other) {
        for (Menu menu : menus.values()) {
            if (menu.getInventory().equals(other)) {
                return menu;
            }
        }

        return null;
    }

    public void registerMenus(Menu... menus) {
        for (Menu menu : menus) {
            menu.setup();
            menu.update();
            this.menus.put(menu.getClass(), menu);
        }
    }
}