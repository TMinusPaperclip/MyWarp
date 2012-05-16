package me.taylorkelly.mywarp.permissions;

import me.taylorkelly.mywarp.utils.WarpLogger;
import me.taylorkelly.mywarp.WarpSettings;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PermissionsHandler implements IPermissionsHandler {
	private enum PermHandler {
		PERMISSIONSEX, BPERMISSIONS, BPERMISSIONS2, SUPERPERMS, NONE
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
		final PluginManager pluginManager = plugin.getServer().getPluginManager();
        
        if (!WarpSettings.forceSuperPerms) {
            final Plugin permExPlugin = pluginManager.getPlugin("PermissionsEx");
            if (permExPlugin != null && permExPlugin.isEnabled()) {
                if (!(handler instanceof PermissionsExHandler)) {
                    permplugin = PermHandler.PERMISSIONSEX;
                    String version = permExPlugin.getDescription().getVersion();
                    WarpLogger.info("Access Control: Using PermissionsEx v"+ version);
                    handler = new PermissionsExHandler();
                }
                return;
            }

            final Plugin bPermPlugin = pluginManager.getPlugin("bPermissions");
            if (bPermPlugin != null && bPermPlugin.isEnabled()) {
                if (bPermPlugin.getDescription().getVersion().charAt(0) == '2') {
                    if (!(handler instanceof BPermissions2Handler)) {
                        permplugin = PermHandler.BPERMISSIONS2;
                        String version = bPermPlugin.getDescription().getVersion();
                        WarpLogger.info("Access Control: Using bPermissions"+ version);
                        handler = new BPermissions2Handler();
                    }
                } else {
                    if (!(handler instanceof BPermissionsHandler)) {
                        permplugin = PermHandler.BPERMISSIONS;
                        String version = bPermPlugin.getDescription().getVersion();
                        WarpLogger.info("Access Control: Using bPermissions"+ version);
                        handler = new BPermissionsHandler();
                    }
                }
                return;
            }
        }

		if (permplugin == PermHandler.NONE) {
			if (!(handler instanceof SuperpermsHandler)) {
				WarpLogger.info("Access Control: Using SuperPerms");
				handler = new SuperpermsHandler(this.plugin);
			}
		}
	}
}
