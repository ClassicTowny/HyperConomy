package regalowl.hyperconomy.command;

import java.util.Iterator;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperItemStack;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.util.LanguageFile;

public class Hv {
	Hv(String args[], Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();
		int amount;
		try {
			HyperPlayer hp = em.getHyperPlayer(player);
			HyperEconomy he = hp.getHyperEconomy();
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
			if ((requireShop && em.inAnyShop(player)) || !requireShop || player.hasPermission("hyperconomy.admin")) {
				ItemStack iinhand = player.getItemInHand();
				if (args.length == 0) {
					amount = 1;
				} else {
					amount = Integer.parseInt(args[0]);
					if (amount > 10000) {
						amount = 10000;
					}
				}
				if (!new HyperItemStack(iinhand).hasEnchants()) {
					HyperObject ho = he.getHyperObject(player.getItemInHand(), em.getShop(player));
					if (ho == null) {
						player.sendMessage(L.get("OBJECT_NOT_AVAILABLE"));
					} else {
						String displayName = ho.getDisplayName();
						double val = ho.getSellPrice(amount, hp);
						if (ho.isDurable() && amount > 1) {
							int numberofitem = ho.count(player.getInventory());
							if (amount - numberofitem > 0) {
								int addamount = amount - numberofitem;
								val = val + ho.getSellPrice(addamount);
							}
						}
						double salestax = hp.getSalesTax(val);
						val = cf.twoDecimals(val - salestax);
						player.sendMessage(L.get("LINE_BREAK"));
						player.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, displayName));
						double cost = ho.getBuyPrice(amount);
						double taxpaid = ho.getPurchaseTax(cost);
						cost = cf.twoDecimals(cost + taxpaid);
						double stock = 0;
						stock = ho.getStock();
						player.sendMessage(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cost, displayName));
						player.sendMessage(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), cf.twoDecimals(stock), displayName));
						player.sendMessage(L.get("LINE_BREAK"));
					}
				} else {
					player.getItemInHand().getEnchantments().keySet().toArray();
					Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
					player.sendMessage(L.get("LINE_BREAK"));
					while (ite.hasNext()) {
						String rawstring = ite.next().toString();
						String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
						Enchantment en = null;
						en = Enchantment.getByName(enchname);
						int lvl = player.getItemInHand().getEnchantmentLevel(en);
						String enam = he.getEnchantNameWithoutLevel(enchname);
						String fnam = enam + lvl;
						String mater = player.getItemInHand().getType().name();
						HyperObject ho = he.getHyperObject(fnam, em.getShop(player));
						double value = ho.getSellPrice(EnchantmentClass.fromString(mater), hp);
						double cost = ho.getBuyPrice(EnchantmentClass.fromString(mater));
						cost = cost + ho.getPurchaseTax(cost);
						value = cf.twoDecimals(value);
						cost = cf.twoDecimals(cost);
						double salestax = 0;
						salestax = hp.getSalesTax(value);
						value = cf.twoDecimals(value - salestax);
						player.sendMessage(L.f(L.get("EVALUE_SALE"), value, fnam));
						player.sendMessage(L.f(L.get("EVALUE_PURCHASE"), cost, fnam));
						player.sendMessage(L.f(L.get("EVALUE_STOCK"), cf.twoDecimals(he.getHyperObject(fnam, em.getShop(player)).getStock()), fnam));
					}
					player.sendMessage(L.get("LINE_BREAK"));
				}
			} else {
				player.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("HV_INVALID"));
		}
	}
}
