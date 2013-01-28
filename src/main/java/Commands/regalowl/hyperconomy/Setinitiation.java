package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setinitiation {
	Setinitiation(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 1) {
				name = args[0];
				if (hc.itemTest(name)) {
					boolean nstatus;
					boolean istatus = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation());
					if (istatus) {
						nstatus = false;
						sender.sendMessage(L.f(L.get("INITIATION_FALSE"), name));
					} else {
						nstatus = true;
						//sender.sendMessage(ChatColor.GOLD + "Initiation price is set to true for " + name);
						sender.sendMessage(L.f(L.get("INITIATION_TRUE"), name));
					}
					sf.getHyperObject(name, playerecon).setInitiation(nstatus + "");
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else if (args.length == 2) {
				String ench = args[1];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					if (hc.enchantTest(name)) {
						boolean nstatus;
						boolean istatus = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation());
						if (istatus) {
							nstatus = false;
							sender.sendMessage(L.f(L.get("INITIATION_FALSE"), name));
						} else {
							nstatus = true;
							sender.sendMessage(L.f(L.get("INITIATION_TRUE"), name));
						}
						sf.getHyperObject(name, playerecon).setInitiation(nstatus + "");
						isign.updateSigns();
					} else {
						sender.sendMessage(L.get("INVALID_ENCHANTMENT_NAME"));
					}
				} else {
					sender.sendMessage(L.get("INITIATION_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("INITIATION_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("INITIATION_INVALID"));
		}
	}
}
