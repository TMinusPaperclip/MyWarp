package me.taylorkelly.mywarp.permissions;

import java.util.LinkedHashMap;
import java.util.Map;

import me.taylorkelly.mywarp.WarpSettings;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class SuperpermsHandler implements IPermissionsHandler {
	private static PluginManager pm;
	
	public SuperpermsHandler(Plugin plugin) {
		pm = plugin.getServer().getPluginManager();
		registerPermissions();
	}
	
	@Override
	public boolean hasPermission(final Player player, final String node, boolean defaultPerm) {
		if(player.isOp() && WarpSettings.opPermissions) {
			return true;
		}
		if (player.hasPermission("-" + node)) {
			return false;
		}
		return player.hasPermission(node);
	}

	@Override
	public int getInteger(final Player player, final String node, final int defaultInt) {
		if(player.isOp() && WarpSettings.opPermissions) {
			return 0;
		}
		return defaultInt;
	}
	
	private static void registerPermissions() {
		registerAdminPerms();
		registerUserPerms();
		overallPerm();
	}
	
	private static void registerAdminPerms() {
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.admin", "Admin Permission", PermissionDefault.OP));
	}

	private static void registerUserPerms() {
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.warp", "Usage of /warp", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.delete", "Can delete warps", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.list", "Can list warps", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.welcome", "Can change the welcome message", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.search", "Can search for a warp", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.soc.give", "Can give to your /warp", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.soc.invite", "Can invite to your /warp", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.soc.uninvite", "Can uninvite people from your /warp", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.soc.public", "Allow anyone to use your /warp", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.soc.private", "Disallow anyone to use your /warp", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.sign.warp", "Can use sign warps", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.sign.create", "Can make sign warps", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.createprivate", "Allowed to create private warps", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.createpublic", "Allowed to create public warps", PermissionDefault.TRUE));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.compass", "Compass can be used", PermissionDefault.TRUE));
		Map<String, Boolean> userbasicmap = new LinkedHashMap<String, Boolean>();
		Map<String, Boolean> usersocmap = new LinkedHashMap<String, Boolean>();
		Map<String, Boolean> usersignmap = new LinkedHashMap<String, Boolean>();
		Map<String, Boolean> userallmap = new LinkedHashMap<String, Boolean>();
		userbasicmap.put("mywarp.warp.basic.warp", true);
		userbasicmap.put("mywarp.warp.basic.list", true);
		userbasicmap.put("mywarp.warp.basic.welcome", true);
		userbasicmap.put("mywarp.warp.basic.search", true);
		userbasicmap.put("mywarp.warp.basic.delete", true);
		userbasicmap.put("mywarp.warp.basic.createpublic", true);
		userbasicmap.put("mywarp.warp.basic.createprivate", true);
		userbasicmap.put("mywarp.warp.basic.compass", true);
		usersocmap.put("mywarp.warp.soc.give", true);
		usersocmap.put("mywarp.warp.soc.list", true);
		usersocmap.put("mywarp.warp.soc.invite", true);
		usersocmap.put("mywarp.warp.soc.uninvite", true);
		usersocmap.put("mywarp.warp.soc.public", true);
		usersocmap.put("mywarp.warp.soc.private", true);
		usersignmap.put("mywarp.warp.sign.warp", true);
		usersignmap.put("mywarp.warp.sign.create", true);
		userallmap.put("mywarp.basic.*", true);
		userallmap.put("mywarp.soc.*", true);
		userallmap.put("mywarp.sign.*", true);
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.*", "Basic /warp commands", PermissionDefault.TRUE, userbasicmap));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.soc.*", "Social /warp commands", PermissionDefault.TRUE, usersocmap));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.sign.*", "All sign based warp permissions", PermissionDefault.TRUE, usersignmap));
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.*", "All user permissions", PermissionDefault.TRUE, userallmap));
	}

	public static void overallPerm() {
		Map<String, Boolean> fullmap = new LinkedHashMap<String, Boolean>();
		fullmap.put("mywarp.warp.*", true);
		fullmap.put("mywarp.admin", true);
		pm.addPermission(new org.bukkit.permissions.Permission("mywarp.*", "Full access", PermissionDefault.OP, fullmap));
	}
	
}

