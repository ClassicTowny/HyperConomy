package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Setshop {
	Setshop(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("p1")) {
				String name = args[1].replace(".", "").replace(":", "");
				if (s.shopExists(name)) {
					s.getShop(name).setPoint1(player);
				} else {
					ServerShop shop = new ServerShop(name, "default");
					shop.setPoint1(player);
					shop.setPoint2(player);
					shop.setDefaultMessages();
					s.addShop(shop);
				}
				player.sendMessage(L.get("P1_SET"));
			} else if (args[0].equalsIgnoreCase("p2")) {
				String name = args[1].replace(".", "").replace(":", "");
				if (s.shopExists(name)) {
					s.getShop(name).setPoint2(player);
				} else {
					ServerShop shop = new ServerShop(name, "default");
					shop.setPoint1(player);
					shop.setPoint2(player);
					shop.setDefaultMessages();
					s.addShop(shop);
				}
				player.sendMessage(L.get("P2_SET"));
			}
		} else {
			player.sendMessage(L.get("SETSHOP_INVALID"));
		}
	}
}
