package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DataFunctions {
	protected HyperConomy hc;
	protected String username;
	protected String password;
	protected int port;
	protected String host;
	protected String database;
	protected boolean sqlloaded;
	protected boolean databuilt;
	protected ArrayList<String> tne = new ArrayList<String>();
	protected ArrayList<String> tname = new ArrayList<String>();
	protected ArrayList<String> teconomy = new ArrayList<String>();
	protected ArrayList<String> ttype = new ArrayList<String>();
	protected ArrayList<String> tcategory = new ArrayList<String>();
	protected ArrayList<String> tmaterial = new ArrayList<String>();
	protected ArrayList<Integer> tid = new ArrayList<Integer>();
	protected ArrayList<Integer> tdata = new ArrayList<Integer>();
	protected ArrayList<Integer> tdurability = new ArrayList<Integer>();
	protected ArrayList<Double> tvalue = new ArrayList<Double>();
	protected ArrayList<String> tstatic = new ArrayList<String>();
	protected ArrayList<Double> tstaticprice = new ArrayList<Double>();
	protected ArrayList<Double> tstock = new ArrayList<Double>();
	protected ArrayList<Double> tmedian = new ArrayList<Double>();
	protected ArrayList<String> tinitiation = new ArrayList<String>();
	protected ArrayList<Double> tstartprice = new ArrayList<Double>();
	protected ArrayList<Double> tceiling = new ArrayList<Double>();
	protected ArrayList<Double> tfloor = new ArrayList<Double>();
	protected ArrayList<String> econplayer = new ArrayList<String>();
	protected ArrayList<String> playerecon = new ArrayList<String>();
	protected ArrayList<Double> playerbalance = new ArrayList<Double>();
	protected ArrayList<String> koec = new ArrayList<String>();
	protected ArrayList<String> hobject = new ArrayList<String>();
	protected ArrayList<String> heconomy = new ArrayList<String>();
	protected ArrayList<Double> hprice = new ArrayList<Double>();
	protected ArrayList<Integer> hcount = new ArrayList<Integer>();
	protected HashMap<String, Integer> historyDataCount = new HashMap<String, Integer>();
	protected int sqllockthreadid;
	protected FileConfiguration items;
	protected FileConfiguration enchants;
	protected ArrayList<String> economies = new ArrayList<String>();
	
	DataFunctions() {
		hc = HyperConomy.hc;
		if (hc.useSQL()) {
			FileConfiguration config = hc.getYaml().getConfig();
			username = config.getString("config.sql-connection.username");
			password = config.getString("config.sql-connection.password");
			port = config.getInt("config.sql-connection.port");
			host = config.getString("config.sql-connection.host");
			database = config.getString("config.sql-connection.database");
		} else {
			items = hc.getYaml().getItems();
			enchants = hc.getYaml().getEnchants();
			economies.add("default");
		}
		sqlloaded = false;
		databuilt = false;
	}

	public void setName(String name, String economy, String newname) {
		name = hc.fixName(name);
		try {
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET NAME='" + newname + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
				int keyloc = tne.indexOf(name + ":" + economy);
				tname.set(keyloc, newname);
			} else {
				// not implemented
			}
		} catch (Exception e) {
			String info = "SQLFunctions setName() passed values name='" + name + "', economy='" + economy + "', value='" + newname + "'";
			new HyperError(e, info);
		}
	}

	public void setEconomy(String name, String economy, String neweconomy) {
		name = hc.fixName(name);
		try {
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET ECONOMY='" + neweconomy + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
				int keyloc = tne.indexOf(name + ":" + economy);
				teconomy.set(keyloc, neweconomy);
			} else {
				// irrelevant
			}
		} catch (Exception e) {
			String info = "SQLFunctions setEconomy() passed values name='" + name + "', economy='" + economy + "', value='" + neweconomy + "'";
			new HyperError(e, info);
		}
	}

	public void setType(String name, String economy, String newtype) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			ttype.set(keyloc, newtype);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET TYPE='" + newtype + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".information.type", newtype);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".information.type", newtype);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setType() passed values name='" + name + "', economy='" + economy + "', value='" + newtype + "'";
			new HyperError(e, info);
		}
	}

	public void setCategory(String name, String economy, String newcategory) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tcategory.set(keyloc, newcategory);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET CATEGORY='" + newcategory + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".information.category", newcategory);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".information.category", newcategory);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setCategory() passed values name='" + name + "', economy='" + economy + "', value='" + newcategory + "'";
			new HyperError(e, info);
		}
	}

	public void setMaterial(String name, String economy, String newmaterial) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tmaterial.set(keyloc, newmaterial);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET MATERIAL='" + newmaterial + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".information.material", newmaterial);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".information.name", newmaterial);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setMaterial() passed values name='" + name + "', economy='" + economy + "', value='" + newmaterial + "'";
			new HyperError(e, info);
		}
	}

	public void setId(String name, String economy, int newid) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tid.set(keyloc, newid);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET ID='" + newid + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".information.id", newid);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".information.id", newid);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setId() passed values name='" + name + "', economy='" + economy + "', value='" + newid + "'";
			new HyperError(e, info);
		}
	}

	public void setData(String name, String economy, int newdata) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tdata.set(keyloc, newdata);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET DATA='" + newdata + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".information.data", newdata);
				} else if (hc.enchantTest(name)) {
					// do nothing
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setData() passed values name='" + name + "', economy='" + economy + "', value='" + newdata + "'";
			new HyperError(e, info);
		}
	}

	public void setDurability(String name, String economy, int newdurability) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tdurability.set(keyloc, newdurability);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET DURABILITY='" + newdurability + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".information.data", newdurability);
				} else if (hc.enchantTest(name)) {
					// do nothing
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setDurability() passed values name='" + name + "', economy='" + economy + "', value='" + newdurability + "'";
			new HyperError(e, info);
		}
	}

	public void setValue(String name, String economy, double newvalue) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tvalue.set(keyloc, newvalue);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET VALUE='" + newvalue + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".value", newvalue);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".value", newvalue);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setValue() passed values name='" + name + "', economy='" + economy + "', value='" + newvalue + "'";
			new HyperError(e, info);
		}
	}

	public void setStatic(String name, String economy, String newstatic) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tstatic.set(keyloc, newstatic);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET STATIC='" + newstatic + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".price.static", Boolean.parseBoolean(newstatic));
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".price.static", Boolean.parseBoolean(newstatic));
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setStatic() passed values name='" + name + "', economy='" + economy + "', value='" + newstatic + "'";
			new HyperError(e, info);
		}
	}

	public void setStaticPrice(String name, String economy, double newstaticprice) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tstaticprice.set(keyloc, newstaticprice);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET STATICPRICE='" + newstaticprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".price.staticprice", newstaticprice);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".price.staticprice", newstaticprice);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setStaticPrice() passed values name='" + name + "', economy='" + economy + "', value='" + newstaticprice + "'";
			new HyperError(e, info);
		}
	}

	public void setStock(String name, String economy, double newstock) {
		name = hc.fixName(name);
		try {
			Calculation calc = hc.getCalculation();
			newstock = calc.round(newstock, 2);
			int keyloc = tne.indexOf(name + ":" + economy);
			tstock.set(keyloc, newstock);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET STOCK='" + newstock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".stock.stock", newstock);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".stock.stock", newstock);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setStock() passed values name='" + name + "', economy='" + economy + "', value='" + newstock + "'";
			new HyperError(e, info);
		}
	}

	public void setMedian(String name, String economy, double newmedian) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tmedian.set(keyloc, newmedian);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET MEDIAN='" + newmedian + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".stock.median", newmedian);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".stock.median", newmedian);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setMedian() passed values name='" + name + "', economy='" + economy + "', value='" + newmedian + "'";
			new HyperError(e, info);
		}
	}

	public void setInitiation(String name, String economy, String newinitiation) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tinitiation.set(keyloc, newinitiation);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET INITIATION='" + newinitiation + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setInitiation() passed values name='" + name + "', economy='" + economy + "', value='" + newinitiation + "'";
			new HyperError(e, info);
		}
	}

	public void setStartPrice(String name, String economy, double newstartprice) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tstartprice.set(keyloc, newstartprice);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET STARTPRICE='" + newstartprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".initiation.startprice", newstartprice);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".initiation.startprice", newstartprice);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setStartPrice() passed values name='" + name + "', economy='" + economy + "', value='" + newstartprice + "'";
			new HyperError(e, info);
		}
	}

	public void setCeiling(String name, String economy, double newceiling) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tceiling.set(keyloc, newceiling);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET CEILING='" + newceiling + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".price.ceiling", newceiling);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".price.ceiling", newceiling);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setCeiling() passed values name='" + name + "', economy='" + economy + "', value='" + newceiling + "'";
			new HyperError(e, info);
		}
	}

	public void setFloor(String name, String economy, double newfloor) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			tfloor.set(keyloc, newfloor);
			if (hc.useSQL()) {
				String statement = "UPDATE hyperobjects SET FLOOR='" + newfloor + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				if (hc.itemTest(name)) {
					items.set(name + ".price.floor", newfloor);
				} else if (hc.enchantTest(name)) {
					enchants.set(name + ".price.floor", newfloor);
				}
			}
		} catch (Exception e) {
			String info = "SQLFunctions setFloor() passed values name='" + name + "', economy='" + economy + "', value='" + newfloor + "'";
			new HyperError(e, info);
		}
	}

	public String getName(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tname.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getName() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return "error";
		}
	}

	public String getEconomy(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return teconomy.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getEconomy() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return "error";
		}
	}

	public String getType(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return ttype.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getType() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return "error";
		}
	}

	public String getCategory(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tcategory.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getCategory() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return "error";
		}
	}

	public String getMaterial(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tmaterial.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getMaterial() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return "error";
		}
	}

	public int getId(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tid.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getId() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return 1;
		}
	}

	public int getData(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tdata.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getData() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return 0;
		}
	}

	public int getDurability(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tdurability.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getDurability() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return 0;
		}
	}

	public double getValue(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tvalue.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getValue() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -11;
		}
	}

	public String getStatic(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tstatic.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getStatic() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return "error";
		}
	}

	public double getStaticPrice(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tstaticprice.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getStaticPrice() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -11;
		}
	}

	public double getStock(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tstock.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getStock() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -11;
		}
	}

	public double getMedian(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tmedian.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getMedian() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -11;
		}
	}

	public String getInitiation(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tinitiation.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getInitiation() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return "error";
		}
	}

	public double getStartPrice(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tstartprice.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getStartPrice() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -11;
		}
	}

	public double getCeiling(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tceiling.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getCeiling() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -11;
		}
	}

	public double getFloor(String name, String economy) {
		name = hc.fixName(name);
		try {
			int keyloc = tne.indexOf(name + ":" + economy);
			return tfloor.get(keyloc);
		} catch (Exception e) {
			String info = "SQLFunctions getFloor() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -11;
		}
	}

	public String testName(String name, String economy) {
		if (!tne.contains(name + ":" + economy)) {
			return null;
		} else {
			return name;
		}
	}

	public void load() {
		reset();
		hc.sqllockShop();
		sqllockthreadid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				SQLWrite sw = hc.getSQLWrite();
				if (hc.getSQLWrite().getBuffer().size() == 0 && !sw.initialWrite()) {
					databuilt = hc.buildData();
					sqlloaded = loadSQL();
					hc.sqlunlockShop();
					hc.getServer().getScheduler().cancelTask(sqllockthreadid);
					hc.onDataLoad();
				}
			}
		}, 0L, 10L);
	}

	private boolean loadSQL() {
		tne.clear();
		tname.clear();
		teconomy.clear();
		ttype.clear();
		tcategory.clear();
		tmaterial.clear();
		tid.clear();
		tdata.clear();
		tdurability.clear();
		tvalue.clear();
		tstatic.clear();
		tstaticprice.clear();
		tstock.clear();
		tmedian.clear();
		tinitiation.clear();
		tstartprice.clear();
		tceiling.clear();
		tfloor.clear();
		playerecon.clear();
		playerbalance.clear();
		econplayer.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		tname = getStringColumn("SELECT NAME FROM hyperobjects");
		teconomy = getStringColumn("SELECT ECONOMY FROM hyperobjects");
		ttype = getStringColumn("SELECT TYPE FROM hyperobjects");
		tcategory = getStringColumn("SELECT CATEGORY FROM hyperobjects");
		tmaterial = getStringColumn("SELECT MATERIAL FROM hyperobjects");
		tid = getIntColumn("SELECT ID FROM hyperobjects");
		tdata = getIntColumn("SELECT DATA FROM hyperobjects");
		tdurability = getIntColumn("SELECT DURABILITY FROM hyperobjects");
		tvalue = getDoubleColumn("SELECT VALUE FROM hyperobjects");
		tstatic = getStringColumn("SELECT STATIC FROM hyperobjects");
		tstaticprice = getDoubleColumn("SELECT STATICPRICE FROM hyperobjects");
		tstock = getDoubleColumn("SELECT STOCK FROM hyperobjects");
		tmedian = getDoubleColumn("SELECT MEDIAN FROM hyperobjects");
		tinitiation = getStringColumn("SELECT INITIATION FROM hyperobjects");
		tstartprice = getDoubleColumn("SELECT STARTPRICE FROM hyperobjects");
		tceiling = getDoubleColumn("SELECT CEILING FROM hyperobjects");
		tfloor = getDoubleColumn("SELECT FLOOR FROM hyperobjects");
		playerecon = getStringColumn("SELECT ECONOMY FROM hyperplayers");
		econplayer = getStringColumn("SELECT PLAYER FROM hyperplayers");
		playerbalance = getDoubleColumn("SELECT BALANCE FROM hyperplayers");
		economies = getStringColumn("SELECT ECONOMY FROM hyperobjects");
		for (int c = 0; c < tname.size(); c++) {
			tne.add(tname.get(c) + ":" + teconomy.get(c));
		}
		// History
		hobject = getStringColumn("SELECT OBJECT FROM hyperhistory");
		heconomy = getStringColumn("SELECT ECONOMY FROM hyperhistory");
		hprice = getDoubleColumn("SELECT PRICE FROM hyperhistory");
		hcount = getIntColumn("SELECT COUNT FROM hyperhistory");
		for (int c = 0; c < hobject.size(); c++) {
			koec.add(hobject.get(c) + ":" + heconomy.get(c) + ":" + hcount.get(c));
		}
		startHistoryDataCount();
		return true;
	}

	public void loadYML() {
		items = hc.getYaml().getItems();
		enchants = hc.getYaml().getEnchants();
		tne.clear();
		tname.clear();
		teconomy.clear();
		ttype.clear();
		tcategory.clear();
		tmaterial.clear();
		tid.clear();
		tdata.clear();
		tdurability.clear();
		tvalue.clear();
		tstatic.clear();
		tstaticprice.clear();
		tstock.clear();
		tmedian.clear();
		tinitiation.clear();
		tstartprice.clear();
		tceiling.clear();
		tfloor.clear();
		playerecon.clear();
		econplayer.clear();
		playerbalance.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		koec.clear();
		Iterator<String> it = hc.getYaml().getPlayers().getKeys(false).iterator();
		while (it.hasNext()) {
			String player = it.next().toString();
			econplayer.add(player);
			playerbalance.add(hc.getYaml().getPlayers().getDouble(player + ".balance"));
			playerecon.add("default");
		}
		ArrayList<String> names = hc.getNames();
		for (int i = 0; i < names.size(); i++) {
			String cname = names.get(i);
			if (hc.itemTest(cname)) {
				tname.add(cname);
				teconomy.add("default");
				if (cname != "xp") {
					ttype.add("item");
				} else {
					ttype.add("xp");
				}
				String testcat = items.getString(cname + ".information.category");
				if (testcat == null) {
					tcategory.add("unknown");
				} else {
					tcategory.add(testcat);
				}
				tmaterial.add(items.getString(cname + ".information.material"));
				tid.add(items.getInt(cname + ".information.id"));
				tdata.add(items.getInt(cname + ".information.data"));
				tdurability.add(items.getInt(cname + ".information.data"));
				tvalue.add(items.getDouble(cname + ".value"));
				tstatic.add(items.getString(cname + ".price.static"));
				tstaticprice.add(items.getDouble(cname + ".price.staticprice"));
				tstock.add(items.getDouble(cname + ".stock.stock"));
				tmedian.add(items.getDouble(cname + ".stock.median"));
				tinitiation.add(items.getString(cname + ".initiation.initiation"));
				tstartprice.add(items.getDouble(cname + ".initiation.startprice"));
				tceiling.add(items.getDouble(cname + ".price.ceiling"));
				tfloor.add(items.getDouble(cname + ".price.floor"));
			} else {
				tname.add(cname);
				teconomy.add("default");
				ttype.add("enchantment");
				String testcat = enchants.getString(cname + ".information.category");
				if (testcat == null) {
					tcategory.add("unknown");
				} else {
					tcategory.add(testcat);
				}
				tmaterial.add(enchants.getString(cname + ".information.name"));
				tid.add(enchants.getInt(cname + ".information.id"));
				tdata.add(-1);
				tdurability.add(-1);
				tvalue.add(enchants.getDouble(cname + ".value"));
				tstatic.add(enchants.getString(cname + ".price.static"));
				tstaticprice.add(enchants.getDouble(cname + ".price.staticprice"));
				tstock.add(enchants.getDouble(cname + ".stock.stock"));
				tmedian.add(enchants.getDouble(cname + ".stock.median"));
				tinitiation.add(enchants.getString(cname + ".initiation.initiation"));
				tstartprice.add(enchants.getDouble(cname + ".initiation.startprice"));
				tceiling.add(enchants.getDouble(cname + ".price.ceiling"));
				tfloor.add(enchants.getDouble(cname + ".price.floor")); 
			}
		}
		for (int c = 0; c < tname.size(); c++) {
			tne.add(tname.get(c) + ":" + "default");
		}
		FileConfiguration history = hc.getYaml().getHistory();
		for (int l = 0; l < names.size(); l++) {
			String object = history.getString(names.get(l));
			ArrayList<Double> data = new ArrayList<Double>();
			if (object != null) {
				while (object.contains(",")) {
					if (object.length() > 1) {
						double cdata = Double.parseDouble(object.substring(0, object.indexOf(",")));
						object = object.substring(object.indexOf(",") + 1, object.length());
						data.add(cdata);
					} else {
						object = "";
					}
				}
			}
			for (int m = 0; m < data.size(); m++) {
				hobject.add(names.get(l));
				heconomy.add("default");
				hprice.add(data.get(m));
				koec.add(names.get(l) + ":" + "default" + ":" + (m + 1));
			}
		}
		startHistoryDataCount();
		hc.onDataLoad();
	}

	public ArrayList<String> getKeys() {
		return tne;
	}

	// make next 3 private again later
	

	
	
	public ArrayList<String> getStringColumn(String statement) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getString(1));
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}

	public ArrayList<Double> getDoubleColumn(String statement) {
		ArrayList<Double> data = new ArrayList<Double>();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getDouble(1));
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}

	public ArrayList<Integer> getIntColumn(String statement) {
		ArrayList<Integer> data = new ArrayList<Integer>();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getInt(1));
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}

	public String getPlayerEconomy(String player) {
		player = fixpN(player);
		try {
			if (player == null) {
				return "default";
			}
			if (econplayer.indexOf(player) == -1) {
				addPlayer(player);
			}
			String econ = playerecon.get(econplayer.indexOf(player));
			return econ;
		} catch (Exception e) {
			e.printStackTrace();
			return "default";
		}
	}

	public String getPlayerEconomy(Player p) {
		try {
			if (p == null) {
				return "default";
			}
			String player = p.getName();
			player = fixpN(player);
			if (econplayer.indexOf(player) == -1) {
				addPlayer(player);
			}
			String econ = playerecon.get(econplayer.indexOf(player));
			return econ;
		} catch (Exception e) {
			e.printStackTrace();
			return "default";
		}
	}

	public void addPlayer(String player) {
		player = fixpN(player);
		if (!econplayer.contains(player)) {
			if (hc.useSQL()) {
				if (!inDatabase(player)) {
					SQLWrite sw = hc.getSQLWrite();
					sw.writeData("Insert Into hyperplayers (PLAYER, ECONOMY, BALANCE)" + " Values ('" + player + "','" + "default" + "','" + 0.0 + "')");
				}
			} else {
				FileConfiguration players = hc.getYaml().getPlayers();
				String test = players.getString(player + ".balance");
				if (test == null) {
					players.set(player + ".balance", 0);
				}
			}
			playerecon.add("default");
			econplayer.add(player);
			playerbalance.add(0.0);
		}
		
		
		
		
	}
	
	public void setPlayerEconomy(String player, String econ) {
		player = fixpN(player);
		try {
			String statement = "UPDATE hyperplayers SET ECONOMY='" + econ + "' WHERE PLAYER = '" + player + "'";
			hc.getSQLWrite().writeData(statement);
			playerecon.set(econplayer.indexOf(player), econ);
		} catch (Exception e) {
			SQLRetry sqr = new SQLRetry();
			sqr.retrySetEconomy(hc, player, econ);
		}
	}
	
	
	
	
	
	
	
	public boolean hasAccount(String name) {
		name = fixpN(name);
		boolean ha = false;
		if (econplayer.contains(name)) {
			ha = true;
		}
		return ha;
	}
	
	public Double getPlayerBalance(Player p) {
		try {
			if (p == null) {
				return 0.0;
			}
			String player = p.getName();
			player = fixpN(player);
			if (econplayer.indexOf(player) != -1) {
				return playerbalance.get(econplayer.indexOf(player));
			}
			return -9999999.0;
		} catch (Exception e) {
			new HyperError(e, "Passed player: " + p.getDisplayName());
			return -9999999.0;
		}
	}
	
	public Double getPlayerBalance(String player) {
		player = fixpN(player);
		try {
			if (econplayer.indexOf(player) != -1) {
				return playerbalance.get(econplayer.indexOf(player));
			}
			return -9999999.0;
		} catch (Exception e) {
			new HyperError(e, "Passed player: " + player);
			return -9999999.0;
		}
	}
	
	public void setPlayerBalance(String player, Double balance) {
		player = fixpN(player);
		Calculation calc = hc.getCalculation();
		balance = calc.twoDecimals(balance);
		try {
			if (hc.useSQL()) {
				String statement = "UPDATE hyperplayers SET BALANCE='" + balance + "' WHERE PLAYER = '" + player + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				hc.getYaml().getPlayers().set(player + ".balance", balance);
			}
			playerbalance.set(econplayer.indexOf(player), balance);
		} catch (Exception e) {
			//TODO
		}
	}
	
	public void setPlayerBalance(Player p, Double balance) {
		Calculation calc = hc.getCalculation();
		balance = calc.twoDecimals(balance);
		String player = p.getName();
		player = fixpN(player);
		try {
			if (hc.useSQL()) {
				String statement = "UPDATE hyperplayers SET BALANCE='" + balance + "' WHERE PLAYER = '" + player + "'";
				hc.getSQLWrite().writeData(statement);
			} else {
				hc.getYaml().getPlayers().set(player + ".balance", balance);
			}
			playerbalance.set(econplayer.indexOf(player), balance);
		} catch (Exception e) {
			//TODO
		}
	}
	
	
	public boolean createPlayerAccount(String player) {
		player = fixpN(player);
		if (!hasAccount(player)) {
			addPlayer(player);
			return true;
		} else {
			return false;
		}
	}
	
	
	public ArrayList<String> getEconPlayers() {
		return econplayer;
	}
	
	public ArrayList<Double> getPlayerBalances() {
		return playerbalance;
	}	
	
	
	
	
	
	
	
	
	


	public int countTableEntries(String table) {
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement statement = connect.createStatement();
			ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + table);
			result.next();
			int rowcount = result.getInt(1);
			result.close();
			statement.close();
			connect.close();
			return rowcount;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return 0;
		}
	}
	


	private void startHistoryDataCount() {
		if (hc.useSQL()) {
			ArrayList<String> names = hc.getNames();
			ArrayList<String> ecns = new ArrayList<String>();
			ArrayList<String> economies = getStringColumn("SELECT ECONOMY FROM hyperobjects");
			HashMap<String, String> uecons = new HashMap<String, String>();
			for (int c = 0; c < economies.size(); c++) {
				uecons.put(economies.get(c), "irrelevant");
			}
			Set<String> econs = uecons.keySet();
			Iterator<String> it = econs.iterator();
			while (it.hasNext()) {
				ecns.add(it.next());
			}
			for (int j = 0; j < ecns.size(); j++) {
				String economy = ecns.get(j);
				for (int k = 0; k < names.size(); k++) {
					String name = names.get(k);
					String match = name + ":" + economy;
					int count = 0;
					for (int i = 0; i < koec.size(); i++) {
						String subkey = koec.get(i);
						subkey = subkey.substring(0, subkey.lastIndexOf(":"));
						if (match.equalsIgnoreCase(subkey)) {
							count++;
						}
					}
					historyDataCount.put(match, count);
				}
			}
		} else {
			ArrayList<String> names = hc.getNames();
			for (int k = 0; k < names.size(); k++) {
				String name = names.get(k);
				String match = name + ":" + "default";
				int count = 0;
				for (int i = 0; i < koec.size(); i++) {
					String subkey = koec.get(i);
					subkey = subkey.substring(0, subkey.lastIndexOf(":"));
					if (match.equalsIgnoreCase(subkey)) {
						count++;
					}
				}
				historyDataCount.put(match, count);
			}
		}
	}

	public int getHistoryDataCount(String name, String economy) {
		try {
			String match = name + ":" + economy;
			return historyDataCount.get(match);
		} catch (Exception e) {
			String info = "SQLFunctions getHistoryDataCount() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -1;
		}
	}

	public void setHistoryDataCount(String name, String economy, int value) {
		try {
			String match = name + ":" + economy;
			historyDataCount.put(match, value);
		} catch (Exception e) {
			String info = "SQLFunctions setHistoryDataCount() passed values name='" + name + "', economy='" + economy + "', value='" + value + "'";
			new HyperError(e, info);
		}
	}

	public void writeHistoryData(String object, String economy, double price) {
		int count = getHistoryDataCount(object, economy) + 1;
		if (hc.useSQL()) {
			String statement = "Insert Into hyperhistory (OBJECT, ECONOMY, TIME, PRICE, COUNT)" + " Values ('" + object + "','" + economy + "', NOW() ,'" + price + "','" + count + "')";
			hc.getSQLWrite().writeData(statement);
			int daystosavehistory = hc.getYaml().getConfig().getInt("config.daystosavehistory");
			statement = "DELETE FROM hyperhistory WHERE time < DATE_SUB(NOW(), INTERVAL " + daystosavehistory + " DAY)";
			hc.getSQLWrite().writeData(statement);
			setHistoryDataCount(object, economy, getHistoryDataCount(object, economy) + 1);
		} else {
			FileConfiguration history = hc.getYaml().getHistory();
			String testhistory = history.getString(object);
			if (testhistory == null) {
				history.set(object, price + ",");
				setHistoryDataCount(object, economy, 1);
			} else {
				String historylist = history.getString(object);
				historylist = historylist + price + ",";
				// Stops the history file from growing larger than 2 weeks of
				// entries.
				int daystosavehistory = hc.getYaml().getConfig().getInt("config.daystosavehistory");
				int historylength = historylist.replaceAll("[\\d]", "").replace(".", "").length();
				if (historylength > (daystosavehistory * 24)) {
					historylist = historylist.substring(historylist.indexOf(",") + 1, historylist.length());
					historylength = historylength - 1;
				}
				history.set(object, historylist);
				setHistoryDataCount(object, economy, getHistoryDataCount(object, economy) + 1);
			}
		}
		hobject.add(object);
		heconomy.add(economy);
		hprice.add(price);
		// hcount.add(count);
		koec.add(object + ":" + economy + ":" + count);
	}

	public Double getHistoryData(String object, String economy, int count) {
		try {
			int lcount = getHistoryDataCount(object, economy);
			count = lcount - count + 1;
			String key = object + ":" + economy + ":" + count;
			int keyloc = koec.indexOf(key);
			if (keyloc == -1) {
				return -1.0;
			}
			Double hvalue = hprice.get(keyloc);
			return hvalue;
		} catch (Exception e) {
			String info = "SQLFunctions getHistoryData() passed values object='" + object + "', economy='" + economy + "', count='" + count + "'";
			new HyperError(e, info);
			return -1.0;
		}
	}

	public boolean testEconomy(String economy) {
		if (teconomy.contains(economy)) {
			return true;
		} else {
			return false;
		}
	}

	public String getUserName() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDatabase() {
		return database;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (int i = 0; i < economies.size(); i++) {
			if (!econs.contains(economies.get(i))) {
				econs.add(economies.get(i));
			}
		}
		return econs;
	}

	public boolean sqlLoaded() {
		return sqlloaded;
	}

	public boolean dataBuilt() {
		return databuilt;
	}
	
	public void reset() {
		sqlloaded = false;
		databuilt = false;
	}
	public void clearData() {
		username = null;
		password = null;
		host = null;
		database = null;
		tne.clear();
		tname.clear();
		teconomy.clear();
		ttype.clear();
		tcategory.clear();
		tmaterial.clear();
		tid.clear();
		tdata.clear();
		tdurability.clear();
		tvalue.clear();
		tstatic.clear();
		tstaticprice.clear();
		tstock.clear();
		tmedian.clear();
		tinitiation.clear();
		tstartprice.clear();
		tceiling.clear();
		tfloor.clear();
		econplayer.clear();
		playerecon.clear();
		koec.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		historyDataCount.clear();
		items = null;
		enchants = null;
		economies.clear();
	}
	
	public void clearHistory() {
		String statement = "TRUNCATE TABLE hyperhistory";
		hc.getSQLWrite().writeData(statement);
		koec.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		historyDataCount.clear();
	}
	
	
	public String fixpN(String player) {
		for (int i = 0; i < econplayer.size(); i++) {
			if (econplayer.get(i).equalsIgnoreCase(player)) {
				return econplayer.get(i);
			}
		}
		return player;
	}
	
	public boolean inDatabase(String player) {
		player = fixpN(player);
		boolean indatabase = true;
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();	
			ResultSet result = state.executeQuery("SELECT PLAYER FROM hyperplayers WHERE PLAYER = " + "'" + player + "'");
			if (!result.next()) {
				indatabase = false;
			}
            result.close();
            state.close();
            connect.close();
            return indatabase;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.admin");
			e.printStackTrace();
			return false;
		}
	}
}
