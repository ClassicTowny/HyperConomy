package regalowl.hyperconomy.account;

import java.util.ArrayList;

import regalowl.databukkit.WriteStatement;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.Shop;

public class HyperBank implements HyperAccount {

	private HyperConomy hc;
	private String name;
	private double balance;
	private ArrayList<String> owners = new ArrayList<String>();
	private ArrayList<String> members = new ArrayList<String>();
	
	
	public HyperBank(String name, HyperPlayer owner) {
		if (name == null || owner == null) {return;}
		this.hc = HyperConomy.hc;
		this.name = name;
		this.balance = 0.0;
		owners.add(owner.getName().toLowerCase());
		WriteStatement ws = new WriteStatement("INSERT INTO hyperconomy_banks (NAME, BALANCE, OWNERS, MEMBERS) VALUES (?,?,?,?)",hc.getDataBukkit());
		ws.addParameter(name);
		ws.addParameter(0.0);
		ws.addParameter(owner.getName().toLowerCase() + ",");
		ws.addParameter("");
		hc.getSQLWrite().addToQueue(ws);
	}
	
	public HyperBank(String name, double balance, String owners, String members) {
		this.hc = HyperConomy.hc;
		this.name = name;
		this.balance = balance;
		this.owners = hc.gCF().explode(owners, ",");
		this.members = hc.gCF().explode(members, ",");
	}
	
	public void delete() {
		WriteStatement ws = new WriteStatement("DELETE FROM hyperconomy_banks WHERE NAME=?",hc.getDataBukkit());
		ws.addParameter(name);
		hc.getSQLWrite().addToQueue(ws);
		hc.getDataManager().removeHyperBank(this);
		if (balance > 0) {
			double share = balance/owners.size();
			for (HyperPlayer hp:getOwners()) {
				hp.deposit(share);
			}
		}
		for (HyperEconomy he:hc.getDataManager().getEconomies()) {
			if (he.getDefaultAccount() == this) {
				he.setDefaultAccount(getOwners().get(0));
			}
		}
		for (Shop s:hc.getDataManager().getShops()) {
			if (s.getOwner() == this) {
				s.setOwner(getOwners().get(0));
			}
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public double getBalance() {
		return balance;
	}
	@Override
	public void deposit(double amount) {
		this.balance += amount;
		setBalance(this.balance);
	}
	@Override
	public void withdraw(double amount) {
		this.balance -= amount;
		setBalance(this.balance);
	}

	@Override
	public void setName(String newName) {
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_banks SET NAME=? WHERE NAME=?",hc.getDataBukkit());
		ws.addParameter(newName);
		ws.addParameter(this.name);
		hc.getSQLWrite().addToQueue(ws);
		this.name = newName;
		hc.getDataManager().removeHyperBank(this);
		hc.getDataManager().addHyperBank(this);
		for (HyperEconomy he:hc.getDataManager().getEconomies()) {
			if (he.getDefaultAccount() == this) {
				he.setDefaultAccount(this);
			}
		}
		for (Shop s:hc.getDataManager().getShops()) {
			if (s.getOwner() == this) {
				s.setOwner(this);
			}
		}
	}
	
	@Override
	public void setBalance(double balance) {
		this.balance = balance;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_banks SET BALANCE=? WHERE NAME=?",hc.getDataBukkit());
		ws.addParameter(this.balance);
		ws.addParameter(this.name);
		hc.getSQLWrite().addToQueue(ws);
	}

	@Override
	public boolean hasBalance(double amount) {
		if ((this.balance - amount) >= 0) {
			return true;
		}
		return false;
	}
	
	
	public void addOwner(HyperPlayer owner) {
		String ownerName = owner.getName().toLowerCase();
		if (!owners.contains(ownerName)) {
			owners.add(ownerName);
		}
		saveOwners();
	}
	public void removeOwner(HyperPlayer owner) {
		String ownerName = owner.getName().toLowerCase();
		if (owners.contains(ownerName)) {
			owners.remove(ownerName);
		}
		saveOwners();
	}
	
	public void addMember(HyperPlayer member) {
		String memberName = member.getName().toLowerCase();
		if (!members.contains(memberName)) {
			members.add(memberName);
		}
		saveMembers();
	}
	public void removeMember(HyperPlayer owner) {
		String memberName = owner.getName().toLowerCase();
		if (members.contains(memberName)) {
			members.remove(memberName);
		}
		saveMembers();
	}
	
	public boolean isOwner(HyperPlayer hp) {
		String ownerName = hp.getName().toLowerCase();
		if (owners.contains(ownerName)) {
			return true;
		}
		return false;
	}
	public boolean isMember(HyperPlayer hp) {
		String memberName = hp.getName().toLowerCase();
		if (members.contains(memberName)) {
			return true;
		}
		return false;
	}
	
	public String getOwnersList() {
		String list = "";
		for (String owner:owners) {
			list += hc.getDataManager().getHyperPlayer(owner).getName() + ",";
		}
		if (list.length() > 0) {
			list = list.substring(0, list.length() - 1);
		}
		return list;
	}
	
	public String getMembersList() {
		String list = "";
		for (String member:members) {
			list += hc.getDataManager().getHyperPlayer(member).getName() + ",";
		}
		if (list.length() > 0) {
			list = list.substring(0, list.length() - 1);
		}
		return list;
	}
	
	public ArrayList<HyperPlayer> getOwners() {
		ArrayList<HyperPlayer> ownersList = new ArrayList<HyperPlayer>();
		for (String owner:owners) {
			ownersList.add(hc.getDataManager().getHyperPlayer(owner));
		}
		return ownersList;
	}
	
	public ArrayList<HyperPlayer> getMembers() {
		ArrayList<HyperPlayer> membersList = new ArrayList<HyperPlayer>();
		for (String member:members) {
			membersList.add(hc.getDataManager().getHyperPlayer(member));
		}
		return membersList;
	}

	private void saveOwners() {
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_banks SET OWNERS=? WHERE NAME=?",hc.getDataBukkit());
		ws.addParameter(hc.gCF().implode(owners, ","));
		ws.addParameter(this.name);
		hc.getSQLWrite().addToQueue(ws);
	}
	private void saveMembers() {
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_banks SET MEMBERS=? WHERE NAME=?",hc.getDataBukkit());
		ws.addParameter(hc.gCF().implode(members, ","));
		ws.addParameter(this.name);
		hc.getSQLWrite().addToQueue(ws);
	}
	
	public void sendMessage(String message) {
		for (HyperPlayer owner: getOwners()) {
			owner.sendMessage(message);
		}
		for (HyperPlayer member: getMembers()) {
			member.sendMessage(message);
		}
	}

}
