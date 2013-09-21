package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Esell {
	Esell(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		InventoryManipulation im = hc.getInventoryManipulation();
		try {
			if (player.getGameMode() == GameMode.CREATIVE && hc.s().gB("block-selling-in-creative-mode")) {
				player.sendMessage(L.get("CANT_SELL_CREATIVE"));
				return;
			}
			HyperPlayer hp = em.getHyperPlayer(player.getName());
			HyperEconomy he = hp.getHyperEconomy();
			if (he.inAnyShop(player)) {
				if (hp.hasSellPermission(he.getShop(player))) {
					String name = args[0];
					if (args[0].equalsIgnoreCase("max")) {
						if (!im.hasenchants(player.getItemInHand())) {
							player.sendMessage(L.get("HAS_NO_ENCHANTMENTS"));
						}
						ArrayList<String> enchants = im.getEnchantments(player.getItemInHand());
						for (String e:enchants) {
							if (he.getShop(player).has(e)) {
								PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
								pt.setHyperObject(he.getHyperObject(e));
								TransactionResponse response = hp.processTransaction(pt);
								response.sendMessages();
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						}

					} else {
						if (he.enchantTest(name)) {
							if (he.getShop(player).has(name)) {
								PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
								pt.setHyperObject(he.getHyperObject(name));
								TransactionResponse response = hp.processTransaction(pt);
								response.sendMessages();
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						} else {
							player.sendMessage(L.get("ENCHANTMENT_NOT_IN_DATABASE"));
						}
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("ESELL_INVALID"));
		}
	}
}
