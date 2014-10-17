package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HyperConomy;
 

public class SerializableItemStack extends SerializableObject implements Serializable {

	private static final long serialVersionUID = 8634824379403255552L;
	private String material;
    private short durability;
    private byte data;
    private SerializableItemMeta itemMeta;
  
 
    
    public SerializableItemStack(SerializableItemMeta itemMeta, String material, short durability, byte data) {
    	this.itemMeta = itemMeta;
    	this.material = material;
    	this.durability = durability;
    	this.data = data;
    }
    

	public SerializableItemStack(String base64String) {
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableItemStack)) {return;}
			SerializableItemStack sis = (SerializableItemStack)o;
	        this.material = sis.getMaterial();
	        this.durability = sis.getDurability();
	        this.data = sis.getData();
	        this.itemMeta = sis.getItemMeta();
    	} catch (Exception e) {
    		HyperConomy.hc.getDataBukkit().writeError(e);
    	}
    }

	public void displayInfo(Player p, ChatColor color1, ChatColor color2) {
		p.sendMessage(color1 + "Material: " + color2 + material);
		p.sendMessage(color1 + "Durability: " + color2 + durability);
		p.sendMessage(color1 + "Data: " + color2 + data);
		if (itemMeta != null) {
			itemMeta.displayInfo(p, color1, color2);
		}
	}

	public String getMaterial() {
		return material;
	}
	
	public Material getMaterialEnum() {
		return Material.matchMaterial(material);
	}

	public short getDurability() {
		return durability;
	}

	public byte getData() {
		return data;
	}

	public SerializableItemMeta getItemMeta() {
		return itemMeta;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + durability;
		result = prime * result + ((itemMeta == null) ? 0 : itemMeta.hashCode());
		result = prime * result + ((material == null) ? 0 : material.hashCode());
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
		SerializableItemStack other = (SerializableItemStack) obj;
		if (considerDamage()) {
			if (data != other.data)
				return false;
			if (durability != other.durability)
				return false;
		}
		if (itemMeta == null) {
			if (other.itemMeta != null)
				return false;
		} else if (!itemMeta.equals(other.itemMeta))
			return false;
		if (material == null) {
			if (other.material != null)
				return false;
		} else if (!material.equals(other.material))
			return false;
		return true;
	}
	
	public boolean considerDamage() {
		Material m = Material.matchMaterial(material);
		boolean ignoreDamage = HyperConomy.hc.getConf().getBoolean("enable-feature.treat-damaged-items-as-equals-to-undamaged-ones");
		if (!ignoreDamage) {
			return true;
		}
		if (m != null && m.getMaxDurability() > 0) {
			return false;
		}
		return true;
	}

}