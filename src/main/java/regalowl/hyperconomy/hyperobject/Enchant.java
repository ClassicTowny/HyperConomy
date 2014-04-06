package regalowl.hyperconomy.hyperobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.serializable.SerializableEnchantment;

public class Enchant extends BasicObject implements HyperObject {

	private SerializableEnchantment se;

	
	public Enchant(String name, String economy, String displayName, String aliases, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String base64ItemData) {
		super(name, economy, displayName, aliases, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
		this.se = new SerializableEnchantment(base64ItemData);
	}
	@Override
	public String getEnchantmentName() {
		return se.getEnchantmentName();
	}


	
	@Override
	public String getData() {
		return se.serialize();
	}
	@Override
	public void setData(String data) {
		se = new SerializableEnchantment(data);
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
	}
	
	
	@Override
	public Image getImage(int width, int height) {
		Image i = null;
		URL url = hc.getClass().getClassLoader().getResource("Images/enchanted_book_0.png");
		try {
			i = ImageIO.read(url);
			if (i != null) {
				return i.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			}
		} catch (Exception e) {}
		return null;
	}

	
	@Override
	public double getBuyPrice(EnchantmentClass eclass) {
		try {
			CommonFunctions cf = hc.gCF();
			double cost = 0;
			double classvalue = EnchantmentClass.getclassValue(eclass);
			boolean stax;
			stax = Boolean.parseBoolean(getIsstatic());
			if (!stax) {
				double shopstock;
				double value;
				double median;
				shopstock = getTotalStock();
				value = getValue();
				median = getMedian();
				double oshopstock = shopstock;
				shopstock = shopstock - 1;
				double price = ((median * value) / shopstock);
				cost = price * classvalue;
				cost = applyCeilingFloor(cost);
				boolean initial;
				initial = Boolean.parseBoolean(getInitiation());
				if (initial == true) {
					double icost;
					icost = getStartprice();
					if (price < icost && oshopstock > 1) {
						setInitiation("false");
					} else {
						cost = icost * classvalue;
						cost = applyCeilingFloor(cost);
					}
				}
				cost = cf.twoDecimals(cost);
			} else {
				double staticcost;
				staticcost = getStaticprice();
				cost = staticcost * classvalue;
				cost = applyCeilingFloor(cost);
			}
			return cf.twoDecimals(cost);
		} catch (Exception e) {
			String info = "getBuyPrice() passed values name='" + getName() + "', material='" + eclass.toString() + "'";
			hc.gDB().writeError(e, info);
			double cost = 99999999;
			return cost;
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass eclass) {
		try {
			CommonFunctions cf = hc.gCF();
			double cost = 0;
			double classvalue = EnchantmentClass.getclassValue(eclass);
			boolean stax;
			stax = Boolean.parseBoolean(getIsstatic());
			if (!stax) {
				double shopstock;
				double value;
				double median;
				double icost;
				shopstock = getTotalStock();
				value = getValue();
				median = getMedian();
				icost = getStartprice();
				if (icost >= ((median * value) / shopstock) && shopstock > 1) {
					setInitiation("false");
				}
				double price = (median * value) / shopstock;
				cost = cost + price;
				cost = cost * classvalue;
				cost = applyCeilingFloor(cost);
				Boolean initial;
				initial = Boolean.parseBoolean(getInitiation());
				if (initial == true) {
					cost = icost * classvalue;
					cost = applyCeilingFloor(cost);
				}
				cost = cf.twoDecimals(cost);
			} else {
				double statprice;
				statprice = getStaticprice();
				cost = statprice * classvalue;
				cost = applyCeilingFloor(cost);
			}
			return cf.twoDecimals(cost);
		} catch (Exception e) {
			String info = "getSellPrice() passed values name='" + getName() + "', material='" + eclass.toString() + "'";
			hc.gDB().writeError(e, info);
			double value = 0;
			return value;
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass eclass, HyperPlayer hp) {
		try {
			CommonFunctions cf = hc.gCF();
			double cost = 0;
			double classvalue = EnchantmentClass.getclassValue(eclass);
			boolean stax;
			stax = Boolean.parseBoolean(getIsstatic());
			HyperItemStack his = new HyperItemStack(hp.getPlayer().getItemInHand());
			double duramult = his.getDurabilityMultiplier();
			if (hp.getPlayer().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
				duramult = 1;
			}
			if (!stax) {
				double shopstock;
				double value;
				double median;
				double icost;
				shopstock = getTotalStock();
				value = getValue();
				median = getMedian();
				icost = getStartprice();
				if (icost >= ((median * value) / shopstock) && shopstock > 1) {
					setInitiation("false");
				}
				double price = (median * value) / shopstock;
				cost = cost + price;
				cost = cost * classvalue;
				
				cost = applyCeilingFloor(cost);
				Boolean initial;
				initial = Boolean.parseBoolean(getInitiation());
				if (initial == true) {
					cost = icost * classvalue * duramult;
					cost = applyCeilingFloor(cost);
				}
				cost = cf.twoDecimals(cost);
			} else {
				double statprice;
				statprice = getStaticprice();
				cost = statprice * classvalue * duramult;
				cost = applyCeilingFloor(cost);
			}
			return cf.twoDecimals(cost);
		} catch (Exception e) {
			String info = "Calculation getEnchantValue() passed values name='" + getName() + "', material='" + eclass.toString() + "'";
			hc.gDB().writeError(e, info);
			double value = 0;
			return value;
		}
	}
	
	@Override
	public double getBuyPrice(int amount) {
		return getBuyPrice(EnchantmentClass.DIAMOND) * amount;
	}
	@Override
	public double getSellPrice(int amount) {
		return getSellPrice(EnchantmentClass.DIAMOND) * amount;
	}
	@Override
	public double getSellPrice(int amount, HyperPlayer hp) {
		return getSellPrice(EnchantmentClass.DIAMOND, hp) * amount;
	}
	@Override
	public Enchantment getEnchantment() {
		return se.getEnchantment();
	}
	@Override
	public int getEnchantmentLevel() {
		return se.getLvl();
	}
	@Override
	public double addEnchantment(ItemStack stack) {
		if (stack == null) {return 0;}
		HyperItemStack his = new HyperItemStack(stack);
		Enchantment e = getEnchantment();
		if (his.canAcceptEnchantment(e) && !his.containsEnchantment(e)) {
			his.addEnchantment(e, getEnchantmentLevel());
			return 1;
		}
		return 0;
	}
	@Override
	public double removeEnchantment(ItemStack stack) {
		if (stack == null) {return 0;}
		HyperItemStack his = new HyperItemStack(stack);
		Enchantment e = getEnchantment();
		int lvl = his.getEnchantmentLevel(e);
		if (getEnchantmentLevel() == lvl && his.containsEnchantment(e)) {
			his.removeEnchant(e);
			double duramult = his.getDurabilityMultiplier();
			return duramult;
		}
		return 0;
	}


	

}