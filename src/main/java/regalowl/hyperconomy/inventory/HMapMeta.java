package regalowl.hyperconomy.inventory;


import java.util.HashMap;
import java.util.List;

import regalowl.databukkit.CommonFunctions;

 

public class HMapMeta extends HItemMeta {

	private boolean isScaling;

	public HMapMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, boolean isScaling) {
		super(displayName, lore, enchantments);
		this.isScaling = isScaling;
	}

	public HMapMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		isScaling = Boolean.parseBoolean(data.get("isScaling"));
    }

	public String serialize() {
		HashMap<String,String> data = super.getMap();
		data.put("isScaling", isScaling+"");
		return CommonFunctions.implodeMap(data);
	}
	

	public boolean isScaling() {
		return isScaling;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isScaling ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HMapMeta other = (HMapMeta) obj;
		if (isScaling != other.isScaling)
			return false;
		return true;
	}
	


}