package regalowl.hyperconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import regalowl.databukkit.CommonFunctions;

public class InfoSign {
	private String signKey;
	private SignType type;
	private String objectName;
	private double multiplier;
	private String economy;
	private EnchantmentClass enchantClass;
	private HyperObject ho;
	private int x;
	private int y;
	private int z;
	private String world;
	private HyperConomy hc;
	private LanguageFile L;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	private boolean dataOk;
	
	private int timeValueHours;
	private int timeValue;
	private String increment;

	InfoSign(String signKey, SignType type, String objectName, double multiplier, String economy, EnchantmentClass enchantClass) {
		this.multiplier = multiplier;
		if (enchantClass == null) {
			this.enchantClass = EnchantmentClass.DIAMOND;
		} else {
			this.enchantClass = enchantClass;
		}
		dataOk = setData(signKey, type, objectName, economy);
		Sign s = getSign();
		if (s != null) {
			line1 = ChatColor.stripColor(s.getLine(0).trim());
			line2 = ChatColor.stripColor(s.getLine(1).trim());
			if (line1.length() > 13) {
				line2 = ChatColor.DARK_BLUE + line1.substring(13, line1.length()) + line2;
				line1 = ChatColor.DARK_BLUE + line1.substring(0, 13);
			} else {
				line1 = ChatColor.DARK_BLUE + line1;
				line2 = ChatColor.DARK_BLUE + line2;
			}
			line3 = s.getLine(2);
			line4 = s.getLine(3);
		}
	}

	InfoSign(String signKey, SignType type, String objectName, double multiplier, String economy, EnchantmentClass enchantClass, String[] lines) {
		this.multiplier = multiplier;
		if (enchantClass == null) {
			this.enchantClass = EnchantmentClass.DIAMOND;
		} else {
			this.enchantClass = enchantClass;
		}
		dataOk = setData(signKey, type, objectName, economy);
		line1 = ChatColor.stripColor(lines[0].trim());
		line2 = ChatColor.stripColor(lines[1].trim());
		if (line1.length() > 13) {
			line2 = ChatColor.DARK_BLUE + line1.substring(13, line1.length()) + line2;
			line1 = ChatColor.DARK_BLUE + line1.substring(0, 13);
		} else {
			line1 = ChatColor.DARK_BLUE + line1;
			line2 = ChatColor.DARK_BLUE + line2;
		}
		line3 = lines[2];
		line4 = lines[3];
	}

	public boolean setData(String signKey, SignType type, String objectName, String economy) {
		try {
			hc = HyperConomy.hc;
			HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
			L = hc.getLanguageFile();
			if (signKey == null || type == null || objectName == null || he == null) {
				hc.gDB().writeError("DEBUG: infosign initialization null: " + signKey + ", " + objectName + ", " + economy);
				return false;
			}
			this.economy = "default";
			this.signKey = signKey;
			this.world = signKey.substring(0, signKey.indexOf("|"));
			signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
			this.x = Integer.parseInt(signKey.substring(0, signKey.indexOf("|")));
			signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
			this.y = Integer.parseInt(signKey.substring(0, signKey.indexOf("|")));
			signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
			this.z = Integer.parseInt(signKey);
			this.type = type;
			this.objectName = he.fixName(objectName);
			if (economy != null) {
				this.economy = economy;
			}
			Location l = new Location(Bukkit.getWorld(world), x, y, z);
			Chunk c = l.getChunk();
			if (!c.isLoaded()) {
				c.load();
			}
			Block signblock = Bukkit.getWorld(world).getBlockAt(x, y, z);
			ho = he.getHyperObject(this.objectName);
			if (signblock.getType().equals(Material.SIGN_POST) || signblock.getType().equals(Material.WALL_SIGN)) {
				return true;
			}
			hc.gDB().writeError("DEBUG: infosign initialization failed: " + x + "," + y + "," + z + "," + world);
			return false;
		} catch (Exception e) {
			hc.gDB().writeError(e, "InfoSign setData() passed signKey='" + signKey + "', SignType='" + type.toString() + "', objectName='" + objectName + "', economy='" + economy + "'");
			return false;
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public String getWorld() {
		return world;
	}

	public String getKey() {
		return signKey;
	}

	public SignType getType() {
		return type;
	}

	public String getObjectName() {
		return objectName;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public String getEconomy() {
		return economy;
	}

	public EnchantmentClass getEnchantmentClass() {
		return enchantClass;
	}

	public boolean testData() {
		getSign();
		return dataOk;
	}

	public void update() {
		if (!dataOk) {
			return;
		}
		if (ho == null) {
			HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
			ho = he.getHyperObject(objectName);
			if (ho == null) {
				hc.gDB().writeError("InfoSign HyperObject null after retry: " + objectName + "," + economy);
				return;
			}
		}
		CommonFunctions cf = hc.getCommonFunctions();
		try {
			switch (type) {
				case BUY:
					if (ho instanceof HyperEnchant) {
						HyperEnchant he = (HyperEnchant)ho;
						double cost = he.getCost(enchantClass);
						cost = cf.twoDecimals((cost + ho.getPurchaseTax(cost)) * multiplier);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.fCS(cost);
					} else if (ho instanceof HyperItem) {
						HyperItem hi = (HyperItem)ho;
						double pcost = hi.getCost(1);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.fCS(cf.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier));
					} else if (ho instanceof BasicObject) {
						BasicObject bo = (BasicObject)ho;
						double pcost = bo.getCost(1);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.fCS(cf.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier));
					}
					break;
				case SELL:
					if (ho instanceof HyperEnchant) {
						HyperEnchant he = (HyperEnchant)ho;
						double value = he.getValue(enchantClass);
						value = cf.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.fCS(value);
					} else if (ho instanceof HyperItem) {
						HyperItem hi = (HyperItem)ho;
						double value = hi.getValue(1);
						value = cf.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.fCS(value);
					} else if (ho instanceof BasicObject) {
						BasicObject bo = (BasicObject)ho;
						double value = bo.getValue(1);
						value = cf.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.fCS(value);
					}
					break;
				case STOCK:
					line3 = ChatColor.WHITE + "Stock:";
					line4 = ChatColor.GREEN + "" + cf.twoDecimals(ho.getStock());
					break;
				case TOTALSTOCK:
					line3 = ChatColor.WHITE + "Total Stock:";
					line4 = ChatColor.GREEN + "" + cf.twoDecimals(ho.getTotalStock());
					break;
				case VALUE:
					line3 = ChatColor.WHITE + "Value:";
					line4 = ChatColor.GREEN + "" + ho.getValue() * multiplier;
					break;
				case STATUS:
					boolean staticstatus;
					staticstatus = Boolean.parseBoolean(ho.getIsstatic());
					line3 = ChatColor.WHITE + "Status:";
					if (staticstatus) {
						line4 = ChatColor.GREEN + "Static";
					} else {
						boolean initialstatus;
						initialstatus = Boolean.parseBoolean(ho.getInitiation());
						if (initialstatus) {
							line4 = ChatColor.GREEN + "Initial";
						} else {
							line4 = ChatColor.GREEN + "Dynamic";
						}
					}
					break;
				case STATICPRICE:
					line3 = ChatColor.WHITE + "Static Price:";
					line4 = ChatColor.GREEN + "" + ho.getStaticprice() * multiplier;
					break;
				case STARTPRICE:
					line3 = ChatColor.WHITE + "Start Price:";
					line4 = ChatColor.GREEN + "" + ho.getStartprice() * multiplier;
					break;
				case MEDIAN:
					line3 = ChatColor.WHITE + "Median:";
					line4 = ChatColor.GREEN + "" + ho.getMedian();
					break;
				case HISTORY:
					String increment = ChatColor.stripColor(line4.replace(" ", "")).toUpperCase().replaceAll("[0-9]", "");
					if (increment.contains("(")) {
						increment = increment.substring(0, increment.indexOf("("));
					}
					String timev = ChatColor.stripColor(line4.replace(" ", "")).toUpperCase().replaceAll("[A-Z]", "");
					int timevalue;
					int timevalueHours;
					if (timev.contains("(")) {
						timevalue = Integer.parseInt(timev.substring(0, timev.indexOf("(")));
					} else {
						timevalue = Integer.parseInt(timev);
					}
					timevalueHours = timevalue;
					if (increment.equalsIgnoreCase("h")) {
						timevalueHours *= 1;
					} else if (increment.equalsIgnoreCase("d")) {
						timevalueHours *= 24;
					} else if (increment.equalsIgnoreCase("w")) {
						timevalueHours *= 168;
					} else if (increment.equalsIgnoreCase("m")) {
						timevalueHours *= 672;
					}
					updateHistorySign(timevalueHours, timevalue, increment);
					break;
				case TAX:
					if (ho instanceof HyperEnchant) {
						HyperEnchant he = (HyperEnchant)ho;
						double price = he.getCost(enchantClass);
						double taxpaid = cf.twoDecimals(he.getPurchaseTax(price) * multiplier);
						line3 = ChatColor.WHITE + "Tax:";
						line4 = ChatColor.GREEN + "" + L.fCS(taxpaid);
					} else if (ho instanceof HyperItem) {
						HyperItem hi = (HyperItem)ho;
						line3 = ChatColor.WHITE + "Tax:";
						line4 = ChatColor.GREEN + L.fCS(cf.twoDecimals(hi.getPurchaseTax(hi.getCost(1) * multiplier)));
					} else if (ho instanceof BasicObject) {
						BasicObject bo = (BasicObject)ho;
						line3 = ChatColor.WHITE + "Tax:";
						line4 = ChatColor.GREEN + L.fCS(cf.twoDecimals(bo.getPurchaseTax(bo.getCost(1) * multiplier)));
					}
					break;
				case SB:
					if (ho instanceof HyperEnchant) {
						HyperEnchant he = (HyperEnchant)ho;
						double cost = he.getCost(enchantClass);
						cost = cf.twoDecimals((cost + ho.getPurchaseTax(cost)) * multiplier);
						line4 = ChatColor.WHITE + "B:" + "\u00A7a" + L.fCS(cost);
						double value = he.getValue(enchantClass);
						value = cf.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "S:" + ChatColor.GREEN + L.fCS(value);
					} else if (ho instanceof HyperItem) {
						HyperItem hi = (HyperItem)ho;
						double pcost = hi.getCost(1);
						line4 = ChatColor.WHITE + "B:" + ChatColor.GREEN + L.fCS(cf.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier));
						double value = hi.getValue(1);
						value = cf.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "S:" + ChatColor.GREEN + L.fCS(value);
					} else if (ho instanceof BasicObject) {
						BasicObject bo = (BasicObject)ho;
						double pcost = bo.getCost(1);
						line4 = ChatColor.WHITE + "B:" + ChatColor.GREEN + L.fCS(cf.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier));
						double value = bo.getValue(1);
						value = cf.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "S:" + ChatColor.GREEN + L.fCS(value);
					}
					break;
				default:
					break;
			}
			if (!type.equals(SignType.HISTORY)) {
				Sign s = getSign();
				if (s != null) {
					s.setLine(0, line1);
					s.setLine(1, line2);
					s.setLine(2, line3);
					s.setLine(3, line4);
					s.update();
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	private void updateHistorySign(int timevalueHours, int timevalue, String inc) {
		try {
			this.timeValueHours = timevalueHours;
			this.timeValue = timevalue;
			this.increment = inc;
			hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
				public void run() {
					String percentchange = hc.getHistory().getPercentChange(ho, timeValueHours);
					String colorcode = getcolorCode(percentchange);
					line3 = ChatColor.WHITE + "History:";
					line4 = ChatColor.WHITE + "" + timeValue + increment.toLowerCase() + colorcode + "(" + percentchange + ")";
					if (line3.length() > 14) {
						line3 = line3.substring(0, 13) + ")";
					}
					hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
						public void run() {
							Sign s = getSign();
							if (s != null) {
								s.setLine(0, line1);
								s.setLine(1, line2);
								s.setLine(2, line3);
								s.setLine(3, line4);
								s.update();
							}
						}
					}, 0L);
				}
			}, 0L);
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	

	private String getcolorCode(String percentchange) {
		String colorcode = "\u00A71";
		if (percentchange.equalsIgnoreCase("?")) {
			colorcode = "\u00A71";
		} else {
			Double percentc = Double.parseDouble(percentchange);
			if (percentc > 0) {
				colorcode = "\u00A7a";
			} else if (percentc < 0) {
				colorcode = "\u00A74";
			}
		}
		return colorcode;
	}
	
	
	public void deleteSign() {
		if (signKey != null && !signKey.equalsIgnoreCase("")) {
			hc.gYH().gFC("signs").set(signKey, null);
		}
	}
	
	
	public Sign getSign() {
		if (world == null) {
			return null;
		}
		Location l = new Location(Bukkit.getWorld(world), x, y, z);
		Chunk c = l.getChunk();
		if (!c.isLoaded()) {
			c.load();
		}
		Block signblock = l.getBlock();
		if (signblock.getType().equals(Material.SIGN_POST) || signblock.getType().equals(Material.WALL_SIGN)) {
			Sign s = (Sign) signblock.getState();
			return s;
		} else {
			dataOk = false;
			return null;
		}

	}
	
}
