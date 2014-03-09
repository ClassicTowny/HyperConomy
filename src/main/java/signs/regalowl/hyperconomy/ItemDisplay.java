package regalowl.hyperconomy;


import java.util.HashMap;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

public class ItemDisplay {
	
	private HyperConomy hc;
	private Item item;
	private Location location;
	private String name;
	private double x;
	private double y;
	private double z;
	private String w;
	private int entityId;
	private boolean active;
	
	ItemDisplay(Location location, String name, boolean newDisplay) {
		this.hc = HyperConomy.hc;
		this.location = location;
		this.active = false;
		HyperEconomy he = hc.getEconomyManager().getEconomy("default");
		this.x = this.location.getX();
		this.y = this.location.getY();
		this.z = this.location.getZ();
		this.w = this.location.getWorld().getName();
		this.name = he.fixName(name);
		if (newDisplay) {
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("WORLD", w);
			values.put("X", x+"");
			values.put("Y", y+"");
			values.put("Z", z+"");
			values.put("HYPEROBJECT", name);
			hc.getSQLWrite().performInsert("hyperconomy_item_displays", values);
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public Item getItem() {
		return item;	
	}
	
	public Block getBaseBlock() {
		int x = (int) Math.floor(this.x);
		int y = (int) Math.floor(this.y - 1);
		int z = (int) Math.floor(this.z);
		return getWorld().getBlockAt(x, y, z);
	}
	
	public Block getItemBlock() {
		int x = (int) Math.floor(this.x);
		int y = (int) Math.floor(this.y - 1);
		int z = (int) Math.floor(this.z);
		return getWorld().getBlockAt(x, y+1, z);
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Chunk getChunk() {
		return location.getChunk();
	}
	
	public String getName() {
		return name;
	}
	
	public double getX() {
		return location.getX();
	}
	
	public double getY() {
		return location.getY();
	}
	
	public double getZ() {
		return location.getZ();
	}
	
	public World getWorld() {
		return location.getWorld();
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public HyperObject getHyperObject() {
		return hc.getEconomyManager().getDefaultEconomy().getHyperObject(name);
	}
	
	public void makeDisplay() {
		if (!location.getChunk().isLoaded()) {return;}
		HyperEconomy he = hc.getEconomyManager().getEconomy("default");
		Location l = new Location(getWorld(), x, y + 1, z);
		ItemStack dropstack = he.getHyperObject(name).getItemStack();
		dropstack.setDurability((short) he.getHyperObject(name).getDurability());
		this.item = getWorld().dropItem(l, dropstack);
		this.entityId = item.getEntityId();
		item.setVelocity(new Vector(0, 0, 0));
		item.setMetadata("HyperConomy", new FixedMetadataValue(hc, "item_display"));
		active = true;
	}
	
	public void refresh() {
		removeItem();
		makeDisplay();
	}
	

	public void removeItem() {
		getChunk().load();
		if (item != null) {
			item.remove();
		}
		clearNearbyItems(.5,true,true);
		active = false;
	}
	
	public void delete() {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("WORLD", w);
		conditions.put("X", x+"");
		conditions.put("Y", y+"");
		conditions.put("Z", z+"");
		hc.getSQLWrite().performDelete("hyperconomy_item_displays", conditions);
		clear();
	}
	
	public void clear() {
		removeItem();
		hc = null;
		location = null;
		w = null;
		name = null;
		item = null;
	}
	
	
	/**
	 *
	 * @param droppedItem
	 * @return true if the item drop event shop be blocked to prevent item stacking, false if not
	 */
	public boolean blockItemDrop(Item droppedItem) {
		if (item == null) {return false;}
		HyperItemStack dropped = new HyperItemStack(droppedItem.getItemStack());
		HyperItemStack displayItem = new HyperItemStack(item.getItemStack());
		Location l = droppedItem.getLocation();
		Material dropType = droppedItem.getItemStack().getType();
		int dropda = dropped.getDamageValue();
		double dropx = l.getX();
		double dropy = l.getY();
		double dropz = l.getZ();
		World dropworld = l.getWorld();
		Material type = item.getItemStack().getType();
		int da = displayItem.getDamageValue();
		if (type == dropType) {
			if (da == dropda) {
				if (dropworld.equals(location.getWorld())) {
					if (Math.abs(dropx - location.getX()) < 10) {
						if (Math.abs(dropz - location.getZ()) < 10) {
							if (Math.abs(dropy - location.getY()) < 30) {
								return true;
							} else {
								droppedItem.setVelocity(new Vector(0,0,0));
								Block dblock = droppedItem.getLocation().getBlock();
								while (dblock.getType().equals(Material.AIR)) {
									dblock = dblock.getRelative(BlockFace.DOWN);
								}
								if (dblock.getLocation().getY() <= (location.getBlockY() + 10)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	
	public boolean blockEntityPickup(Entity entity) {
		if (entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.PIG_ZOMBIE) {
			Location el = entity.getLocation();	
			World ew = el.getWorld();
			double ex = el.getX();
			double ez = el.getZ();
			if (w.equals(ew)) {
				if (Math.abs(ex - x) < 1000) {
					if (Math.abs(ez - z) < 1000) {
						return true;
					}
				}
			}
		}
		return false;
	}
	

	public void clearNearbyItems(double radius, boolean removeDisplays, boolean removeSelf) {
		HyperObject hi = getHyperObject();
		if (hi == null) {return;}
		Item tempItem = getWorld().dropItem(location, hi.getItemStack());
		List<Entity> nearbyEntities = tempItem.getNearbyEntities(radius, radius, radius);
		for (Entity entity : nearbyEntities) {
			if (!(entity instanceof Item)) {continue;}
			Item nearbyItem = (Item) entity;
			boolean display = false;
			for (MetadataValue cmeta: nearbyItem.getMetadata("HyperConomy")) {
				if (cmeta.asString().equalsIgnoreCase("item_display")) {
					display = true;
					break;
				}
			}
			if (nearbyItem.equals(tempItem)) {continue;}
			if (nearbyItem.equals(item) && !removeSelf) {continue;}
			if (nearbyItem.getItemStack().getType() != tempItem.getItemStack().getType()) {continue;}
			if (!removeDisplays && display) {continue;}
			HyperItemStack near = new HyperItemStack(nearbyItem.getItemStack());
			HyperItemStack displayItem = new HyperItemStack(tempItem.getItemStack());
			if (near.getDamageValue() == displayItem.getDamageValue()) {
				entity.remove();
			}
		}
		tempItem.remove();
	}

}
