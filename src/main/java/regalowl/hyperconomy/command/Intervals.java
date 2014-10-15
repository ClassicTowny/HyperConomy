package regalowl.hyperconomy.command;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Intervals {
	Intervals(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				SQLWrite sw = hc.getSQLWrite();
				sender.sendMessage(L.get("LINE_BREAK"));
				sender.sendMessage(ChatColor.GREEN + "" + em.getHyperShopManager().getShopCheckInterval() + ChatColor.BLUE + " tick (" + ChatColor.GREEN + "" + em.getHyperShopManager().getShopCheckInterval() / 20 + ChatColor.BLUE + " second) shop update interval.");
				sender.sendMessage(ChatColor.GREEN + "" + hc.gYH().getSaveInterval()/1000 + ChatColor.BLUE + " second save interval.");
				sender.sendMessage(ChatColor.GREEN + "" + sw.getBufferSize() + ChatColor.BLUE + " statements in the SQL write buffer.");
				sender.sendMessage(L.get("LINE_BREAK"));
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
		}
	}
}
