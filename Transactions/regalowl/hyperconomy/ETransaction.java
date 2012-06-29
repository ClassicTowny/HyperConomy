package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * 
 * 
 * This class handles the purchase and sale of enchantments.
 * 
 */
public class ETransaction {
	private ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();
	
	ETransaction(HyperConomy hyc) {
		hc = hyc;
		
		enchantments.add(Enchantment.ARROW_DAMAGE);
		enchantments.add(Enchantment.ARROW_FIRE);
		enchantments.add(Enchantment.ARROW_INFINITE);
		enchantments.add(Enchantment.ARROW_KNOCKBACK);
		enchantments.add(Enchantment.DAMAGE_ALL);
		enchantments.add(Enchantment.DAMAGE_ARTHROPODS);
		enchantments.add(Enchantment.DAMAGE_UNDEAD);
		enchantments.add(Enchantment.DIG_SPEED);
		enchantments.add(Enchantment.DURABILITY);
		enchantments.add(Enchantment.FIRE_ASPECT);
		enchantments.add(Enchantment.KNOCKBACK);
		enchantments.add(Enchantment.LOOT_BONUS_BLOCKS);
		enchantments.add(Enchantment.LOOT_BONUS_MOBS);
		enchantments.add(Enchantment.OXYGEN);
		enchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
		enchantments.add(Enchantment.PROTECTION_EXPLOSIONS);
		enchantments.add(Enchantment.PROTECTION_FALL);
		enchantments.add(Enchantment.PROTECTION_FIRE);
		enchantments.add(Enchantment.PROTECTION_PROJECTILE);
		enchantments.add(Enchantment.SILK_TOUCH);
		enchantments.add(Enchantment.WATER_WORKER);
		
		sf = hc.getSQLFunctions();
	}
	
	
	/**
	 * 
	 * 
	 * This function handles the sale of enchantments.
	 * 
	 */
	public void sellEnchant() {
		
		//Handles sellEnchant errors.
		try {		
			
			//Gets the enchantment from the enchants.yml file and creates a new enchantment from the stored name.
			FileConfiguration enchants = hc.getYaml().getEnchants();
			String nenchant = "";
			if (hc.useSQL()) {
				playerecon = sf.getPlayerEconomy(p.getName());
				nenchant = sf.getMaterial(name, playerecon);
			} else {
				nenchant = enchants.getString(name + ".information.name");
			}
			
			Enchantment ench = Enchantment.getByName(nenchant);
			
			//Makes sure the item being held has the correct enchantment and enchantment level.
			int lvl = Integer.parseInt(name.substring(name.length() - 1, name.length()));
			int truelvl = p.getItemInHand().getEnchantmentLevel(ench);
			if (p.getItemInHand().containsEnchantment(ench) && lvl == truelvl) {
				
				
				//Gets the actual value of the enchantment and stores it in fprice, factoring in durability, and then
				//adds the value to the player's balance.
				double dura = p.getItemInHand().getDurability();
				double maxdura = p.getItemInHand().getType().getMaxDurability();
				double duramult = (1 - dura/maxdura);	
				String mater = p.getItemInHand().getType().toString();
				setVC(hc, name, mater, calc);
				double price = getValue();
				double fprice = duramult * price;
				
				
				boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
				if (acc.checkshopBalance(fprice) || sunlimited) {
					

					//Removes the sold enchantment from the item.
					p.getItemInHand().removeEnchantment(ench);
			
					//Adds the sold items to the shopstock and saves the yaml file.
					int shopstock = 0;
					if (hc.useSQL()) {
						shopstock = (int) sf.getStock(name, playerecon);
						sf.setStock(name, playerecon, shopstock + 1);
					} else {
						shopstock = enchants.getInt(name + ".stock.stock");
						enchants.set((name + ".stock.stock"), (shopstock + 1));	
					}
				
					double salestax = calc.getSalesTax(hc, p, fprice);
							
					acc.setAccount(hc, p, economy);
					acc.deposit(fprice - salestax);
					
					//Removes the final transaction price from the shop's account.
					acc.withdrawShop(fprice - salestax);
					
					//Reverts any changes to the global shop account if the account is set to unlimited.
					if (sunlimited) {
						String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
						acc.setBalance(globalaccount, 0);
					}
					
					//Formats the sale value to two digits for display.
					fprice = calc.twoDecimals(fprice);
					
					//Informs the player of their sale.
					p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You sold" + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + "" + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + fprice + ChatColor.BLUE + "" + ChatColor.ITALIC + " of which " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + calc.twoDecimals(salestax) + ChatColor.BLUE + ChatColor.ITALIC + " went to tax!");
					p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					
					//Writes the transaction to the log.
					
					if (hc.useSQL()) {
						String type = "dynamic";
						if (enchants.getBoolean(name + ".initiation.initiation")) {
							type = "initial";
						} else if (enchants.getBoolean(name + ".price.static")) {
							type = "static";
						}
						log.writeSQLLog(p.getName(), "sale", name, 1.0, fprice - salestax, salestax, playerecon, type);
					} else {
						String logentry = p.getName() + " sold " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + fprice + ". [Static Price=" + enchants.getBoolean(name + ".price.static") + "][Initial Price=" + enchants.getBoolean(name + ".initiation.initiation") + "]";
						log.setEntry(logentry);
						log.writeBuffer();
					}

					
					
					//Updates all information signs.
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
					
					//Sends price update notifications.
					not.setNotify(hc, calc, this, name, mater, playerecon);
					not.sendNotification();
	
				} else {
					p.sendMessage(ChatColor.BLUE + "Sorry, the shop currently does not have enough money.");
				}
				//If the item does not have the enchantment that the player is trying to sell, this informs them.
			} else {
				p.sendMessage(ChatColor.BLUE + "The item you're holding doesn't have " + ChatColor.AQUA + "" + name + "!");
			}		
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #17");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #17", "hyperconomy.error");
		}
	}
	

	
	/**
	 * 
	 * 
	 * This function handles the purchase of enchantments.
	 * 
	 */
	public void buyEnchant() {
		
		//Handles buyEnchant errors.
		try {
		
			//Gets the enchantment from the enchants.yml file and creates a new enchantment from the stored name.
			FileConfiguration enchants = hc.getYaml().getEnchants();
			String nenchant = enchants.getString(name + ".information.name");
			Enchantment ench = Enchantment.getByName(nenchant);		
					
			//Makes sure the shop has the given enchantment.
			int shopstock = 0;
			if (hc.useSQL()) {
				playerecon = sf.getPlayerEconomy(p.getName());
				shopstock = (int) sf.getStock(name, playerecon);
			} else {
				shopstock = enchants.getInt(name + ".stock.stock");
			}
			if (shopstock >= 1) {
				
				//Gets the material of the item the player is holding.
				String mater = p.getItemInHand().getType().toString();
				
				//Calculates the cost to buy the given enchantment for the relevant material class
				setVC(hc, name, mater, calc);
				double price = getCost();
				
				//Checks for infinite values.  (The cost returns as this number if such a value exists.)
				if (price != 123456789) {
						
					//Makes sure the item the player is holding doesn't have the enchantment they're trying to buy.
					if (!p.getItemInHand().containsEnchantment(ench)) {
						
						//Makes sure the player has enough money for the purchase.
						acc.setAccount(hc, p, economy);
						if (acc.checkFunds(price)) {
							
							//Makes sure the item can accept the chosen enchantment.  (Need to add new bukkit method for this when 1.2 RB comes out.)
							boolean enchtest = ench.canEnchantItem(p.getItemInHand());
							//add later
							
							setHE(p.getItemInHand());
							if (hasenchants()) {
								String allenchants = p.getItemInHand().getEnchantments().toString();
								allenchants = allenchants.substring(0, allenchants.length() - 1) + ", E";
									while (allenchants.length() > 1) {
									String enchantname = allenchants.substring(allenchants.indexOf(",") + 2, allenchants.indexOf("]"));
									allenchants = allenchants.substring(allenchants.indexOf("]") + 5, allenchants.length());
									Enchantment enchant = Enchantment.getByName(enchantname);
									if (ench.conflictsWith(enchant)) {
										enchtest = false;
									}
								}
							}
							
							//ench.conflictsWith(arg0)
							if (enchtest) {
		
								//Removes 1 of the enchantment from the shop and saves the yml.	
								if (hc.useSQL()) {
									sf.setStock(name, playerecon, shopstock - 1);
								} else {
									enchants.set((name + ".stock.stock"), (shopstock - 1));	
								}
								
								
								//Removes the cost from the player's account.
								acc.withdraw(price);
								
								//Deposits the money spent by the player into the server account.
								acc.depositShop(price);
								
								//Reverts any changes to the global shop account if the account is set to unlimited.
								if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
									String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
									acc.setBalance(globalaccount, 0);
								}
		
								//Gets the enchantment level from the enchantment's name.
								int l = name.length();
								String lev = name.substring(l - 1, l);
								int level = Integer.parseInt(lev);
									
								//Adds the enchantment to the item the player is holding.
								p.getItemInHand().addEnchantment(ench, level);
								
								//Gets whether or not the enchantment uses static pricing.
								boolean stax;
								if (hc.useSQL()) {
									stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
								} else {
									stax = enchants.getBoolean(name + ".price.static");
								}

								
								//Sets the taxrate to the correct rate, after determining what sort of tax should be used.
								double taxrate;
								if (!stax) {
									taxrate = hc.getYaml().getConfig().getDouble("config.enchanttaxpercent");
								} else {
									taxrate = hc.getYaml().getConfig().getDouble("config.statictaxpercent");
								}
								
								//Calculates the tax that was paid and formats it to two decimals.
								double taxpaid = price - (price/(1 + taxrate/100));
								taxpaid = calc.twoDecimals(taxpaid);
									
								//Formats the price to two decimals for display.	
								price = calc.twoDecimals(price);
								
								//Displays purchase information to the player.
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought" + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + "" + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " of which " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + taxpaid + " was tax!" );
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	
									
								//Logs the transaction.
								String logentry = "";
								/*
								if (hc.useSQL()) {
									logentry = p.getName() + " bought " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
								} else {
									
								}
								*/
								if (hc.useSQL()) {
									String type = "dynamic";
									if (enchants.getBoolean(name + ".initiation.initiation")) {
										type = "initial";
									} else if (enchants.getBoolean(name + ".price.static")) {
										type = "static";
									}
									log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, taxpaid, playerecon, type);
								} else {
									logentry = p.getName() + " bought " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ". [Static Price=" + enchants.getBoolean(name + ".price.static") + "][Initial Price=" + enchants.getBoolean(name + ".initiation.initiation") + "]";
									log.setEntry(logentry);
									log.writeBuffer();
								}
								
								
								//Updates all information signs.
								isign.setrequestsignUpdate(true);
								isign.checksignUpdate();
								
								
								//Sends price update notifications.
								not.setNotify(hc, calc, this, name, mater, playerecon);
								not.sendNotification();
		
							} else {
								p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
							}
						} else {
							p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
						}					
					} else {
						p.sendMessage(ChatColor.BLUE + "The item you're holding already has an enchantment of that type!");
					}		
				} else {
					p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
				}
			} else {
				p.sendMessage(ChatColor.BLUE + "The shop doesn't have enough " + name + "!");
			}			
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #18");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #18", "hyperconomy.error");
		}
	}


	
	/**
	 * 
	 * 
	 * This function handles the purchase of chestshop enchantments.
	 * 
	 */
	public boolean buyChestEnchant(ItemStack item, String owner) {
		
		//Handles buyEnchant errors.
		try {
		
			//Gets the enchantment from the enchants.yml file and creates a new enchantment from the stored name.
			FileConfiguration enchants = hc.getYaml().getEnchants();
			String nenchant = "";
			if (hc.useSQL()) {
				playerecon = sf.getPlayerEconomy(owner);
				nenchant = sf.getMaterial(name, playerecon);
			} else {
				nenchant = enchants.getString(name + ".information.name");
			}
			Enchantment ench = Enchantment.getByName(nenchant);		
					
				
				//Gets the material of the item the player is holding.
				String mater = p.getItemInHand().getType().toString();
				
				//Calculates the cost to buy the given enchantment for the relevant material class
				setVC(hc, name, mater, calc);
				double price = getValue();
				
				//Checks for infinite values.  (The cost returns as this number if such a value exists.)
				if (price != 123456789) {
						
					//Makes sure the item the player is holding doesn't have the enchantment they're trying to buy.
					if (!p.getItemInHand().containsEnchantment(ench)) {
						
						
						//Makes sure the item can accept the chosen enchantment.  (Need to add new bukkit method for this when 1.2 RB comes out.)
						boolean enchtest = ench.canEnchantItem(p.getItemInHand());
						//add later
						
						setHE(p.getItemInHand());
						if (hasenchants()) {
							String allenchants = p.getItemInHand().getEnchantments().toString();
							allenchants = allenchants.substring(0, allenchants.length() - 1) + ", E";
								while (allenchants.length() > 1) {
								String enchantname = allenchants.substring(allenchants.indexOf(",") + 2, allenchants.indexOf("]"));
								allenchants = allenchants.substring(allenchants.indexOf("]") + 5, allenchants.length());
								Enchantment enchant = Enchantment.getByName(enchantname);
								if (ench.conflictsWith(enchant)) {
									enchtest = false;
								}
							}
						}

						if (enchtest) {
						
						//Makes sure the player has enough money for the purchase.
						acc.setAccount(hc, p, economy);
						if (acc.checkFunds(price)) {
							


								
								//Removes the cost from the player's account.
								acc.withdraw(price);
								

								//Deposits the money spent by the player into the chest owner's account.
								acc.setAccount(hc, Bukkit.getPlayer(owner), economy);
								acc.depositAccount(owner, price);

		
								//Gets the enchantment level from the enchantment's name.
								int l = name.length();
								String lev = name.substring(l - 1, l);
								int level = Integer.parseInt(lev);
									
								//Adds the enchantment to the item the player is holding.
								p.getItemInHand().addEnchantment(ench, level);
								
								item.removeEnchantment(ench);
								
								
									
								//Formats the price to two decimals for display.	
								price = calc.twoDecimals(price);
								
								//Displays purchase information to the player.
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought" + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + "" + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " from " + owner);
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	

								//This writes a log entry for the transaction in the HyperConomy log.txt file.
								
								String logentry = "";
								/*
								if (hc.useSQL()) {
									logentry = p.getName() + " bought " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + " from " + owner + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
								} else {
										
								}
								*/
								if (hc.useSQL()) {
									String type = "dynamic";
									if (enchants.getBoolean(name + ".initiation.initiation")) {
										type = "initial";
									} else if (enchants.getBoolean(name + ".price.static")) {
										type = "static";
									}
									log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, 0.0, owner, type);
								} else {
									logentry = p.getName() + " bought " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + " from " + owner + ". [Static Price=" + enchants.getBoolean(name + ".price.static") + "][Initial Price=" + enchants.getBoolean(name + ".initiation.initiation") + "]";
									log.setEntry(logentry);
									log.writeBuffer();
								}
								
								Player o = Bukkit.getPlayer(owner);
								if (o != null) {
									o.sendMessage("�9" + p.getName() + " bought"  + " �b" + name + " �9from you for �a" + hc.getYaml().getConfig().getString("config.currency-symbol") + price + "�9.");
								}
								
								return true;

		
								
							} else {
								p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
							}		
						} else {
							p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
						}	
					} else {
						p.sendMessage(ChatColor.BLUE + "The item you're holding already has an enchantment of that type!");
					}		
				} else {
					p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
				}	
				return false;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #18");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #18", "hyperconomy.error");
			return false;
		}
	}
	

	

	/**
	 * 
	 * 
	 * This function calculates the value for the given enchantment.
	 * 
	 */
	public double getValue(){
		
		//Handles getValue errors.
		try {
			//Stores the calculated value.  (value variable is already used.)
			double cost = 0;
			
			//Gets the enchantment yml file.
			FileConfiguration enchants = hc.getYaml().getEnchants();
			
		    //Gets the classvalue for the given material type.
		    double classvalue = getclassValue(mater);
			
			//Checks if the price is set to static.
		    boolean stax;
		    if (hc.useSQL()) {
		    	if (p != null) {
		    		playerecon = sf.getPlayerEconomy(p.getName());
		    	}
		    	
		    	stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
		    } else {
		    	stax = enchants.getBoolean(name + ".price.static");
		    }
			if (!stax) {
				
				//Gets item data from the items yml file.
				double shopstock;
				double value;
				double median;
				double icost;
				if (hc.useSQL()) {
					shopstock = sf.getStock(name, playerecon);
					value = sf.getValue(name, playerecon);
					median = sf.getMedian(name, playerecon);
					icost = sf.getStartPrice(name, playerecon);
				} else {
					shopstock = enchants.getDouble(name + ".stock.stock");
					value = enchants.getDouble(name + ".value");
					median = enchants.getDouble(name + ".stock.median");
					icost = enchants.getDouble(name + ".initiation.startprice");
				}

		
				//Deactivates the initial pricing period if the value is equal to the normal value and the shop has more than 0 items.
				if (icost >= ((median * value)/shopstock) && shopstock > 0) {
					if (hc.useSQL()) {
						sf.setInitiation(name, playerecon, "false");
					} else {
						enchants.set(name + ".initiation.initiation", false);
					}
					
				}
		
				//Calculates the value for the given enchantment.
				double price = (median * value)/shopstock;
				shopstock = shopstock + 1;
				cost = cost + price;
				cost = cost * classvalue;
					
				//Determines whether initial prices will be used, if yes it recalculates the value.
				
				Boolean initial;
				if (hc.useSQL()) {
					initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
				} else {
					initial = enchants.getBoolean(name + ".initiation.initiation");
				}
				if (initial == true){	
						cost = icost * classvalue;		
				}
			
				//Checks if the price is infinite, and if it is sets the cost to a specific value that can later be identified.
				if (cost < Math.pow(10, 10)) {
					
					//Rounds to two decimal places.
					cost = calc.twoDecimals(cost);
				} else {
					cost = 3235624645000.7;
				}
			
			//Handles the value calculation if the enchantment is using a static price.
			} else {
				double statprice;
				if (hc.useSQL()) {
					statprice = sf.getStaticPrice(name, playerecon);
				} else {
					statprice = enchants.getDouble(name + ".price.staticprice");
				}
				cost = statprice * classvalue;
			}	
			return cost;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #19");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #19", "hyperconomy.error");
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
	public double getCost() {
		
		//Handles getCost errors.
		try {

			//Stores the cost of the given enchantment.
			double cost = 0;
			
			//Gets the enchantment yml file.
			FileConfiguration enchants = hc.getYaml().getEnchants();
			
			//Gets the classvalue and makes sure that the class is enchantable.  Returns 123456789 if not.
	        double classvalue = getclassValue(mater);
			if (classvalue != 123456789) {
					
				//Checks if the enchantment uses static pricing.	
			    boolean stax;
			    if (hc.useSQL()) {
			    	if (p != null) {
			    		playerecon = sf.getPlayerEconomy(p.getName());
			    	}
			    	stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
			    } else {
			    	stax = enchants.getBoolean(name + ".price.static");
			    }
				if (!stax) {
	
					double shopstock;
					double value;
					double median;
					if (hc.useSQL()) {
						shopstock = sf.getStock(name, playerecon);
						value = sf.getValue(name, playerecon);
						median = sf.getMedian(name, playerecon);
					} else {
						shopstock = enchants.getDouble(name + ".stock.stock");
						value = enchants.getDouble(name + ".value");
						median = enchants.getDouble(name + ".stock.median");
					}
					//Gets the shopstock.
					
					
					//Stores the original shopstock before modification.
					double oshopstock = shopstock;
					
					//Calculates the cost.

					shopstock = shopstock - 1;
					double price = ((median * value)/shopstock);			
					cost = price * classvalue;
							
					//Checks whether or not the enchantment is in the initial pricing period.
					boolean initial;
					if (hc.useSQL()) {
						initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
					} else {
						initial = enchants.getBoolean(name + ".initiation.initiation");
					}
					
					//Gets the enchantment tax percent from the config file.
					double etax = (hc.getYaml().getConfig().getDouble("config.enchanttaxpercent"))/100;
					
					//If the enchantment is in the initial pricing period, the cost is recalculated.
					if (initial == true) {
						double icost;
						if (hc.useSQL()) {
							icost = sf.getStaticPrice(name, playerecon);
						} else {
							icost = enchants.getDouble(name + ".initiation.startprice");
						}
	
						//Checks to see if initiation should be disabled, if not, recalculates the cost and applies tax.
						if (price < icost && oshopstock > 0){
							if (hc.useSQL()) {
								sf.setInitiation(name, playerecon, "false");
							} else {
								enchants.set(name + ".initiation.initiation", false);
							}
							
						} else {
							cost = ((icost * etax) + icost) * classvalue;
						} 
					}
					
					//If the cost is extremely high it sets it to an arbitrary value for later detection.
					if (cost < Math.pow(10, 10)) {
						
						//Applies tax to the cost if initiation is false.
						if (initial == false) {
							cost = cost * etax + cost;
						}
						
						//Formats the cost.
						cost = calc.twoDecimals(cost);
					} else {
						cost = 3235624645000.7;
					}
				
				//Handles enchantments using static pricing.
				} else {
					
					//Gets the static tax from the config.
					double statictax = (hc.getYaml().getConfig().getDouble("config.statictaxpercent"))/100;
					
					//Gets the static price from the enchantments file.
					double staticcost;
					if (hc.useSQL()) {
						staticcost = sf.getStartPrice(name, playerecon);
					} else {
						staticcost = enchants.getDouble(name + ".price.staticprice");
					}
					
					//Calculates the static cost.
					cost = (((staticcost * statictax) + staticcost) * classvalue);
				}		
			} else {
				cost = 123456789;
			}	
			return cost;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #20");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #20", "hyperconomy.error");
			double cost = 99999999;
			return cost;
		}
	}
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function returns the durability multiplier for an item.
	 * 
	 */
	public double getDuramult(){
		try {
			double dura = p.getItemInHand().getDurability();
			double maxdura = p.getItemInHand().getType().getMaxDurability();
			double duramult = (1 - dura/maxdura);
			return duramult;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #21");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #21", "hyperconomy.error");
			double duramult = 0;
			return duramult;
		}
	}



	/**
	 * 
	 * 
	 * This function checks if an item is enchanted.
	 * 
	 */
	public boolean hasenchants() {
		
		try {
			boolean hasenchants = false;
			
			//If the ItemStack's enchantment list is not empty, the function returns true.  It makes sure that the ItemStack is not null.
			if (stack != null) {
				Map<Enchantment, Integer> enchants = stack.getEnchantments();
				hasenchants = !enchants.isEmpty();
			}
			return hasenchants;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #22");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #22", "hyperconomy.error");
			boolean hasenchants = false;
			return hasenchants;
		}
	}



	/**
	 * 
	 * 
	 * This function returns the class value (diamond, stone, etc.) of the given material.
	 * 
	 */
	private double getclassValue(String matname) {
		try {
			double value;
			if (matname.toLowerCase().indexOf("leather") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.leather"));
			} else if (matname.toLowerCase().indexOf("wood") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.wood"));
			} else if (matname.toLowerCase().indexOf("stone") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.stone"));
			} else if (matname.toLowerCase().indexOf("chainmail") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.chainmail"));
			} else if (matname.toLowerCase().indexOf("iron") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.iron"));
			} else if (matname.toLowerCase().indexOf("gold") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.gold"));
			} else if (matname.toLowerCase().indexOf("diamond") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.diamond"));
			} else if (matname.toLowerCase().indexOf("bow") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.bow"));
			} else {
				value = 123456789;
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #23");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #23", "hyperconomy.error");
			double value = 987654321;
			return value;
		}
	}
	
	
	public boolean isEnchantable(ItemStack item) {
		boolean enchantable = false;
		int count = 0;
		
		while (count < enchantments.size()) {
			Enchantment enchant = enchantments.get(count);
			if (enchant.canEnchantItem(item)) {
				enchantable = true;
			}
			count++;
		}
		return enchantable;
	}

		
		public void setSBE(HyperConomy hyperc, Player player, String nam, Economy econ, Log lo, Account account, InfoSign infosign, Notify no, Calculation cal) {
			hc = hyperc;
			p = player;
			name = nam;
			economy = econ;
			log = lo;
			acc = account;
			isign = infosign;
			not = no;
			calc = cal;
		}
		
		public void setVC(HyperConomy hyperc, String nam, String mat, Calculation c){
			hc = hyperc;
			name = nam;
			mater = mat;
			calc = c;
		}
		
		public void setDM(Player player) {
			p = player;
		}
		
		public void setHE(ItemStack st) {
			stack = st;
		}
		
		public void setPlayerEconomy(String econ) {
			playerecon = econ;
		}
	
		//Enchant fields.
		private HyperConomy hc;
		private Player p;
		private String name;
		private Economy economy;
		private String mater;
		private ItemStack stack;
		private Log log;
		private Account acc;
		private InfoSign isign;
		private Notify not;
		private Calculation calc;
		private SQLFunctions sf;
		
		private String playerecon;

}
