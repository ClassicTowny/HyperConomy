package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setstaticall {
	Setstaticall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> names = hc.getNames();
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		if (!(args.length == 1)) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /setstaticall ['true' or 'false']");
			return;
		}
		String setting = "";
		if (args[0].equalsIgnoreCase("true")) {
			setting = "true";
		} else if (args[0].equalsIgnoreCase("false")) {
			setting = "false";
		} else if (args[0].equalsIgnoreCase("copy")) {
			setting = "copy";
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /setstaticall ['true' or 'false']");
			return;
		}
		new Backup();

		if (setting.equalsIgnoreCase("copy")) {
			for (int i = 0; i < names.size(); i++) {
				String name = names.get(i);
				sf.setStaticPrice(name, playerecon, sf.getStartPrice(name, playerecon));
				sf.setStatic(name, playerecon, "true");
			}
			setting = "true + dynamic prices copied";
		} else {
			for (int i = 0; i < names.size(); i++) {
				sf.setStatic(names.get(i), playerecon, setting);
			}
		}
		
		
		isign.setrequestsignUpdate(true);
		isign.checksignUpdate();
		sender.sendMessage(ChatColor.GOLD + "All objects set to static pricing: " + setting + ".");
	}
}
