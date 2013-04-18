package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
	
	private HyperConomy hc;
	private LanguageFile L;
	private FileConfiguration shopFile;

	
	PlayerShop(String name, String economy, HyperPlayer owner) {
		this.name = name;
		this.economy = economy;
		this.owner = owner;
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
		shopFile = hc.getYaml().getShops();
		shopFile.set(name + ".economy", economy);
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
		return true;
	}
	
	
	public void addAllObjects() {
		FileConfiguration sh = hc.getYaml().getShops();
		sh.set(name + ".unavailable", null);
	}
	
	public void removeAllObjects() {
		FileConfiguration sh = hc.getYaml().getShops();
		sh.set(name + ".unavailable", "all");
	}
	
	public void addObjects(ArrayList<String> objects) {
		DataHandler dh = hc.getDataFunctions();
		FileConfiguration sh = hc.getYaml().getShops();
		SerializeArrayList sal = new SerializeArrayList();
		ArrayList<String> unavailable = sal.stringToArray(sh.getString(name + ".unavailable"));
		if (unavailable.size() == 1 && unavailable.get(0).equalsIgnoreCase("all")) {
			unavailable = dh.getNames();
		}
		for (String object:objects) {
			if (unavailable.contains(dh.fixName(object))) {
				unavailable.remove(object);
			}
		}
		sh.set(name + ".unavailable", sal.stringArrayToString(unavailable));
	}
	
	public void removeObjects(ArrayList<String> objects) {
		DataHandler dh = hc.getDataFunctions();
		FileConfiguration sh = hc.getYaml().getShops();
		SerializeArrayList sal = new SerializeArrayList();
		ArrayList<String> unavailable = sal.stringToArray(sh.getString(name + ".unavailable"));
		if (unavailable.size() == 1 && unavailable.get(0).equalsIgnoreCase("all")) {
			return;
		}
		for (String object:objects) {
			if (!unavailable.contains(dh.fixName(object))) {
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
}
