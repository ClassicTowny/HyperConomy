package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Economyinfo {
	Economyinfo(_Command command, String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				if (player != null) {
					sender.sendMessage(L.f(L.get("PART_OF_ECONOMY"), sf.getHyperPlayer(player).getEconomy()));
				} else {
					sender.sendMessage(L.f(L.get("PART_OF_ECONOMY"), command.getNonPlayerEconomy()));
				}
			} else {
				sender.sendMessage(L.get("ECONOMYINFO_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("ECONOMYINFO_INVALID"));
		}
	}
}
