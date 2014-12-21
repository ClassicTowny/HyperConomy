package regalowl.hyperconomy.transaction;




import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.event.TransactionEvent;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HEnchantmentStorageMeta;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemMeta;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.Log;
import regalowl.hyperconomy.util.MessageBuilder;



public class TransactionProcessor {

	private HyperConomy hc;
	private HyperEventHandler heh;
	private LanguageFile L;
	private HyperPlayer hp;
	private DataManager dm;
	private Log log;
	
	private TransactionType transactionType;
	private HyperAccount tradePartner;
	private TradeObject tradeObject;
	private int amount;
	private HInventory giveInventory;
	private HInventory receiveInventory;
	private double money;
	//private boolean chargeTax;
	private boolean setPrice;
	//private HItemStack giveItem;
	private TradeObjectStatus status;
	private boolean overMaxStock;
	private boolean obeyShops;
	
	
	private boolean shopUnlimitedMoney;
	
	private TransactionResponse response;
	
	
	public TransactionProcessor(HyperConomy hc, HyperPlayer hp) {
		this.hc = hc;
		this.hp = hp;
		L = hc.getLanguageFile();
		dm = hc.getDataManager();
		log = hc.getLog();
		heh = hc.getHyperEventHandler();
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction pt) {
		transactionType = pt.getTransactionType();
		tradeObject = pt.getHyperObject();
		amount = pt.getAmount();
		overMaxStock = false;
		if (tradeObject.isShopObject()) {
			status = tradeObject.getShopObjectStatus();
			int maxStock = tradeObject.getShopObjectMaxStock();
			int globalMaxStock = hc.getConf().getInt("shop.max-stock-per-item-in-playershops");
			if ((tradeObject.getStock() + amount) > maxStock || (tradeObject.getStock() + amount) > globalMaxStock) {
				overMaxStock = true;
			}
		} else {
			status = TradeObjectStatus.TRADE;
		}
		tradePartner = pt.getTradePartner();
		if (tradePartner == null) {
			tradePartner = hp.getHyperEconomy().getDefaultAccount();
		}
		giveInventory = pt.getGiveInventory();
		if (giveInventory == null) {
			giveInventory = hp.getInventory();
		}
		receiveInventory = pt.getReceiveInventory();
		if (receiveInventory == null) {
			receiveInventory = hp.getInventory();
		}
		money = pt.getMoney();
		//chargeTax = pt.isChargeTax();
		setPrice = pt.isSetPrice();
		//giveItem = pt.getGiveItem();
		obeyShops = pt.obeyShops();
		
		shopUnlimitedMoney = hc.getConf().getBoolean("shop.server-shops-have-unlimited-money");
		
		response = new TransactionResponse(hc, hp);

		switch (this.transactionType) {
			case BUY:
				checkShopBuy();
				if (response.getFailedObjects().size() > 0) {break;}
				if (tradeObject.getType() == TradeObjectType.ITEM) {
					buy();
					break;
				} else if (tradeObject.getType() == TradeObjectType.EXPERIENCE) {
					buyXP();
					break;
				} else if (tradeObject.getType() == TradeObjectType.ENCHANTMENT) {
					buyEnchant();
					break;
				}
			case SELL:
				checkShopSell();
				if (response.getFailedObjects().size() > 0) {break;}
				if (tradeObject.getType() == TradeObjectType.ITEM) {
					sell();
					break;
				} else if (tradeObject.getType() == TradeObjectType.EXPERIENCE) {
					sellXP();
					break;
				} else if (tradeObject.getType() == TradeObjectType.ENCHANTMENT) {
					sellEnchant();
					break;
				}
			case SELL_TO_INVENTORY:
				sellToInventory();
				break;
			case BUY_FROM_INVENTORY:
				buyFromInventory();
				break;
			//case BUY_FROM_ITEM:
			//	buyEnchantFromItem();
			//	break;
		}
		heh.fireEvent(new TransactionEvent(pt, response));
		return response;
	}
	
	
	private void resetBalanceIfUnlimited() {
		if (shopUnlimitedMoney && tradePartner.equals(hp.getHyperEconomy().getDefaultAccount())) {
			tradePartner.setBalance(0);
		}
	}
	
	private boolean hasBalance(double price) {
		if (!tradePartner.hasBalance(price)) {
			if (shopUnlimitedMoney && tradePartner.equals(hp.getHyperEconomy().getDefaultAccount())) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	
	private void checkShopBuy() {
		if (hp == null || tradeObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), tradeObject);
			return;
		}
		if (obeyShops) {
			if (!dm.getHyperShopManager().inAnyShop(hp)) {
				response.addFailed(L.get("MUST_BE_IN_SHOP"), tradeObject);
				return;
			} else {
				Shop shop = dm.getHyperShopManager().getShop(hp);
				if (!hp.hasBuyPermission(shop)) {
					response.addFailed(L.get("NO_TRADE_PERMISSION"), tradeObject);
					return;
				}
				if (shop.isBanned(tradeObject)) {
					response.addFailed(L.get("CANT_BE_TRADED"), tradeObject);
					return;
				}
			}
		}
		if (status == TradeObjectStatus.NONE) {
			response.addFailed(L.f(L.get("NO_TRADE_ITEM"), tradeObject.getDisplayName()), tradeObject);
			return;
		} else if (status == TradeObjectStatus.SELL) {
			response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
		if (amount <= 0) {
			response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
		if (tradeObject.getStock() < amount) {
			response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
	}
	

	public void buy() {
		try {
			double price = tradeObject.getBuyPrice(amount);
			double taxpaid = tradeObject.getPurchaseTax(price);
			price = CommonFunctions.twoDecimals(price + taxpaid);
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), tradeObject);
				return;
			}
			int space = receiveInventory.getAvailableSpace(tradeObject.getItem());
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, tradeObject.getDisplayName()), tradeObject);
				return;
			}
			receiveInventory.add(amount, tradeObject.getItem());
			if (!tradeObject.isStatic() || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || tradeObject.isShopObject()) {
				tradeObject.setStock(tradeObject.getStock() - amount);
			}
			hp.withdraw(price);
			tradePartner.deposit(price);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price, tradeObject.getDisplayName(), CommonFunctions.twoDecimals(taxpaid)), price, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", tradeObject.getDisplayName(), (double) amount, CommonFunctions.twoDecimals(price - taxpaid), CommonFunctions.twoDecimals(taxpaid), tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction buy() passed values name='" + tradeObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
			return;
		}
	}
	
	
	public void buyXP() {
		try {
			double price = tradeObject.getBuyPrice(amount);
			double taxpaid = tradeObject.getPurchaseTax(price);
			price = CommonFunctions.twoDecimals(price + taxpaid);
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), tradeObject);
				return;
			}
			tradeObject.add(amount, hp);
			if (!tradeObject.isStatic() || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || tradeObject.isShopObject()) {
				tradeObject.setStock(tradeObject.getStock() - amount);
			}
			hp.withdraw(price);
			tradePartner.deposit(price);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, CommonFunctions.twoDecimals(price), tradeObject.getDisplayName(), CommonFunctions.twoDecimals(taxpaid)), CommonFunctions.twoDecimals(price), tradeObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hp.getName(), (double) amount, CommonFunctions.twoDecimals(price), CommonFunctions.twoDecimals(taxpaid), tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction buyXP() passed values name='" + tradeObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function handles the purchase of enchantments.
	 * 
	 */
	public void buyEnchant() {
		try {
			HInventory inv = hp.getInventory();
			HItemStack heldItem = inv.getHeldItem();
			HEnchantment ench = tradeObject.getEnchantment();
			double price = tradeObject.getBuyPrice(EnchantmentClass.fromString(heldItem.getMaterial()));
			double taxpaid = tradeObject.getPurchaseTax(price);
			price = CommonFunctions.twoDecimals(taxpaid + price);
			if (heldItem.containsEnchantment(ench)) {
				response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), tradeObject);
				return;
			}
			if (!heldItem.canAcceptEnchantment(ench)) {
				response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), tradeObject);
				return;
			}
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), tradeObject);
				return;
			}
			tradeObject.setStock(tradeObject.getStock() - amount);
			hp.withdraw(price);
			tradePartner.deposit(price);
			resetBalanceIfUnlimited();
			
			if (heldItem.getMaterial().equalsIgnoreCase("BOOK")) {
				HItemMeta cMeta = heldItem.getItemMeta();
				cMeta.addEnchantment(ench);
				heldItem.setMaterial("ENCHANTED_BOOK");
				heldItem.setHItemMeta(new HEnchantmentStorageMeta(cMeta));
			} else {
				heldItem.addEnchantment(ench);
			}
			inv.updateInventory();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("ENCHANTMENT_PURCHASE_MESSAGE"), 1, price, tradeObject.getDisplayName(), CommonFunctions.twoDecimals(taxpaid)), CommonFunctions.twoDecimals(price), tradeObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", tradeObject.getDisplayName(), 1.0, CommonFunctions.twoDecimals(price), CommonFunctions.twoDecimals(taxpaid), tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "ETransaction buyEnchant() passed values name='" + tradeObject.getDisplayName() + "', player='" + hp.getName() + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void checkShopSell() {
		if (hp == null || tradeObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), tradeObject);
			return;
		}
		if (hp.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
			response.addFailed(L.get("CANT_SELL_CREATIVE"), tradeObject);
			return;
		}
		if (obeyShops) {
			if (!dm.getHyperShopManager().inAnyShop(hp)) {
				response.addFailed(L.get("MUST_BE_IN_SHOP"), tradeObject);
				return;
			} else {
				Shop shop = dm.getHyperShopManager().getShop(hp);
				if (!hp.hasSellPermission(shop)) {
					response.addFailed(L.get("NO_TRADE_PERMISSION"), tradeObject);
					return;
				}
				if (shop.isBanned(tradeObject)) {
					response.addFailed(L.get("CANT_BE_TRADED"), tradeObject);
					return;
				}
			}
		}
		if (status == TradeObjectStatus.NONE) {
			response.addFailed(L.f(L.get("NO_TRADE_ITEM"), tradeObject.getDisplayName()), tradeObject);
			return;
		} else if (status == TradeObjectStatus.BUY) {
			response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
		if (amount <= 0) {
			response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
		if (overMaxStock) {
			response.addFailed(L.f(L.get("OVER_MAX_STOCK"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
	}
	
	

	/**
	 * 
	 * 
	 * This function handles the sale of items.
	 * 
	 */
	
	public void sell() {
		try {
			String name = tradeObject.getDisplayName();
			if (tradeObject.getItem() == null) {
				response.addFailed(L.f(L.get("CANNOT_BE_SOLD_WITH"), name), tradeObject);
				return;
			}
			int totalitems = giveInventory.count(tradeObject.getItem());
			if (totalitems < amount) {
				boolean sellRemaining = hc.getConf().getBoolean("shop.sell-remaining-if-less-than-requested-amount");
				if (sellRemaining) {
					amount = totalitems;
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), tradeObject);
					return;
				}
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), tradeObject);
				return;
			}
			double price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(amount, hp));
			double amountRemoved = giveInventory.remove(amount, tradeObject.getItem());
			double shopstock = tradeObject.getStock();
			if (!tradeObject.isStatic() || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || tradeObject.isShopObject()) {
				tradeObject.setStock(shopstock + amountRemoved);
			}
			double salestax = CommonFunctions.twoDecimals(hp.getSalesTax(price));
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, price, name, salestax), price - salestax, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", name, (double)amount, price - salestax, salestax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction sell() passed values name='" + tradeObject.getDisplayName() + "', player='" + hp.getName() + ", amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}
	
	
	



	public void sellXP() {
		try {
			if (hp.getTotalXpPoints() < amount) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), tradeObject.getDisplayName()), tradeObject);
				return;
			}
			double price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(amount));
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), tradeObject);
				return;
			}
			tradeObject.remove(amount, hp);
			if (!tradeObject.isStatic() || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || tradeObject.isShopObject()) {
				tradeObject.setStock(amount + tradeObject.getStock());
			}
			double salestax = CommonFunctions.twoDecimals(hp.getSalesTax(price));
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, price, tradeObject.getDisplayName(), salestax), price, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", tradeObject.getDisplayName(), (double) amount, price - salestax, salestax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction sellXP() passed values name='" + tradeObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}


	
	


	
	
	
	/**
	 * 
	 * 
	 * This function handles the sale of enchantments.
	 * 
	 */
	public void sellEnchant() {
		try {
			HInventory inv = hp.getInventory();
			HItemStack heldItem = inv.getHeldItem();
			if (!(heldItem.containsEnchantment(tradeObject.getEnchantment()))) {
				response.addFailed(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), tradeObject.getDisplayName()), tradeObject);
				return;
			}
			String mater = heldItem.getMaterial().toString();
			double price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(EnchantmentClass.fromString(mater), hp));
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), tradeObject);
				return;
			}
			double shopstock = tradeObject.getStock();
			double amountRemoved = heldItem.removeEnchantment(tradeObject.getEnchantment());
			if (heldItem.getMaterial().equalsIgnoreCase("ENCHANTED_BOOK") && !heldItem.hasEnchantments()) {
				heldItem.setMaterial("BOOK");
				heldItem.setHItemMeta(new HItemMeta(heldItem.getItemMeta()));
			}
			
			inv.updateInventory();
			tradeObject.setStock(shopstock + amountRemoved);
			double salestax = CommonFunctions.twoDecimals(hp.getSalesTax(price));
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, price, tradeObject.getDisplayName(), salestax), price - salestax, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", tradeObject.getDisplayName(), 1.0, price - salestax, salestax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "ETransaction sellEnchant() passed values name='" + tradeObject.getDisplayName() + "', player='" + hp.getName() + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void buyFromInventory() {
		if (hp == null || tradePartner == null || tradeObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), tradeObject);
			return;
		}
		try {
			double price = 0.0;
			if (setPrice) {
				price = money;
			} else {
				price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(amount));
			}
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), tradeObject);
				return;
			}
			int space = hp.getInventory().getAvailableSpace(tradeObject.getItem());
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, tradeObject.getDisplayName()), tradeObject);
				return;
			}
			hp.getInventory().add(amount, tradeObject.getItem());
			giveInventory.remove(amount, tradeObject.getItem());
			hp.withdraw(price);
			tradePartner.deposit(price);
			response.addSuccess(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, price, tradeObject.getDisplayName(), tradePartner.getName()), price, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", tradeObject.getDisplayName(), (double) amount, price, 0.0, tradePartner.getName(), "chestshop");
			
			MessageBuilder mb = new MessageBuilder(hc, "CHEST_BUY_NOTIFICATION");
			mb.setAmount(amount);
			mb.setObjectName(tradeObject.getDisplayName());
			mb.setPrice(price);
			mb.setPlayerName(hp.getName());
			tradePartner.sendMessage(mb.build());
		} catch (Exception e) {
			String info = "Transaction buyChest() passed values name='" + tradeObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}



	/**
	 * 
	 * 
	 * This function handles the sale of items from HyperChests.
	 * 
	 */
	public void sellToInventory() {
		if (hp == null || tradePartner == null || tradeObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), tradeObject);
			return;
		}
		try {
			double price = 0.0;
			if (setPrice) {
				price = money;
			} else {
				price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(amount, hp));
			}
			hp.getInventory().remove(amount, tradeObject.getItem());
			receiveInventory.add(amount, tradeObject.getItem());
			hp.deposit(price);
			tradePartner.withdraw(price);
			response.addSuccess(L.f(L.get("SELL_CHEST_MESSAGE"), amount, price, tradeObject.getDisplayName(), tradePartner.getName()), price, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", tradeObject.getDisplayName(), (double) amount, price, 0.0, tradePartner.getName(), "chestshop");
			
			MessageBuilder mb = new MessageBuilder(hc, "CHEST_SELL_NOTIFICATION");
			mb.setAmount(amount);
			mb.setObjectName(tradeObject.getDisplayName());
			mb.setPrice(price);
			mb.setPlayerName(hp.getName());
			tradePartner.sendMessage(mb.build());
		} catch (Exception e) {
			String info = "Transaction sellChest() passed values name='" + tradeObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}
	
	
	

	
	
	

	
	
	
	
}
