package regalowl.hyperconomy;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;


public class Addcategory {
	private HyperConomy hc;

	Addcategory(String args[], CommandSender sender) {
		hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		SerializeArrayList sal = new SerializeArrayList();
		try {
			FileConfiguration category = hc.getYaml().getCategories();
			String testcategory = category.getString(args[0]);
			if (testcategory == null) {
				sender.sendMessage(L.get("CATEGORY_NOT_EXIST"));
				return;
			}
			ArrayList<String> objects = sal.stringToArray(testcategory);
			if (args.length == 2) {
				String shopname = s.fixShopName(args[1]);
				if (s.shopExists(shopname)) {
					ServerShop shop = s.getShop(shopname);
					shop.addObjects(objects);
					sender.sendMessage(ChatColor.GOLD + args[0] + " " + L.get("ADDED_TO") + " " + shopname.replace("_", " "));
				} else {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("ADD_CATEGORY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("ADD_CATEGORY_INVALID"));
		}
	}
}
