package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setstartprice {
	Setstartprice(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 2) {
				name = he.fixName(args[0]);
				double startprice = Double.parseDouble(args[1]);
				if (he.itemTest(name)) {
					he.getHyperObject(name).setStartprice(startprice);
					sender.sendMessage(L.f(L.get("START_PRICE_SET"), name));
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else if (args.length == 3) {
				String ench = args[2];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					double startprice = Double.parseDouble(args[1]);
					if (he.enchantTest(name)) {
						he.getHyperObject(name).setStartprice(startprice);
						sender.sendMessage(L.f(L.get("START_PRICE_SET"), name));
						isign.updateSigns();
					} else {
						sender.sendMessage(L.get("INVALID_ENCHANTMENT_NAME"));
					}
				} else {
					sender.sendMessage(L.get("SETSTARTPRICE_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("SETSTARTPRICE_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTARTPRICE_INVALID"));
		}
	}
}
