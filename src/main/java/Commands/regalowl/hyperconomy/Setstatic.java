package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setstatic {
	Setstatic(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 1) {
				name = args[0];
				if (sf.itemTest(name)) {
					boolean nstatus;
					boolean sstatus = false;
					sstatus = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic());
					if (sstatus) {
						nstatus = false;
						sender.sendMessage(L.f(L.get("USE_DYNAMIC_PRICE"), name));
					} else {
						nstatus = true;
						sender.sendMessage(L.f(L.get("USE_STATIC_PRICE"), name));
					}
					sf.getHyperObject(name, playerecon).setIsstatic(nstatus + "");
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else if (args.length == 2) {
				String ench = args[1];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					if (sf.enchantTest(name)) {
						boolean nstatus;
						boolean sstatus = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic());
						if (sstatus) {
							nstatus = false;
							sender.sendMessage(L.f(L.get("USE_DYNAMIC_PRICE"), name));
						} else {
							nstatus = true;
							sender.sendMessage(L.f(L.get("USE_STATIC_PRICE"), name));
						}
						sf.getHyperObject(name, playerecon).setIsstatic(nstatus + "");
						isign.updateSigns();
					} else {
						sender.sendMessage(L.get("INVALID_ENCHANTMENT_NAME"));
					}
				} else {
					sender.sendMessage(L.get("SETSTATIC_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("SETSTATIC_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTATIC_INVALID"));
		}
	}
}
