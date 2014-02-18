package regalowl.hyperconomy;

import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Topenchants {
	Topenchants(String args[], Player player, CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			boolean requireShop = hc.gYH().gFC("config").getBoolean("config.limit-info-commands-to-shops");
			if (args.length > 1) {
				sender.sendMessage(L.get("TOPENCHANTS_INVALID"));
				return;
			}
			String nameshop = "";
			if (player != null) {
				if (em.inAnyShop(player)) {
					nameshop = em.getShop(player).getName();
				} 				
				if (requireShop && em.getShop(player) == null && !player.hasPermission("hyperconomy.admin")) {
					sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
					return;
				}
			}
			int page;
			if (args.length == 0) {
				page = 1;
			} else {
				page = Integer.parseInt(args[0]);
			}
			SortedMap<Double, String> enchantstocks = new TreeMap<Double, String>();
			for (HyperObject ho:he.getHyperObjects()) {
				if (!(ho instanceof HyperEnchant)) {continue;}
				PlayerShopObject pso = null;
				boolean allowed = false;
				boolean stocked = false;
				boolean banned = false;
				if (nameshop != "") {
					banned = em.getShop(nameshop).isBanned(ho);
				}
				if (ho.getStock() > 0) {stocked = true;}
				if (ho instanceof PlayerShopObject) {
					pso = (PlayerShopObject)ho;
					allowed = pso.getShop().isAllowed(em.getHyperPlayer(player));
					if (pso.getStatus() == HyperObjectStatus.NONE && !allowed) {
						continue;
					}
				}
				boolean unavailable = false;
				if (nameshop != "") {
					if (banned && !(allowed && stocked)) {
						unavailable = true;
					}
				}
				if (!unavailable) {
					double samount = he.getHyperObject(ho.getName(), em.getShop(player)).getStock();
					if (samount > 0) {
						while (enchantstocks.containsKey(samount * 100)) {
							samount += .00001;
						}
						enchantstocks.put(samount * 100, ho.getDisplayName());
					}
				}
			}
			int numberpage = page * 10;
			int count = 0;
			int le = enchantstocks.size();
			double maxpages = le / 10;
			maxpages = Math.ceil(maxpages);
			int maxpi = (int) maxpages + 1;
			sender.sendMessage(L.f(L.get("PAGE_NUMBER"), page, maxpi));
			try {
				while (count < numberpage) {
					double lk = enchantstocks.lastKey();
					if (count > ((page * 10) - 11)) {
						sender.sendMessage(ChatColor.WHITE + enchantstocks.get(lk) + ChatColor.WHITE + ": " + ChatColor.AQUA + "" + Math.floor(lk)/100);
					}
					enchantstocks.remove(lk);
					count++;
				}
			} catch (Exception e) {
				sender.sendMessage(L.get("YOU_HAVE_REACHED_THE_END"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("TOPENCHANTS_INVALID"));
		}
	}
}
