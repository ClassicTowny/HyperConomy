package regalowl.hyperconomy.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.minecraft.ChestShopClickEvent;
import regalowl.hyperconomy.event.minecraft.FrameShopEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HyperPlayerJoinEvent;
import regalowl.hyperconomy.event.minecraft.HyperPlayerQuitEvent;
import regalowl.hyperconomy.event.minecraft.HyperSignChangeEvent;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.ChestShop;
import regalowl.hyperconomy.shop.FrameShop;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.HSign;
import regalowl.hyperconomy.util.SimpleLocation;

public class BukkitListener implements Listener {

	private BukkitConnector bc;
	
	public BukkitListener(BukkitConnector bc) {
		this.bc = bc;
	}
	

	public void unregisterAllListeners() {
		HandlerList.unregisterAll((Plugin)bc);
	}

	public void registerListeners() {
		bc.getServer().getPluginManager().registerEvents(this, bc);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent event) {
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		Location l = event.getBlock().getLocation();
		SimpleLocation sl = new SimpleLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
		HSign sign = HyperConomy.mc.getSign(sl);
		HyperSignChangeEvent se = new HyperSignChangeEvent(sign, hp);
		HyperConomy.hc.getHyperEventHandler().fireEvent(se);
		if (se.isCancelled()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		HyperConomy.hc.getHyperEventHandler().fireEvent(new HyperPlayerJoinEvent(hp));
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		HyperConomy.hc.getHyperEventHandler().fireEvent(new HyperPlayerQuitEvent(hp));
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onFrameShopSell(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		Entity entity = event.getEntity();
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (entity instanceof ItemFrame) {
				SimpleLocation l = BukkitCommon.getLocation(entity.getLocation());
				if (HyperConomy.hc.getFrameShopHandler().frameShopExists(l)) {
					event.setCancelled(true);
					FrameShop fs = HyperConomy.hc.getFrameShopHandler().getFrameShop(l);
					HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(p.getName());
					HyperConomy.hc.getHyperEventHandler().fireEvent(new FrameShopEvent(fs, hp, TransactionType.SELL));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onFrameShopBuy(PlayerInteractEntityEvent event) {
		if (event.isCancelled()) return;
		Entity entity = event.getRightClicked();
		if (entity instanceof ItemFrame) {
			ItemFrame iFrame = (ItemFrame) entity;
			if (iFrame.getItem().getType().equals(Material.MAP)) {
				SimpleLocation l = BukkitCommon.getLocation(entity.getLocation());
				if (HyperConomy.hc.getFrameShopHandler().frameShopExists(l)) {
					event.setCancelled(true);
					Player p = event.getPlayer();
					HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(p.getName());
					FrameShop fs = HyperConomy.hc.getFrameShopHandler().getFrameShop(l);
					HyperConomy.hc.getHyperEventHandler().fireEvent(new FrameShopEvent(fs, hp, TransactionType.BUY));
				}
			}
		}
	}

	
	/**
	 * Fires HBlockBreakEvents when a chest shop, or sign is broken.
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent bbevent) {
		if (bbevent.isCancelled()) {return;}
		SimpleLocation l = BukkitCommon.getLocation(bbevent.getBlock().getLocation());
		HBlockBreakEvent event = new HBlockBreakEvent(l);
		if (BukkitCommon.isChestShopSign(l)) {
			event.setChestShopBlock();
		} else if (BukkitCommon.isChestShop(l, true)) {
			event.setChestShopBlock();
		} else if (BukkitCommon.isTransactionSign(l)) {
			event.setTransactionSign();
		} else if (BukkitCommon.isInfoSign(l)) {
			event.setInfoSign();
		}
		HyperConomy.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bbevent.setCancelled(true);
	}
	
	/**
	 * Fires events for chest shop inventory clicks.
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChestShopInventoryClickEvent(InventoryClickEvent icevent) {
		if (icevent.isCancelled()) {return;}
		HumanEntity he = icevent.getWhoClicked();
		if (!(he instanceof Player)) return;
		Player p = (Player)he;
		InventoryHolder ih = icevent.getView().getTopInventory().getHolder();
		if (!(ih instanceof Chest)) return;
		Chest c = (Chest)ih;
		if (!BukkitCommon.isChestShop(BukkitCommon.getLocation(c.getLocation()), false)) return;
		ChestShop cs = new ChestShop(BukkitCommon.getLocation(c.getBlock().getLocation()));
		HyperPlayer clicker = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(p.getName());
		int slot = icevent.getRawSlot();
		SerializableItemStack clickedStack = BukkitCommon.getSerializableItemStack(icevent.getCurrentItem());
		ChestShopClickEvent event = new ChestShopClickEvent(clicker, cs, slot, clickedStack);
		if (icevent.isLeftClick()) {
			event.setLeftClick();
		} else if (icevent.isRightClick()) {
			event.setRightClick();
		} else if (icevent.isShiftClick()) {
			event.setShiftClick();
		}
		HyperConomy.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) icevent.setCancelled(true);
	}
	
	
	
}
