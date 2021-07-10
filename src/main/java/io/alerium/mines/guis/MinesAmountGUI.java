package io.alerium.mines.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.alerium.mines.MinesPlugin;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MinesAmountGUI implements InventoryProvider {

    private final MinesPlugin plugin;
    private final double money;

    private int mines = 1;

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(1, 2, ClickableItem.of(plugin.getMinesAmountGUIConfig().getItemStack("items.remove"), event -> updateMinesAmount(player, mines - 1, contents)));
        setMinesButton(plugin.getMinesAmountGUIConfig().getItemStack("items.mines"), contents);
        contents.set(1, 6, ClickableItem.of(plugin.getMinesAmountGUIConfig().getItemStack("items.add"), event -> updateMinesAmount(player, mines + 1, contents)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    private void updateMinesAmount(Player player, int amount, InventoryContents contents) {
        if (amount > plugin.getMaxMinesAmount()) {
            player.sendMessage(plugin.getConfiguration().getMessage("messages.maxMinesAmountReached"));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
            return;
        }

        if (amount < plugin.getMinMinesAmount()) {
            player.sendMessage(plugin.getConfiguration().getMessage("messages.minMinesAmountReached"));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
            return;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        this.mines = amount;
        setMinesButton(contents.get(1, 4).get().getItem(), contents);
    }

    private void setMinesButton(ItemStack item, InventoryContents contents) {
        item.setAmount(mines);
        contents.set(1, 4, ClickableItem.of(item, event -> {
            Player player = (Player) event.getWhoClicked();
            EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, money);
            if (!response.transactionSuccess()) {
                player.sendMessage(plugin.getConfiguration().getMessage("messages.notEnoughMoney"));
                player.closeInventory();
                return;
            }

            player.sendMessage(plugin.getConfiguration().getMessage("messages.minesSelected", "%mines%", Integer.toString(mines)));
            MinesGUI.open(plugin, player, mines, money);
        }));
    }

    public static void open(MinesPlugin plugin, Player player, double money) {
        SmartInventory.builder()
                .title(plugin.getMinesAmountGUIConfig().getLegacyMessage("title"))
                .size(3, 9)
                .provider(new MinesAmountGUI(plugin, money))
                .manager(plugin.getInventoryManager())
                .build()
                .open(player);
    }

}
