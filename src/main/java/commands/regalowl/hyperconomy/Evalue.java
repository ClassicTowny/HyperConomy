package regalowl.hyperconomy;

import java.util.Iterator;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;

public class Evalue {
	Evalue(String args[], Player player, CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			HyperPlayer hp = em.getHyperPlayer(player.getName());
			HyperEconomy he = hp.getHyperEconomy();
			boolean requireShop = hc.gYH().gFC("config").getBoolean("config.limit-info-commands-to-shops");
			if ((requireShop && em.inAnyShop(player)) || !requireShop || player.hasPermission("hyperconomy.admin")) {
				if (args.length == 2) {
					String nam = args[0];
					HyperObject ho = he.getHyperObject(nam, em.getShop(player));
					if (ho != null) {

						String type = args[1];
						if (type.equalsIgnoreCase("s")) {
							String[] classtype = new String[9];
							classtype[0] = "leather";
							classtype[1] = "wood";
							classtype[2] = "iron";
							classtype[3] = "chainmail";
							classtype[4] = "stone";
							classtype[5] = "gold";
							classtype[6] = "diamond";
							classtype[7] = "bow";
							classtype[8] = "book";
							int n = 0;
							sender.sendMessage(L.get("LINE_BREAK"));
							while (n < 9) {
								double value = ho.getSellPrice(EnchantmentClass.fromString(classtype[n]));
								double salestax = hp.getSalesTax(value);
								value = cf.twoDecimals(value - salestax);
								sender.sendMessage(L.f(L.get("EVALUE_CLASS_SALE"), 1, value, nam, classtype[n]));
								n++;
							}
							sender.sendMessage(L.get("LINE_BREAK"));
						} else if (type.equalsIgnoreCase("b")) {
							String[] classtype = new String[9];
							classtype[0] = "leather";
							classtype[1] = "wood";
							classtype[2] = "iron";
							classtype[3] = "chainmail";
							classtype[4] = "stone";
							classtype[5] = "gold";
							classtype[6] = "diamond";
							classtype[7] = "bow";
							classtype[8] = "book";
							int n = 0;
							sender.sendMessage(L.get("LINE_BREAK"));
							while (n < 9) {
								double cost = ho.getBuyPrice(EnchantmentClass.fromString(classtype[n]));
								cost = cost + ho.getPurchaseTax(cost);
								sender.sendMessage(L.f(L.get("EVALUE_CLASS_PURCHASE"), 1, cost, nam, classtype[n]));
								n++;
							}
							sender.sendMessage(L.get("LINE_BREAK"));
						} else if (type.equalsIgnoreCase("a")) {
							sender.sendMessage(L.get("LINE_BREAK"));
							sender.sendMessage(L.f(L.get("EVALUE_STOCK"), cf.twoDecimals(he.getHyperObject(nam, em.getShop(player)).getStock()), nam));
							sender.sendMessage(L.get("LINE_BREAK"));
						} else {
							sender.sendMessage(L.get("EVALUE_INVALID"));
						}
					} else {
						sender.sendMessage(L.get("ENCHANTMENT_NOT_IN_DATABASE"));
					}
				} else if (args.length == 0 && player != null) {
					if (new HyperItemStack(player.getItemInHand()).hasenchants()) {
						Iterator<Enchantment> ite = new HyperItemStack(player.getItemInHand()).getEnchantmentMap().keySet().iterator();
						player.sendMessage(L.get("LINE_BREAK"));

						while (ite.hasNext()) {
							String rawstring = ite.next().toString();
							String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
							Enchantment en = null;
							en = Enchantment.getByName(enchname);
							int lvl = new HyperItemStack(player.getItemInHand()).getEnchantmentLevel(en);
							String nam = he.getEnchantNameWithoutLevel(enchname);
							String fnam = nam + lvl;
							HyperObject ho = he.getHyperObject(fnam, em.getShop(player));
							if (ho == null) {continue;}
							String mater = player.getItemInHand().getType().name();
							double value = ho.getSellPrice(EnchantmentClass.fromString(mater), hp);
							double cost = ho.getBuyPrice(EnchantmentClass.fromString(mater));
							cost = cost + ho.getPurchaseTax(cost);
							value = cf.twoDecimals(value);
							cost = cf.twoDecimals(cost);
							double salestax = 0;
							if (hc.gYH().gFC("config").getBoolean("config.dynamic-tax.use-dynamic-tax")) {
								double moneycap = hc.gYH().gFC("config").getDouble("config.dynamic-tax.money-cap");
								double cbal = em.getHyperPlayer(player.getName()).getBalance();
								if (cbal >= moneycap) {
									salestax = value * (hc.gYH().gFC("config").getDouble("config.dynamic-tax.max-tax-percent") / 100);
								} else {
									salestax = value * (cbal / moneycap);
								}
							} else {
								double salestaxpercent = hc.gYH().gFC("config").getDouble("config.sales-tax-percent");
								salestax = (salestaxpercent / 100) * value;
							}
							value = cf.twoDecimals(value - salestax);
							sender.sendMessage(L.f(L.get("EVALUE_SALE"), value, fnam));
							sender.sendMessage(L.f(L.get("EVALUE_PURCHASE"), cost, fnam));
							sender.sendMessage(L.f(L.get("EVALUE_STOCK"), cf.twoDecimals(he.getHyperObject(fnam, em.getShop(player)).getStock()), fnam));
						}
						player.sendMessage(L.get("LINE_BREAK"));
					} else {
						sender.sendMessage(L.get("HAS_NO_ENCHANTMENTS"));
					}
				} else {
					sender.sendMessage(L.get("EVALUE_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("EVALUE_INVALID"));
		}
	}
}
