package regalowl.hyperconomy.bukkit;

import org.bukkit.Material;

public class BukkitUtils
{
	
	public static boolean isWallSign(Material material) {
		if(material.toString().contains("WALL_SIGN")) return true;
		return false;
	}
	
	public static boolean isLegacySignPost(Material material) {
		if(material.toString().contains("WALL_SIGN")) return false;
		if(material.toString().contains("SIGN")) return true;
		return false;
	}
	
	public static boolean isLeaves(Material material) {
		if(material.toString().contains("LEAVES")) return true;
		return false;
	}
}