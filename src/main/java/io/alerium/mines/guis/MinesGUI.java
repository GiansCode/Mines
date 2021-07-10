package io.alerium.mines.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.alerium.mines.MinesPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class MinesGUI implements InventoryProvider {

    private final MinesPlugin plugin;
    private final double money;
    private final int mines;

    private final boolean[][] matrix;
    private int rightMines = 0;
    private boolean finished = false;

    private MinesGUI(MinesPlugin plugin, double money, int mines) {
        this.plugin = plugin;
        this.money = money;
        this.mines = mines;

        matrix = new boolean[4][5];
        int placedMines = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (placedMines < mines) {
            int i = random.nextInt(4);
            int j = random.nextInt(5);
            if (matrix[i][j])
                continue;

            matrix[i][j] = true;
            placedMines++;
        }
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack mine = plugin.getMinesGUIConfig().getItemStack("items.mine");
        ItemStack goodMine = plugin.getMinesGUIConfig().getItemStack("items.good");
        ItemStack badMine = plugin.getMinesGUIConfig().getItemStack("items.bad");

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                int finalJ = j;
                int finalI = i;

                contents.set(i + 1, j + 2, ClickableItem.of(mine, event -> {
                    if (finished)
                        return;

                    if (matrix[finalI][finalJ]) {
                        contents.set(finalI + 1, finalJ + 2, ClickableItem.empty(badMine));
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        player.sendMessage(plugin.getConfiguration().getMessage("messages.lostMessage"));
                        plugin.getInventoryManager().getInventory(player).ifPresent(inv -> inv.setCloseable(true));
                        rightMines = 0;
                        updateEndButton(player, contents);
                        finished = true;
                        return;
                    }

                    contents.set(finalI + 1, finalJ + 2, ClickableItem.empty(goodMine));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    rightMines++;
                    updateEndButton(player, contents);
                }));
            }
        }

        updateEndButton(player, contents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    private void updateEndButton(Player player, InventoryContents contents) {
        contents.set(5, 8, ClickableItem.of(plugin.getMinesGUIConfig().getItemStack("items.end", "%money%", MinesPlugin.DECIMAL_FORMAT.format(money), "%multiplier%", Double.toString(mines * 0.125), "%win_money%", MinesPlugin.DECIMAL_FORMAT.format(money + (money * rightMines * mines * 0.125))), event -> {
            if (!finished) {
                double prize = money + (money * rightMines * mines * 0.125);
                plugin.getEconomy().depositPlayer(player, prize);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                player.sendMessage(plugin.getConfiguration().getMessage("messages.winMessage", "%prize%", MinesPlugin.DECIMAL_FORMAT.format(prize)));
            }

            plugin.getInventoryManager().getInventory(player).ifPresent(inv -> inv.close(player));
        }));
    }

    public static void open(MinesPlugin plugin, Player player, int mines, double money) {
        SmartInventory.builder()
                .title(plugin.getMinesGUIConfig().getLegacyMessage("title"))
                .size(6, 9)
                .provider(new MinesGUI(plugin, money, mines))
                .manager(plugin.getInventoryManager())
                .closeable(false)
                .build()
                .open(player);
    }

}
