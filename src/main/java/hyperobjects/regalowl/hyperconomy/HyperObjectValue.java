package regalowl.hyperconomy;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HyperObjectValue {

	private HyperConomy hc;
	private HyperObject ho;
	private InventoryManipulation im;
	
	HyperObjectValue(HyperObject ho) {
		hc = HyperConomy.hc;
		this.ho = ho;
		im = hc.getInventoryManipulation();
	}
	/**
	 * 
	 * 
	 * This function determines the sale value for one or more of the item.
	 * 
	 */
	public double getValue(int amount, HyperPlayer hp) {
		try {
			double totalvalue = 0;
			double damage = 0;
			boolean isstatic = false;
			isstatic = Boolean.parseBoolean(ho.getIsstatic());
			if (!isstatic) {
				damage = getDamageMultiplier(amount, hp.getPlayer().getInventory());
				double shopstock = 0;
				double value = 0;
				double median = 0;
				double icost = 0;
				shopstock = ho.getTotalStock();
				value = ho.getValue();
				median = ho.getMedian();
				icost = ho.getStartprice();
				if (icost >= ((median * value) / shopstock) && shopstock > 1) {
					ho.setInitiation("false");
				}
				int counter = 0;
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(price);
					shopstock = shopstock + 1;
					totalvalue = totalvalue + price;
					counter++;
				}
				totalvalue = totalvalue * damage;
				Boolean initial = false;
				initial = Boolean.parseBoolean(ho.getInitiation());
				if (initial == true) {
					double ivalue = applyCeilingFloor(icost);
					totalvalue = ivalue * damage * amount;
				}
				if (totalvalue >= Math.pow(10, 10)) {
					totalvalue = 3235624645000.7;
				}
			} else {
				damage = getDamageMultiplier(amount, hp.getPlayer().getInventory());
				double statprice = ho.getStaticprice();
				double svalue = applyCeilingFloor(statprice);
				totalvalue = svalue * amount * damage;
			}
			return twoDecimals(totalvalue);
		} catch (Exception e) {
			String info = "Calculation countItems() passed values name='" + ho.getName() + "', amount='" + amount + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
			double totalvalue = 0;
			return totalvalue;
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function determines the purchase price for one or more of the item.
	 * 
	 */
	public double getCost(int amount) {
		try {
			double cost = 0;
			boolean isstatic = Boolean.parseBoolean(ho.getIsstatic());
			if (isstatic == false) {
				double shopstock = 0;
				double oshopstock = 0;
				double value = 0;
				double median = 0;
				shopstock = ho.getTotalStock();
				oshopstock = shopstock;
				value = ho.getValue();
				median = ho.getMedian();
				shopstock = shopstock - 1;
				int counter = 0;
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(price);
					shopstock = shopstock - 1;
					cost = cost + price;
					counter++;
				}
				boolean initial = Boolean.parseBoolean(ho.getInitiation());
				if (initial == true) {
					double icost = 0;
					icost = ho.getStartprice();
					if (cost < (icost * amount) && oshopstock > 1) {
						ho.setInitiation("false");
					} else {
						double price = applyCeilingFloor(icost);
						cost = price * amount;
					}
				}
				if (cost >= Math.pow(10, 10)) {
					cost = 3235624645000.7;
				}
			} else {
				double staticcost = ho.getStaticprice();
				double price = applyCeilingFloor(staticcost);
				cost = price * amount;
			}
			return twoDecimals(cost);
		} catch (Exception e) {
			String info = "Calculation getCost() passed values name='" + ho.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			double cost = 99999999;
			return cost;
		}
	}
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function calculates the theoretical value for items, ignoring
	 * durability.
	 * 
	 */
	public double getTheoreticalValue(int amount) {
		try {
			double cost = 0;
			int counter = 0;
			Boolean initial = false;
			initial = Boolean.parseBoolean(ho.getInitiation());
			boolean isstatic = false;
			isstatic = Boolean.parseBoolean(ho.getIsstatic());
			if (!isstatic) {
				double shopstock = 0;
				double value = 0;
				double median = 0;
				double icost = 0;
				shopstock = ho.getTotalStock();
				value = ho.getValue();
				median = ho.getMedian();
				icost = ho.getStartprice();
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(price);
					shopstock = shopstock + 1;
					cost = cost + price;
					counter++;
				}
				if (initial == true) {
					double price = applyCeilingFloor(icost);
					cost = price * amount;
				}
				if (cost >= Math.pow(10, 10)) {
					cost = 3235624645000.7;
				}
			} else {
				double statprice = ho.getStaticprice();
				double price = applyCeilingFloor(statprice);
				cost = price * amount;
			}
			return twoDecimals(cost);
		} catch (Exception e) {
			String info = "Calculation getTvalue() passed values name='" + ho.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			double cost = 99999999;
			return cost;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function calculates the value for the given enchantment.
	 * 
	 */
	public double getEnchantValue(EnchantmentClass eclass, HyperPlayer hp) {
		try {
			Calculation calc = hc.getCalculation();
			double cost = 0;
			double classvalue = im.getclassValue(eclass);
			boolean stax;
			stax = Boolean.parseBoolean(ho.getIsstatic());
			double duramult = im.getDurabilityMultiplier(hp.getPlayer().getItemInHand());
			if (hp.getPlayer().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
				duramult = 1;
			}
			if (!stax) {
				double shopstock;
				double value;
				double median;
				double icost;
				shopstock = ho.getTotalStock();
				value = ho.getValue();
				median = ho.getMedian();
				icost = ho.getStartprice();
				if (icost >= ((median * value) / shopstock) && shopstock > 1) {
					ho.setInitiation("false");
				}
				double price = (median * value) / shopstock;
				cost = cost + price;
				cost = cost * classvalue;
				
				cost = applyCeilingFloor(cost);
				Boolean initial;
				initial = Boolean.parseBoolean(ho.getInitiation());
				if (initial == true) {
					cost = icost * classvalue * duramult;
					cost = applyCeilingFloor(cost);
				}
				if (cost < Math.pow(10, 10)) {
					cost = calc.twoDecimals(cost);
				} else {
					cost = 3235624645000.7;
				}
			} else {
				double statprice;
				statprice = ho.getStaticprice();
				cost = statprice * classvalue * duramult;
				cost = applyCeilingFloor(cost);
			}
			return twoDecimals(cost);
		} catch (Exception e) {
			String info = "Calculation getEnchantValue() passed values name='" + ho.getName() + "', material='" + eclass.toString() + "'";
			hc.gDB().writeError(e, info);
			double value = 0;
			return value;
		}
	}
	
	
	
	
	
	
	
	

	/**
	 * 
	 * 
	 * This function calculates the value for the given enchantment.
	 * 
	 */
	public double getTheoreticalEnchantValue(EnchantmentClass eclass) {
		try {
			Calculation calc = hc.getCalculation();
			double cost = 0;
			double classvalue = im.getclassValue(eclass);
			boolean stax;
			stax = Boolean.parseBoolean(ho.getIsstatic());
			if (!stax) {
				double shopstock;
				double value;
				double median;
				double icost;
				shopstock = ho.getTotalStock();
				value = ho.getValue();
				median = ho.getMedian();
				icost = ho.getStartprice();
				if (icost >= ((median * value) / shopstock) && shopstock > 1) {
					ho.setInitiation("false");
				}
				double price = (median * value) / shopstock;
				cost = cost + price;
				cost = cost * classvalue;
				cost = applyCeilingFloor(cost);
				Boolean initial;
				initial = Boolean.parseBoolean(ho.getInitiation());
				if (initial == true) {
					cost = icost * classvalue;
					cost = applyCeilingFloor(cost);
				}
				if (cost < Math.pow(10, 10)) {
					cost = calc.twoDecimals(cost);
				} else {
					cost = 3235624645000.7;
				}
			} else {
				double statprice;
				statprice = ho.getStaticprice();
				cost = statprice * classvalue;
				cost = applyCeilingFloor(cost);
			}
			return twoDecimals(cost);
		} catch (Exception e) {
			String info = "Calculation getEnchantValue() passed values name='" + ho.getName() + "', material='" + eclass.toString() + "'";
			hc.gDB().writeError(e, info);
			double value = 0;
			return value;
		}
	}

	/**
	 * 
	 * 
	 * This function calculates the cost for the given enchantment.
	 * 
	 */
	public double getEnchantCost(EnchantmentClass eclass) {
		try {
			Calculation calc = hc.getCalculation();
			double cost = 0;
			double classvalue = im.getclassValue(eclass);
			if (classvalue != 123456789) {
				boolean stax;
				stax = Boolean.parseBoolean(ho.getIsstatic());
				if (!stax) {
					double shopstock;
					double value;
					double median;
					shopstock = ho.getTotalStock();
					value = ho.getValue();
					median = ho.getMedian();
					double oshopstock = shopstock;
					shopstock = shopstock - 1;
					double price = ((median * value) / shopstock);
					cost = price * classvalue;
					cost = applyCeilingFloor(cost);
					boolean initial;
					initial = Boolean.parseBoolean(ho.getInitiation());
					if (initial == true) {
						double icost;
						icost = ho.getStartprice();
						if (price < icost && oshopstock > 1) {
							ho.setInitiation("false");
						} else {
							cost = icost * classvalue;
							cost = applyCeilingFloor(cost);
						}
					}
					if (cost < Math.pow(10, 10)) {
						cost = calc.twoDecimals(cost);
					} else {
						cost = 3235624645000.7;
					}
				} else {
					double staticcost;
					staticcost = ho.getStaticprice();
					cost = staticcost * classvalue;
					cost = applyCeilingFloor(cost);
				}
			} else {
				cost = 123456789;
			}
			return twoDecimals(cost);
		} catch (Exception e) {
			String info = "Calculation getEnchantCost() passed values name='" + ho.getName() + "', material='" + eclass.toString() + "'";
			hc.gDB().writeError(e, info);
			double cost = 99999999;
			return cost;
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function calculates the total damage multiplier for a cost/value
	 * calculation.
	 * 
	 */
	private double getDamageMultiplier(int amount, Inventory inventory) {
		int id = ho.getId();
		try {
			double damage = 0;
			if (isDurable()) {
				int heldslot = -1;
				int totalitems = 0;
				HashMap<Integer, ? extends ItemStack> stacks = inventory.all(id);
				if (inventory.getType() == InventoryType.PLAYER) {
					Player p = (Player) inventory.getHolder();
					heldslot = p.getInventory().getHeldItemSlot();
					if (p.getItemInHand().getTypeId() == id && !im.hasenchants(stacks.get(heldslot))) {
						damage = getdurabilityPercent(stacks.get(heldslot));
						totalitems++;
					}
				}
				for (int slot = 0; slot < inventory.getSize(); slot++) {
					if (slot == heldslot) {
						continue;
					}
					if (stacks.get(slot) != null && totalitems < amount && !im.hasenchants(stacks.get(slot))) {
						damage = getdurabilityPercent(stacks.get(slot)) + damage;
						totalitems++;
					}
				}
				damage = damage / amount;
			} else {
				damage = 1;
			}
			return damage;
		} catch (Exception e) {
			String info = "Calculation getDamage() passed values id='" + id + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			double damage = 0;
			return damage;
		}
	}
	
	public double applyCeilingFloor(double price) {
		double floor = ho.getFloor();
		double ceiling = ho.getCeiling();
		if (price > ceiling) {
			price = ceiling;
		} else if (price < floor) {
			price = floor;
		}
		return price;
	}
	
	/**
	 * 
	 * 
	 * This function returns the percentage of remaining durability in an
	 * itemstack. (0 = 0% durability, 1 = 100%) It returns as 1 if the item
	 * cannot be damaged.
	 * 
	 */
	public double getdurabilityPercent(ItemStack i) {
		try {
			double durabilitypercent = 1;
			try {
				double cdurability = i.getDurability();
				double maxdurability = i.getData().getItemType().getMaxDurability();
				durabilitypercent = (1 - (cdurability / maxdurability));
			} catch (Exception e) {
				durabilitypercent = 1;
			}
			if (durabilitypercent < 0) {
				durabilitypercent = 1;
			}
			return durabilitypercent;
		} catch (Exception e) {
			String info = "Calculation getdurabilityPercent() passed values ItemStack='" + i + "'";
			hc.gDB().writeError(e, info);
			double durabilitypercent = 1;
			return durabilitypercent;
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function compares the given id with its list to determine if the
	 * item can be damaged. If it can be damaged it returns true, false if not.
	 * 
	 */
	public boolean isDurable() {
		Material m = Material.getMaterial(ho.getId());
		if (m != null && m.getMaxDurability() > 0) {
			return true;
		}
		return false;
	}
	
	public double twoDecimals(double input) {
		int nodecimals = (int) Math.ceil((input * 100) - .5);
		double twodecimals = (double) nodecimals / 100.0;
		return twodecimals;
	}
	
	
	
	
	public double getPurchaseTax(double cost) {
		double tax = 0.0;
		if (Boolean.parseBoolean(ho.getIsstatic())) {
			tax = hc.gYH().gFC("config").getDouble("config.statictaxpercent") / 100.0;
		} else {
			if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				tax = hc.gYH().gFC("config").getDouble("config.enchanttaxpercent") / 100.0;
			} else {
				if (Boolean.parseBoolean(ho.getInitiation())) {
					tax = hc.gYH().gFC("config").getDouble("config.initialpurchasetaxpercent") / 100.0;
				} else {
					tax = hc.gYH().gFC("config").getDouble("config.purchasetaxpercent") / 100.0;
				}
			}
		}
		return twoDecimals(cost * tax);
	}
	
	
	public double getSalesTaxEstimate(Double price) {
		double salestax = 0;
		if (hc.gYH().gFC("config").getBoolean("config.dynamic-tax.use-dynamic-tax")) {
			return 0.0;
		} else {
			double salestaxpercent = hc.gYH().gFC("config").getDouble("config.sales-tax-percent");
			salestax = (salestaxpercent / 100) * price;
		}
		return twoDecimals(salestax);
	}


	
}
