package me.taylorkelly.mywarp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.taylorkelly.mywarp.data.Lister;
import me.taylorkelly.mywarp.data.Searcher;
import me.taylorkelly.mywarp.data.WarpList;
import me.taylorkelly.mywarp.griefcraft.Updater;
import me.taylorkelly.mywarp.listeners.MWBlockListener;
import me.taylorkelly.mywarp.listeners.MWPlayerListener;
import me.taylorkelly.mywarp.permissions.WarpPermissions;
import me.taylorkelly.mywarp.sql.ConnectionManager;
import me.taylorkelly.mywarp.utils.WarpLogger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyWarp extends JavaPlugin {

    private WarpList warpList;
    private MWPlayerListener playerListener;
    private MWBlockListener blockListener;
    public String name;
    public String version;
    private Updater updater;
    private PluginManager pm;
    public static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
        ConnectionManager.closeConnection();
    }

    @Override
    public void onEnable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        pm = getServer().getPluginManager();
        
        WarpSettings.initialize(getDataFolder());
        
        libCheck();
        if(!sqlCheck()) { return; }

        Connection conn = ConnectionManager.initialize();
        if (conn == null) {
            log.log(Level.SEVERE, "[MYWARP] Could not establish SQL connection. Disabling MyWarp");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        warpList = new WarpList(getServer());
        blockListener = new MWBlockListener(warpList);
        playerListener = new MWPlayerListener(warpList);

        WarpPermissions.initialize(this);
        
        pm.registerEvents(blockListener, this);
        pm.registerEvents(playerListener, this);
        
        WarpLogger.info(name + " " + version + " enabled");
    }


    private void libCheck(){
        updater = new Updater();
        try {
            updater.check();
            updater.update();
        } catch (Exception e) {
        }
    }
    
    private boolean sqlCheck() {
        Connection conn = ConnectionManager.initialize();
        if (conn == null) {
            WarpLogger.severe("Could not establish SQL connection. Disabling MyWarp");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        } 
        return true;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (commandName.equals("warp") || commandName.equals("mywarp") || commandName.equals("mw")) {
            	/**
                 *  /warp reload
                 */
            	if (split.length == 1 && split[0].equalsIgnoreCase("reload") && player.hasPermission("mywarp.admin")) {
            		WarpSettings.initialize(getDataFolder());
            		player.sendMessage("[MyWarp] Reloading config");
                /**
                 * /warp list or /warp list #
                 */
                } else if ((split.length == 1 || (split.length == 2 && isInteger(split[1]))) && split[0].equalsIgnoreCase("list")
                        && player.hasPermission("mywarp.warp.basic.list")) {
                    Lister lister = new Lister(warpList);
                    lister.addPlayer(player);

                    if (split.length == 2) {
                        int page = Integer.parseInt(split[1]);
                        if (page < 1) {
                            player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                            return true;
                        } else if (page > lister.getMaxPages(player)) {
                            player.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages(player) + " pages of warps");
                            return true;
                        }
                        lister.setPage(page);
                    } else {
                        lister.setPage(1);
                    }
                    lister.list();

                    /**
                     * /warp slist
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("slist") && player.hasPermission("mywarp.warp.basic.list")) {
                    warpList.list(player);
                    /**
                     * /warp search <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("search") && player.hasPermission("mywarp.warp.basic.search")) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    Searcher searcher = new Searcher(warpList);
                    searcher.addPlayer(player);
                    searcher.setQuery(name);
                    searcher.search();
                    /**
                     * /warp create <name>
                     */
                } else if (split.length > 1 && (split[0].equalsIgnoreCase("create") || split[0].equalsIgnoreCase("set"))
                        && (player.hasPermission("mywarp.warp.basic.createpublic") || player.hasPermission("mywarp.warp.basic.createprivate"))) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    if (player.hasPermission("mywarp.warp.basic.createpublic")) {
                        warpList.addWarp(name, player);
                    } else {
                        warpList.addWarpPrivate(name, player);
                    }
                    /**
                     * /warp point <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("point") && player.hasPermission("mywarp.warp.basic.compass")) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.point(name, player);
                    /**
                     * /warp pcreate <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("pcreate") && player.hasPermission("mywarp.warp.basic.createprivate")) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.addWarpPrivate(name, player);
                    /**
                     * /warp delete <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("delete") && player.hasPermission("mywarp.warp.basic.delete")) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.deleteWarp(name, player);
                    /**
                     * /warp welcome <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("welcome") && player.hasPermission("mywarp.warp.basic.welcome")) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.welcomeMessage(name, player);
                    /**
                     * /warp private <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("private") && player.hasPermission("mywarp.warp.soc.private")) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.privatize(name, player);
                    /**
                     * /warp public <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("public") && player.hasPermission("mywarp.warp.soc.public")) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.publicize(name, player);

                    /**
                     * /warp give <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("give") && player.hasPermission("mywarp.warp.soc.give")) {
                    Player givee = getServer().getPlayer(split[1]);
                    // TODO Change to matchPlayer
                    String giveeName = (givee == null) ? split[1] : givee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.give(name, player, giveeName);

                    /**
                     * /warp invite <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("invite") && player.hasPermission("mywarp.warp.soc.invite")) {
                    Player invitee = getServer().getPlayer(split[1]);
                    // TODO Change to matchPlayer
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.invite(name, player, inviteeName);
                    /**
                     * /warp uninvite <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("uninvite") && player.hasPermission("mywarp.warp.soc.uninvite")) {
                    Player invitee = getServer().getPlayer(split[1]);
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.uninvite(name, player, inviteeName);

                    /**
                     * /warp player <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("player") && player.hasPermission("mywarp.admin")) {
                    Player invitee = getServer().getPlayer(split[1]);
                    //String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    // TODO ChunkLoading
                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.adminWarpTo(name, invitee, player);

                    /**
                     * /warp help
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("help")) {
                    ArrayList<String> messages = new ArrayList<String>();
                    messages.add(ChatColor.RED + "-------------------- " + ChatColor.WHITE + "/WARP HELP" + ChatColor.RED + " --------------------");
                    if (player.hasPermission("mywarp.warp.basic.warp")) {
                        messages.add(ChatColor.RED + "/warp [name]" + ChatColor.WHITE + "  -  Warp to " + ChatColor.GRAY + "[name]");
                    }
                    if (player.hasPermission("mywarp.warp.basic.createpublic") || player.hasPermission("mywarp.warp.basic.createprivate")) {
                        messages.add(ChatColor.RED + "/warp create [name]" + ChatColor.WHITE + "  -  Create warp " + ChatColor.GRAY + "[name]");
                    }
                    if (player.hasPermission("mywarp.warp.basic.createprivate")) {
                        messages.add(ChatColor.RED + "/warp pcreate [name]" + ChatColor.WHITE + "  -  Create warp " + ChatColor.GRAY + "[name]");
                    }

                    if (player.hasPermission("mywarp.warp.basic.delete")) {
                        messages.add(ChatColor.RED + "/warp delete [name]" + ChatColor.WHITE + "  -  Delete warp " + ChatColor.GRAY + "[name]");
                    }

                    if (player.hasPermission("mywarp.warp.basic.welcome")) {
                        messages.add(ChatColor.RED + "/warp welcome [name]" + ChatColor.WHITE + "  -  Change the welcome message of " + ChatColor.GRAY
                                + "[name]");
                    }

                    if (player.hasPermission("mywarp.warp.basic.list")) {
                        messages.add(ChatColor.RED + "/warp list (#)" + ChatColor.WHITE + "  -  Views warp page " + ChatColor.GRAY + "(#)");
                    }

                    if (player.hasPermission("mywarp.warp.basic.search")) {
                        messages.add(ChatColor.RED + "/warp search [query]" + ChatColor.WHITE + "  -  Search for " + ChatColor.GRAY + "[query]");
                    }
                    if (player.hasPermission("mywarp.warp.soc.give")) {
                        messages.add(ChatColor.RED + "/warp give [player] [name[" + ChatColor.WHITE + "  -  Give " + ChatColor.GRAY + "[player]"
                                + ChatColor.WHITE + " your " + ChatColor.GRAY + "[name]");
                    }
                    if (player.hasPermission("mywarp.warp.soc.invite")) {
                        messages.add(ChatColor.RED + "/warp invite [player] [name]" + ChatColor.WHITE + "  -  Invite " + ChatColor.GRAY + "[player]"
                                + ChatColor.WHITE + " to " + ChatColor.GRAY + "[name]");
                    }
                    if (player.hasPermission("mywarp.warp.soc.uninvite")) {
                        messages.add(ChatColor.RED + "/warp uninvite [player] [name[" + ChatColor.WHITE + "  -  Uninvite " + ChatColor.GRAY + "[player]"
                                + ChatColor.WHITE + " to " + ChatColor.GRAY + "[name]");
                    }
                    if (player.hasPermission("mywarp.warp.soc.public")) {
                        messages.add(ChatColor.RED + "/warp public [name]" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "[name]" + ChatColor.WHITE
                                + " public");
                    }
                    if (player.hasPermission("mywarp.warp.soc.private")) {
                        messages.add(ChatColor.RED + "/warp private [name]" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "[name]"
                                + ChatColor.WHITE + " private");
                    }
                    for (String message : messages) {
                        player.sendMessage(message);
                    }

                    /**
                     * /warp <name>
                     */
                } else if (split.length > 0 && player.hasPermission("mywarp.warp.basic.warp")) {
                    // TODO ChunkLoading
                    String name = "";
                    for (int i = 0; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.warpTo(name, player);
                } else {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void severe(String string, Exception ex) {
        log.log(Level.SEVERE, "[MYHOME]" + string, ex);

    }

    public static void severe(String string) {
        log.log(Level.SEVERE, "[MYHOME]" + string);
    }
}
