package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Importfromyml {
	Importfromyml(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 1 || args.length == 2) {
				String economy = args[0];
				if (hc.getDataFunctions().testEconomy(economy)) {
					if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
						if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
							new Backup();
						}
						SQLEconomy sqe = hc.getSQLEconomy();
						SQLWrite sw = hc.getSQLWrite();
						sw.executeSQL("DELETE FROM hyperconomy_objects WHERE ECONOMY = '" + economy + "'");
						sqe.migrate(economy);
						sender.sendMessage(L.get("ECONOMY_IMPORTED"));
					} else {
						sender.sendMessage(L.get("IMPORT_PROCEED"));
					}
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("IMPORTFROMYML_INVALID"));
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTFROMYML_INVALID"));
			return;
		}
	}
}
