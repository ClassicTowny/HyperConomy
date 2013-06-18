package regalowl.hyperconomy;



import org.bukkit.command.CommandSender;

public class Hcweb {
	Hcweb(String[] args, CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		WebHandler wh = hc.getWebHandler();
		try {
			if (args[0].equalsIgnoreCase("enable")) {
				hc.getYaml().getConfig().set("config.web-page.use-web-page", true);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_ENABLED"));
			} else if (args[0].equalsIgnoreCase("disable")) {
				hc.getYaml().getConfig().set("config.web-page.use-web-page", false);
				wh.endServer();
				sender.sendMessage(L.get("WEB_PAGE_DISABLED"));
			} else if (args[0].equalsIgnoreCase("background")) {
				hc.getYaml().getConfig().set("config.web-page.background-color", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("tabledata")) {
				hc.getYaml().getConfig().set("config.web-page.table-data-color", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("fontsize")) {
				hc.getYaml().getConfig().set("config.web-page.font-size", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("font")) {
				hc.getYaml().getConfig().set("config.web-page.font", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("port")) {
				hc.getYaml().getConfig().set("config.web-page.port", Integer.parseInt(args[1]));
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("economy")) {
				hc.getYaml().getConfig().set("config.web-page.web-page-economy", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("font")) {
				hc.getYaml().getConfig().set("config.web-page.font-color", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("border")) {
				hc.getYaml().getConfig().set("config.web-page.border-color", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("increase")) {
				hc.getYaml().getConfig().set("config.web-page.increase-value-color", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("decrease")) {
				hc.getYaml().getConfig().set("config.web-page.decrease-value-color", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("highlight")) {
				hc.getYaml().getConfig().set("config.web-page.highlight-row-color", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("header")) {
				hc.getYaml().getConfig().set("config.web-page.header-color", args[1]);
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("refresh")) {
				wh.updatePages();
			} else if (args[0].equalsIgnoreCase("setdefault")) {
	    		hc.getYaml().getConfig().set("config.web-page.background-color", "8FA685");
	    		hc.getYaml().getConfig().set("config.web-page.font-color", "F2F2F2");
	    		hc.getYaml().getConfig().set("config.web-page.border-color", "091926");
	    		hc.getYaml().getConfig().set("config.web-page.increase-value-color", "C8D9B0");
	    		hc.getYaml().getConfig().set("config.web-page.decrease-value-color", "F2B2A8");
	    		hc.getYaml().getConfig().set("config.web-page.highlight-row-color", "8FA685");
	    		hc.getYaml().getConfig().set("config.web-page.header-color", "091926");
	    		hc.getYaml().getConfig().set("config.web-page.table-data-color", "314A59");
	    		hc.getYaml().getConfig().set("config.web-page.font-size", 12);
	    		hc.getYaml().getConfig().set("config.web-page.font", "verdana");
	    		hc.getYaml().getConfig().set("config.web-page.port", 7777);
	    		sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else {
				sender.sendMessage(L.get("HCWEB_INVALID"));
			}
			
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("HCWEB_INVALID"));
			return;
		}
	}
}
