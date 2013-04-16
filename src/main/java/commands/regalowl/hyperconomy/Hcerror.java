package regalowl.hyperconomy;



import org.bukkit.command.CommandSender;

public class Hcerror {
	Hcerror(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			boolean logError = hc.getYaml().getConfig().getBoolean("config.log-errors");
			boolean toggleSqlWrite = false;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("sql")) {
					toggleSqlWrite = true;
				}
			}
			if (logError) {
				hc.getYaml().getConfig().set("config.log-errors", false);
				if (toggleSqlWrite) {
					hc.getYaml().getConfig().set("config.log-sqlwrite-errors", false);
				}
				sender.sendMessage(L.get("HCERROR_DISABLED"));
			} else {
				hc.getYaml().getConfig().set("config.log-errors", true);
				if (toggleSqlWrite) {
					hc.getYaml().getConfig().set("config.log-sqlwrite-errors", true);
				}
				sender.sendMessage(L.get("HCERROR_ENABLED"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("HCERROR_INVALID"));
			return;
		}
	}
}
