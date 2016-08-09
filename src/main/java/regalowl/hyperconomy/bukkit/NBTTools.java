package regalowl.hyperconomy.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTTools {

	@SuppressWarnings("rawtypes")
	private Class craftItemStackClass;
	private Method craftItemStackAsNMSCopyMethod;
	private Method craftItemStackAsCraftMirrorMethod;
	private Method nmsItemStackGetTagMethod;
	private Method nmsItemStackSetTagMethod;
	private Method nbtTagCompoundHasKeyMethod;
	private Method nbtTagCompoundCMethod;
	private Method nbtTagCompoundSetMethod;
	private Method nbtTagCompoundGetCompoundMethod;
	private Method nbtTagCompoundSetStringMethod;
	private Method nbtTagCompoundGetStringMethod;
	private Method nbtTagCompoundSetIntMethod;
	private Method nbtTagCompoundGetIntMethod;
	private Method nbtTagCompoundSetDoubleMethod;
	private Method nbtTagCompoundGetDoubleMethod;
	private Method nbtTagCompoundSetBooleanMethod;
	private Method nbtTagCompoundGetBooleanMethod;
	@SuppressWarnings("rawtypes")
	private Class nbtTagCompound;
	private boolean loadedSuccessfully = false;

	@SuppressWarnings("unchecked")
	public NBTTools() {

		String version = Bukkit.getServer().getClass().getPackage().getName();
		version = version.substring(version.lastIndexOf(".") + 1, version.length());
		try {
			craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
			nbtTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
			Object nbtTag = nbtTagCompound.newInstance();
			@SuppressWarnings("rawtypes")
			Class nbtBase = Class.forName("net.minecraft.server." + version + ".NBTBase");
			craftItemStackAsNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			ItemStack bukkitItemStack = new ItemStack(Material.COBBLESTONE, 1);
			Object nmsItemStack = craftItemStackAsNMSCopyMethod.invoke(craftItemStackClass, bukkitItemStack);
			craftItemStackAsCraftMirrorMethod = craftItemStackClass.getMethod("asCraftMirror", nmsItemStack.getClass());
			nmsItemStackGetTagMethod = nmsItemStack.getClass().getMethod("getTag");
			nmsItemStackSetTagMethod = nmsItemStack.getClass().getMethod("setTag", nbtTag.getClass());

			nbtTagCompoundHasKeyMethod = nbtTag.getClass().getMethod("hasKey", String.class);
			nbtTagCompoundCMethod = nbtTag.getClass().getMethod("c");
			nbtTagCompoundSetMethod = nbtTag.getClass().getMethod("set", String.class, nbtBase);
			nbtTagCompoundGetCompoundMethod = nbtTag.getClass().getMethod("getCompound", String.class);
			nbtTagCompoundSetStringMethod = nbtTag.getClass().getMethod("setString", String.class, String.class);
			nbtTagCompoundGetStringMethod = nbtTag.getClass().getMethod("getString", String.class);
			nbtTagCompoundSetIntMethod = nbtTag.getClass().getMethod("setInt", String.class, int.class);
			nbtTagCompoundGetIntMethod = nbtTag.getClass().getMethod("getInt", String.class);
			nbtTagCompoundSetDoubleMethod = nbtTag.getClass().getMethod("setDouble", String.class, double.class);
			nbtTagCompoundGetDoubleMethod = nbtTag.getClass().getMethod("getDouble", String.class);
			nbtTagCompoundSetBooleanMethod = nbtTag.getClass().getMethod("setBoolean", String.class, boolean.class);
			nbtTagCompoundGetBooleanMethod = nbtTag.getClass().getMethod("getBoolean", String.class);
			loadedSuccessfully = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean loadedSuccessfully() {
		return loadedSuccessfully;
	}

	public boolean hasKey(ItemStack itemStack, String key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Object nmsItemStack = craftItemStackAsNMSCopyMethod.invoke(craftItemStackClass, itemStack);
		if (nmsItemStack == null) return false;
		Object nbtTag = nmsItemStackGetTagMethod.invoke(nmsItemStack);
		if (nbtTag == null) return false;
		return (Boolean) nbtTagCompoundHasKeyMethod.invoke(nbtTag, key);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getNMSKeys(ItemStack itemStack) {
		try {
			Object nmsItemStack = craftItemStackAsNMSCopyMethod.invoke(craftItemStackClass, itemStack);
			if (nmsItemStack == null) return new ArrayList<String>();
			Object nbtTagCompound = nmsItemStackGetTagMethod.invoke(nmsItemStack);
			if (nbtTagCompound == null) return new ArrayList<String>();
			Set<String> tags = (Set<String>) nbtTagCompoundCMethod.invoke(nbtTagCompound);
			ArrayList<String> tagArray = new ArrayList<String>(tags);
			return tagArray;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}

	}
	
	/*
	public String getMobEggType(ItemStack itemStack) {
		try {
			Object nmsItemStack = craftItemStackAsNMSCopyMethod.invoke(craftItemStackClass, itemStack);
			if (nmsItemStack == null) return null;
			Object nbtTag = nmsItemStackGetTagMethod.invoke(nmsItemStack);
			if (nbtTag == null) return null;
			Object entityTag = nbtTagCompoundGetCompoundMethod.invoke(nbtTag, "EntityTag");
			if (entityTag == null) return null;
			return (String) nbtTagCompoundGetStringMethod.invoke(entityTag, "id");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	*/
	
	
	public Object getNMSItemStack(ItemStack itemStack) {
		try {
			return craftItemStackAsNMSCopyMethod.invoke(craftItemStackClass, itemStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public ItemStack getBukkitItemStack(Object nmsItemStack) {
		try {
			return (ItemStack) craftItemStackAsCraftMirrorMethod.invoke(craftItemStackClass, nmsItemStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getNBTTag(Object nmsItemStack) {
		try {
			return nmsItemStackGetTagMethod.invoke(nmsItemStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void setNBTTag(Object nmsItemStack, Object nbtTag) {
		try {
			nmsItemStackSetTagMethod.invoke(nmsItemStack, nbtTag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Object generateNBTTag() {
		try {
			return nbtTagCompound.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getNBTTagCompoundString(Object nbtTag, String key) {
		try {
			return (String) nbtTagCompoundGetStringMethod.invoke(nbtTag, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void setNBTTagCompoundString(Object nbtTag, String key, String s) {
		try {
			nbtTagCompoundSetStringMethod.invoke(nbtTag, key, s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object getNBTTagCompoundCompound(Object nbtTag, String key) {
		try {
			return nbtTagCompoundGetCompoundMethod.invoke(nbtTag, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void setNBTTagCompoundCompound(Object nbtTag, String key, Object nestedTag) {
		try {
			nbtTagCompoundSetMethod.invoke(nbtTag, key, nestedTag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public String getString(ItemStack itemStack, String key) {
		Object nmsItemStack = getNMSItemStack(itemStack);
		if (nmsItemStack == null) return null;
		Object nbtTag = getNBTTag(nmsItemStack);
		if (nbtTag == null) return null;
		return getNBTTagCompoundString(nbtTag, key);
	}

	public ItemStack setString(ItemStack itemStack, String key, String s) {
		Object nmsItemStack = getNMSItemStack(itemStack);
		if (nmsItemStack == null) return null;
		Object nbtTag = getNBTTag(nmsItemStack);
		if (nbtTag == null) nbtTag = generateNBTTag();
		setNBTTagCompoundString(nbtTag, key, s);
		setNBTTag(nmsItemStack, nbtTag);
		return getBukkitItemStack(nmsItemStack);
	}
	
	
	
	public Object getCompound(ItemStack itemStack, String key) {
		Object nmsItemStack = getNMSItemStack(itemStack);
		if (nmsItemStack == null) return null;
		Object nbtTag = getNBTTag(nmsItemStack);
		if (nbtTag == null) return null;
		return getNBTTagCompoundCompound(nbtTag, key);
	}

	public ItemStack setCompound(ItemStack itemStack, String key, Object c) {
		Object nmsItemStack = getNMSItemStack(itemStack);
		if (nmsItemStack == null) return null;
		Object nbtTag = getNBTTag(nmsItemStack);
		if (nbtTag == null) nbtTag = generateNBTTag();
		setNBTTagCompoundCompound(nbtTag, key, c);
		setNBTTag(nmsItemStack, nbtTag);
		return getBukkitItemStack(nmsItemStack);
	}
	
	
	public Integer getInt(ItemStack itemStack, String key) {
		try {
			Object nmsItemStack = getNMSItemStack(itemStack);
			if (nmsItemStack == null) return null;
			Object nbtTag = nmsItemStackGetTagMethod.invoke(nmsItemStack);
			if (nbtTag == null) return null;
			return (Integer) nbtTagCompoundGetIntMethod.invoke(nbtTag, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ItemStack setInt(ItemStack itemStack, String key, Integer i) {
		try {
			Object nmsItemStack = getNMSItemStack(itemStack);
			if (nmsItemStack == null) return null;
			Object nbtTag = nmsItemStackGetTagMethod.invoke(nmsItemStack);
			if (nbtTag == null) nbtTag = nbtTagCompound.newInstance();
			nbtTagCompoundSetIntMethod.invoke(nbtTag, key, i);
			nmsItemStackSetTagMethod.invoke(nmsItemStack, nbtTag);
			return getBukkitItemStack(nmsItemStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Double getDouble(ItemStack itemStack, String key) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object nmsItemStack = craftItemStackAsNMSCopyMethod.invoke(craftItemStackClass, itemStack);
		if (nmsItemStack == null) return null;
		Object nbtTag = nmsItemStackGetTagMethod.invoke(nmsItemStack);
		if (nbtTag == null) return null;
		return (Double) nbtTagCompoundGetDoubleMethod.invoke(nbtTag, key);
	}

	public ItemStack setDouble(ItemStack itemStack, String key, Double d) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Object nmsItemStack = craftItemStackAsNMSCopyMethod.invoke(craftItemStackClass, itemStack);
		if (nmsItemStack == null) return null;
		Object nbtTag = nmsItemStackGetTagMethod.invoke(nmsItemStack);
		if (nbtTag == null) nbtTag = nbtTagCompound.newInstance();
		nbtTagCompoundSetDoubleMethod.invoke(nbtTag, key, d);
		nmsItemStackSetTagMethod.invoke(nmsItemStack, nbtTag);
		return getBukkitItemStack(nmsItemStack);
	}

	public Boolean getBoolean(ItemStack itemStack, String key) {
		try {
			Object nmsItemStack = getNMSItemStack(itemStack);
			if (nmsItemStack == null) return null;
			Object nbtTag = nmsItemStackGetTagMethod.invoke(nmsItemStack);
			if (nbtTag == null) return null;
			return (Boolean) nbtTagCompoundGetBooleanMethod.invoke(nbtTag, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ItemStack setBoolean(ItemStack itemStack, String key, Boolean b) {
		try {
			Object nmsItemStack = getNMSItemStack(itemStack);
			if (nmsItemStack == null) return null;
			Object nbtTag = nmsItemStackGetTagMethod.invoke(nmsItemStack);
			if (nbtTag == null) nbtTag = nbtTagCompound.newInstance();
			nbtTagCompoundSetBooleanMethod.invoke(nbtTag, key, b);
			nmsItemStackSetTagMethod.invoke(nmsItemStack, nbtTag);
			return getBukkitItemStack(nmsItemStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



}
