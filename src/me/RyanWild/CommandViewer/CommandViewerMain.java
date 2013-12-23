package me.RyanWild.CommandViewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import net.pravian.bukkitlib.command.BukkitCommandHandler;
import net.pravian.bukkitlib.config.YamlConfig;
import org.bukkit.Bukkit;

public class CommandViewerMain extends JavaPlugin implements Listener
{
    
    public static final Logger logger = Bukkit.getLogger();
    
    public static BukkitCommandHandler handler;
    
    public static YamlConfig config;

    public static final String MSG_NO_PERMS = ChatColor.RED + "You do not have permission to use this command.";

    private Map<String, String[]> a = new HashMap();

    private List<String> b = new ArrayList();

    private String c = null;

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);

        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists())
        {
            getConfig().options().copyDefaults(true);
            getConfig().options().copyHeader(true);
            saveDefaultConfig();
            getLogger().info("[CommandViewer] Generated default config.yml");
        }

        try
        {
            Metrics metrics = new Metrics(this);
            metrics.start();
        }
        catch (IOException e)
        {
            // Failed to submit the stats :-(
        }


        this.c = ChatColor.translateAlternateColorCodes('&', getConfig().getString("msgFormat", "&1{player} has used the command: &3{command}"));
        getLogger().log(Level.INFO, "[CommandViewer] Format: {0}", this.c);
    }

    @EventHandler
    public void a(PlayerCommandPreprocessEvent e)
    {
        Player player = e.getPlayer();
        String command = e.getMessage();

        for (Player p : getServer().getOnlinePlayers())
        {
            if ((this.b.contains(p.getName())) && (!p.equals(e.getPlayer())))
            {
                p.sendMessage(this.c.replace("{player}", player.getDisplayName()).replace("{command}", command));
            }
        }
        a(player.getName(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return false;
        }
        Player player = (Player) sender;
        if (((commandLabel.equalsIgnoreCase("commandviewer")) || (commandLabel.equalsIgnoreCase("comv"))) && (player.hasPermission("CommandViewer.use")))
        {
            if (args.length == 0)
            {
                a(player);
            }
            else if (args.length >= 1)
            {
                if ((args[0].equalsIgnoreCase("toggle")) || (args[0].equalsIgnoreCase("t")))
                {
                    b(player);
                }
                else
                {
                    a(player, args[0]);
                }
            }
        }
        else if ((commandLabel.equalsIgnoreCase("commandviewer")) || (commandLabel.equalsIgnoreCase("comv")))
        {
            player.sendMessage("No permission");
        }
        return false;
    }

    private void a(Player p)
    {
        p.sendMessage(ChatColor.GRAY + "-=- [Command Viewer] -=-");
        p.sendMessage(ChatColor.BLUE + "/commandviewer = /comv");
        p.sendMessage(ChatColor.BLUE + "/comv toggle OR /comv t" + " - Toggles view mode on/off.");
        p.sendMessage(ChatColor.BLUE + "/comv <player>" + " - Shows the player's last five commands.");
    }

    private void b(Player p)
    {
        if (this.b.contains(p.getName()))
        {
            this.b.remove(p.getName());
            p.sendMessage(ChatColor.GREEN + "Toggled Command Viewer off.");
            return;
        }
        this.b.add(p.getName());
        p.sendMessage(ChatColor.GREEN + "Toggled Command Viewer on.");
    }

    private void a(Player p, String n)
    {
        if (getServer().getOfflinePlayer(n) != null)
        {
            n = getServer().getOfflinePlayer(n).getName();
        }
        if ((!this.a.containsKey(n.toLowerCase())) || (this.a.get(n.toLowerCase()) == null) || (((String[]) this.a.get(n.toLowerCase())).length <= 0))
        {
            p.sendMessage(ChatColor.GOLD + "Player " + ChatColor.BLUE + n + ChatColor.GOLD + " does not have a history.");
            return;
        }
        for (String cm : (String[]) this.a.get(n.toLowerCase()))
        {
            if ((cm != null) || ((cm != null) && (!cm.equalsIgnoreCase("null"))))
            {
                p.sendMessage(this.c.replace("{player}", n).replace("{command}", cm));
            }
        }
    }

    private void a(String n, String c)
    {
        n = n.toLowerCase();
        if (this.a.containsKey(n))
        {
            String[] l0 = (String[]) this.a.get(n);
            String[] l1 = new String[l0.length < 5 ? l0.length + 1 : 5];
            for (int i = 0; i < l0.length - 1; i++)
            {
                l1[i] = l0[(i + 1)];
            }
            l1[(l1.length - 1)] = c;
            this.a.put(n, l1);
        }
        else
        {
            String[] l =
            {
                c
            };
            this.a.put(n, l);
        }
    }

}