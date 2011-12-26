/*
 * free fight arena class
 * 
 * author: slipcor
 * 
 * version: v0.4.4 - Random spawns per team, not shared
 * 
 * history:
 * 
 *     v0.4.1 - command manager, arena information and arena config check
 *     v0.4.0 - mayor rewrite, improved help
 *     v0.3.14 - timed arena modes
 *     v0.3.10 - CraftBukkit #1337 config version, rewrite
 *     v0.3.9 - Permissions, rewrite
 *     v0.3.8 - BOSEconomy, rewrite
 *     v0.3.7 - Bugfixes
 *     v0.3.6 - CTF Arena
 *     v0.3.5 - Powerups!!
 *     v0.3.4 - Customisable Teams
 *     v0.3.3 - Random spawns possible for every arena
 *     v0.3.1 - New Arena! FreeFight
 */

package net.slipcor.pvparena.arenas;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import net.slipcor.pvparena.PVPArena;
import net.slipcor.pvparena.managers.ConfigManager;
import net.slipcor.pvparena.managers.ArenaManager;
import net.slipcor.pvparena.managers.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FreeArena extends Arena {
	/*
	 * freefight constructor
	 * 
	 * - open or create a new configuration file - parse the arena config
	 */
	public FreeArena(String sName) {
		super();
		this.name = sName;
		cfg = new Config(new File("plugins/pvparena/config.free_" + name
				+ ".yml"));
		cfg.load();
		if (cfg.get("cfgver") == null) {
			ConfigManager.legacyImport(this, cfg);
		}
		ConfigManager.configParse(this, cfg);

		db.i("FreeFight Arena default overrides START");
		db.i("+teamKilling, -manualTeamSelect, +randomTeamSelect,");
		db.i("-forceWoolHead, -forceEven, +randomSpawn");
		db.i("only one team: free");
		db.i("FreeFight Arena default overrides END");

		cfg.set("general.teamkill", true);
		cfg.set("general.manual", false);
		cfg.set("general.random", true);
		cfg.set("general.woolhead", false);
		cfg.set("general.forceeven", false);
		cfg.set("general.randomSpawn", true);
		cfg.set("teams", null);
		cfg.set("teams.free", "WHITE");
		cfg.save();
		
		paTeams.clear();
		paTeams.put("free", ChatColor.WHITE.name());
	}
	
	/*
	 * stick a player into the standard team
	 */
	@Override
	public void chooseColor(Player player) {
		if (!(playerManager.getPlayerTeamMap().containsKey(player.getName()))) {
			tpPlayerToCoordName(player, "lounge");
			playerManager.setTeam(player, "free");
			ArenaManager.tellPlayer(player,
					PVPArena.lang.parse("youjoinedfree"));
			playerManager.tellEveryoneExcept(player,
					PVPArena.lang.parse("playerjoinedfree", player.getName()));

		} else {
			ArenaManager.tellPlayer(player,
					PVPArena.lang.parse("alreadyjoined"));
		}
	}

	/*
	 * return "only one player/team alive"
	 * 
	 * - if only one player/team is alive: - announce winning player - teleport
	 * everyone out - give rewards - check for bets won
	 */
	@Override
	public boolean checkEndAndCommit() {
		if (playerManager.getPlayerTeamMap().size() > 1) {
			return false;
		}

		Set<String> set = playerManager.getPlayerTeamMap().keySet();
		Iterator<String> iter = set.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();

			playerManager.tellEveryone(PVPArena.lang.parse("playerhaswon",
					ChatColor.WHITE + o.toString()));

			Player z = Bukkit.getServer().getPlayer(o.toString());
			StatsManager.addWinStat(z, "free", this);
			resetPlayer(z, cfg.getString("tp.win", "old"));
			giveRewards(z); // if we are the winning team, give reward!
			playerManager.setClass(z, "");
		}
		reset();
		return true;
	}
	
	@Override
	public String getType() {
		return "free";
	}
}