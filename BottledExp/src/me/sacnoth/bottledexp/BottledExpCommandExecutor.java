package me.sacnoth.bottledexp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class BottledExpCommandExecutor implements CommandExecutor
{

   private BottledExp plugin = null;

   public BottledExpCommandExecutor(BottledExp plugin) 
   {
      this.plugin = plugin;
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
   {
      Player player = null;
      int currentxp = 0;
      int amount = 0;

      if (sender instanceof Player) 
      {
         player = (Player) sender;

         currentxp = BottledExp.getPlayerExperience(player);
      }

      if (cmd.getName().equalsIgnoreCase("bottle"))
      {
         if (args.length == 0) 
         {
            if(null != player)
            {
               if(player.isOp() || player.hasPermission("bottle.use"))
               {
                  player.sendMessage(BottledExp.langCurrentXP + ": " + currentxp + " XP!");
               }
            }

            return true;
         }
         else if (args.length == 1) 
         {
            if (args[0].equals("max") || args[0].equals("alles"))
            {
               if(null != player)
               {
                  if (player.isOp() || player.hasPermission("bottle.use"))
                  {
                     amount = (int) Math.floor(currentxp / BottledExp.xpCost);

                     if (currentxp < amount * BottledExp.xpCost) // does player have enough XP?
                     {
                        player.sendMessage(ChatColor.RED + BottledExp.errXP);
                        return true;
                     }

                     if (BottledExp.settingUseItems) 
                     {
                        amount = Math.min(BottledExp.countItems(player, BottledExp.settingConsumedItem) / BottledExp.amountConsumed, amount);
                     }
                     if (BottledExp.useVaultEcon) 
                     {
                        amount = Math.min((int) Math.floor(BottledExp.getBalance(player) / BottledExp.moneyCost), amount);
                     }
                  }                  
               }
            }
            else if (args[0].equals("reload"))
            {
               if (sender.isOp() || sender.hasPermission("bottle.admin"))
               {
                  BottledExp.config.reload(sender);
                  sender.sendMessage(ChatColor.GREEN + "Config reloaded!");                     
               } 
               else 
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to reload " + plugin.getDescription().getName() + "!");
               }

               return true;
            }
            else if (args[0].equalsIgnoreCase("version"))
            {
               if(null != player)
               {
                  player.sendMessage(ChatColor.YELLOW + "This server is running " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion());
               }
               else
               {
                  sender.sendMessage("This server is running " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion());
               }

               return true;
            }
            else
            { // argument may be a number to fill one or more bottles
               if(null != player)
               {
                  if(player.isOp() || player.hasPermission("bottle.use"))
                  {
                     try
                     {
                        amount = Integer.valueOf(args[0]).intValue();
                     } 
                     catch (NumberFormatException nfe) 
                     {
                        player.sendMessage(ChatColor.RED + BottledExp.errAmount);
                        return false;
                     }
                  }
                  else
                  {
                     player.sendMessage(ChatColor.RED + "You have no permission to store XP in bottles!");
                     return true;
                  }
               }
            }

            if (currentxp < amount * BottledExp.xpCost)
            {
               sender.sendMessage(ChatColor.RED + BottledExp.errXP);
               return true;
            }
            else if (amount <= 0) 
            {
               amount = 0;
               sender.sendMessage(BottledExp.langOrder1 + " " + amount + " " + BottledExp.langOrder2);
               return true;
            }

            boolean money = false;

            if (BottledExp.useVaultEcon) // Check if the player has enough money
            {
               if (BottledExp.getBalance(player) >= (BottledExp.moneyCost * amount)) 
               {
                  money = true;
               } 
               else
               {
                  player.sendMessage(BottledExp.errMoney);
                  return true;
               }
            }

            boolean consumeItems = false;

            if (BottledExp.settingUseItems) // Check if the player has enough items
            {
               consumeItems = BottledExp.checkInventory(player, BottledExp.settingConsumedItem, (amount * BottledExp.amountConsumed));

               if (!consumeItems)
               {
                  sender.sendMessage(ChatColor.RED + BottledExp.langItemConsumer);
                  return true;
               }
            }

            PlayerInventory inventory = player.getInventory();
            ItemStack items = new ItemStack(384, amount);

            // Add metadata to created bottles to make a distinction between NPC bottles and BottledExp bottles

            ItemMeta meta = items.getItemMeta();
            List<String> lore = new ArrayList<String>();
            lore.add("Enthaelt " + plugin.getConfig().getInt("bottle.xpEarn") + " XP.");
            meta.setDisplayName("XP Flasche");
            meta.setLore(lore);                              
            items.setItemMeta(meta);

            //----------------

            HashMap<Integer, ItemStack> leftoverItems = inventory.addItem(items);
            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0);
            player.giveExp(currentxp - (amount * BottledExp.xpCost));

            if (leftoverItems.containsKey(0))
            {
               int refundAmount = leftoverItems.get(0).getAmount();
               player.giveExp(refundAmount * BottledExp.xpCost);
               player.sendMessage(BottledExp.langRefund + ": " + refundAmount);
               amount -= refundAmount;
            }

            if (money) // Remove money from player
            {
               BottledExp.withdrawMoney(player, BottledExp.moneyCost * amount);
               player.sendMessage(BottledExp.langMoney + ": " + BottledExp.moneyCost * amount + " " + BottledExp.econ.currencyNamePlural());
            }

            if (consumeItems) // Remove items from Player
            {
               if (!BottledExp.consumeItem(player, BottledExp.settingConsumedItem, amount * BottledExp.amountConsumed))
               {
                  sender.sendMessage(ChatColor.RED + BottledExp.langItemConsumer);
                  return true;
               }
            }

            sender.sendMessage(BottledExp.langOrder1 + " " + amount + " " + BottledExp.langOrder2);
         }
         return true;
      }    

      return false;
   }
}
