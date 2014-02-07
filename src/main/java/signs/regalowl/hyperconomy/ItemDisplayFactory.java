package regalowl.hyperconomy;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.MetadataValue;

public class ItemDisplayFactory implements Listener {
	
	private HyperConomy hc; 
	private int refreshthreadid;
	private final long refreshInterval = 4800L;
	//private final long refreshInterval = 100L;
	private ConcurrentHashMap<String, ItemDisplay> displays = new ConcurrentHashMap<String, ItemDisplay>();


	ItemDisplayFactory() {
		try {
			hc = HyperConomy.hc;
			if (hc.gYH().gFC("config").getBoolean("config.use-item-displays")) {
				hc.getServer().getPluginManager().registerEvents(this, hc);
				loadDisplays();
				startRefreshThread();
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	

	public void loadDisplays() {
		try {
			unloadDisplays();
			FileConfiguration disp = hc.gYH().gFC("displays");
			Iterator<String> it = disp.getKeys(false).iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				String name = disp.getString(key + ".name");
				double x = disp.getDouble(key + ".x");
				double y = disp.getDouble(key + ".y");
				double z = disp.getDouble(key + ".z");
				World w = Bukkit.getWorld(disp.getString(key + ".world"));
				Location l = new Location(w, x, y, z);
				ItemDisplay display = new ItemDisplay(key, l, name);
				String hkey = (int) Math.floor(x) + ":" + (int) Math.floor(y) + ":" + (int) Math.floor(z) + ":" + w.getName();
				displays.put(hkey, display);
				display.makeDisplay();
				display.clearNearbyItems();
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	

	public void unloadDisplays() {
		for (ItemDisplay display:displays.values()) {
			display.clearNearbyItems();
			display.clear();
		}
		displays.clear();
	}
	
	

	public void startRefreshThread() {
		refreshthreadid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				for (ItemDisplay display:displays.values()) {
					display.refresh();
				}
			}
		}, refreshInterval, refreshInterval);
	}
	
	public void cancelRefreshThread() {
		hc.getServer().getScheduler().cancelTask(refreshthreadid);
	}
	

	public boolean removeDisplay(int x, int y, int z, World w) {
		String hkey = x + ":" + y + ":" + z + ":" + w.getName();
		if (displays.containsKey(hkey)) {
			ItemDisplay display = displays.get(hkey);
			display.delete();
			displays.remove(hkey);
			return true;
		} 
		return false;
	}
	
	public boolean removeDisplay(int x, int z, World w) {
		for (String key:displays.keySet()) {
			int kx = Integer.parseInt(key.substring(0, key.indexOf(":")));
			key = key.substring(key.indexOf(":") + 1, key.length());
			int ky = Integer.parseInt(key.substring(0, key.indexOf(":")));
			key = key.substring(key.indexOf(":") + 1, key.length());
			int kz = Integer.parseInt(key.substring(0, key.indexOf(":")));
			key = key.substring(key.indexOf(":") + 1, key.length());
			String kw = key;
			if (kx == x && kz == z && kw.equalsIgnoreCase(w.getName())) {
				key = kx + ":" + ky + ":" + kz + ":" + kw;
				ItemDisplay display = displays.get(key);
				display.delete();
				displays.remove(key);
				return true;
			}
		}
		return false;
	}

	public boolean addDisplay(double x, double y, double z, World w, String name) {
		x = Math.floor(x) + .5;
		z = Math.floor(z) + .5;	
		FileConfiguration disp = hc.gYH().gFC("displays");
		Iterator<String> it = disp.getKeys(false).iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			double tx = disp.getDouble(key + ".x");
			double ty = disp.getDouble(key + ".y");
			double tz = disp.getDouble(key + ".z");
			World tw = Bukkit.getWorld(disp.getString(key + ".world"));
			if (x == tx && y == ty && z == tz && w == tw) {
				return false;
			}
		}
		Location l = new Location(w, x, y, z);
		ItemDisplay display = new ItemDisplay(l, name);
		String hkey = (int)Math.floor(x) + ":" + (int)Math.floor(y) + ":" + (int)Math.floor(z) + ":" + w.getName();
		displays.put(hkey, display);
		Chunk locChunk = l.getChunk();
		if (locChunk.isLoaded()) {
			display.makeDisplay();
			display.clearNearbyItems();
		}
		return true;
	}

	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		try {
			Item droppedItem = event.getItemDrop();
			for (ItemDisplay display:displays.values()) {
				if (!display.isActive()) {continue;}
				if (display.blockItemDrop(droppedItem)) {
					event.setCancelled(true);
					return;
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent event) {
		try {
			Chunk chunk = event.getChunk();
			if (chunk == null) {return;}
			for (ItemDisplay display:displays.values()) {
				if (display == null) {continue;}
				if (chunk.equals(display.getChunk())) {
					display.refresh();
					return;
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkUnload(ChunkUnloadEvent event) {
		try {
			Chunk chunk = event.getChunk();
			if (chunk == null) {return;}
			for (ItemDisplay display:displays.values()) {
				if (display == null) {continue;}
				if (chunk.equals(display.getChunk())) {
					display.removeItem();
					return;
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		try {
			Item item = event.getItem();
			if (!event.isCancelled()) {
				List<MetadataValue> meta = item.getMetadata("HyperConomy");
				for (MetadataValue cmeta : meta) {
					if (cmeta.asString().equalsIgnoreCase("item_display")) {
						event.setCancelled(true);
						break;
					}
				}
			}
			for (ItemDisplay display : displays.values()) {
				if (display.getEntityId() == item.getEntityId() || item.equals(display.getItem())) {
					event.setCancelled(true);
					break;
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		try {
		Block bb = event.getBlock();
		for (ItemDisplay display:displays.values()) {
			if (!display.isActive()) {continue;}
			if (display.getBaseBlock().equals(bb) || display.getItemBlock().equals(bb)) {
				event.setCancelled(true);
				display.removeItem();
				display.makeDisplay();
			}
		}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		try {
			Block bb = event.getBlock();
			for (ItemDisplay display : displays.values()) {
				if (!display.isActive()) {continue;}
				if (display.getBaseBlock().equals(bb) || display.getItemBlock().equals(bb)) {
					event.setCancelled(true);
					display.refresh();
				}
			}
			if (bb.getType().equals(Material.GRAVEL) || bb.getType().equals(Material.SAND)) {
				Block below = bb.getRelative(BlockFace.DOWN);
				while (below.getType().equals(Material.AIR)) {
					below = below.getRelative(BlockFace.DOWN);
				}
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {continue;}
					if (display.getBaseBlock().equals(below) || display.getItemBlock().equals(below)) {
						event.setCancelled(true);
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {
		try {
			if (hc.gYH().gFC("config").getBoolean("config.use-chest-shops")) {
				Location l = event.getRetractLocation();
				Block b = l.getBlock();
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {continue;}
					if (display.getBaseBlock().equals(b) || display.getItemBlock().equals(b)) {
						event.setCancelled(true);
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
		try {
			List<Block> blocks = event.getBlocks();
			for (Block cblock : blocks) {
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {continue;}
					if (display.getBaseBlock().equals(cblock) || display.getItemBlock().equals(cblock)) {
						event.setCancelled(true);
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		try {
			if (hc.gYH().gFC("config").getBoolean("config.use-chest-shops")) {
				List<Block> blocks = event.blockList();
				for (Block cblock : blocks) {
					for (ItemDisplay display : displays.values()) {
						if (!display.isActive()) {continue;}
						if (display.getBaseBlock().equals(cblock) || display.getItemBlock().equals(cblock)) {
							event.setCancelled(true);
							display.refresh();
						}
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		try {
			for (ItemDisplay display : displays.values()) {
				if (!display.isActive()) {continue;}
				if (display.blockEntityPickup(event.getEntity())) {
					event.getEntity().setCanPickupItems(false);
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	public boolean isDisplay(Item item) {
		try {
			for (ItemDisplay display : displays.values()) {
				if (item.equals(display.getItem())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			hc.gDB().writeError(e);
			return false;
		}
	}

}
