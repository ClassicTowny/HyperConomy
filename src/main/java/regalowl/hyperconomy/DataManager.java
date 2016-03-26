package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.simpledatalib.file.FileTools;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.simpledatalib.sql.SQLWrite;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperBankManager;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEconomyCreationEvent;
import regalowl.hyperconomy.event.HyperEconomyDeletionEvent;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.shop.HyperShopManager;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.util.DatabaseUpdater;

public class DataManager implements HyperEventListener {

	private transient HyperConomy hc;
	private transient SQLRead sr;
	private transient SQLWrite sw;
	private transient DatabaseUpdater du;
	private transient FileConfiguration config;

	
	
	private boolean loadActive;
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	private CopyOnWriteArrayList<String> categories = new CopyOnWriteArrayList<String>();
	private String defaultServerShopAccount;
	private HyperPlayerManager hpm;
	private HyperBankManager hbm;
	private HyperShopManager hsm;



	public DataManager(HyperConomy hc) {
		this.hc = hc;
		loadActive = false;
		config = hc.getConf();
		defaultServerShopAccount = config.getString("shop.default-server-shop-account");
		hc.getHyperEventHandler().registerListener(this);
		sr = hc.getSQLRead();
		sw = hc.getSimpleDataLib().getSQLManager().getSQLWrite();
		hpm = new HyperPlayerManager(hc);
		hbm = new HyperBankManager(hc);
		hsm = new HyperShopManager(hc);
		du = new DatabaseUpdater(hc);
	}


	public ArrayList<String> getTablesList() {
		return du.getTablesList();
	}
	
	public DatabaseUpdater getDatabaseUpdater() {
		return du;
	}
	public HyperPlayerManager getHyperPlayerManager() {
		return hpm;
	}
	public HyperBankManager getHyperBankManager() {
		return hbm;
	}
	public HyperShopManager getHyperShopManager() {
		return hsm;
	}
	

	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof DataLoadEvent) {
			DataLoadEvent devent = (DataLoadEvent)event;
			if (devent.loadType == DataLoadType.START) {
				if (loadActive) {return;}
				loadActive = true;
				new Thread(new Runnable() {
					public void run() {
						loadEconomies();
					}
				}).start();
			} else if (devent.loadType == DataLoadType.SHOP) {
				loadAllCategories();
				hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.COMPLETE));
			} else if (devent.loadType == DataLoadType.COMPLETE) {
				hc.getHyperLock().setLoadLock(false);
				loadActive = false;
			}
		} else if (event instanceof TradeObjectModificationEvent) {
			TradeObjectModificationEvent tevent = (TradeObjectModificationEvent)event;
			TradeObject to = tevent.getTradeObject();
			if (to != null) {
				for (String cat:to.getCategories()) {
					if (!categories.contains(cat)) {
						categories.add(cat);
					}
				}
			}
		}
	}

	
	private void loadAllCategories() {
		categories.clear();
		for (TradeObject to:getTradeObjects()) {
			for (String cat:to.getCategories()) {
				if (!categories.contains(cat)) {
					categories.add(cat);
				}
			}
		}
	}
	
	public ArrayList<String> getCategories() {
		ArrayList<String> cats = new ArrayList<String>();
		cats.addAll(categories);
		return cats;
	}
	
	public boolean categoryExists(String category) {
		return categories.contains(category);
	}
	
	private void loadEconomies() {
		hc.getSQLRead().setErrorLogging(false);
		QueryResult qr = sr.select("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = 'version'");
		hc.getSQLRead().setErrorLogging(true);
		boolean success = du.updateTables(qr);
		if (!success) {
			hc.disable(false);
			return;
		}
		qr = sr.select("SELECT * FROM hyperconomy_objects WHERE economy = 'default'");
		if (!qr.next()) {setupDefaultEconomy();}
		economies.clear();
		qr = sr.select("SELECT * FROM hyperconomy_economies");
		boolean successfulLoad = true;
		while (qr.next()) {
			String name = qr.getString("NAME");
			HyperEconomy econ = new HyperEconomy(hc, name, qr.getString("HYPERACCOUNT"));
			if (!econ.successfulLoad()) successfulLoad = false;
			economies.put(name, econ);
		}
		if (!successfulLoad) return;
		hc.getDebugMode().ayncDebugConsoleMessage("Economies loaded.");
		hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.ECONOMY));
	}
	
	private void setupDefaultEconomy() {
		boolean writeState = sw.writeSync();
		sw.writeSync(true);
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (ft.fileExists(defaultObjectsPath)) {ft.deleteFile(defaultObjectsPath);}
		ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", "default");
		values.put("HYPERACCOUNT", config.getString("shop.default-server-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		while (data.next()) {
			values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		ft.deleteFile(defaultObjectsPath);
		sw.writeSyncQueue();
		sw.writeSync(writeState);
		hc.getDebugMode().ayncDebugConsoleMessage("Default economy created.");
	}
	
	
	
	public HyperEconomy getEconomy(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(name)) {
				return he;
			}
		}
		return null;
	}
	public HyperEconomy getDefaultEconomy() {
		return getEconomy("default");
	}
	
	
	public boolean economyExists(String economy) {
		if (economy == null || economy == "") {return false;}
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(economy)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<HyperEconomy> getEconomies() {
		ArrayList<HyperEconomy> econs = new ArrayList<HyperEconomy>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			econs.add(entry.getValue());
		}
		return econs;
	}
	
	public void shutDown() {
		hpm.purgeDeadAccounts();
		for (HyperEconomy he: economies.values()) {
			he.clearData();
		}
		economies.clear();
	}


	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			econs.add(he.getName());
		}
		return econs;
	}


	
	public ArrayList<TradeObject> getTradeObjects() {
		ArrayList<TradeObject> hyperObjects = new ArrayList<TradeObject>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			for (TradeObject ho:he.getTradeObjects()) {
				hyperObjects.add(ho);
			}
		}
		return hyperObjects;
	}



	
	/*
	public void addEconomy(HyperEconomy econ) {
		new Thread(new EconomyBuilder(econ)).start();
	}
	 */
	
	public void createNewEconomy(String name, String templateEconomy, boolean cloneAll) {
		new Thread(new EconomyBuilder(name, templateEconomy, cloneAll)).start();
	}
	
	private class EconomyBuilder implements Runnable {
		private String name;
		private String templateEconomyName = "";
		private boolean cloneAll = false;
		private HyperEconomy templateEconomy = null;
		
		//public EconomyBuilder(HyperEconomy templateEconomy) {
		//	this.templateEconomy = templateEconomy;
		//}
		public EconomyBuilder(String name, String templateEconomy, boolean cloneAll) {
			this.name = name;
			this.templateEconomyName = templateEconomy;
			this.cloneAll = cloneAll;
		}
		
		@Override
		public void run() {
			if (templateEconomy == null) {
				if (!economyExists(templateEconomyName)) templateEconomyName = "default";
				templateEconomy = getEconomy(templateEconomyName);
			} else {
				name = templateEconomy.getName();
				cloneAll = true;
			}
			if (name == null) return;
			SQLWrite sw = hc.getSQLWrite();
			boolean writeState = sw.writeSync();
			sw.writeSync(true);
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("NAME", name);
			values.put("HYPERACCOUNT", defaultServerShopAccount);
			sw.performInsert("hyperconomy_economies", values);
			for (TradeObject ho:templateEconomy.getTradeObjects()) {
				values = new HashMap<String,String>();
				values.put("NAME", ho.getName());
				values.put("DISPLAY_NAME", ho.getDisplayName());
				values.put("ALIASES", ho.getAliasesString());
				values.put("CATEGORIES", ho.getCategoriesString());
				values.put("ECONOMY", name);
				values.put("TYPE", ho.getType().toString());
				values.put("VALUE", ho.getValue()+"");
				values.put("STATIC", ho.isStatic()+"");
				values.put("STATICPRICE", ho.getStaticPrice()+"");
				values.put("MEDIAN", ho.getMedian()+"");
				values.put("STARTPRICE", ho.getStartPrice()+"");
				values.put("CEILING", ho.getCeiling()+"");
				values.put("FLOOR", ho.getFloor()+"");
				values.put("MAXSTOCK", ho.getMaxStock()+"");
				values.put("COMPONENTS", ho.getCompositeData());
				values.put("DATA", ho.getData());
				if (cloneAll) {
					values.put("INITIATION", ho.useInitialPricing()+"");
					values.put("STOCK", ho.getStock()+"");
				} else {
					values.put("INITIATION", "true");
					values.put("STOCK", 0+"");
				}
				sw.performInsert("hyperconomy_objects", values);
			}
			sw.writeSyncQueue();
			sw.writeSync(writeState);
			HyperEconomy newEconomy = new HyperEconomy(hc, name, templateEconomy.getAccountData());
			economies.put(name, newEconomy);
			hc.getHyperEventHandler().fireEvent(new HyperEconomyCreationEvent(newEconomy));
		}
	}
	
	public void deleteEconomy(String economy) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("ECONOMY", economy);
		hc.getSQLWrite().performDelete("hyperconomy_objects", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("NAME", economy);
		hc.getSQLWrite().performDelete("hyperconomy_economies", conditions);
		economies.remove(economy);
		hc.getHyperEventHandler().fireEvent(new HyperEconomyDeletionEvent(economy));
	}

	//TODO add restore default economy command.  (Replace with csv data)
	/*
	public void createEconomyFromDefaultCSV(String econ, boolean restart) {
		if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {
			new Backup();
		}
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (!ft.fileExists(defaultObjectsPath)) {
			ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.addToQueue("DELETE FROM hyperconomy_economies WHERE NAME = '"+econ+"'");
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", econ);
		values.put("HYPERACCOUNT", hc.getConf().getString("shop.default-server-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		sw.addToQueue("DELETE FROM hyperconomy_objects WHERE ECONOMY = '"+econ+"'");
		while (data.next()) {
			values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		ft.deleteFile(defaultObjectsPath);
		if (restart) {
			hc.restart();
		}
	}
	*/



	public boolean accountExists(String name) {
		if (name.contains(":")) {
			String[] accountData = name.split(Pattern.quote(":"));
			String accountName = accountData[1];
			if (accountData[0].equalsIgnoreCase("PLAYER")) {
				return hc.getHyperPlayerManager().hyperPlayerExists(accountName);
			} else if (accountData[0].equalsIgnoreCase("BANK")) {
				return hc.getHyperBankManager().hasBank(accountName);
			} else {
				return false;
			}
		} else {
			if (hc.getHyperPlayerManager().hyperPlayerExists(name) || hc.getHyperBankManager().hasBank(name)) return true;
			return false;
		}
	}
	public HyperAccount getAccount(String name) {
		if (name.contains(":")) {
			String[] accountData = name.split(Pattern.quote(":"));
			String accountName = accountData[1];
			if (accountData[0].equalsIgnoreCase("PLAYER")) {
				return hc.getHyperPlayerManager().getHyperPlayer(accountName);
			} else if (accountData[0].equalsIgnoreCase("BANK")) {
				return hc.getHyperBankManager().getHyperBank(accountName);
			} else {
				return null;
			}
		} else {
			String accountName = name;
			if (hc.getHyperPlayerManager().hyperPlayerExists(accountName)) {
				return hc.getHyperPlayerManager().getHyperPlayer(accountName);
			} else if (hc.getHyperBankManager().hasBank(accountName)) {
				return hc.getHyperBankManager().getHyperBank(accountName);
			}
			return null;
		}
	}
	
	public ArrayList<HyperAccount> getAccounts() {
		ArrayList<HyperAccount> accts = new ArrayList<HyperAccount>();
		accts.addAll(hc.getHyperPlayerManager().getHyperPlayers());
		accts.addAll(hc.getHyperBankManager().getHyperBanks());
		return accts;
	}
	
	/**
	 * Replaces all economies with the given economy ArrayList.  Doesn't save the replacement economies to the database.
	 * @param econArray
	 */
	public void setEconomies(ArrayList<HyperEconomy> econArray) {
		economies.clear();
		for(HyperEconomy he:econArray) {
			he.setHyperConomy(hc);
			economies.put(he.getName(), he);
		}
	}
	
	public void addEconomy(HyperEconomy econ) {
		econ.setHyperConomy(hc);
		economies.put(econ.getName(), econ);
		loadAllCategories();
	}




	
}
