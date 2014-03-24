package regalowl.hyperconomy.command;

import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.InfoSignHandler;
import regalowl.hyperconomy.util.LanguageFile;

public class Settax {

	Settax(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		double taxrate = 0.0;
		try {
			if (args.length != 2) {
				sender.sendMessage(L.get("SETTAX_INVALID"));
				return;
			} else {
				String type = args[0];
				if (type.equalsIgnoreCase("purchase")) {
					taxrate = Double.parseDouble(args[1]);
					hc.gYH().gFC("config").set("tax.purchase", taxrate);
					sender.sendMessage(L.get("PURCHASE_TAX_SET"));
				} else if (type.equalsIgnoreCase("sales")) {
					taxrate = Double.parseDouble(args[1]);
					hc.gYH().gFC("config").set("tax.sales", taxrate);
					sender.sendMessage(L.get("SALES_TAX_SET"));
				} else if (type.equalsIgnoreCase("static")) {
					taxrate = Double.parseDouble(args[1]);
					hc.gYH().gFC("config").set("tax.static", taxrate);
					sender.sendMessage(L.get("STATIC_TAX_SET"));
				} else if (type.equalsIgnoreCase("initial")) {
					taxrate = Double.parseDouble(args[1]);
					hc.gYH().gFC("config").set("tax.initial", taxrate);
					sender.sendMessage(L.get("INITIAL_TAX_SET"));
				} else if (type.equalsIgnoreCase("enchant")) {
					taxrate = Double.parseDouble(args[1]);
					hc.gYH().gFC("config").set("tax.enchant", taxrate);
					sender.sendMessage(L.get("ENCHANT_TAX_SET"));
				} else {
					sender.sendMessage(L.get("SETTAX_INVALID"));
					return;
				}
				isign.updateSigns();
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETTAX_INVALID"));
		}
	}
	
	
}
