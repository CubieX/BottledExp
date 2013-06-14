package me.sacnoth.bottledexp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Config 
{
   private BottledExp plugin;

   public Config(BottledExp plugin)
   {
      this.plugin = plugin;
   }

   public void load()
   {
      final FileConfiguration config = plugin.getConfig();

      config.addDefault("bottle.xpCost", 25);
      BottledExp.xpCost = config.getInt("bottle.xpCost");
      config.set("bottle.xpCost", BottledExp.xpCost);

      config.addDefault("bottle.xpEarn", 20);
      BottledExp.xpEarn = config.getInt("bottle.xpEarn");
      config.set("bottle.xpEarn", BottledExp.xpEarn);

      config.addDefault("bottle.useItems", true);
      BottledExp.settingUseItems = config.getBoolean("bottle.useItems");
      config.set("bottle.useItems", BottledExp.settingUseItems);

      config.addDefault("bottle.consumedItem", 374);
      BottledExp.settingConsumedItem = config.getInt("bottle.consumedItem");
      config.set("bottle.consumedItem", BottledExp.settingConsumedItem);

      config.addDefault("bottle.amountConsumed", 1);
      BottledExp.amountConsumed = config.getInt("bottle.amountConsumed");
      config.set("bottle.amountConsumed", BottledExp.amountConsumed);

      config.addDefault("bottle.useMoney", false);
      BottledExp.useVaultEcon = config.getBoolean("bottle.useMoney");
      config.set("bottle.useMoney", BottledExp.useVaultEcon);

      config.addDefault("bottle.moneyCost", 100);
      BottledExp.moneyCost = config.getDouble("bottle.moneyCost");
      config.set("bottle.moneyCost", BottledExp.moneyCost);

      config.addDefault("language.errAmount", "Die Anzahl muss eine Zahl sein!");
      BottledExp.errAmount = config.getString("language.errAmount");
      config.set("language.errAmount", BottledExp.errAmount);

      config.addDefault("language.errXP", "Du hast nicht genug XP!");
      BottledExp.errXP = config.getString("language.errXP");
      config.set("language.errXP", BottledExp.errXP);

      config.addDefault("language.errMoney", "Du hast nicht genug Geld!");
      BottledExp.errMoney = config.getString("language.errMoney");
      config.set("language.errMoney", BottledExp.errMoney);

      config.addDefault("language.currentXP", "Du hast momentan");
      BottledExp.langCurrentXP = config.getString("language.currentXP");
      config.set("language.currentXP", BottledExp.langCurrentXP);

      config.addDefault("language.order1", "Du hast");
      BottledExp.langOrder1 = config.getString("language.order1");
      config.set("language.order1", BottledExp.langOrder1);

      config.addDefault("language.order2", "Flaschen befuellt.");
      BottledExp.langOrder2 = config.getString("language.order2");
      config.set("language.order2", BottledExp.langOrder2);

      config.addDefault("language.refund", "Rueckzahlungsbetrag");
      BottledExp.langRefund = config.getString("language.refund");
      config.set("language.refund", BottledExp.langRefund);

      config.addDefault("language.itemConsume", "Du hast nicht genuegend leere Flaschen!");
      BottledExp.langItemConsumer = config.getString("language.itemConsume");
      config.set("language.itemConsume", BottledExp.langItemConsumer);

      config.addDefault("language.money", "Kosten");
      BottledExp.langMoney = config.getString("language.money");
      config.set("language.money", BottledExp.langMoney);

      if (BottledExp.xpEarn > BottledExp.xpCost)
      {
         BottledExp.log.warning("Players earn more XP through XP bottles than they cost!");
      }

      plugin.saveConfig();
   }

   public void reload(CommandSender sender)
   {
      plugin.reloadConfig();
      final FileConfiguration config = plugin.getConfig();

      BottledExp.xpCost = config.getInt("bottle.xpCost");
      BottledExp.xpEarn = config.getInt("bottle.xpEarn");
      BottledExp.settingUseItems = config.getBoolean("bottle.useItems");
      BottledExp.settingConsumedItem = config.getInt("bottle.consumedItem");
      BottledExp.amountConsumed = config.getInt("bottle.amountConsumed");
      BottledExp.useVaultEcon = config.getBoolean("bottle.useMoney");
      BottledExp.moneyCost = config.getDouble("bottle.moneyCost");
      BottledExp.errAmount = config.getString("language.errAmount");
      BottledExp.errXP = config.getString("language.errXP");
      BottledExp.errMoney = config.getString("language.errMoney");
      BottledExp.langCurrentXP = config.getString("language.currentXP");
      BottledExp.langOrder1 = config.getString("language.order1");
      BottledExp.langOrder2 = config.getString("language.order2");
      BottledExp.langRefund = config.getString("language.refund");
      BottledExp.langItemConsumer = config.getString("language.itemConsume");
      BottledExp.langMoney = config.getString("language.money");

      if (BottledExp.xpEarn > BottledExp.xpCost)
      {
         sender.sendMessage(ChatColor.RED + "Players earn more XP through XP bottles than they cost!");
      }

      sender.sendMessage(ChatColor.YELLOW + "XP-Cost: " + BottledExp.xpCost);
      sender.sendMessage(ChatColor.YELLOW + "XP-Earn: " + BottledExp.xpEarn);
      sender.sendMessage(ChatColor.YELLOW + "Use items: " + BottledExp.settingUseItems);
      sender.sendMessage(ChatColor.YELLOW + "Item used: " + BottledExp.settingConsumedItem);
      sender.sendMessage(ChatColor.YELLOW + "Amount used: " + BottledExp.amountConsumed);
      sender.sendMessage(ChatColor.YELLOW + "Use money: " + BottledExp.useVaultEcon);
      sender.sendMessage(ChatColor.YELLOW + BottledExp.langMoney + ": " + BottledExp.moneyCost);
   }
}