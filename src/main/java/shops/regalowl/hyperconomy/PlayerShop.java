package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import regalowl.databukkit.QueryResult;

public class PlayerShop implements Shop, Comparable<Shop> {

	private String name;
	private String world;
	private String economy;
	private HyperPlayer owner;
	private String message1;
	private String message2;
	private int p1x;
	private int p1y;
	private int p1z;
	private int p2x;
	private int p2y;
	private int p2z;
	
	private boolean useshopexitmessage;
	
	private HyperConomy hc;
	private LanguageFile L;
	private FileConfiguration shopFile;
	private EconomyManager em;
	
	private CopyOnWriteArrayList<PlayerShopObject> shopContents = new CopyOnWriteArrayList<PlayerShopObject>();
	private ArrayList<String> inShop = new ArrayList<String>();
	
	PlayerShop(String name, String economy, HyperPlayer owner) {
		this.name = name;
		this.economy = economy;
		this.owner = owner;
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		L = hc.getLanguageFile();
		shopFile = hc.gYH().getFileConfiguration("shops");
		shopFile.set(name + ".economy", economy);
		shopFile.set(name + ".owner", owner.getName());
		useshopexitmessage = hc.gYH().gFC("config").getBoolean("config.use-shop-exit-message");	
		QueryResult result = hc.getSQLRead().aSyncSelect("SELECT * FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"'");
		while (result.next()) {
			double price = result.getDouble("PRICE");
			if (price == 0.0) {
				//PlayerShopObject pso = new PlayerShopObject(hc.getDataFunctions().getHyperObject(result.getString("HYPEROBJECT"), economy), result.getDouble("QUANTITY"), PlayerShopObjectStatus.fromString(result.getString("STATUS")));
				//shopContents.add(pso);
			} else {
				//PlayerShopObject pso = new PlayerShopObject(hc.getDataFunctions().getHyperObject(result.getString("HYPEROBJECT"), economy), result.getDouble("QUANTITY"), PlayerShopObjectStatus.fromString(result.getString("STATUS")), price);
				//shopContents.add(pso);
			}
		}
		result.close();
	}
	

	
	public int compareTo(Shop s) {
		return name.compareTo(s.getName());
	}
	
	public void setPoint1(String world, int x, int y, int z) {
		this.world = world;
		p1x = x;
		p1y = y;
		p1z = z;
		shopFile.set(name + ".world", world);
		shopFile.set(name + ".p1.x", x);
		shopFile.set(name + ".p1.y", y);
		shopFile.set(name + ".p1.z", z);
	}
	
	public void setPoint2(String world, int x, int y, int z) {
		this.world = world;
		p2x = x;
		p2y = y;
		p2z = z;
		shopFile.set(name + ".world", world);
		shopFile.set(name + ".p2.x", x);
		shopFile.set(name + ".p2.y", y);
		shopFile.set(name + ".p2.z", z);
	}
	
	
	public void setPoint1(Player player) {
		Location l = player.getLocation();
		setPoint1(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public void setPoint2(Player player) {
		Location l = player.getLocation();
		setPoint2(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	
	
	public void setMessage1(String message) {
		message1 = message;
		shopFile.set(name + ".shopmessage1", message1);
		
	}
	
	public void setMessage2(String message) {
		message2 = message;
		shopFile.set(name + ".shopmessage2", message2);
	}
	
	public void setDefaultMessages() {
		setMessage1("&aWelcome to %n");
		setMessage2("&9Type &b/hc &9for help.");
	}
	
	public void setWorld(String world) {
		this.world = world;
		shopFile.set(name + ".world", world);
	}
	
	public void setName(String name) {
		shopFile.set(this.name, null);
		this.name = name;
		shopFile.set(this.name, this.name);
		shopFile.set(name + ".world", world);
		shopFile.set(name + ".p1.x", p1x);
		shopFile.set(name + ".p1.y", p1y);
		shopFile.set(name + ".p1.z", p1z);
		shopFile.set(name + ".p2.x", p2x);
		shopFile.set(name + ".p2.y", p2y);
		shopFile.set(name + ".p2.z", p2z);
		shopFile.set(name + ".shopmessage1", message1);
		shopFile.set(name + ".shopmessage2", message2);
		shopFile.set(name + ".economy", economy);
	}
	
	public void setEconomy(String economy) {
		this.economy = economy;
		shopFile.set(name + ".economy", economy);
	}
	
	
	public boolean inShop(int x, int y, int z, String world) {
		if (world.equalsIgnoreCase(this.world)) {
			int rangex = Math.abs(p1x - p2x);
			if (Math.abs(x - p1x) <= rangex && Math.abs(x - p2x) <= rangex) {
				int rangez = Math.abs(p1z - p2z);
				if (Math.abs(z - p1z) <= rangez && Math.abs(z - p2z) <= rangez) {
					int rangey = Math.abs(p1y - p2y);
					if (Math.abs(y - p1y) <= rangey && Math.abs(y - p2y) <= rangey) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public boolean inShop(Player player) {
		Location l = player.getLocation();
		return inShop(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
	}
	
	public void sendEntryMessage(Player player) {
		if (message1 == null || message2 == null) {
			message1 = "&aWelcome to %n";
			message2 = "&9Type &b/hc &9for help.";
		}
		player.sendMessage(L.get("SHOP_LINE_BREAK"));
		player.sendMessage(message1.replace("%n", name).replace("_", " ").replace("&","\u00A7"));
		player.sendMessage(message2.replace("%n", name).replace("_", " ").replace("&","\u00A7"));
		player.sendMessage(L.get("SHOP_LINE_BREAK"));
	}
	
	public String getEconomy() {
		return economy;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return name.replace("_", " ");
	}
	
	
	public boolean has(String item) {
		/*
		FileConfiguration sh = hc.getYaml().getShops();
		String unavailableS = sh.getString(name + ".unavailable");
		if (unavailableS == null || unavailableS.equalsIgnoreCase("")) {
			return true;
		}
		if (unavailableS.equalsIgnoreCase("all")) {
			return false;
		}
		item = hc.getDataFunctions().fixNameTest(item);
		if (item == null) {
			return false;
		}
		SerializeArrayList sal = new SerializeArrayList();

		ArrayList<String> unavailable = sal.stringToArray(unavailableS);
		for (String object : unavailable) {
			if (object.equalsIgnoreCase(item)) {
				return false;
			}
		}
		HyperObject ho = hc.getDataFunctions().getHyperObject(item, economy);
		if (getPlayerShopObject(ho) != null) {
			return true;
		}
		*/
		return false;
		
	}
	
	
	public void addAllObjects() {
		shopFile.set(name + ".unavailable", null);
	}
	
	public void removeAllObjects() {
		shopFile.set(name + ".unavailable", "all");
	}
	
	public void addObjects(ArrayList<String> objects) {
		HyperEconomy he = em.getEconomy(owner.getEconomy());
		FileConfiguration sh = hc.gYH().gFC("shops");
		SerializeArrayList sal = new SerializeArrayList();
		ArrayList<String> unavailable = sal.stringToArray(sh.getString(name + ".unavailable"));
		if (unavailable.size() == 1 && unavailable.get(0).equalsIgnoreCase("all")) {
			unavailable = he.getNames();
		}
		for (String object:objects) {
			if (unavailable.contains(he.fixName(object))) {
				unavailable.remove(object);
			}
		}
		sh.set(name + ".unavailable", sal.stringArrayToString(unavailable));
	}
	
	public void removeObjects(ArrayList<String> objects) {
		HyperEconomy he = em.getEconomy(owner.getEconomy());
		FileConfiguration sh = hc.gYH().gFC("shops");
		SerializeArrayList sal = new SerializeArrayList();
		ArrayList<String> unavailable = sal.stringToArray(sh.getString(name + ".unavailable"));
		if (unavailable.size() == 1 && unavailable.get(0).equalsIgnoreCase("all")) {
			return;
		}
		for (String object:objects) {
			if (!unavailable.contains(he.fixName(object))) {
				unavailable.add(object);
			}
		}
		sh.set(name + ".unavailable", sal.stringArrayToString(unavailable));
	}
	
	
	public int getP1x() {
		return p1x;
	}

	public int getP1y() {
		return p1y;
	}

	public int getP1z() {
		return p1z;
	}

	public int getP2x() {
		return p2x;
	}

	public int getP2y() {
		return p2y;
	}

	public int getP2z() {
		return p2z;
	}
	
	public HyperPlayer getOwner() {
		return owner;
	}
	
	public void setOwner(HyperPlayer owner) {
		this.owner = owner;
	}

	public ArrayList<HyperObject> getAvailableObjects() {
		ArrayList<HyperObject> available = new ArrayList<HyperObject>();
		for (PlayerShopObject pso:shopContents) {
			available.add(pso.getHyperObject());
		}
		return available;
	}
	
	public void deleteShop() {
		hc.getSQLWrite().executeSQL("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"'");
	}
	
	public void removeFromShop(HyperObject hyperObject) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return;
		} else {
			shopContents.remove(pso);
			hc.getSQLWrite().executeSQL("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"' AND HYPEROBJECT = '"+hyperObject.getName()+"'");
		}
	}
	
	
	public PlayerShopObject getPlayerShopObject(HyperObject hyperObject) {
		for (PlayerShopObject pso:shopContents) {
			if (hyperObject.equals(pso.getHyperObject())) {
				return pso;
			}
		}
		return null;
	}
	
	public double getStock(HyperObject hyperObject) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return 0;
		} else {
			return pso.getQuantity();
		}
	}
	
	public void setStock(HyperObject hyperObject, double stock) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			pso = new PlayerShopObject(hyperObject, stock, PlayerShopObjectStatus.TRADE);
			shopContents.add(pso);
			hc.getSQLWrite().executeSQL("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+name+"', '"+hyperObject.getName()+"', '"+stock+"', '0.0', 'trade')");
		} else {
			pso.setQuantity(stock);
			hc.getSQLWrite().executeSQL("UPDATE hyperconomy_shop_objects SET QUANTITY='"+stock+"' WHERE SHOP='"+name+"' AND HYPEROBJECT='"+hyperObject.getName()+"'");
		}
	}
	

	
	public double getPrice(HyperObject hyperObject, HyperPlayer hyperPlayer, EnchantmentClass enchantClass) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
				return hyperObject.getValue(enchantClass, hyperPlayer);
			} else {
				return hyperObject.getValue(1, hyperPlayer);
			}
			
		} else {
			if (pso.getPrice() == 0.0) {
				return hyperObject.getValue(1, hyperPlayer);
			} else {
				return pso.getPrice();
			}
		}
	}
	
	public void setPrice(HyperObject hyperObject, double price) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			pso = new PlayerShopObject(hyperObject, 0, PlayerShopObjectStatus.TRADE, price);
			shopContents.add(pso);
			hc.getSQLWrite().executeSQL("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+name+"', '"+hyperObject.getName()+"', '0', '"+price+"', 'trade')");
		} else {
			pso.setPrice(price);
			hc.getSQLWrite().executeSQL("UPDATE hyperconomy_shop_objects SET PRICE='"+price+"' WHERE SHOP='"+name+"' AND HYPEROBJECT='"+hyperObject.getName()+"'");
		}
	}

	
	public PlayerShopObjectStatus getObjectStatus(HyperObject hyperObject) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return PlayerShopObjectStatus.NONE;
		} else {
			return pso.getStatus();
		}
	}
	
	public void setObjectStatus(HyperObject hyperObject, PlayerShopObjectStatus status) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return;
		} else {
			pso.setStatus(status);
			hc.getSQLWrite().executeSQL("UPDATE hyperconomy_shop_objects SET STATUS='"+status.toString()+"' WHERE SHOP='"+name+"' AND HYPEROBJECT='"+hyperObject.getName()+"'");
		}
	}



	public void setGlobal() {
		// TODO Auto-generated method stub
		
	}



	public HyperEconomy getHyperEconomy() {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean has(HyperObject ho) {
		// TODO Auto-generated method stub
		return false;
	}



	public void updatePlayerStatus() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (inShop.contains(p.getName())) {
				if (!inShop(p)) {
					inShop.remove(p.getName());
					if (useshopexitmessage) {
						p.sendMessage(L.get("SHOP_EXIT_MESSAGE"));
					}
				}
			} else {
				if (inShop(p)) {
					inShop.add(p.getName());
					sendEntryMessage(p);
					hc.getEconomyManager().getHyperPlayer(p.getName()).setEconomy(owner.getEconomy());
				}
			}
		}
	}
}
