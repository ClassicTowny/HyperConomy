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

public class Hb extends BaseCommand implements HyperCommand{

	public Hb() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		double amount;
		boolean ma = false;
		try {
			HyperEconomy he = hp.getHyperEconomy();
			HyperObject ho = he.getHyperObject(hp.getItemInHand(), em.getHyperShopManager().getShop(player));
			if (ho == null) {
				data.addResponse(L.get("OBJECT_NOT_AVAILABLE"));
				return data;
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
						data.addResponse(L.get("HB_INVALID"));
						return data;
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
			Shop s = hc.getHyperShopManager().getShop(hp);

			PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
			pt.setObeyShops(true);
			pt.setHyperObject(ho);
			pt.setAmount((int) Math.rint(amount));
			pt.setTradePartner(s.getOwner());
			TransactionResponse response = hp.processTransaction(pt);
			response.sendMessages();

		} catch (Exception e) {
			HyperConomy.hc.getDebugMode().debugWriteError(e);
			data.addResponse(L.get("HB_INVALID"));
		}
		return data;
	}
}
