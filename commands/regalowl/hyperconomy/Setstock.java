package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

import regalowl.databukkit.CommonFunctions;

public class Setstock {
	Setstock(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		CommonFunctions cf = hc.gCF();
		String name = "";
		try {
			if (args.length == 2) {
				name = he.fixName(args[0]);
				double stock = cf.round(Double.parseDouble(args[1]), 2);
				if (he.objectTest(name)) {
					he.getHyperObject(name).setStock(stock);
					sender.sendMessage(L.f(L.get("STOCK_SET"), name));
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else {
				sender.sendMessage(L.get("SETSTOCK_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTOCK_INVALID"));
		}
	}
}
