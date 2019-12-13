package regalowl.hyperconomy.inventory;


import java.util.HashMap;

import org.bukkit.NamespacedKey;

import regalowl.simpledatalib.CommonFunctions;

 

public class HEnchantment {
	private String enchantmentKey;
    private int lvl;
 
	public HEnchantment(String enchantmentKey, int lvl) {
        this.enchantmentKey = enchantmentKey;
        this.lvl = lvl;
    }
	
	public HEnchantment(HEnchantment he) {
        this.enchantmentKey = he.enchantmentKey;
        this.lvl = he.lvl;
    }
	
	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("enchantment", enchantmentKey);
		data.put("lvl", lvl+"");
		return CommonFunctions.implodeMap(data);
	}
	
	public HEnchantment(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.enchantmentKey = data.get("enchantment");
		this.lvl = Integer.parseInt(data.get("lvl"));
    }


	public NamespacedKey getEnchantmentKey() {
		return NamespacedKey.minecraft(enchantmentKey);
	}

	public int getLvl() {
		return lvl;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((enchantmentKey == null) ? 0 : enchantmentKey.hashCode());
		result = prime * result + lvl;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HEnchantment other = (HEnchantment) obj;
		if (enchantmentKey == null) {
			if (other.enchantmentKey != null)
				return false;
		} else if (!enchantmentKey.equals(other.enchantmentKey))
			return false;
		if (lvl != other.lvl)
			return false;
		return true;
	}

}