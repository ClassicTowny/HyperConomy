package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.file.FileTools;
import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SQLRead;
import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.event.DataLoadListener;
import regalowl.hyperconomy.hyperobject.ComponentItem;
import regalowl.hyperconomy.hyperobject.CompositeItem;
import regalowl.hyperconomy.hyperobject.Enchant;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.hyperobject.Xp;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;



public class HyperEconomy implements DataLoadListener {

	private HyperAccount defaultAccount;
	private ConcurrentHashMap<String, HyperObject> hyperObjectsName = new ConcurrentHashMap<String, HyperObject>();
	private ConcurrentHashMap<String, String> hyperObjectsAliases = new ConcurrentHashMap<String, String>();
	

	private HashMap<String,String> composites = new HashMap<String,String>();
	private boolean useComposites;
	private HyperConomy hc;
	private SQLRead sr;
	private String economyName;
	private boolean dataLoaded;
	private String xpName = null;
	

	HyperEconomy(String economy) {
		dataLoaded = false;
		hc = HyperConomy.hc;	
		this.economyName = economy;
		hc.getHyperEventHandler().registerDataLoadListener(this);
		sr = hc.getSQLRead();
		useComposites = hc.gYH().gFC("config").getBoolean("enable-feature.composite-items");
		//loadCompositeKeys();
		load();
	}

	@Override
	public void onDataLoad() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				HashMap<String,String> conditions = new HashMap<String,String>();
				conditions.put("NAME", economyName);
				String account = sr.getString("hyperconomy_economies", "hyperaccount", conditions);
				defaultAccount = hc.getDataManager().getAccount(account);
				if (defaultAccount == null) {
					defaultAccount = hc.getDataManager().getAccount(account);
				} 
			}
		});
	}

	public boolean dataLoaded() {
		return dataLoaded;
	}

	private void load() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				composites.clear();
				QueryResult result = sr.select("SELECT * FROM hyperconomy_composites");
				while (result.next()) {
					composites.put(result.getString("NAME").toLowerCase(), result.getString("COMPONENTS"));
				}
				hyperObjectsName.clear();
				result = sr.select("SELECT * FROM hyperconomy_objects WHERE ECONOMY = '"+economyName+"'");
				while (result.next()) {
					if (useComposites && composites.containsKey(result.getString("NAME").toLowerCase())) {continue;}
					HyperObjectType type = HyperObjectType.fromString(result.getString("TYPE"));
					if (type == HyperObjectType.ITEM) {
						HyperObject hobj = new ComponentItem(result.getString("NAME"), result.getString("ECONOMY"), 
								result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("TYPE"), result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
								result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
								result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"), result.getString("DATA"));
						hyperObjectsName.put(hobj.getName().toLowerCase(), hobj);
						for (String alias:hobj.getAliases()) {
							hyperObjectsAliases.put(alias.toLowerCase(), hobj.getName().toLowerCase());
						}
						hyperObjectsAliases.put(hobj.getName().toLowerCase(), hobj.getName().toLowerCase());
						hyperObjectsAliases.put(hobj.getDisplayName().toLowerCase(), hobj.getName().toLowerCase());
					} else if (type == HyperObjectType.ENCHANTMENT) {
						HyperObject hobj = new Enchant(result.getString("NAME"), result.getString("ECONOMY"), 
								result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("TYPE"), 
								result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
								result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
								result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"), result.getString("DATA"));
						hyperObjectsName.put(hobj.getName().toLowerCase(), hobj);
						for (String alias:hobj.getAliases()) {
							hyperObjectsAliases.put(alias.toLowerCase(), hobj.getName().toLowerCase());
						}
						hyperObjectsAliases.put(hobj.getName().toLowerCase(), hobj.getName().toLowerCase());
						hyperObjectsAliases.put(hobj.getDisplayName().toLowerCase(), hobj.getName().toLowerCase());
					} else if (type == HyperObjectType.EXPERIENCE) {
						HyperObject hobj = new Xp(result.getString("NAME"), result.getString("ECONOMY"), 
								result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("TYPE"), 
								result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
								result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
								result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"));
						hyperObjectsName.put(hobj.getName().toLowerCase(), hobj);
						xpName = result.getString("NAME");
						for (String alias:hobj.getAliases()) {
							hyperObjectsAliases.put(alias.toLowerCase(), hobj.getName().toLowerCase());
						}
						hyperObjectsAliases.put(hobj.getName().toLowerCase(), hobj.getName().toLowerCase());
						hyperObjectsAliases.put(hobj.getDisplayName().toLowerCase(), hobj.getName().toLowerCase());
					}
				}
				result.close();
				if (xpName == null) {xpName = "xp";}
				if (!useComposites) {
					dataLoaded = true;
					return;
				}
				loadComposites();
				dataLoaded = true;
			}
		});
	}
	
	private void loadComposites() {
		boolean loaded = false;
		int counter = 0;
		while (!loaded) {
			counter++;
			if (counter > 100) {
				hc.getDataBukkit().writeError("Infinite loop when loading composites.yml.  You likely have an error in your composites.yml file.  Your items will not work properly until this is fixed.");
				return;
			}
			loaded = true;
			QueryResult result = sr.select("SELECT hyperconomy_objects.NAME, hyperconomy_objects.DISPLAY_NAME, "
					+ "hyperconomy_objects.ALIASES, hyperconomy_objects.TYPE, hyperconomy_composites.COMPONENTS,"
					+ " hyperconomy_objects.DATA FROM hyperconomy_composites, hyperconomy_objects WHERE "
					+ "hyperconomy_composites.NAME = hyperconomy_objects.NAME");
			while (result.next()) {
				String name = result.getString("NAME");
				if (!componentsLoaded(name)) {
					loaded = false;
					continue;
				}
				HyperObject ho = new CompositeItem(name, economyName, result.getString("DISPLAY_NAME"), result.getString("ALIASES"), 
						result.getString("TYPE"), result.getString("COMPONENTS"), result.getString("DATA"));
				hyperObjectsName.put(ho.getName().toLowerCase(), ho);
				for (String alias:ho.getAliases()) {
					hyperObjectsAliases.put(alias.toLowerCase(), ho.getName().toLowerCase());
				}
				hyperObjectsAliases.put(ho.getName().toLowerCase(), ho.getName().toLowerCase());
				hyperObjectsAliases.put(ho.getDisplayName().toLowerCase(), ho.getName().toLowerCase());
			}
		}
	}
	private boolean componentsLoaded(String name) {
		CommonFunctions cf = hc.gCF();
		HashMap<String,String> tempComponents = cf.explodeMap(composites.get(name.toLowerCase()));
		for (Map.Entry<String,String> entry : tempComponents.entrySet()) {
		    String oname = entry.getKey();
		    HyperObject ho = getHyperObject(oname);
		    if (ho == null) {
		    	//hc.getLogger().severe("Not loaded: " + oname);
		    	return false;
		    }
		}
		return true;
	}
	

	public HyperAccount getDefaultAccount() {
		return defaultAccount;
	}
	
	public void setDefaultAccount(HyperAccount account) {
		if (account == null) {return;}
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", economyName);
		values.put("HYPERACCOUNT", account.getName());
		hc.getSQLWrite().performUpdate("hyperconomy_economies", values, conditions);
		this.defaultAccount = account;
	}
	
	public String getName() {
		return economyName;
	}
	


	public HyperObject getHyperObject(ItemStack stack) {
		return getHyperObject(stack, null);
	}
	public HyperObject getHyperObject(ItemStack stack, Shop s) {
		if (stack == null) {return null;}
		if (s != null && s instanceof PlayerShop) {
			for (HyperObject ho:hyperObjectsName.values()) {
				if (ho.getType() != HyperObjectType.ITEM) {continue;}
				if (!ho.matchesItemStack(stack)) {continue;}
				return (HyperObject) ((PlayerShop) s).getPlayerShopObject(ho);
			}
		} else {
			for (HyperObject ho:hyperObjectsName.values()) {
				if (ho.getType() != HyperObjectType.ITEM) {continue;}
				if (ho.matchesItemStack(stack)) {return ho;}
			}
		}
		return null;
	}
	public HyperObject getHyperObject(String name, Shop s) {
		if (name == null) {return null;}
		String sname = name.toLowerCase();
		if (hyperObjectsAliases.containsKey(sname)) {
			sname = hyperObjectsAliases.get(sname);
		}
		if (s != null && s instanceof PlayerShop) {
			if (hyperObjectsName.containsKey(sname)) {
				return (HyperObject) ((PlayerShop) s).getPlayerShopObject(hyperObjectsName.get(sname));
			} else {
				return null;
			}
		} else {
			if (hyperObjectsName.containsKey(sname)) {
				return hyperObjectsName.get(sname);
			} else {
				return null;
			}
		}
	}
	public HyperObject getHyperObject(String name) {
		return getHyperObject(name, null);
	}

	public void removeHyperObject(String name) {
		HyperObject ho = getHyperObject(name);
		if (hyperObjectsName.containsKey(ho.getName().toLowerCase())) {
			hyperObjectsName.remove(ho.getName().toLowerCase());
		}
	}
	
	
	public ArrayList<HyperObject> getHyperObjects(Shop s) {
		ArrayList<HyperObject> hos = new ArrayList<HyperObject>();
		for (HyperObject ho:hyperObjectsName.values()) {
			hos.add(getHyperObject(ho.getName(), s));
		}
		return hos;
	}
	
	
	public ArrayList<HyperObject> getHyperObjects() {
		ArrayList<HyperObject> hos = new ArrayList<HyperObject>();
		for (HyperObject ho:hyperObjectsName.values()) {
			hos.add(ho);
		}
		return hos;
	}




	public ArrayList<String> getObjectKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		for (String key:hyperObjectsName.keySet()) {
			keys.add(key);
		}
		return keys;
	}



	



	public void clearData() {
		hyperObjectsName.clear();
	}




	
	
	
	public ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjectsName.values()) {
			names.add(ho.getName());
		}
		return names;
	}
	

	
	public String getEnchantNameWithoutLevel(String bukkitName) {
		for (HyperObject ho:hyperObjectsName.values()) {
			if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				if (ho.getEnchantmentName().equalsIgnoreCase(bukkitName)) {
					String name = ho.getName();
					return name.substring(0, name.length() - 1);
				}
			}
		}
		return null;
	}
	
	public boolean objectTest(String name) {
		String sname = name.toLowerCase();
		if (hyperObjectsAliases.containsKey(sname)) {
			sname = hyperObjectsAliases.get(sname);
		}
		if (hyperObjectsName.containsKey(sname)) {
			return true;
		}
		return false;
	}
	
	
	public boolean itemTest(String name) {
		String sname = name.toLowerCase();
		if (hyperObjectsAliases.containsKey(sname)) {
			sname = hyperObjectsAliases.get(sname);
		}
		if (hyperObjectsName.containsKey(sname)) {
			HyperObject ho = hyperObjectsName.get(sname);
			if (ho.getType() == HyperObjectType.ITEM) {
				return true;
			}
		}
		return false;
	}
	

	public boolean enchantTest(String name) {
		String sname = name.toLowerCase();
		if (hyperObjectsAliases.containsKey(sname)) {
			sname = hyperObjectsAliases.get(sname);
		}
		if (hyperObjectsName.containsKey(sname)) {
			HyperObject ho = hyperObjectsName.get(sname);
			if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				return true;
			}
		}
		return false;
	}
	
	
	public String fixName(String nam) {
		String sname = nam.toLowerCase();
		if (hyperObjectsAliases.containsKey(sname)) {
			sname = hyperObjectsAliases.get(sname);
		}
		for (String name:getNames()) {
			if (name.equalsIgnoreCase(sname)) {
				return name;
			}
		}
		return nam;
	}
	
	public String fixNameTest(String nam) {
		String sname = nam.toLowerCase();
		if (hyperObjectsAliases.containsKey(sname)) {
			sname = hyperObjectsAliases.get(sname);
		}
		ArrayList<String> names = getNames();
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equalsIgnoreCase(sname)) {
				return names.get(i);
			}
		}
		return null;
	}
	


	
	
	public ArrayList<String> loadNewItems() {
		ArrayList<String> objectsAdded = new ArrayList<String>();
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (!ft.fileExists(defaultObjectsPath)) {
			ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		}
		SQLWrite sw = hc.getSQLWrite();
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		while (data.next()) {
			String objectName = data.getString("NAME");
			if (hyperObjectsName.keySet().contains(objectName.toLowerCase())) {continue;}
			objectsAdded.add(objectName);
			HashMap<String, String> values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		ft.deleteFile(defaultObjectsPath);
		hc.restart();
		return objectsAdded;
	}
	
	
	public void updateNamesFromYml() {
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (!ft.fileExists(defaultObjectsPath)) {
			ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		}
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		while (data.next()) {
			String objectName = data.getString("NAME");
			String aliasString = data.getString("ALIASES");
			ArrayList<String> names = hc.gCF().explode(aliasString, ",");
			String displayName = data.getString("DISPLAY_NAME");
			names.add(displayName);
			names.add(objectName);
			for (String cname:names) {
				HyperObject ho = getHyperObject(cname);
				if (ho == null) {continue;}
				ho.setAliases(hc.gCF().explode(aliasString, ","));
				ho.setDisplayName(displayName);
				ho.setName(objectName);
			}
		}
		for (Shop s:hc.getDataManager().getShops()) {
			if (s instanceof PlayerShop) {
				PlayerShop ps = (PlayerShop)s;
				for (HyperObject ho:ps.getShopObjects()) {
					ho.setHyperObject(ho.getHyperObject());
				}
			}
		}
		ft.deleteFile(defaultObjectsPath);
	}

	public String getXpName() {
		return xpName;
	}
	
	public void addHyperObject(HyperObject hobj) {
		hyperObjectsName.put(hobj.getName().toLowerCase(), hobj);
		for (String alias:hobj.getAliases()) {
			hyperObjectsAliases.put(alias.toLowerCase(), hobj.getName().toLowerCase());
		}
		hyperObjectsAliases.put(hobj.getName().toLowerCase(), hobj.getName().toLowerCase());
		hyperObjectsAliases.put(hobj.getDisplayName().toLowerCase(), hobj.getName().toLowerCase());
	}
}
