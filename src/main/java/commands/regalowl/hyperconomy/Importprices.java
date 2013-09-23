package regalowl.hyperconomy;

import java.util.Iterator;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Importprices {

	Importprices(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();

		try {
			if (args.length == 1) {
				String economy = args[0];
				if (em.economyExists(economy)) {
					new Backup();
					FileConfiguration itemsyaml = hc.gYH().gFC("items");
					Iterator<String> it = itemsyaml.getKeys(false).iterator();
					while (it.hasNext()) {
						String name = it.next().toString();
						double value = itemsyaml.getDouble(name + ".value");
						double staticprice = itemsyaml.getDouble(name + ".price.staticprice");
						double startprice = itemsyaml.getDouble(name + ".initiation.startprice");
						HyperObject ho = em.getEconomy(economy).getHyperObject(name);
						ho.setValue(value);
						ho.setStartprice(startprice);
						ho.setStaticprice(staticprice);
					}
					FileConfiguration enchantsyaml = hc.gYH().gFC("enchants");
					Iterator<String> it2 = enchantsyaml.getKeys(false).iterator();
					while (it2.hasNext()) {
						String name = it2.next().toString();
						double value = enchantsyaml.getDouble(name + ".value");
						double staticprice = enchantsyaml.getDouble(name + ".price.staticprice");
						double startprice = enchantsyaml.getDouble(name + ".initiation.startprice");
						HyperObject ho = em.getEconomy(economy).getHyperObject(name);
						ho.setValue(value);
						ho.setStartprice(startprice);
						ho.setStaticprice(staticprice);
					}
					sender.sendMessage(L.get("PRICES_IMPORTED"));
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
					return;
				}
			} else {
				sender.sendMessage(L.get("IMPORTPRICES_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTPRICES_INVALID"));
		}
	}
}
