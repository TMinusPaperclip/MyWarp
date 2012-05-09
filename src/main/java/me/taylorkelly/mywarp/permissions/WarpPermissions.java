package me.taylorkelly.mywarp.permissions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class WarpPermissions {
    private static PluginManager pm;

	public static void initialize(Plugin plugin) {
		pm = plugin.getServer().getPluginManager();
        registerPermissions();
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
        
        userallmap.put("mywarp.warp.basic.*", true);
        userallmap.put("mywarp.warp.soc.*", true);
        userallmap.put("mywarp.warp.sign.*", true);
        
        pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.basic.*", "Basic /warp commands", PermissionDefault.TRUE, userbasicmap));
        pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.soc.*", "Social /warp commands", PermissionDefault.TRUE, usersocmap));
        pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.sign.*", "All sign based warp permissions", PermissionDefault.TRUE, usersignmap));
        pm.addPermission(new org.bukkit.permissions.Permission("mywarp.warp.*", "All user permissions", PermissionDefault.TRUE, userallmap));
    }

    public static void overallPerm() {
        Map<String, Boolean> fullmap = new LinkedHashMap<String, Boolean>();
        fullmap.put("mywarp.warp.*", true);
        fullmap.put("mywarp.warp.*", true);
        fullmap.put("mywarp.admin", true);
        fullmap.put("mywarp.warp.*", true);
        pm.addPermission(new org.bukkit.permissions.Permission("mywarp.*", "Full access", PermissionDefault.OP, fullmap));
    }
}