package me.taylorkelly.mywarp.permissions;

import me.taylorkelly.mywarp.utils.WarpLogger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PermissionsHandler implements IPermissionsHandler {
	private enum PermHandler {
		PERMISSIONSEX, PERMISSIONS3, PERMISSIONS2, GROUPMANAGER, BPERMISSIONS, BPERMISSIONS2, SUPERPERMS, NONE
	}
	private static PermHandler permplugin = PermHandler.NONE;
	private transient IPermissionsHandler handler = new NullHandler();
	private final transient Plugin plugin;

	public PermissionsHandler(final Plugin plugin) {
		this.plugin = plugin;
		checkPermissions();
	}

	@Override
	public boolean hasPermission(final Player player, final String node, boolean defaultPerm) {
		return handler.hasPermission(player, node, defaultPerm);
	}

	@Override
	public int getInteger(final Player player, final String node, int defaultInt) {
		return handler.getInteger(player, node, defaultInt);
	}
	
	public void checkPermissions() {
        if (!(handler instanceof SuperpermsHandler)) {
            WarpLogger.info("Access Control: Using SuperPerms");
            handler = new SuperpermsHandler(this.plugin);
        }
	}
}
