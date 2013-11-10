package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import regalowl.databukkit.DataBukkit;
import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;


public class Copydatabase {
	
	private HyperConomy hc;
	private BukkitTask waitTask;
	private CommandSender sender;
	private String mysqlMessage;
	private String sqliteMessage;
	private LanguageFile L;
	private boolean includeHistory;
	private SQLWrite sw;

	Copydatabase(CommandSender csender, String args[]) {
		hc = HyperConomy.hc;
		sender = csender;
		L = hc.getLanguageFile();
		includeHistory = false;
		boolean useMySQL = hc.gYH().gQFC("config").gB("sql-connection.use-mysql");

		try {
			mysqlMessage = L.get("COPYDATABASE_MYSQL");
			sqliteMessage = L.get("COPYDATABASE_SQLITE");
			if (args.length == 0) {
				if (useMySQL) {
					sender.sendMessage(L.get("COPYDATABASE_MYSQL_WARNING"));
				} else {
					sender.sendMessage(L.get("COPYDATABASE_SQLITE_WARNING"));
				}
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("confirm")) {
				if (args.length == 2 && args[1].equalsIgnoreCase("history")) {
					includeHistory = true;
				}
				if (hc.getSQLWrite().getBufferSize() != 0) {
					sender.sendMessage(L.get("WAIT_FOR_QUEUE"));
					return;
				}

				DataBukkit db2 = new DataBukkit(hc);
				if (!useMySQL) {
					FileConfiguration config = hc.gYH().gFC("config");
					String username = config.getString("config.sql-connection.username");
					String password = config.getString("config.sql-connection.password");
					int port = config.getInt("config.sql-connection.port");
					String host = config.getString("config.sql-connection.host");
					String database = config.getString("config.sql-connection.database");
					db2.enableMySQL(host, database, username, password, port);
				}
				db2.createDatabase();
				if (!useMySQL) {
					if (!db2.useMySQL()) {
						sender.sendMessage(L.get("COPYDATABASE_CONNECTION_FAILED_MYSQL"));
						return;
					}
				}
				sw = db2.getSQLWrite();

				hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
					public void run() {
						EconomyManager em = hc.getEconomyManager();
						SQLRead sr = hc.getSQLRead();
						hc.getHyperLock().setLoadLock(true);
						sw.addToQueue("DELETE FROM hyperconomy_objects");
						sw.addToQueue("DELETE FROM hyperconomy_players");
						sw.addToQueue("DELETE FROM hyperconomy_audit_log");
						sw.addToQueue("DELETE FROM hyperconomy_history");
						sw.addToQueue("DELETE FROM hyperconomy_log");
						sw.addToQueue("DELETE FROM hyperconomy_settings");
						sw.addToQueue("DELETE FROM hyperconomy_shop_objects");
						for (HyperObject ho : em.getHyperObjects()) {
							if (ho instanceof HyperItem) {
								HyperItem hi = (HyperItem)ho;
								sw.addToQueue("INSERT INTO hyperconomy_objects (NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " VALUES ('" + ho.getName() + "','" + ho.getEconomy() + "','" + ho.getType() + "','" + hi.getMaterial() + "','" + hi.getData() + "','" + hi.getDurability() + "','" + ho.getValue() + "','" + ho.getIsstatic() + "','"
										+ ho.getStaticprice() + "','" + ho.getStock() + "','" + ho.getMedian() + "','" + ho.getInitiation() + "','" + ho.getStartprice() + "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
							} else if (ho instanceof HyperEnchant) {
								HyperEnchant he = (HyperEnchant)ho;
								sw.addToQueue("INSERT INTO hyperconomy_objects (NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " VALUES ('" + ho.getName() + "','" + ho.getEconomy() + "','" + ho.getType() + "','" + he.getEnchantmentName() + "','" + "','-1','-1','" + ho.getValue() + "','" + ho.getIsstatic() + "','"
										+ ho.getStaticprice() + "','" + ho.getStock() + "','" + ho.getMedian() + "','" + ho.getInitiation() + "','" + ho.getStartprice() + "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
							} else if (ho instanceof BasicObject) {
								sw.addToQueue("INSERT INTO hyperconomy_objects (NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " VALUES ('" + ho.getName() + "','" + ho.getEconomy() + "','" + ho.getType() + "','none','-1','-1','" + ho.getValue() + "','" + ho.getIsstatic() + "','"
										+ ho.getStaticprice() + "','" + ho.getStock() + "','" + ho.getMedian() + "','" + ho.getInitiation() + "','" + ho.getStartprice() + "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
							}
						}
						for (HyperPlayer hp : em.getHyperPlayers()) {
							sw.addToQueue("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH)" + " VALUES ('" + hp.getName() + "','" + hp.getEconomy() + "','" + hp.getBalance() + "','" + hp.getX() + "','" + hp.getY() + "','" + hp.getZ() + "','" + hp.getWorld() + "','" + hp.getHash() + "')");
						}
						QueryResult result = sr.aSyncSelect("SELECT * FROM hyperconomy_audit_log");
						while (result.next()) {
							sw.addToQueue("INSERT INTO hyperconomy_audit_log (TIME, ACCOUNT, ACTION, AMOUNT, ECONOMY) VALUES ('" + result.getString("TIME") + "','" + result.getString("ACCOUNT") + "','" + result.getString("ACTION") + "','" + result.getDouble("AMOUNT") + "','" + result.getString("ECONOMY") + "')");
						}
						result.close();
						result = sr.aSyncSelect("SELECT * FROM hyperconomy_log");
						while (result.next()) {
							sw.addToQueue("INSERT INTO hyperconomy_log (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE) VALUES ('" + result.getString("TIME") + "','" + result.getString("CUSTOMER") + "','" + result.getString("ACTION") + "','" + result.getString("OBJECT") + "','" + result.getDouble("AMOUNT") + "','" + result.getDouble("MONEY") + "','" + result.getDouble("TAX") + "','" + result.getString("STORE") + "','" + result.getString("TYPE") + "')");
						}
						result.close();
						result = sr.aSyncSelect("SELECT * FROM hyperconomy_settings");
						while (result.next()) {
							sw.addToQueue("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME)" + " VALUES ('" + result.getString("SETTING") + "','" + result.getString("VALUE") + "','" + result.getString("TIME") + "')");
						}
						result.close();
						result = sr.aSyncSelect("SELECT * FROM hyperconomy_shop_objects");
						while (result.next()) {
							sw.addToQueue("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+result.getString("SHOP")+"', '"+result.getString("HYPEROBJECT")+"', '"+result.getDouble("QUANTITY")+"', '"+result.getDouble("PRICE")+"', '"+result.getString("STATUS")+"')");
						}
						result.close();
						if (includeHistory) {
							result = sr.aSyncSelect("SELECT * FROM hyperconomy_history");
							while (result.next()) {
								sw.addToQueue("INSERT INTO hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE)" + " VALUES ('" + result.getString("OBJECT") + "','" + result.getString("ECONOMY") + "','" + result.getString("TIME") + "','" + result.getDouble("PRICE") + "')");
							}
							result.close();
						}
						waitForFinish();
						hc.getServer().getScheduler().runTask(hc, new Runnable() {
							public void run() {
								sender.sendMessage(L.get("COPYDATABASE_STARTED"));
							}
						});
					}
				});

			} else {
				sender.sendMessage(L.get("COPYDATABASE_INVALID"));
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("COPYDATABASE_INVALID"));
			hc.getHyperLock().setLoadLock(false);
			return;
		}
	}
	
	
	
	
	
	
	
	private void waitForFinish() {
		waitTask = hc.getServer().getScheduler().runTaskTimerAsynchronously(hc, new Runnable() {
    		public void run() {
				hc.getServer().getScheduler().runTask(hc, new Runnable() {
		    		public void run() {
		    			if (sw != null) {
		    				sender.sendMessage(sw.getBufferSize() + " statements remaining.");
		    			}
		    		}
		    	});
    			if (sw == null || !sw.writeActive()) {
    				hc.getHyperLock().setLoadLock(false);
    				hc.getServer().getScheduler().runTask(hc, new Runnable() {
    		    		public void run() {
    		    			if (hc.gYH().gQFC("config").gB("sql-connection.use-mysql")) {
    		    				sender.sendMessage(mysqlMessage);
    		    			} else {
    		    				sender.sendMessage(sqliteMessage);
    		    			}
    		    		}
    		    	});
    				waitTask.cancel();
    			}
    		}
    	}, 40L, 40L);
	}

	
}
