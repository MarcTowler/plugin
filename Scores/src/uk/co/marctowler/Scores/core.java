package uk.co.marctowler.Scores;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by marctowler on 03/08/2015.
 */
public class core extends JavaPlugin implements Listener {

    File statsFile;
    FileConfiguration stats;
    ScoreboardManager sbm = Bukkit.getScoreboardManager();
    Scoreboard board = sbm.getNewScoreboard();



    @Override
    public void onEnable() {
        getLogger().info("Loading Hardcore Score files");

        if((statsFile = new File(getDataFolder(), "scores.yml")).exists()) {
            stats = YamlConfiguration.loadConfiguration(statsFile);
        } else {
            statsFile = new File(getDataFolder(), "scores.yml");
            stats = YamlConfiguration.loadConfiguration(statsFile);
        }

        getServer().getPluginManager().registerEvents(this, this);

        Objective obj = board.registerNewObjective("Kill Stats", "playerKillCount");
        obj.setDisplayName("Kill Count");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "Kills:"));
        showScoreboard();
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving Scores");

        saveFile();

    }

    /*public void addKills(String playername, Integer killsToAdd) {
        if (stats.getInt(playername + ".kills") == 0) {
            stats.set(playername + ".kills", killsToAdd);
            saveFile();
            return;
        }
        stats.set(playername + ".kills", stats.getInt(playername + ".kills") + killsToAdd);
        saveFile();
    }*/

    public void addKill(String player) {
        getLogger().info("Kills set to: " + (getKills(player) + 1));
        stats.set("Players."+player+".Kills", getKills(player)+1);
        board.getObjective(DisplaySlot.SIDEBAR).getScore(player).setScore(getKills(player));
        saveFile();
    }

    public int getKills(String player) {
        try {
            return stats.getInt("Players."+player+".Kills");
        } catch (Exception e) {
            return 0;
        }
    }

    public void saveFile() {
        try {
            stats.save(statsFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getDeaths(String player) {
        try {
            return stats.getInt("Players."+player+".Deaths");
        } catch (Exception e) {
            return 0;
        }
    }

    public void addDeath(String player) {
        stats.set("Players."+player+".Deaths", getDeaths(player)+1);
        saveFile();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        showScoreboard();
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e){
        if(e.getEntity().getKiller() instanceof Player){
            Player victim = e.getEntity();
            Player killer = victim.getKiller();

            addKill(killer.getName());
            addDeath(victim.getName());
        }
    }

    public void showScoreboard() {



        for(Player current : Bukkit.getOnlinePlayers()) {
            current.setScoreboard(board);
        }

    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(cmd.getName().equalsIgnoreCase("topkills")) {


            return true;
        }

        return false;
    }
}
