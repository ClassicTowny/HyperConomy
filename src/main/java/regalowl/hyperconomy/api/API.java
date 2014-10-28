package regalowl.hyperconomy.api;

import java.util.ArrayList;




import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.ServerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.TransactionResponse;



public interface API {


	public String getDefaultServerShopAccountName();
	
	
	public Shop getShop(String name);
	public ServerShop getServerShop(String name);
	public PlayerShop getPlayerShop(String name);
	public String getDefaultServerShopAccount();
	
	
	public ArrayList<String> getServerShopList();
	public ArrayList<String> getPlayerShopList();
	
	public HyperPlayer getHyperPlayer(String name);
	/**
	 * @param player (name of player)
	 * @return true if the hash matches the player's hash and false if it doesn't
	 */
	public boolean checkHash(String player, String hash);

	/**
	 * @param player (name of player)
	 * @return The random hash for the specified player.  If the player is not in the HyperConomy database it returns ""
	 */
	public String getSalt(String player);
	/**
	 * @param Item entity
	 * @return Returns true if the given Item is being used as an ItemDisplay and false if it is not.
	 */
	public boolean isItemDisplay(Item item);
	
	public EnchantmentClass getEnchantmentClass(SerializableItemStack stack);

	public HyperObject getHyperObject(String name, String economy);
	public HyperObject getHyperObject(String name, String economy, Shop s);
	public HyperObject getHyperObject(SerializableItemStack stack, String economy);
	public HyperObject getHyperObject(SerializableItemStack stack, String economy, Shop s);
	public ArrayList<HyperObject> getEnchantmentHyperObjects(SerializableItemStack stack, String player);
	
	public ArrayList<HyperObject> getAvailableObjects(String shopname);
	public ArrayList<HyperObject> getAvailableObjects(String shopname, int startingPosition, int limit);
	public ArrayList<HyperObject> getAvailableObjects(HyperPlayer p);
	public ArrayList<HyperObject> getAvailableObjects(HyperPlayer p, int startingPosition, int limit);
	
	public TransactionResponse buy(HyperPlayer p, HyperObject o, int amount);
	public TransactionResponse buy(HyperPlayer p, HyperObject o, int amount, Shop shop);
	public TransactionResponse sell(HyperPlayer p, HyperObject o, int amount);
	public TransactionResponse sell(HyperPlayer p, HyperObject o, int amount, Shop shop);
	public TransactionResponse sellAll(HyperPlayer p);
	public TransactionResponse sellAll(HyperPlayer p, SerializableInventory inventory);
	
	public boolean addItemToEconomy(SerializableItemStack stack, String economyName, String requestedName);

}
