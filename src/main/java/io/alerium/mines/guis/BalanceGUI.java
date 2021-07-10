package io.alerium.mines.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.alerium.mines.MinesPlugin;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceGUI implements InventoryProvider {

    private final MinesPlugin plugin;

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(0, 4, ClickableItem.of(plugin.getBalanceGUIConfig().getItemStack("items.customAmount"), event -> {
            new AnvilGUI.Builder()
                    .plugin(plugin)
                    .text("Write here")
                    .title(plugin.getBalanceGUIConfig().getLegacyMessage("title"))
                    .itemLeft(new ItemStack(Material.PAPER))
                    .onComplete((p, s) -> {
                        try {
                            double money = Double.parseDouble(s);
                            if (plugin.getEconomy().has(p, money))
                                MinesAmountGUI.open(plugin, p, money);
                            else
                                p.sendMessage(plugin.getConfiguration().getMessage("messages.notEnoughMoney"));

                        } catch (NumberFormatException e) {
                            p.sendMessage(plugin.getConfiguration().getMessage("messages.numberNotValid"));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .open(player);
        }));

        contents.set(1, 1, ClickableItem.of(plugin.getBalanceGUIConfig().getItemStack("items.quarterAmount", "%amount%", MinesPlugin.DECIMAL_FORMAT.format(plugin.getEconomy().getBalance(player) / 4)), event -> startGame(player, plugin.getEconomy().getBalance(player) / 4)));
        contents.set(1, 4, ClickableItem.of(plugin.getBalanceGUIConfig().getItemStack("items.halfAmount", "%amount%", MinesPlugin.DECIMAL_FORMAT.format(plugin.getEconomy().getBalance(player) / 2)), event -> startGame(player, plugin.getEconomy().getBalance(player) / 2)));
        contents.set(1, 7, ClickableItem.of(plugin.getBalanceGUIConfig().getItemStack("items.fullAmount", "%amount%", MinesPlugin.DECIMAL_FORMAT.format(plugin.getEconomy().getBalance(player))), event -> startGame(player, plugin.getEconomy().getBalance(player))));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    private void startGame(Player player, double money) {
        player.sendMessage(plugin.getConfiguration().getMessage("messages.startGame", "%money%", MinesPlugin.DECIMAL_FORMAT.format(money)));
        MinesAmountGUI.open(plugin, player, money);
    }

    public static void open(MinesPlugin plugin, Player player) {
        SmartInventory.builder()
                .title(plugin.getBalanceGUIConfig().getLegacyMessage("title"))
                .size(3, 9)
                .provider(new BalanceGUI(plugin))
                .manager(plugin.getInventoryManager())
                .build()
                .open(player);
    }

}
