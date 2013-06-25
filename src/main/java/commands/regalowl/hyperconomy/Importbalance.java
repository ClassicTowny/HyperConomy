package regalowl.hyperconomy;

import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandSender;

public class Importbalance {
	Importbalance(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataHandler df = hc.getDataFunctions();
		Economy econ = hc.getEconomy();
		Log l = hc.getLog();
		try {
			if (hc.s().gB("use-external-economy-plugin")) {
				ArrayList<String> players = df.getEconPlayers();
				if (args.length == 0) {
					
					for (String player:players) {
						if (econ.hasAccount(player)) {
							df.getHyperPlayer(player).setBalance(econ.getBalance(player));
							l.writeAuditLog(player, "initialization", econ.getBalance(player), "HyperConomy");
						}
					}
					sender.sendMessage(L.get("PLAYERS_IMPORTED"));
				} else if (args.length > 0) {
					for (int i = 0; i < args.length; i++) {
						String player = df.fixpN(args[i]);
						if (econ.hasAccount(player)) {
							if (players.contains(player)) {
								df.getHyperPlayer(player).setBalance(econ.getBalance(player));
							} else {
								df.addPlayer(player);
								df.getHyperPlayer(player).setBalance(econ.getBalance(player));
								l.writeAuditLog(player, "initialization", econ.getBalance(player), "HyperConomy");
							}
						}
					}
					sender.sendMessage(L.get("PLAYERS_IMPORTED"));
				}
			} else {
				sender.sendMessage(L.get("MUST_USE_EXTERNAL_ECONOMY"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTBALANCES_INVALID"));
		}
	}
}
