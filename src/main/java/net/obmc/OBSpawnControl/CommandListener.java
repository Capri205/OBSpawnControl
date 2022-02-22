package net.obmc.OBSpawnControl;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {

	static Logger log = Logger.getLogger( "Minecraft" );
	
	private String chatmsgprefix = null;
	private String logmsgprefix = null;
	
	public CommandListener() {
		chatmsgprefix = OBSpawnControl.getInstance().getChatMsgPrefix();
		logmsgprefix = OBSpawnControl.getInstance().getLogMsgPrefix();
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {

		// for now only op can use the command
		if ( !sender.isOp() ) {
			sender.sendMessage( ChatColor.RED + "Sorry, command is reserved for server operators." );
			return true;
		}

		// usage if no arguments passed
		if ( args.length == 0 ) {
			Usage( sender );
			return true;
		}

		// process the command and any arguments
		if ( command.getName().equalsIgnoreCase( "obspawn" ) || command.getName().equals( "obs" ) ) {
			Player player = (Player) sender;
			switch (args[0].toLowerCase()) {

				case "list":
				case "show":
					HashMap<String, Location> spawnpoints = OBSpawnControl.getInstance().getSpawnPoints();
					if ( spawnpoints.size() > 0 ) {
						for ( String world : spawnpoints.keySet() ) {
							Location spawnpoint = spawnpoints.get( world );
							sender.sendMessage( chatmsgprefix + world +
									" (X:" + spawnpoint.getX() + ", Y:" + spawnpoint.getY() + ", Z:" + spawnpoint.getZ() +
									" / Pitch:" + spawnpoint.getPitch() + ", Yaw:" + spawnpoint.getYaw() + ")" );
						}
					} else {
						sender.sendMessage( chatmsgprefix + "No worlds currently using spawn control" );
					}
					break;

				case "setspawn":
					sender.sendMessage( chatmsgprefix + "spawn set for '" + player.getWorld().getName() + "'" );
					OBSpawnControl.getInstance().setSpawnPoint( player.getLocation() );
					break;

				case "delspawn":
					OBSpawnControl.getInstance().delSpawnPoint( player.getLocation() );
					sender.sendMessage( chatmsgprefix + "spawn removed for '" + player.getWorld().getName() + "'" );
					break;
					
				default:
					sender.sendMessage( chatmsgprefix + ChatColor.RED + " Unknown command");
					Usage(sender);
					break;
			}
		}
		return true;
	}

    void Usage(CommandSender sender) {
        sender.sendMessage(chatmsgprefix + "/obs show" + ChatColor.GOLD + " - Show available damage types");
        sender.sendMessage(chatmsgprefix + "/obs setspawn" + ChatColor.GOLD + " - Set override spawn for world at player location");
    }
}
