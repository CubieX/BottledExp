package me.sacnoth.bottledexp;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BottledExp extends JavaPlugin
{
   static Logger log;
   private BottledExpCommandExecutor comExecutor;
   static int xpCost = 25;
   static int xpEarn = 20;
   static boolean useVaultEcon = true;
   static String errAmount;
   static String errXP;
   static String errMoney;
   static String langCurrentXP;
   static String langOrder1;
   static String langOrder2;
   static String langRefund;
   static String langItemConsumer;
   static String langMoney;
   static boolean settingUseItems = true;
   static int settingConsumedItem = 374;
   static int amountConsumed = 1;
   static double moneyCost = 100.0;
   static Config config;
   public static Economy econ = null;

   public void onEnable()
   {
      log = this.getLogger();

      comExecutor = new BottledExpCommandExecutor(this);
      getCommand("bottle").setExecutor(comExecutor);

      getServer().getPluginManager().registerEvents(new EventListener(), this);

      config = new Config(this);
      config.load();

      if (!setupEconomy())
      {
         log.info("Vault not found - Disabling economy capabilities.");
         useVaultEcon = false;
      }

      log.info("You are now able to fill XP into Bottles");
   }

   public void onDisable() 
   {
      log.info("You are no longer able to fill XP into Bottles");
   }
  
   // simulate vanilla values (see wiki)
   public static int levelToExp(int level)
   {
      if (level <= 15)
      {
         return 17 * level;
      }
      else if (level <= 30)
      {
         return (3*level*level/2)-(59*level/2)+360;
      }
      else
      {
         return (7*level*level/2)-(303*level/2)+2220;
      }
   }

   public static int deltaLevelToExp(int level)
   {
      if (level <= 15)
      {
         return 17;
      }
      else if (level <= 30)
      {
         return 3 * level - 31;
      }

      else
      {
         return 7 * level - 155;
      }
   }

   public static int getPlayerExperience(Player player)
   {
      int bukkitExp = player.getTotalExperience();

      return bukkitExp;
   }

   public static boolean checkInventory(Player player, int itemID, int amount)
   {
      PlayerInventory inventory = player.getInventory();

      if (inventory.contains(itemID, amount))
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   public static boolean consumeItem(Player player, int itemID, int amount) 
   {
      PlayerInventory inventory = player.getInventory();

      if (inventory.contains(itemID, amount)) 
      {
         ItemStack items = new ItemStack(itemID, amount);
         inventory.removeItem(items);
         return true;
      }
      else
      {
         return false;
      }
   }

   public static int countItems(Player player, int itemID) 
   {
      PlayerInventory inventory = player.getInventory();

      int amount = 0;
      ItemStack curItem;

      for (int slot = 0; slot < inventory.getSize(); slot++) 
      {
         curItem = inventory.getItem(slot);
         if (curItem != null && curItem.getTypeId() == itemID)
         {
            amount += curItem.getAmount();
         }
      }
      return amount;
   }

   private boolean setupEconomy()
   {
      if (getServer().getPluginManager().getPlugin("Vault") == null)
      {
         return false;
      }
      RegisteredServiceProvider<Economy> rsp = getServer()
            .getServicesManager().getRegistration(Economy.class);
      if (rsp == null)
      {
         return false;
      }
      econ = rsp.getProvider();
      return econ != null;
   }

   public static double getBalance(Player player)
   {
      return BottledExp.econ.getBalance(player.getName());
   }

   public static void withdrawMoney(Player player, double price)
   {
      BottledExp.econ.withdrawPlayer(player.getName(), price);
   }
}
