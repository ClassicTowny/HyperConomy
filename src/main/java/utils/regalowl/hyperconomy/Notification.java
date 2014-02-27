package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Notification implements TransactionListener {
	
	private HyperConomy hc;
	private ArrayList<HyperObject> notificationQueue = new ArrayList<HyperObject>();
	private String previousmessage;
	private int notifrequests;
	boolean usenotify;
	
	Notification() {
		hc = HyperConomy.hc;
		usenotify = hc.gYH().gFC("config").getBoolean("config.use-notifications");
		if (!usenotify) {return;}
		previousmessage = "";
		notifrequests = 0;
		hc.getHyperEventHandler().registerTransactionListener(this);
	}
	

	public void onTransaction(PlayerTransaction pt, TransactionResponse response) {
		if (response.successful()) {
			TransactionType tt = pt.getTransactionType();
			if (tt == TransactionType.BUY || tt == TransactionType.SELL) {
				if (pt.getHyperObject() != null) {
					notificationQueue.add(pt.getHyperObject());
					sendNotification();
				}
			}
		}
	}
	
	
	private void sendNotification() {
		usenotify = hc.gYH().gFC("config").getBoolean("config.use-notifications");
		notifrequests++;
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
				send();
				notifrequests--;
			}
		}, notifrequests * 20);
	}

	private void send() {
		HyperObject ho = notificationQueue.get(0);
		LanguageFile L = hc.getLanguageFile();
		String econ = ho.getEconomy();
		if (checkNotify(ho.getName())) {
			double cost = 0.0;
			int stock = 0;

			if (ho instanceof HyperItem) {
				HyperItem hi = (HyperItem)ho;
				stock = (int) ho.getStock();
				cost = hi.getCost(1);
				String message = L.f(L.get("SQL_NOTIFICATION"), (double) stock, cost, ho.getDisplayName(), econ);
				if (!message.equalsIgnoreCase(previousmessage)) {
					notify(message);
					previousmessage = message;
				}
			} else if (ho instanceof BasicObject) {
				BasicObject bo = (BasicObject)ho;
				stock = (int) ho.getStock();
				cost = bo.getCost(1);
				String message = L.f(L.get("SQL_NOTIFICATION"), (double) stock, cost, ho.getDisplayName(), econ);
				if (!message.equalsIgnoreCase(previousmessage)) {
					notify(message);
					previousmessage = message;
				}
			} else if (ho instanceof HyperEnchant) {
				HyperEnchant hye = (HyperEnchant)ho;
				cost = hye.getCost(EnchantmentClass.DIAMOND);
				cost = cost + ho.getPurchaseTax(cost);
				stock = (int) ho.getStock();
				String message = L.f(L.get("SQL_NOTIFICATION"), (double) stock, cost, ho.getDisplayName(), econ);
				if (!message.equalsIgnoreCase(previousmessage)) {
					notify(message);
					previousmessage = message;
				}
			} else {
				Logger log = Logger.getLogger("Minecraft");
				log.info("HyperConomy ERROR #32--Notifcation Error");
		    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #32--Notifcation Error", "hyperconomy.error");
			}
		}
		notificationQueue.remove(0);
	}
	
	
	
	private boolean checkNotify(String name) {
		boolean note = false;
		String notify = hc.gYH().gFC("config").getString("config.notify-for");
		if (notify != null && name != null) {		
			if (notify.contains("," + name + ",")) {
				note = true;
			}
			if (notify.length() >= name.length() && name.equalsIgnoreCase(notify.substring(0, name.length()))) {
				note = true;
			}
		}
		return note;
	}
	

	private void notify(String message) {
		Player[] players = Bukkit.getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			Player p = players[i];
			if (p.hasPermission("hyperconomy.notify")) {
				p.sendMessage(message);
			}
		}
	}


}
