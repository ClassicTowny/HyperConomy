package regalowl.hyperconomy.event.minecraft;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.HItem;

public class HPlayerDropItemEvent extends Event {

	private HItem i;
	private HyperPlayer hp;
	
	public HPlayerDropItemEvent(HItem i, HyperPlayer hp) {
		this.i = i;
		this.hp = hp;
	}
	
	public HItem getItem() {
		return i;
	}
	public HyperPlayer getPlayer() {
		return hp;
	}
	
}
