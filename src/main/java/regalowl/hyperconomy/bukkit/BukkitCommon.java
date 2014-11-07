package regalowl.hyperconomy.bukkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.display.SignType;
import regalowl.hyperconomy.inventory.HBookMeta;
import regalowl.hyperconomy.inventory.HColor;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HEnchantmentStorageMeta;
import regalowl.hyperconomy.inventory.HFireworkEffect;
import regalowl.hyperconomy.inventory.HFireworkEffectMeta;
import regalowl.hyperconomy.inventory.HFireworkMeta;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HInventoryType;
import regalowl.hyperconomy.inventory.HItemMeta;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.inventory.HLeatherArmorMeta;
import regalowl.hyperconomy.inventory.HMapMeta;
import regalowl.hyperconomy.inventory.HPotionEffect;
import regalowl.hyperconomy.inventory.HPotionMeta;
import regalowl.hyperconomy.inventory.HSkullMeta;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.shop.ChestShop;

public class BukkitCommon {

	protected static final BlockFace[] planeFaces = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
	protected static final BlockFace[] allFaces = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.DOWN, BlockFace.UP};
	
	private BukkitCommon() {}
	
	
	
	protected static Location getLocation(HLocation l) {
		return new Location(Bukkit.getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ());
	}

	protected static HLocation getLocation(Location l) {
		return new HLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
	}
	
	protected static Block getBlock(HLocation location) {
		Location l = getLocation(location);
		return l.getBlock();
	}
	
	protected static HBlock getBlock(Block b) {
		HLocation l = getLocation(b.getLocation());
		return new HBlock(l);
	}
	
	protected static Sign getSign(HLocation l) {
		BlockState bs = getBlock(l).getState();
		if (bs instanceof Sign) {
			return (Sign)bs;
		}
		return null;
	}
	

	protected static boolean isTransactionSign(HLocation l) {
		Block b = getBlock(l);
		if (b != null && b.getType().equals(Material.SIGN_POST) || b != null && b.getType().equals(Material.WALL_SIGN)) {
			Sign s = (Sign) b.getState();
			String line3 = ChatColor.stripColor(s.getLine(2)).trim();
			if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
				return true;
			}
		}
		return false;
	}

	protected static boolean isInfoSign(HLocation l) {
		Block b = getBlock(l);
		if (b != null && b.getType().equals(Material.SIGN_POST) || b != null && b.getType().equals(Material.WALL_SIGN)) {
			Sign s = (Sign) b.getState();
			String type = ChatColor.stripColor(s.getLine(2)).trim().replace(":", "").replace(" ", "");
			if (SignType.isSignType(type)) return true;
		}
		return false;
	}
	
	protected static boolean isChestShopChest(HLocation l) {
		Block b = getBlock(l);
		if (b == null) return false;
		if (!(b.getState() instanceof Chest)) return false;
		Chest chest = (Chest) b.getState();
		HLocation sl = getLocation(chest.getLocation());
		sl.setY(sl.getY() + 1);
		if (isChestShopSign(sl)) return true;
		return false;
	}
	
	protected static boolean isChestShopSign(HLocation l) {
		Block b = getBlock(l);
		if (b == null) return false;
		if (!(b instanceof Sign)) return false;
		Sign s = (Sign) b;
		String line2 = ChatColor.stripColor(s.getLine(1)).trim();
		if (!(line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]"))) return false;
		HLocation sl = new HLocation(l);
		sl.setY(sl.getY() - 1);
		Block cb = getBlock(sl);
		if (cb == null) return false;
		if (!(cb.getState() instanceof Chest)) return false;
		return true;
	}
	
	protected static boolean isChestShopSignBlock(HLocation l) {
		HSign sign = getAttachedSign(l);
		if (sign == null) return false;
		if (!isChestShopSign(sign.getLocation())) return false;
		return true;
	}
	
	protected static HSign getAttachedSign(HLocation l) {
		Block b = getBlock(l);
		if (b == null) return null;
		for (BlockFace cface : planeFaces) {
			Block block = b.getRelative(cface);
			if (block.getType().equals(Material.WALL_SIGN)) {
				org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();
				BlockFace attachedface = sign.getFacing();
				if (block.getRelative(attachedface.getOppositeFace()).equals(b)) {
					Sign s = (Sign) block.getState();
					ArrayList<String> lines = new ArrayList<String>();
					for (String li:s.getLines()) {
						lines.add(li);
					}
					return new HSign(l, lines, true);
				}
			}
		}
		return null;
	}
	
	protected static boolean isPartOfChestShop(HLocation l) {
		if (isChestShopChest(l)) return true;
		if (isChestShopSign(l)) return true;
		if (isChestShopSignBlock(l)) return true;
		return false;
	}

	protected static ChestShop getChestShop(HLocation l) {
		Block b = getBlock(l);
		if (b == null) {
			return null;
		}
		if (isChestShopChest(l)) {
			return new ChestShop(l);
		} else if (isChestShopSign(l)) {
			HLocation cl = new HLocation(l);
			cl.setY(cl.getY() - 1);
			return new ChestShop(cl);
		} else if (isChestShopSignBlock(l)) {
			HSign s = getAttachedSign(l);
			HLocation cl = s.getLocation();
			cl.setY(cl.getY() - 1);
			return new ChestShop(cl);
		}
		return null;
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected static HyperPlayer getPlayer(Player p) {
		if (p == null) return null;
		return HC.hc.getHyperPlayerManager().getHyperPlayer(p.getName());
	}
	
	@SuppressWarnings("deprecation")
	protected static Player getPlayer(HyperPlayer hp) {
		if (hp.getName() == null) return null;
		return Bukkit.getPlayer(hp.getName());
	}
	
	
	
	
	
	
	@SuppressWarnings("deprecation")
	protected static HInventory getInventory(HyperPlayer hp) {
		ArrayList<HItemStack> items = new ArrayList<HItemStack>();
		Player p = Bukkit.getPlayer(hp.getName());
		Inventory i = p.getInventory();
		int size = i.getSize();
		int heldSlot = p.getInventory().getHeldItemSlot();
		for (int c = 0; c < size; c++) {
	        items.add(getSerializableItemStack(i.getItem(c)));
		}
		HInventory si = new HInventory(items, HInventoryType.PLAYER);
		si.setOwner(hp.getName());
		si.setHeldSlot(heldSlot);
		return si;
	}

	protected static HInventory getInventory(Inventory i) {
		ArrayList<HItemStack> items = new ArrayList<HItemStack>();
		HInventoryType type = null;
		if (i.getType() == InventoryType.PLAYER) {
			type = HInventoryType.PLAYER;
		} else if (i.getType() == InventoryType.CHEST) {
			type = HInventoryType.CHEST;
		}
		int size = i.getSize();
		for (int c = 0; c < size; c++) {
	        items.add(getSerializableItemStack(i.getItem(c)));
		}
		HInventory si = new HInventory(items, type);
		if (i.getType() == InventoryType.PLAYER) {
			if (i.getHolder() instanceof Player) {
				Player p = (Player)i.getHolder();
				si.setOwner(p.getName());
				si.setHeldSlot(p.getInventory().getHeldItemSlot());
			}
		} else if (i.getType() == InventoryType.CHEST) {
			if (i.getHolder() instanceof Chest) {
				Chest c = (Chest)i.getHolder();
				si.setLocation(getLocation(c.getLocation()));
			}
		}
		return si;
	}
	

	protected static HInventory getChestInventory(HLocation l) {
		Location loc = new Location(Bukkit.getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ());
		if (loc.getBlock().getState() instanceof Chest) {
			Chest chest = (Chest)loc.getBlock().getState();
			Inventory i = chest.getInventory();
			ArrayList<HItemStack> items = new ArrayList<HItemStack>();
			int size = i.getSize();
			for (int c = 0; c < size; c++) {
		        items.add(getSerializableItemStack(i.getItem(c)));
			}
			HInventory si = new HInventory(items, HInventoryType.CHEST);
			si.setLocation(l);
			return si;
		}
		return null;
	}
	
	
	@SuppressWarnings("deprecation")
	protected static void setInventory(HInventory inventory) {
		if (inventory.getInventoryType() == HInventoryType.PLAYER) {
			HyperPlayer hp = inventory.getHyperPlayer();
			Player p = Bukkit.getPlayer(hp.getName());
			p.getInventory().setHeldItemSlot(inventory.getHeldSlot());
			ArrayList<HItemStack> currentInventory = getInventory(hp).getItems();
			ArrayList<HItemStack> newInventory = inventory.getItems();
			if (currentInventory.size() != newInventory.size()) return;
			Inventory inv = p.getInventory();
			for (int i = 0; i < newInventory.size(); i++) {
				if (newInventory.get(i).equals(currentInventory.get(i))) continue;
				ItemStack is = getItemStack(newInventory.get(i));
				if (is == null) {
					inv.clear(i);
				} else {
					inv.setItem(i, is);
				}
			}
			p.updateInventory();
		} else if (inventory.getInventoryType() == HInventoryType.CHEST) {
			HLocation l = inventory.getLocation();
			Location loc = new Location(Bukkit.getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ());
			if (loc.getBlock() instanceof Chest) {
				Chest chest = (Chest)loc.getBlock();
				ArrayList<HItemStack> currentInventory = getChestInventory(l).getItems();
				ArrayList<HItemStack> newInventory = inventory.getItems();
				Inventory chestInv = chest.getInventory();
				for (int i = 0; i < newInventory.size(); i++) {
					if (newInventory.get(i).equals(currentInventory.get(i))) continue;
					ItemStack is = getItemStack(newInventory.get(i));
					if (is == null) {
						chestInv.clear(i);
					} else {
						chestInv.setItem(i, is);
					}
				}
			}
		}
	}
	
	//TODO make protected
	@SuppressWarnings("deprecation")
	public static HItemStack getSerializableItemStack(ItemStack s) {
		if (s == null) return new HItemStack();
		boolean isBlank = (s.getType() == Material.AIR) ? true:false;
        String material = s.getType().toString();
        short durability = s.getDurability();
        byte data = s.getData().getData(); 
        int amount = s.getAmount();
        int maxStackSize = s.getType().getMaxStackSize();
        int maxDurability = s.getType().getMaxDurability();
        HItemStack sis = new HItemStack(new HItemMeta("", new ArrayList<String>(), new ArrayList<HEnchantment>()), material, durability, data, amount, maxStackSize, maxDurability);
        if (isBlank) sis.setBlank();
        if (s.hasItemMeta()) {
        	ItemMeta im = s.getItemMeta();
            String displayName = (im.getDisplayName() == null) ? "":im.getDisplayName();
            ArrayList<String> lore = new ArrayList<String>();
            if (im.getLore() != null) lore.addAll(im.getLore());
            ArrayList<HEnchantment> enchantments = new ArrayList<HEnchantment>();
            Map<Enchantment, Integer> enchants = im.getEnchants();
    		Iterator<Enchantment> it = enchants.keySet().iterator();
    		while (it.hasNext()) {
    			Enchantment e = it.next();
    			int lvl = enchants.get(e);
    			enchantments.add(new HEnchantment(e.getName(), lvl));
    		}
    		HItemMeta itemMeta = null;
        	if (im instanceof EnchantmentStorageMeta) {
        		EnchantmentStorageMeta sItemMeta = (EnchantmentStorageMeta)im;
        		ArrayList<HEnchantment> storedEnchantments = new ArrayList<HEnchantment>();
    			Map<Enchantment, Integer> stored = sItemMeta.getStoredEnchants();
    			Iterator<Enchantment> iter = stored.keySet().iterator();
    			while (iter.hasNext()) {
    				Enchantment e = iter.next();
    				int lvl = stored.get(e);
    				storedEnchantments.add(new HEnchantment(e.getName(), lvl));
    			}
        		itemMeta = new HEnchantmentStorageMeta(displayName, lore, enchantments, storedEnchantments);
        	} else if (im instanceof BookMeta) {
        		BookMeta sItemMeta = (BookMeta)im;
        		ArrayList<String> pages = new ArrayList<String>();
        		if (sItemMeta.getPages() != null) pages.addAll(sItemMeta.getPages());
        		itemMeta = new HBookMeta(displayName, lore, enchantments, sItemMeta.getAuthor(), pages, sItemMeta.getTitle());
        	} else if (im instanceof FireworkEffectMeta) {
        		FireworkEffectMeta sItemMeta = (FireworkEffectMeta)im;
        		FireworkEffect fe = sItemMeta.getEffect();
        		ArrayList<HColor> colors = new ArrayList<HColor>();
        		ArrayList<HColor> fadeColors = new ArrayList<HColor>();
        		if (fe != null) {
	        		for (Color color:fe.getColors()) {
	        			colors.add(new HColor(color.getRed(), color.getGreen(), color.getBlue()));
	        		}
	        		for (Color color:fe.getFadeColors()) {
	        			fadeColors.add(new HColor(color.getRed(), color.getGreen(), color.getBlue()));
	        		}
        		}
        		HFireworkEffect sfe = new HFireworkEffect(colors, fadeColors, fe.getType().toString(), fe.hasFlicker(), fe.hasTrail());
        		itemMeta = new HFireworkEffectMeta(displayName, lore, enchantments, sfe);
        	} else if (im instanceof FireworkMeta) {
        		FireworkMeta sItemMeta = (FireworkMeta)im;
        		ArrayList<HFireworkEffect> fireworkEffects = new ArrayList<HFireworkEffect>();
        		if (sItemMeta.getEffects() != null) {
	    			for (FireworkEffect fe:sItemMeta.getEffects()) {
		        		ArrayList<HColor> colors = new ArrayList<HColor>();
		        		ArrayList<HColor> fadeColors = new ArrayList<HColor>();
		        		for (Color color:fe.getColors()) {
		        			colors.add(new HColor(color.getRed(), color.getGreen(), color.getBlue()));
		        		}
		        		for (Color color:fe.getFadeColors()) {
		        			fadeColors.add(new HColor(color.getRed(), color.getGreen(), color.getBlue()));
		        		}
		        		fireworkEffects.add(new HFireworkEffect(colors, fadeColors, fe.getType().toString(), fe.hasFlicker(), fe.hasTrail()));
	    			}
        		}
        		itemMeta = new HFireworkMeta(displayName, lore, enchantments, fireworkEffects, sItemMeta.getPower());
        	} else if (im instanceof LeatherArmorMeta) {
        		LeatherArmorMeta sItemMeta = (LeatherArmorMeta)im;
        		Color color = sItemMeta.getColor();
        		itemMeta = new HLeatherArmorMeta(displayName, lore, enchantments, new HColor(color.getRed(), color.getGreen(), color.getBlue()));
        	} else if (im instanceof PotionMeta) {
        		PotionMeta sItemMeta = (PotionMeta)im;
        		ArrayList<HPotionEffect> potionEffects = new ArrayList<HPotionEffect>();
        		if (sItemMeta.getCustomEffects() != null) {
	        		for (PotionEffect pe:sItemMeta.getCustomEffects()) {
	        			potionEffects.add(new HPotionEffect(pe.getType().toString(), pe.getAmplifier(), pe.getDuration(), pe.isAmbient()));
	        		}
        		}
        		itemMeta = new HPotionMeta(displayName, lore, enchantments, potionEffects);
        	} else if (im instanceof SkullMeta) {
        		SkullMeta sItemMeta = (SkullMeta)im;
        		itemMeta = new HSkullMeta(displayName, lore, enchantments, sItemMeta.getOwner());
        	} else if (im instanceof MapMeta) {
        		MapMeta sItemMeta = (MapMeta)im;
        		itemMeta = new HMapMeta(displayName, lore, enchantments, sItemMeta.isScaling());
        	} else {
        		itemMeta = new HItemMeta(displayName, lore, enchantments);
        	}
        	sis.setHItemMeta(itemMeta);
        }   
        return sis;
	}
	//TODO make protected
	@SuppressWarnings("deprecation")
	public static ItemStack getItemStack(HItemStack sis) {
		if (sis == null || sis.isBlank()) return null;
        ItemStack item = new ItemStack(Material.matchMaterial(sis.getMaterial()));
        item.setAmount(sis.getAmount());
        item.setDurability(sis.getDurability());
        item.getData().setData(sis.getData());
        if (sis.getItemMeta() != null) {
        	HItemMeta sim = sis.getItemMeta();
        	ItemMeta itemMeta = item.getItemMeta();
        	itemMeta.setDisplayName(sim.getDisplayName());
        	itemMeta.setLore(sim.getLore());
    		for (HEnchantment se:sim.getEnchantments()) {
    			itemMeta.addEnchant(Enchantment.getByName(se.getEnchantmentName()), se.getLvl(), true);
    		}
        	if (sim instanceof HEnchantmentStorageMeta) {
        		HEnchantmentStorageMeta sItemMeta = (HEnchantmentStorageMeta)sim;
        		EnchantmentStorageMeta esm = (EnchantmentStorageMeta)itemMeta;
        		for (HEnchantment se:sItemMeta.getEnchantments()) {
        			esm.addStoredEnchant(Enchantment.getByName(se.getEnchantmentName()), se.getLvl(), true);
        		}
        	} else if (sim instanceof HBookMeta) {
        		HBookMeta sItemMeta = (HBookMeta)sim;
        		BookMeta bm = (BookMeta)itemMeta;
        		bm.setPages(sItemMeta.getPages());
        		bm.setAuthor(sItemMeta.getAuthor());
        		bm.setTitle(sItemMeta.getTitle());
        	} else if (sim instanceof HFireworkEffectMeta) {
        		HFireworkEffectMeta sItemMeta = (HFireworkEffectMeta)sim;
        		FireworkEffectMeta fem = (FireworkEffectMeta)itemMeta;
        		HFireworkEffect sfe = sItemMeta.getEffect();
    			Builder fb = FireworkEffect.builder();
    			for (HColor c:sfe.getColors()) {
    				fb.withColor(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
    			}
    			for (HColor c:sfe.getFadeColors()) {
    				fb.withFade(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
    			}
    			fb.with(FireworkEffect.Type.valueOf(sfe.getType()));
    			fb.flicker(sfe.hasFlicker());
    			fb.trail(sfe.hasTrail());
    			fem.setEffect(fb.build());
        	} else if (sim instanceof HFireworkMeta) {
        		HFireworkMeta sItemMeta = (HFireworkMeta)sim;
        		FireworkMeta fm = (FireworkMeta)itemMeta;
        		for (HFireworkEffect sfe:sItemMeta.getEffects()) {
        			Builder fb = FireworkEffect.builder();
        			for (HColor c:sfe.getColors()) {
        				fb.withColor(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
        			}
        			for (HColor c:sfe.getFadeColors()) {
        				fb.withFade(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
        			}
        			fb.with(FireworkEffect.Type.valueOf(sfe.getType()));
        			fb.flicker(sfe.hasFlicker());
        			fb.trail(sfe.hasTrail());
        			fm.addEffect(fb.build());
        		}
        		fm.setPower(sItemMeta.getPower());
        	} else if (sim instanceof HLeatherArmorMeta) {
        		HLeatherArmorMeta sItemMeta = (HLeatherArmorMeta)sim;
        		LeatherArmorMeta lam = (LeatherArmorMeta)itemMeta;
        		HColor sc = sItemMeta.getColor();
        		lam.setColor(Color.fromRGB(sc.getRed(), sc.getGreen(), sc.getBlue()));
        	} else if (sim instanceof HPotionMeta) {
        		HPotionMeta sItemMeta = (HPotionMeta)sim;
        		PotionMeta pm = (PotionMeta)itemMeta;
        		for (HPotionEffect spe:sItemMeta.getPotionEffects()) {
        			PotionEffect pe = new PotionEffect(PotionEffectType.getByName(spe.getType()), spe.getDuration(), spe.getAmplifier(), spe.isAmbient());
        			pm.addCustomEffect(pe, true);
        		}
        	} else if (sim instanceof HSkullMeta) {
        		HSkullMeta sItemMeta = (HSkullMeta)sim;
        		SkullMeta sm = (SkullMeta)itemMeta;
        		sm.setOwner(sItemMeta.getOwner());
        	} else if (sim instanceof HMapMeta) {
        		HMapMeta sItemMeta = (HMapMeta)sim;
        		MapMeta mm = (MapMeta)itemMeta;
        		mm.setScaling(sItemMeta.isScaling());
        	}
        	item.setItemMeta(itemMeta);
        }
        return item;
	}
	
	
	
	protected static String applyColor(String message) {
		message = message.replace("&0", ChatColor.BLACK + "");
		message = message.replace("&1", ChatColor.DARK_BLUE + "");
		message = message.replace("&2", ChatColor.DARK_GREEN + "");
		message = message.replace("&3", ChatColor.DARK_AQUA + "");
		message = message.replace("&4", ChatColor.DARK_RED + "");
		message = message.replace("&5", ChatColor.DARK_PURPLE + "");
		message = message.replace("&6", ChatColor.GOLD + "");
		message = message.replace("&7", ChatColor.GRAY + "");
		message = message.replace("&8", ChatColor.DARK_GRAY + "");
		message = message.replace("&9", ChatColor.BLUE + "");
		message = message.replace("&a", ChatColor.GREEN + "");
		message = message.replace("&b", ChatColor.AQUA + "");
		message = message.replace("&c", ChatColor.RED + "");
		message = message.replace("&d", ChatColor.LIGHT_PURPLE + "");
		message = message.replace("&e", ChatColor.YELLOW + "");
		message = message.replace("&f", ChatColor.WHITE + "");
		message = message.replace("&k", ChatColor.MAGIC + "");
		message = message.replace("&l", ChatColor.BOLD + "");
		message = message.replace("&m", ChatColor.STRIKETHROUGH + "");
		message = message.replace("&n", ChatColor.UNDERLINE + "");
		message = message.replace("&o", ChatColor.ITALIC + "");
		message = message.replace("&r", ChatColor.RESET + "");
		return message;
	}
	
	
	
	protected static Item getItem(HItem i) {
		Location l = getLocation(i.getLocation());
		for (Entity e:l.getWorld().getEntities()) {
			if (e instanceof Item) {
				Item item = (Item)e;
				if (item.getEntityId() == i.getId()) {
					return item;
				}
			}
		}
		return null;
	}
	
	protected static HItem getItem(Item i) {
		HLocation l = getLocation(i.getLocation());
		HItemStack stack = getSerializableItemStack(i.getItemStack());
		return new HItem(l, i.getEntityId(), stack);
	}
	
	protected static boolean chunkContainsLocation(HLocation l, Chunk c) {
		Location loc = getLocation(l);
		if (loc.getChunk().equals(c)) return true;
		return false;
	}
	
	
}
