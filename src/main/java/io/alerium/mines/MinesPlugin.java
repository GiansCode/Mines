package io.alerium.mines;

import fr.minuskube.inv.InventoryManager;
import io.alerium.mines.commands.MinesCommand;
import io.alerium.mines.utils.Configuration;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public class MinesPlugin extends JavaPlugin {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    @Getter private Economy economy;

    @Getter private Configuration configuration;
    @Getter private Configuration balanceGUIConfig;
    @Getter private Configuration minesAmountGUIConfig;
    @Getter private Configuration minesGUIConfig;

    @Getter private InventoryManager inventoryManager;

    @Getter private int maxMinesAmount;
    @Getter private int minMinesAmount;
    @Getter private double multiplier;
    @Getter private String expression;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("This plugin requires Vault with an economy plugin installed.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        setupConfigs();
        setupManagers();
        setupCommands();
    }

    @Override
    public void onDisable() {

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;

        economy = rsp.getProvider();
        return true;
    }

    private void setupConfigs() {
        configuration = new Configuration(this, "config");
        balanceGUIConfig = new Configuration(this, "balance_gui");
        minesAmountGUIConfig = new Configuration(this, "mines_amount_gui");
        minesGUIConfig = new Configuration(this, "mines_gui");

        maxMinesAmount = configuration.getConfig().getInt("maxMinesAmount");
        minMinesAmount = configuration.getConfig().getInt("minMinesAmount");
        multiplier = configuration.getConfig().getDouble("multiplier");
        expression = configuration.getConfig().getString("expression");
    }

    private void setupManagers() {
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
    }

    private void setupCommands() {
        getCommand("mines").setExecutor(new MinesCommand(this));
    }

}
