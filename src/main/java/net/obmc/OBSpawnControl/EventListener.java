package net.obmc.OBSpawnControl;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EventListener implements Listener
{
	static Logger log = Logger.getLogger( "Minecraft" );
  
	@EventHandler
	public void onPlayerJoin( PlayerJoinEvent event ) {
		Player player = event.getPlayer();
		if ( OBSpawnControl.getInstance().hasSpawnPoint( player.getWorld().getName() ) ) {
			player.teleport( OBSpawnControl.getInstance().getSpawnPoint( player.getWorld().getName() ) );
		}
	}

	@EventHandler
	public void onReSpawn( PlayerRespawnEvent event ) {
		Player player = event.getPlayer();		
		if ( OBSpawnControl.getInstance().hasSpawnPoint( player.getWorld().getName() ) ) {
			event.setRespawnLocation( OBSpawnControl.getInstance().getSpawnPoint( player.getWorld().getName() ) );
		}
	}
	@EventHandler
	public void onWorldChange( PlayerChangedWorldEvent event ) {
		Player player = event.getPlayer();
		if ( OBSpawnControl.getInstance().hasSpawnPoint( player.getWorld().getName() ) ) {
			player.teleport( OBSpawnControl.getInstance().getSpawnPoint( player.getWorld().getName() ) );
		}
	}
	
	@EventHandler
	public void onWorldInit( WorldInitEvent event ) {
		OBSpawnControl.getInstance().refreshWorldList();
		OBSpawnControl.getInstance().loadSpawnPoint( event.getWorld().getName() );
	}

	@EventHandler
	public void onWorldUnload( WorldUnloadEvent event ) {
		new BukkitRunnable() {
			public void run() {
				OBSpawnControl.getInstance().refreshWorldList();
			}
		}.runTaskLater(OBSpawnControl.getInstance(), 20L);
	}
}
