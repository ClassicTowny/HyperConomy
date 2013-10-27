package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

//UNDER CONSTRUCTION

public interface ObjectAPI
{

	/**
	 * 
	 * @param id
	 *            The id of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param nameOfEconomy
	 *            The name of the economy that the object is a part of. Put
	 *            "default" if SQL is not used or to specify the default
	 *            economy.
	 * @return The purchase price of an object before tax and price modifiers.
	 */
	@Deprecated
	double getTheoreticalPurchasePrice(int id, int durability, int amount, String nameOfEconomy);

	/**
	 * 
	 * @param id
	 *            The id of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param nameOfEconomy
	 *            The name of the economy that the object is a part of. Put
	 *            "default" if SQL is not used or to specify the default
	 *            economy.
	 * @return The sale value of an object ignoring durability, tax, and all
	 *         price modifiers.
	 */
	@Deprecated
	double getTheoreticalSaleValue(int id, int durability, int amount, String nameOfEconomy);

	/**
	 * 
	 * @param id
	 *            The id of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param nameOfEconomy
	 *            The name of the economy that the object is a part of. Put
	 *            "default" if SQL is not used or to specify the default
	 *            economy.
	 * @return The purchase price of an object including taxes and all price
	 *         modifiers.
	 */
	@Deprecated
	double getTruePurchasePrice(int id, int durability, int amount, String nameOfEconomy);

	/**
	 * 
	 * @param id
	 *            The id of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param player
	 *            The player that is selling the object, item, or enchantment.
	 * @return The sale value of an object including all taxes, durability, and
	 *         price modifiers.
	 */
	@Deprecated
	double getTrueSaleValue(int id, int durability, int amount, Player player);

	
	
	
	double getTruePurchasePrice(HyperObject hyperObject, EnchantmentClass enchantClass, int amount);

	double getTrueSaleValue(HyperObject hyperObject, HyperPlayer hyperPlayer, EnchantmentClass enchantClass, int amount);
	
	double getTheoreticalSaleValue(HyperObject hyperObject, EnchantmentClass enchantClass, int amount);
	
	
	
	
	public String getName(String name, String economy);

	public String getEconomy(String name, String economy);

	public HyperObjectType getType(String name, String economy);

	public String getCategory(String name, String economy);

	public String getMaterial(String name, String economy);

	public int getId(String name, String economy);

	public int getData(String name, String economy);

	public int getDurability(String name, String economy);

	public double getValue(String name, String economy);

	public String getStatic(String name, String economy);

	public double getStaticPrice(String name, String economy);

	public double getStock(String name, String economy);

	public double getMedian(String name, String economy);

	public String getInitiation(String name, String economy);

	public double getStartPrice(String name, String economy);

	public void setName(String name, String economy, String newname);

	public void setEconomy(String name, String economy, String neweconomy);

	public void setType(String name, String economy, String newtype);

	public void setCategory(String name, String economy, String newcategory);

	public void setMaterial(String name, String economy, String newmaterial);

	public void setId(String name, String economy, int newid);

	public void setData(String name, String economy, int newdata);

	public void setDurability(String name, String economy, int newdurability);

	public void setValue(String name, String economy, double newvalue);

	public void setStatic(String name, String economy, String newstatic);

	public void setStaticPrice(String name, String economy, double newstaticprice);

	public void setStock(String name, String economy, double newstock);

	public void setMedian(String name, String economy, double newmedian);

	public void setInitiation(String name, String economy, String newinitiation);

	public void setStartPrice(String name, String economy, double newstartprice);
	
	
	
	/**
	 * @param ItemStack stack
	 * @param Player player
	 * @return They HyperObject representing the Minecraft item.  Returns null if there is no HyperObject for the item.
	 */
	public HyperObject getHyperObject(ItemStack stack, Player player);
	
	public HyperObject getHyperObject(ItemStack stack, String player);
	
	public HyperObject getHyperObject(String name, String economy);
	public HyperItem getHyperItem(String name, String economy);
	public HyperEnchant getHyperEnchant(String name, String economy);
	public BasicObject getBasicObject(String name, String economy);
	public HyperXP getHyperXP(String economy);
	
	public PlayerShopObject getPlayerShopObject(String name, PlayerShop s);
	public PlayerShopItem getPlayerShopItem(String name, PlayerShop s);
	public PlayerShopEnchant getPlayerShopEnchant(String name, PlayerShop s);
	public BasicShopObject getBasicShopObject(String name, PlayerShop s);
	public ShopXp getShopXp(String name, PlayerShop s);
	
	public HyperPlayer getHyperPlayer(String name);
	
	public ArrayList<HyperObject> getEnchantmentHyperObjects(ItemStack stack, String player);
	
	public TransactionResponse buy(Player p, HyperObject o, int amount);
	
	public TransactionResponse sellAll(Player p);
	
	public TransactionResponse sellAll(Player p, Inventory inventory);
	
	public ArrayList<HyperObject> getAvailableObjects(Player p);
	
	public ArrayList<HyperObject> getAvailableObjects(Player p, int startingPosition, int limit);
	
	

	//public List<Map<String, String>> getAllStockPlayer(Player pPlayer);
	
	//public List<Map<String, String>> getAllStockEconomy(String economy);
	
	public EnchantmentClass getEnchantmentClass(ItemStack stack);
}
