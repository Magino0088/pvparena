package net.slipcor.pvparena.runnables;

import org.bukkit.Bukkit;

import net.slipcor.pvparena.core.Debug;
import net.slipcor.pvparena.definitions.Arena;

/**
 * custom runnable class
 * 
 * -
 * 
 * implements an own runnable class in order to commit a powerup spawn in the
 * arena it is running in
 * 
 * @author slipcor
 * 
 * @version v0.6.2
 * 
 */

public class PowerupRunnable implements Runnable {
	private final Arena a;
	private Debug db = new Debug();

	/**
	 * construct a powerup spawn runnable
	 * 
	 * @param a
	 *            the arena it's running in
	 */
	public PowerupRunnable(Arena a) {
		this.a = a;
		db.i("PowerupRunnable constructor");
	}

	/**
	 * the run method, spawn a powerup
	 */
	@Override
	public void run() {
		db.i("PowerupRunnable commiting spawn");
		if (a.fightInProgress)
			a.calcPowerupSpawn();
		else {
			// deactivate the auto saving task
			Bukkit.getServer().getScheduler().cancelTask(a.SPAWN_ID);
		}
	}
}
