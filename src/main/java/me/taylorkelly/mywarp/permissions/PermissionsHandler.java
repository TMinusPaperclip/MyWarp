package me.taylorkelly.mywarp.permissions;

import me.taylorkelly.mywarp.utils.WarpLogger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PermissionsHandler implements IPermissionsHandler {
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
	
	public void checkPermissions() {
        if (!(handler instanceof SuperpermsHandler)) {
            WarpLogger.info("Access Control: Using SuperPerms");
            handler = new SuperpermsHandler(this.plugin);
        }
	}
}
