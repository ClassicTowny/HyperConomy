package regalowl.hyperconomy.command;


import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectStatus;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.util.LanguageFile;




public class Manageshop implements CommandExecutor {
	
	private HashMap<HyperPlayer, PlayerShop> currentShop = new HashMap<HyperPlayer, PlayerShop>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);
			return true;
		}
		if (!hc.gYH().gFC("config").getBoolean("enable-feature.player-shops")) {
			sender.sendMessage(L.get("PLAYERSHOPS_DISABLED"));
			return true;
		}
		int maxVolume = hc.gYH().gFC("config").getInt("shop.max-player-shop-volume");
		DataManager em = hc.getDataManager();
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if (player == null) {return true;}
		HyperPlayer hp = em.getHyperPlayer(player.getName());
		HyperEconomy he = em.getEconomy(hp.getEconomy());
		if (em.inAnyShop(player)) {
			Shop s = em.getShop(player);
			if (s instanceof PlayerShop) {
				PlayerShop ps = (PlayerShop)s;
				if (ps.getOwner().equals(hp) || ps.isAllowed(hp) || player.hasPermission("hyperconomy.admin")) {
					currentShop.put(hp, ps);
				}
			}
		}
		PlayerShop cps = null;
		if (currentShop.containsKey(hp)) {
			cps = currentShop.get(hp);
			if (!(cps.getOwner() == hp) && !cps.isAllowed(hp) && !player.hasPermission("hyperconomy.admin")) {
				currentShop.remove(hp);
				cps = null;
			}
		}
		if (args.length == 0) {
			player.sendMessage(L.get("MANAGESHOP_HELP"));
			if (cps != null) {
				player.sendMessage(L.f(L.get("MANAGESHOP_HELP2"), cps.getName()));
				player.sendMessage(L.f(L.get("MANAGESHOP_HELP3"), cps.getName()) + " " + ChatColor.AQUA + cps.getOwner().getName());
				player.sendMessage(L.get("MANAGESHOP_HELP4") + " " + ChatColor.AQUA +  hc.getDataBukkit().getCommonFunctions().implode(cps.getAllowed(), ","));
			} else {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("sel")) {
			if (args.length == 1) {
				player.sendMessage(L.get("MANAGESHOP_SELECT_HELP"));
				return true;
			}
			if (!em.shopExists(args[1])) {
				player.sendMessage(L.get("SHOP_NOT_EXIST"));
				return true;
			}
			Shop s = em.getShop(args[1]);
			if (!(s instanceof PlayerShop)) {
				player.sendMessage(L.get("ONLY_PLAYER_SHOPS"));
				return true;
			}
			PlayerShop ps = (PlayerShop)s;
			if ((!(ps.getOwner().equals(hp) || ps.isAllowed(hp))) && !player.hasPermission("hyperconomy.admin")) {
				player.sendMessage(L.get("ONLY_EDIT_OWN_SHOPS"));
				return true;
			}
			currentShop.put(hp, ps);
			player.sendMessage(L.get("SHOP_SELECTED"));
		} else if (args[0].equalsIgnoreCase("setstock") && player.hasPermission("hyperconomy.admin")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			HyperObject ho = null;
			double amount = 0.0;
			if (args.length == 3) {
				ho = hp.getHyperEconomy().getHyperObject(args[1]);
				try {
					amount = Double.parseDouble(args[2]);
				} catch (Exception e) {
					player.sendMessage(L.get("MANAGESHOP_SETSHOP_HELP"));
					return true;
				}
			} else {
				player.sendMessage(L.get("MANAGESHOP_SETSHOP_HELP"));
				return true;
			}
			if (ho == null) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			HyperObject ho2 = he.getHyperObject(ho.getName(), cps);
			if (ho2.isShopObject()) {
				ho2.setStock(amount);
				player.sendMessage(L.f(L.get("STOCK_SET"), ho2.getName()));
				return true;
			} else {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
			}
		} else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("a")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			int amount = 1;
			HyperObject ho = null;
			if (args.length == 1) {
				ItemStack selectedItem = player.getItemInHand();
				ho = hp.getHyperEconomy().getHyperObject(selectedItem);
			} else if (args.length == 2) {
				try {
					amount = Integer.parseInt(args[1]);
				} catch (Exception e) {
					player.sendMessage(L.get("MANAGESHOP_ADD_HELP"));
					return true;
				}
				ItemStack selectedItem = player.getItemInHand();
				ho = hp.getHyperEconomy().getHyperObject(selectedItem);
			} else if (args.length == 3) {
				ho = hp.getHyperEconomy().getHyperObject(args[1]);
				try {
					amount = Integer.parseInt(args[2]);
				} catch (Exception e) {
					player.sendMessage(L.get("MANAGESHOP_ADD_HELP"));
					return true;
				}
			} else {
				player.sendMessage(L.get("MANAGESHOP_ADD_HELP"));
				return true;
			}

	

			if (ho == null) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			
			HyperObject ho2 = he.getHyperObject(ho.getName(), cps);
			int globalMaxStock = hc.gYH().gFC("config").getInt("shop.max-stock-per-item-in-playershops");
			if (ho2.getStock() + amount > globalMaxStock) {
				player.sendMessage(L.get("CANT_ADD_MORE_STOCK"));
				return true;
			}
			if (ho2.getType() == HyperObjectType.ITEM) {
				int count = ho2.count(player.getInventory());
				if (amount > count) {
					amount = count;
				}
				if (amount <= 0) {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return true;
				}
				double amountRemoved = ho2.remove(amount, player.getInventory());
				ho2.setStock(ho2.getStock() + amountRemoved);
				player.sendMessage(L.get("STOCK_ADDED"));
				return true;
			} else if (ho2.getType() == HyperObjectType.ENCHANTMENT) {
				if (amount < 1) {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return true;
				}
				double removed = ho2.removeEnchantment(player.getItemInHand());
				if (removed > 0) {
					ho2.setStock(ho2.getStock() + removed);
				} else {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
				}
				return true;
			} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
				if (amount < 1) {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return true;
				}
				int count = hp.getTotalXpPoints();
				if (amount > count) {
					amount = count;
				}
				double rcount = ho2.remove(amount, hp);
				if (rcount > 0) {
					ho2.setStock(ho2.getStock() + amount);
				} else {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
				}
			} else {
				hc.getDataBukkit().writeError("Setting PlayerShopObject stock failed in /ms add.");
				return true;
			}	
		} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length < 2) {
				player.sendMessage(L.get("MANAGESHOP_REMOVE_HELP"));
				return true;
			}
			int amount = 1;
			if (args.length == 3) {
				try {
					amount = Integer.parseInt(args[2]);
				} catch (Exception e) {}
			}

			
			HyperObject ho = he.getHyperObject(args[1], cps);
			if (ho == null) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			if (ho.getType() == HyperObjectType.ITEM) {
				if (ho.getStock() < amount) {
					amount = (int) Math.floor(ho.getStock());
				}
				if (amount <= 0.0) {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return true;
				}
				int space = ho.getAvailableSpace(player.getInventory());
				if (space < amount) {
					player.sendMessage(L.get("NOT_ENOUGH_SPACE"));
					return true;
				}
				ho.add(amount, player.getInventory());
				ho.setStock(ho.getStock() - amount);
				player.sendMessage(L.get("STOCK_REMOVED"));
				return true;
			} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				if (ho.getStock() < 1) {
					amount = (int) Math.floor(ho.getStock());
				}
				if (amount < 1) {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return true;
				}
				double amountAdded = ho.addEnchantment(player.getItemInHand());
				if (amountAdded > 0) {
					ho.setStock(ho.getStock() - amountAdded);
				} else {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
				}
			} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
				if (ho.getStock() < amount) {
					amount = (int) Math.floor(ho.getStock());
				}
				if (amount < 1) {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return true;
				}
				boolean success = hp.addXp(amount);
				if (success) {
					ho.setStock(ho.getStock() - amount);
				} else {
					player.sendMessage(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
				}
			} else {
				hc.getDataBukkit().writeError("Setting PlayerShopObject stock failed in /ms remove.");
				return true;
			}
		} else if ((args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c"))) {
			if (!player.hasPermission("hyperconomy.playershop.create")) {
				player.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
				return true;
			}
			if (args.length == 1) {
				player.sendMessage(L.get("MANAGESHOP_CREATE_HELP"));
				return true;
			}
			String name = args[1].replace(".", "").replace(":", "");
			if (em.shopExists(name)){
				player.sendMessage(L.get("SHOP_ALREADY_EXISTS"));
				return true;
			}
			int maxShops = hc.gYH().gFC("config").getInt("shop.max-player-shops-per-player");
			if (em.getShops(hp).size() > maxShops && !player.hasPermission("hyperconomy.admin")) {
				player.sendMessage(L.f(L.get("SHOP_LIMIT_REACHED"), maxShops));
				return true;
			}
			int radius = 2;
			if (args.length > 2) {
				try {
					radius = Integer.parseInt(args[2]);
				} catch (Exception e) {
					//continue
				}
			}
			Location l = player.getLocation();
			Location p1 = new Location(player.getWorld(), l.getBlockX() - radius, l.getBlockY() - radius, l.getBlockZ() - radius);
			Location p2 = new Location(player.getWorld(), l.getBlockX() + radius, l.getBlockY() + radius, l.getBlockZ() + radius);
			PlayerShop newShop = new PlayerShop(name, hp.getEconomy(), hp, p1, p2);
			if (newShop.getVolume() > maxVolume) {
				player.sendMessage(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				newShop.deleteShop();
				return true;
			}
			for (Shop s:em.getShops()) {
				if (newShop.intersectsShop(s, 10000)) {
					player.sendMessage(L.f(L.get("SHOP_INTERSECTS_SHOP"), s.getDisplayName()));
					newShop.deleteShop();
					return true;
				}
			}
			for (HyperObject ho:he.getHyperObjects(newShop)) {
				if (ho.isShopObject()) {
					ho.setStatus(HyperObjectStatus.NONE);
				}
			}
			em.addShop(newShop);
			player.sendMessage(L.get("SHOP_CREATED"));
		} else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (cps.isEmpty()) {
				cps.deleteShop();
				currentShop.remove(hp);
				player.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), cps.getName()));
				return true;
			} else {
				if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
					cps.deleteShop();
					currentShop.remove(hp);
					player.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), cps.getName()));
					return true;
				} else {
					player.sendMessage(L.get("MANAGESHOP_DELETE_CONFIRM"));
					return true;
				}
			}
			
			
		} else if (args[0].equalsIgnoreCase("set1") || args[0].equalsIgnoreCase("s1")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			Location priorLoc = cps.getLocation1();
			cps.setPoint1(player.getLocation());
			if (cps.getVolume() > maxVolume) {
				player.sendMessage(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				cps.setPoint1(priorLoc);
				return true;
			}
			for (Shop s:em.getShops()) {
				if (cps.intersectsShop(s, 10000)) {
					if (cps.equals(s)) {continue;}
					player.sendMessage(L.f(L.get("SHOP_INTERSECTS_SHOP"), s.getDisplayName()));
					cps.setPoint1(priorLoc);
					return true;
				}
			}
			player.sendMessage(L.get("P1_SET"));
		} else if (args[0].equalsIgnoreCase("set2") || args[0].equalsIgnoreCase("s2")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			Location priorLoc = cps.getLocation2();
			cps.setPoint2(player.getLocation());
			if (cps.getVolume() > maxVolume) {
				player.sendMessage(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				cps.setPoint2(priorLoc);
				return true;
			}
			for (Shop s:em.getShops()) {
				if (cps.intersectsShop(s, 10000)) {
					if (cps.equals(s)) {continue;}
					player.sendMessage(L.f(L.get("SHOP_INTERSECTS_SHOP"), s.getDisplayName()));
					cps.setPoint2(priorLoc);
					return true;
				}
			}
			player.sendMessage(L.get("P2_SET"));
		} else if (args[0].equalsIgnoreCase("price") || args[0].equalsIgnoreCase("p")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 3) {
				player.sendMessage(L.get("MANAGESHOP_PRICE_HELP"));
				return true;
			}
			double price = 0.0;
			try {
				price = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage(L.get("MANAGESHOP_PRICE_HELP"));
				return true;
			}
			if (!he.objectTest(args[1])) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			HyperObject ho = he.getHyperObject(args[1], cps);
			if (ho.isShopObject()) {
				ho.setBuyPrice(price);
				ho.setSellPrice(price);
				player.sendMessage(L.get("PRICE_SET"));
				return true;
			} else {
				hc.getDataBukkit().writeError("Setting PlayerShopObject price failed.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("buyprice") || args[0].equalsIgnoreCase("bp")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 3) {
				player.sendMessage(L.get("MANAGESHOP_PRICE_HELP"));
				return true;
			}
			double price = 0.0;
			try {
				price = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage(L.get("MANAGESHOP_PRICE_HELP"));
				return true;
			}
			if (!he.objectTest(args[1])) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			HyperObject ho = he.getHyperObject(args[1], cps);
			if (ho.isShopObject()) {
				ho.setBuyPrice(price);
				player.sendMessage(L.get("PRICE_SET"));
				return true;
			} else {
				hc.getDataBukkit().writeError("Setting PlayerShopObject buyprice failed.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("sellprice") || args[0].equalsIgnoreCase("sp")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 3) {
				player.sendMessage(L.get("MANAGESHOP_PRICE_HELP"));
				return true;
			}
			double price = 0.0;
			try {
				price = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage(L.get("MANAGESHOP_PRICE_HELP"));
				return true;
			}
			if (!he.objectTest(args[1])) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			HyperObject ho = he.getHyperObject(args[1], cps);
			if (ho.isShopObject()) {
				ho.setSellPrice(price);
				player.sendMessage(L.get("PRICE_SET"));
				return true;
			} else {
				hc.getDataBukkit().writeError("Setting PlayerShopObject sellprice failed.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("maxstock") || args[0].equalsIgnoreCase("ms")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 3) {
				player.sendMessage(L.get("MANAGESHOP_MAXSTOCK_HELP"));
				return true;
			}
			int maxStock = 1000000;
			try {
				maxStock = Integer.parseInt(args[2]);
			} catch (Exception e) {
				player.sendMessage(L.get("MANAGESHOP_MAXSTOCK_HELP"));
				return true;
			}
			if (!he.objectTest(args[1])) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			HyperObject ho = he.getHyperObject(args[1], cps);
			if (ho.isShopObject()) {
				ho.setMaxStock(maxStock);
				player.sendMessage(L.get("MAXSTOCK_SET"));
				return true;
			} else {
				hc.getDataBukkit().writeError("Setting PlayerShopObject max stock failed.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("s")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 3) {
				player.sendMessage(L.get("MANAGESHOP_STATUS_HELP"));
				return true;
			}
			HyperObjectStatus status = HyperObjectStatus.fromString(args[2]);
			if (status == HyperObjectStatus.NONE && !args[2].equalsIgnoreCase("none")) {
				player.sendMessage(L.get("INVALID_STATUS"));
				return true;
			}
			if (!he.objectTest(args[1]) && !args[1].equalsIgnoreCase("all")) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			if (args[1].equalsIgnoreCase("all")) {
				for (HyperObject ho:he.getHyperObjects(cps)) {
					if (ho.isShopObject()) {
						ho.setStatus(status);
					}
				}
				player.sendMessage(L.get("ALL_STATUS_SET"));
				return true;
			} else {
				HyperObject ho = he.getHyperObject(args[1], cps);
				if (ho.isShopObject()) {
					ho.setStatus(status);
					player.sendMessage(L.get("STATUS_SET"));
					return true;
				} else {
					hc.getDataBukkit().writeError("Setting PlayerShopObject status failed.");
					return true;
				}
			}
		} else if (args[0].equalsIgnoreCase("allow")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(L.get("MANAGESHOP_ALLOW_HELP"));
				return true;
			}
			if (!em.accountExists(args[1])) {
				player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				return true;
			}
			HyperAccount ap = em.getAccount(args[1]);
			if (cps.isAllowed(ap)) {
				cps.removeAllowed(ap);
				player.sendMessage(L.get("DISALLOWED_TO_MANAGE_SHOP"));
			} else {
				cps.addAllowed(ap);
				player.sendMessage(L.get("ALLOWED_TO_MANAGE_SHOP"));
			}
			return true;
		} else if (args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("m")) {
			try {
				if (cps == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				cps.setMessage(args[1]);
				sender.sendMessage(L.get("MESSAGE_SET"));
			} catch (Exception e) {
				player.sendMessage(L.get("MANAGESHOP_MESSAGE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("owner")) {
			if (!player.hasPermission("hyperconomy.admin")) {
				player.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
				return true;
			}
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(L.get("MANAGESHOP_OWNER_HELP"));
				return true;
			}
			if (!em.accountExists(args[1])) {
				player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				return true;
			}
			HyperAccount newOwner = em.getAccount(args[1]);
			cps.setOwner(newOwner);
			player.sendMessage(L.get("OWNER_SET"));
			return true;
		} else if (args[0].equalsIgnoreCase("goto")) {
			if (!player.hasPermission("hyperconomy.admin")) {
				player.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
				return true;
			}
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			player.teleport(cps.getLocation1());
		} else {
			player.sendMessage(L.get("MANAGESHOP_HELP"));
			if (cps != null) {
				player.sendMessage(L.f(L.get("MANAGESHOP_HELP2"), cps.getName()));
				player.sendMessage(L.f(L.get("MANAGESHOP_HELP3"), cps.getName()) + " " + ChatColor.AQUA + cps.getOwner().getName());
				player.sendMessage(L.get("MANAGESHOP_HELP4") + " " + ChatColor.AQUA +  hc.getDataBukkit().getCommonFunctions().implode(cps.getAllowed(), ","));
			} else {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
			}
			return true;
		}

		
		
		return true;
	}
	
	

}
