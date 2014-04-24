package regalowl.hyperconomy.command;


import org.bukkit.entity.Player;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;

public class Hb {
	HyperConomy hc;

	Hb(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		double amount;
		boolean ma = false;
		try {
			HyperPlayer hp = em.getHyperPlayer(player);
			HyperEconomy he = hp.getHyperEconomy();
			if (em.inAnyShop(player)) {
				if (hp.hasBuyPermission(em.getShop(player))) {
					HyperObject ho = he.getHyperObject(player.getItemInHand(), em.getShop(player));
					if (ho == null) {
						player.sendMessage(L.get("OBJECT_NOT_AVAILABLE"));
						return;
					}
					if (args.length == 0) {
						amount = 1;
					} else {
						try {
							amount = Integer.parseInt(args[0]);
							if (amount > 10000) {
								amount = 10000;
							}
						} catch (Exception e) {
							String max = args[0];
							if (max.equalsIgnoreCase("max")) {
								ma = true;
								int space = ho.getAvailableSpace(player.getInventory());
								amount = space;
							} else {
								player.sendMessage(L.get("HB_INVALID"));
								return;
							}
						}
					}

					double shopstock = 0;
					shopstock = ho.getStock();
					// Buys the most possible from the shop if the
					// amount is more than that for max.
					if (amount > shopstock && ma) {
						amount = shopstock;
					}
					Shop s = em.getShop(player);
					if (!s.isBanned(ho.getName())) {
						PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
						pt.setHyperObject(ho);
						pt.setAmount((int) Math.rint(amount));
						pt.setTradePartner(s.getOwner());
						TransactionResponse response = hp.processTransaction(pt);
						response.sendMessages();
					} else {
						player.sendMessage(L.get("CANT_BE_TRADED"));
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
			}
			return;
		} catch (Exception e) {
			player.sendMessage(L.get("HB_INVALID"));
		}
	}
}
