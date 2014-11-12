package regalowl.hyperconomy.tradeobject;


import java.awt.Image;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperObjectModificationEvent;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;



public class BasicTradeObject implements TradeObject {
	

	private static final long serialVersionUID = 3220675400415233555L;
	protected String name;
	protected String displayName;
	protected ArrayList<String> aliases = new ArrayList<String>();
	protected String economy;
	protected TradeObjectType type;
	protected double value;
	protected String isstatic;
	protected double staticprice;
	protected double stock;
	protected double median;
	protected String initiation;
	protected double startprice;
	protected double ceiling;
	protected double floor;
	protected double maxstock;
	

	public BasicTradeObject() {}
	
	/**
	 * Standard Constructor
	 */
	public BasicTradeObject(String name, String economy, String displayName, String aliases, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		this.name = name;
		this.economy = economy;
		this.displayName = displayName;
		ArrayList<String> tAliases = CommonFunctions.explode(aliases, ",");
		for (String cAlias:tAliases) {
			this.aliases.add(cAlias);
		}
		this.type = TradeObjectType.fromString(type);
		this.value = value;
		this.isstatic = isstatic;
		this.staticprice = staticprice;
		this.stock = stock;
		this.median = median;
		this.initiation = initiation;
		this.startprice = startprice;
		this.ceiling = ceiling;
		this.floor = floor;
		this.maxstock = maxstock;
	}
	@Override
	public void delete() {
		HC hc = HC.hc;
		HC.hc.getDataManager().getEconomy(economy).removeHyperObject(name);
		String statement = "DELETE FROM hyperconomy_objects WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	
	@Override
	public int compareTo(TradeObject ho) {
		if (displayName == null || ho == null) {return 0;}
		return displayName.compareTo(ho.getDisplayName());
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getDisplayName() {
		if (displayName != null) {
			return displayName;
		} else {
			return name;
		}
	}
	@Override
	public ArrayList<String> getAliases() {
		return new ArrayList<String>(aliases);
	}
	@Override
	public String getAliasesString() {
		return CommonFunctions.implode(aliases, ",");
	}
	@Override
	public boolean hasName(String testName) {
		if (name.equalsIgnoreCase(testName)) {
			return true;
		}
		if (displayName.equalsIgnoreCase(testName)) {
			return true;
		}
		for (int i = 0; i < aliases.size(); i++) {
			String alias = aliases.get(i);
			if (alias.equalsIgnoreCase(testName)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public String getEconomy() {
		return economy;
	}
	@Override
	public TradeObjectType getType() {
		return type;
	}
	@Override
	public double getValue() {
		return value;
	}
	@Override
	public String getIsstatic() {
		return isstatic;
	}
	@Override
	public double getStaticprice() {
		return staticprice;
	}
	@Override
	public double getStock() {
		return stock;
	}
	@Override
	public double getTotalStock() {
		HC hc = HC.hc;
		double totalStock = 0.0;
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (!(s instanceof PlayerShop)) {continue;}
			PlayerShop ps = (PlayerShop)s;
			if (!ps.hasPlayerShopObject(this)) {continue;}
			if (!ps.getEconomy().equalsIgnoreCase(economy)) {continue;}
			totalStock += ((PlayerShop) s).getPlayerShopObject(this).getStock();
		}
		totalStock += stock;
		return totalStock;
	}
	@Override
	public double getMedian() {
		return median;
	}
	@Override
	public String getInitiation() {
		return initiation;
	}
	@Override
	public double getStartprice() {
		return startprice;
	}
	@Override
	public double getCeiling() {
		if (ceiling <= 0 || floor > ceiling) {
			return 9999999999999.99;
		}
		return ceiling;
	}
	@Override
	public double getFloor() {
		if (floor < 0 || ceiling < floor) {
			return 0.0;
		}
		return floor;
	}
	@Override
	public double getMaxstock() {
		return maxstock;
	}
	

	
	@Override
	public void setName(String name) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET NAME='" + name + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.name = name;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setDisplayName(String displayName) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET DISPLAY_NAME='" + displayName + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.displayName = displayName;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setAliases(ArrayList<String> newAliases) {
		HC hc = HC.hc;
		String stringAliases = CommonFunctions.implode(newAliases, ",");
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + stringAliases + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		aliases.clear();
		for (String cAlias:newAliases) {
			aliases.add(cAlias);
		}
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void addAlias(String addAlias) {
		HC hc = HC.hc;
		if (aliases.contains(addAlias)) {return;}
		aliases.add(addAlias);
		String stringAliases = CommonFunctions.implode(aliases, ",");
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + stringAliases + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void removeAlias(String removeAlias) {
		HC hc = HC.hc;
		if (!aliases.contains(removeAlias)) {return;}
		aliases.remove(removeAlias);
		String stringAliases = CommonFunctions.implode(aliases, ",");
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + stringAliases + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setEconomy(String economy) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET ECONOMY='" + economy + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.economy = economy;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setType(TradeObjectType type) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET TYPE='" + type.toString() + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.type = type;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setValue(double value) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET VALUE='" + value + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.value = value;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setIsstatic(String isstatic) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET STATIC='" + isstatic + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.isstatic = isstatic;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setStaticprice(double staticprice) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET STATICPRICE='" + staticprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.staticprice = staticprice;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setStock(double stock) {
		HC hc = HC.hc;
		if (stock < 0.0) {stock = 0.0;}
		String statement = "UPDATE hyperconomy_objects SET STOCK='" + stock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.stock = stock;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setMedian(double median) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET MEDIAN='" + median + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.median = median;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setInitiation(String initiation) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET INITIATION='" + initiation + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.initiation = initiation;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setStartprice(double startprice) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET STARTPRICE='" + startprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.startprice = startprice;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setCeiling(double ceiling) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET CEILING='" + ceiling + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.ceiling = ceiling;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setFloor(double floor) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET FLOOR='" + floor + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.floor = floor;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setMaxstock(double maxstock) {
		HC hc = HC.hc;
		String statement = "UPDATE hyperconomy_objects SET MAXSTOCK='" + maxstock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.maxstock = maxstock;
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	

	@Override
	public int getMaxInitial() {
		double medianStock = ((median * value) / startprice);
		int maxInitial = (int) (Math.ceil(medianStock) - stock);
		if (maxInitial < 0) {
			maxInitial = 0;
		}
		return maxInitial;
	}
	
	@Override
	public void checkInitiationStatus() {
		if (((getMedian() * getValue()) / getTotalStock()) <= getStartprice()) {
			setInitiation("false");
		}
	}
	
	@Override
	public String getStatusString() {
		String status = "dynamic";
		if (Boolean.parseBoolean(getInitiation())) {
			status = "initial";
		} else if (Boolean.parseBoolean(getIsstatic())) {
			status = "static";
		}
		return status;
	}
	

	@Override
	public double getPurchaseTax(double cost) {
		HC hc = HC.hc;
		double tax = 0.0;
		if (Boolean.parseBoolean(getIsstatic())) {
			tax = hc.getConf().getDouble("tax.static") / 100.0;
		} else {
			if (getType() == TradeObjectType.ENCHANTMENT) {
				tax = hc.getConf().getDouble("tax.enchant") / 100.0;
			} else {
				if (Boolean.parseBoolean(getInitiation())) {
					tax = hc.getConf().getDouble("tax.initial") / 100.0;
				} else {
					tax = hc.getConf().getDouble("tax.purchase") / 100.0;
				}
			}
		}
		return CommonFunctions.twoDecimals(cost * tax);
	}
	@Override
	public double getSalesTaxEstimate(double value) {
		HC hc = HC.hc;
		double salestax = 0;
		if (hc.getConf().getBoolean("tax.dynamic.enable")) {
			return 0.0;
		} else {
			double salestaxpercent = hc.getConf().getDouble("tax.sales");
			salestax = (salestaxpercent / 100) * value;
		}
		return CommonFunctions.twoDecimals(salestax);
	}
	
	@Override
	public double applyCeilingFloor(double price, double quantity) {
		double floor = getFloor() * quantity;
		double ceiling = getCeiling() * quantity;
		if (price > ceiling) {
			price = ceiling;
		} else if (price < floor) {
			price = floor;
		}
		return price;
	}
	
	@Override
	public double getSellPriceWithTax(double amount, HyperPlayer hp) {
		double price = getSellPrice(amount, hp);
		price -= hp.getSalesTax(price);
		return CommonFunctions.twoDecimals(price);
	}
	
	@Override
	public double getBuyPriceWithTax(double amount) {
		double price = getBuyPrice(amount);
		price += getPurchaseTax(price);
		return CommonFunctions.twoDecimals(price);
	}
	
	@Override
	public double getSellPrice(double amount) {
		HC hc = HC.hc;
		try {
			double totalPrice = 0;
			if (Boolean.parseBoolean(getIsstatic())) {
				totalPrice = getStaticprice() * amount;
			} else {
				if (getTotalStock() <= 0) {
					totalPrice = Math.pow(10, 21);
				} else {
					totalPrice = (Math.log(getTotalStock() + amount) - Math.log(getTotalStock())) * getMedian() * getValue();
				}
				if (Boolean.parseBoolean(getInitiation()) && totalPrice > (getStartprice() * amount)) {
					totalPrice = getStartprice() * amount;
				}
			}
			return applyCeilingFloor(totalPrice, amount);
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
			return Math.pow(10, 21);
		}
	}
	

	@Override
	public double getBuyPrice(double amount) {
		HC hc = HC.hc;
		try {
			double totalPrice = 0;
			if (Boolean.parseBoolean(getIsstatic())) {
				totalPrice = getStaticprice() * amount;
			} else {
				if (getTotalStock() - amount <= 0) {
					totalPrice = Math.pow(10, 21);
				} else {
					totalPrice = (Math.log(getTotalStock()) - Math.log(getTotalStock() - amount)) * getMedian() * getValue();
				}
				if (Boolean.parseBoolean(getInitiation()) && totalPrice > (getStartprice() * amount)) {
					totalPrice = getStartprice() * amount;
				}
			}
			return applyCeilingFloor(totalPrice, amount);
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
			return 0;
		}
	}
	
	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return getSellPrice(amount);
	}
	
	@Override
	public boolean nameStartsWith(String part) {
		part = part.toLowerCase();
		if (displayName.toLowerCase().startsWith(part)) {
			return true;
		}
		if (name.toLowerCase().startsWith(part)) {
			return true;
		}
		for (String alias:aliases) {
			if (alias.toLowerCase().startsWith(part)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean nameContains(String part) {
		part = part.toLowerCase();
		if (displayName != null && displayName.toLowerCase().contains(part)) {
			return true;
		}
		if (name.toLowerCase().contains(part)) {
			return true;
		}
		for (String alias:aliases) {
			if (alias.toLowerCase().contains(part)) {
				return true;
			}
		}
		return false;
	}
	

	
	@Override
	public boolean isShopObject() {return false;}
	@Override
	public boolean isCompositeObject() {return false;}

	
	//SUBCLASS METHODS
	
	@Override
    public Image getImage(int width, int height) {return null;}
	
	//GENERAL ADD AND REMOVE OBJECT METHODS
	@Override
	public void add(int amount, HyperPlayer hp) {}
	@Override
	public double remove(int amount, HyperPlayer hp) {return 0;}
	
	//GENERAL SERIALIZED DATA METHODS
	@Override
	public String getData() {return "";}
	@Override
	public void setData(String data) {}

	//ITEM METHODS
	@Override
	public void add(int amount, HInventory i) {}
	@Override
	public double remove(int amount, HInventory i) {return 0;}
	@Override
	public int count(HInventory inventory) {return 0;}
	@Override
	public int getAvailableSpace(HInventory inventory) {return 0;}
	@Override
	public HItemStack getItem() {return null;}
	@Override
	public HItemStack getItemStack(int amount) {return null;}
	@Override
	public void setItemStack(HItemStack stack) {}
	@Override
	public boolean matchesItemStack(HItemStack stack) {return false;}
	@Override
	public boolean isDurable() {return false;}
	@Override
	public double getDamageMultiplier(int amount, HInventory inventory) {return 1;}
	@Override
	public boolean isDamaged() {return false;}
	@Override
	public double getDurabilityPercent() {return 1;}
	
	
	//COMPOSITE ITEM METHODS
	@Override
	public ConcurrentHashMap<String, Double> getComponents() {return null;}
	@Override
	public void setComponents(String components) {}
	
	
	
	
	
	//ENCHANTMENT METHODS
	@Override
	public double getBuyPrice(EnchantmentClass enchantClass) {return 0;}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass) {return 0;}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp) {return 0;}
	@Override
	public double getSellPriceWithTax(EnchantmentClass enchantClass, HyperPlayer hp) {return 0;}
	@Override
	public HEnchantment getEnchantment() {return null;}
	@Override
	public int getEnchantmentLevel() {return 0;}
	@Override
	public double addEnchantment(HItemStack stack) {return 0;}
	@Override
	public double removeEnchantment(HItemStack stack) {return 0;}
	@Override
	public String getEnchantmentName() {return null;}
	@Override
	public boolean matchesEnchantment(HEnchantment enchant) {return false;}
	
	
	
	
	//SHOP OBJECT METHODS
	@Override
	public PlayerShop getShop() {return null;}
	@Override
	public TradeObject getTradeObject() {return null;}
	@Override
	public double getBuyPrice() {return 0;}
	@Override
	public double getSellPrice() {return 0;}
	@Override
	public int getMaxStock() {return 0;}
	@Override
	public TradeObjectStatus getStatus() {return null;}
	@Override
	public boolean useEconomyStock() {return true;}
	@Override
	public void setShop(PlayerShop playerShop) {}
	@Override
	public void setBuyPrice(double buyPrice) {}
	@Override
	public void setSellPrice(double sellPrice) {}
	@Override
	public void setMaxStock(int maxStock) {}
	@Override
	public void setStatus(TradeObjectStatus status) {}
	@Override
	public void setTradeObject(TradeObject ho) {}
	@Override
	public void setUseEconomyStock(boolean state) {}







}