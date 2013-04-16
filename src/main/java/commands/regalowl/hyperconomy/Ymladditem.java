package regalowl.hyperconomy;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Ymladditem {
	Ymladditem(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		DataHandler dh = hc.getDataFunctions();
		try {
			String name = args[0];
			double value = Double.parseDouble(args[1]);
			int median = Integer.parseInt(args[2]);
			double startprice = Double.parseDouble(args[3]);
			int itd = player.getItemInHand().getTypeId();
			int da = calc.getDamageValue(player.getItemInHand());
			String playerecon = dh.getHyperPlayer(player).getEconomy();
			HyperObject ho =  hc.getDataFunctions().getHyperObject(itd, da, playerecon);
			if (ho != null) {
				player.sendMessage(L.get("ALREADY_IN_DATABASE"));
				return;
			}
			FileConfiguration items = hc.getYaml().getItems();
			items.set(name + ".information.type", "item");
			items.set(name + ".information.category", "unknown");
			items.set(name + ".information.material", player.getItemInHand().getType().toString());
			items.set(name + ".information.id", itd);
			items.set(name + ".information.data", da);
			items.set(name + ".value", value);
			items.set(name + ".price.static", false);
			items.set(name + ".price.staticprice", startprice);
			items.set(name + ".stock.stock", 0);
			items.set(name + ".stock.median", median);
			items.set(name + ".initiation.initiation", true);
			items.set(name + ".initiation.startprice", startprice);
			player.sendMessage(L.get("ITEM_ADDED"));
			return;
		} catch (Exception e) {
			player.sendMessage(L.get("YMLADDITEM_INVALID"));
			return;
		}
	}
}
