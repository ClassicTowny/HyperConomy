package regalowl.hyperconomy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;

public class _Command {
	private Player player;
	private HyperConomy hc;
	private DataManager em;
	private String playerecon;
	private String nonPlayerEconomy;

	public _Command() {
		nonPlayerEconomy = "default";
	}

	public boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		hc = HyperConomy.hc;
		em = hc.getDataManager();
		player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
			playerecon = em.getHyperPlayer(player.getName()).getEconomy();
		} else {
			playerecon = nonPlayerEconomy;
		}
		if (cmd.getName().equalsIgnoreCase("heldbuy") && (player != null)) {
			new Hb(args, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("xpinfo") && (player != null)) {
			new Xpinfo(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("heldsell") && (player != null)) {
			new Hs(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("heldvalue") && (player != null)) {
			new Hv(args, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("settax")) {
			new Settax(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("topitems")) {
			new Topitems(args, player, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("topenchants")) {
			new Topenchants(args, player, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("browseshop")) {
			new Browseshop(args, sender, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("iteminfo") && (player != null)) {
			new Iteminfo(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("objectsettings")) {
			new Objectsettings(args, sender, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("taxsettings")) {
			new Taxsettings(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("createeconomy")) {
			new Createeconomy(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("seteconomy")) {
			new Seteconomy(this, args, sender, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("economyinfo")) {
			new Economyinfo(this, args, sender, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("deleteeconomy")) {
			new Deleteeconomy(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("listeconomies")) {
			new Listeconomies(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("importnewitems")) {
			new Importnewitems(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hyperlog")) {
			new Hyperlog(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("listcategories")) {
			new Listcategories(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcbackup")) {
			new Hcbackup(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("intervals")) {
			new Intervals(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("notify")) {
			new Notify(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("scalebypercent")) {
			new Scalebypercent(sender, args, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("renameeconomyaccount")) {
			new Renameeconomyaccount(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("makedisplay") && player != null) {
			new Makedisplay(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("removedisplay") && player != null) {
			new Removedisplay(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setlanguage")) {
			new Setlanguage(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("importprices")) {
			new Importprices(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("repairsigns") && player != null) {
			new Repairsigns(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcbalance")) {
			new Hcbalance(args, sender, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hctop")) {
			new Hctop(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcpay") && player != null) {
			new Hcpay(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("toggleeconomy")) {
			new Toggleeconomy(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("makeaccount")) {
			new Makeaccount(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("importbalance")) {
			new Importbalance(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("audit")) {
			new Audit(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setchestowner")) {
			new Setchestowner(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setpassword")) {
			new Setpassword(args, player);
			return true;
		}
		
		return false;
	}

	public void setNonPlayerEconomy(String economy) {
		nonPlayerEconomy = economy;
	}
	public String getNonPlayerEconomy() {
		return nonPlayerEconomy;
	}
}
