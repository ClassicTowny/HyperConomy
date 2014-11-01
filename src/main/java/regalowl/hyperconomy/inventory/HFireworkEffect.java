package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;

public class HFireworkEffect extends SerializableObject implements Serializable {

	private static final long serialVersionUID = 2644823685312321272L;
	private List<HColor> colors = new ArrayList<HColor>();
	private List<HColor> fadeColors = new ArrayList<HColor>();
	private String type;
	private boolean hasFlicker;
	private boolean hasTrail;
 
	public HFireworkEffect(ArrayList<HColor> colors, ArrayList<HColor> fadeColors, String type, boolean hasFlicker, boolean hasTrail) {
		this.colors = colors;
		this.fadeColors = fadeColors;
        this.type = type;
        this.hasFlicker = hasFlicker;
        this.hasTrail = hasTrail;
    }

	public HFireworkEffect(String base64String) {
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HFireworkEffect)) {return;}
			HFireworkEffect sfe = (HFireworkEffect)o;
	        this.colors = sfe.getColors();
	        this.fadeColors = sfe.getFadeColors();
	        this.type = sfe.getType();
	        this.hasFlicker = sfe.hasFlicker();
	        this.hasTrail = sfe.hasTrail();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }
	

	public List<HColor> getColors() {
		return colors;
	}
	public List<HColor> getFadeColors() {
		return fadeColors;
	}
	public String getType() {
		return type;
	}
	public boolean hasFlicker() {
		return hasFlicker;
	}
	public boolean hasTrail() {
		return hasTrail;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((colors == null) ? 0 : colors.hashCode());
		result = prime * result + ((fadeColors == null) ? 0 : fadeColors.hashCode());
		result = prime * result + (hasFlicker ? 1231 : 1237);
		result = prime * result + (hasTrail ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		HFireworkEffect other = (HFireworkEffect) obj;
		if (colors == null) {
			if (other.colors != null)
				return false;
		} else if (!colors.equals(other.colors))
			return false;
		if (fadeColors == null) {
			if (other.fadeColors != null)
				return false;
		} else if (!fadeColors.equals(other.fadeColors))
			return false;
		if (hasFlicker != other.hasFlicker)
			return false;
		if (hasTrail != other.hasTrail)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
