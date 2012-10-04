package regalowl.hyperconomy;

import static regalowl.hyperconomy.Messages.ALREADY_IN_DATABASE;
import static regalowl.hyperconomy.Messages.ITEM_ADDED;
import static regalowl.hyperconomy.Messages.YMLADDITEM_INVALID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Ymladditem {
	Ymladditem(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		try {
			String name = args[0];
			double value = Double.parseDouble(args[1]);
			int median = Integer.parseInt(args[2]);
			double startprice = Double.parseDouble(args[3]);
			int itd = player.getItemInHand().getTypeId();
			int da = calc.getpotionDV(player.getItemInHand());
			int newdat = calc.newData(itd, da);
			String ke = itd + ":" + newdat;
			String nam = hc.getnameData(ke);
			if (nam != null) {
				player.sendMessage(ALREADY_IN_DATABASE);
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
			player.sendMessage(ITEM_ADDED);
			return;
		} catch (Exception e) {
			player.sendMessage(YMLADDITEM_INVALID);
			return;
		}
	}
}
