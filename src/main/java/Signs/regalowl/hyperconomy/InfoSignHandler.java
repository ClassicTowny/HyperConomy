package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class InfoSignHandler implements Listener {
	
	private HyperConomy hc;
	private FileConfiguration sns;
	private ArrayList<InfoSign> infoSigns = new ArrayList<InfoSign>();
	
	private long signUpdateInterval;
	private boolean signUpdateActive;
	private int signUpdateTaskId;
	

	private int currentSign;
	
	InfoSignHandler() {
		hc = HyperConomy.hc;
		if (hc.getYaml().getConfig().getBoolean("config.use-info-signs")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
			sns = hc.getYaml().getSigns();
			signUpdateInterval = hc.getYaml().getConfig().getLong("config.signupdateinterval");
			signUpdateActive = false;
			loadSigns();
		}
	}
	
	private void loadSigns() {
		infoSigns.clear();
		Iterator<String> iterat = sns.getKeys(false).iterator();
		while (iterat.hasNext()) {
			String signKey = iterat.next().toString();
			String name = sns.getString(signKey + ".itemname");
			SignType type = SignType.fromString(sns.getString(signKey + ".type"));
			String economy = sns.getString(signKey + ".economy");
			EnchantmentClass enchantClass = EnchantmentClass.fromString(sns.getString(signKey + ".enchantclass"));
			int multiplier = sns.getInt(signKey + ".multiplier");
			if (multiplier < 1) {
				multiplier = 1;
			}
			infoSigns.add(new InfoSign(signKey, type, name, multiplier, economy, enchantClass));
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		Player p = scevent.getPlayer();
		if (p.hasPermission("hyperconomy.createsign")) {
			String[] lines = scevent.getLines();
			String economy = "default";
			DataFunctions df = hc.getDataFunctions();
			economy = df.getPlayerEconomy(p);
			String objectName = lines[0].trim() + lines[1].trim();
			objectName = hc.fixName(objectName);
			int multiplier = 1;
			try {
				multiplier = Integer.parseInt(lines[3]);
			} catch (Exception e) {
				multiplier = 1;
			}
			EnchantmentClass enchantClass = EnchantmentClass.NONE;
			if (EnchantmentClass.fromString(lines[3]) != null) {
				enchantClass = EnchantmentClass.fromString(lines[3]);
			}
			if (hc.objectTest(objectName)) {
				SignType type = SignType.fromString(lines[2]);
				if (type != null) {
					String signKey = scevent.getBlock().getWorld().getName() + "|" + scevent.getBlock().getX() + "|" + scevent.getBlock().getY() + "|" + scevent.getBlock().getZ();
					sns.set(signKey + ".itemname", objectName);
					sns.set(signKey + ".type", type.toString());
					sns.set(signKey + ".multiplier", multiplier);
					sns.set(signKey + ".economy", economy);
					sns.set(signKey + ".enchantclass", enchantClass.toString());
					infoSigns.add(new InfoSign(signKey, type, objectName, multiplier, economy, enchantClass, lines));
					updateSigns();
				}
			}
		}
	}
	
	
	public void updateSigns() {
		startSignUpdate();
	}
	
	
	
	public void startSignUpdate() {
		signUpdateActive = true;
		currentSign = 0;
		signUpdateTaskId = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				for (int i = 0; i < 4; i++) {
					if (currentSign < infoSigns.size()) {
						InfoSign infoSign = infoSigns.get(currentSign);
						if (infoSign.testData()) {
							infoSign.update();
						} else {
							infoSigns.remove(infoSign);
						}
						currentSign++;
					} else {
						stopSignUpdate();
						return;
					}
				}
			}
		}, signUpdateInterval, signUpdateInterval);
	}

	public void stopSignUpdate() {
		hc.getServer().getScheduler().cancelTask(signUpdateTaskId);
		signUpdateActive = false;
	}
	
	public void setInterval(long interval) {
		if (signUpdateActive) {
			stopSignUpdate();
			startSignUpdate();
		}
		signUpdateInterval = interval;
	}
	
	
	public long getUpdateInterval() {
		return signUpdateInterval;
	}
	
	public void reloadSigns() {
		loadSigns();
	}
	
}
