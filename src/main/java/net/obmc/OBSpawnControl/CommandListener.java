package net.obmc.OBSpawnControl;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CommandListener implements CommandExecutor {

	static Logger log = Logger.getLogger("Minecraft");
	
	private Component chatmsgprefix = null;
	
	public CommandListener() {
		chatmsgprefix = OBSpawnControl.getInstance().getChatMsgPrefix();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// for now only op can use the command
		if (!sender.isOp()) {
			sender.sendMessage(Component.text("Sorry, command is reserved for server operators.", NamedTextColor.RED));
			return true;
		}

		// usage if no arguments passed
		if (args.length == 0) {
			Usage(sender);
			return true;
		}

		// process the command and any arguments
		if (command.getName().equalsIgnoreCase("obspawn") || command.getName().equals("obs")) {
			Player player = (Player) sender;
			switch (args[0].toLowerCase()) {

				case "list":
				case "show":
					HashMap<String, Location> spawnpoints = OBSpawnControl.getInstance().getSpawnPoints();
					if (spawnpoints.size() > 0) {
						for (String world : spawnpoints.keySet()) {
							Location spawnpoint = spawnpoints.get(world);
							sender.sendMessage(chatmsgprefix.append(Component.text(world +
							     " (X:" + String.format("%.3f", spawnpoint.getX()) +
							     ", Y:" + String.format("%.3f", spawnpoint.getY()) +
							     ", Z:" + String.format("%.3f", spawnpoint.getZ()) +
							     " / Pitch:" + String.format("%.3f", spawnpoint.getPitch()) +
							     ", Yaw:" + String.format("%.3f", spawnpoint.getYaw()) + ")", NamedTextColor.LIGHT_PURPLE))
							 );
						}
					} else {
						sender.sendMessage(chatmsgprefix.append(Component.text("No worlds currently using spawn control", NamedTextColor.LIGHT_PURPLE)));
					}
					break;

				case "setspawn":
					sender.sendMessage(chatmsgprefix.append(Component.text("spawn set for '" + player.getWorld().getName() + "'", NamedTextColor.LIGHT_PURPLE)));
					OBSpawnControl.getInstance().setSpawnPoint(player.getLocation());
					break;

				case "delspawn":
					OBSpawnControl.getInstance().delSpawnPoint(player.getLocation());
					sender.sendMessage(chatmsgprefix.append(Component.text("spawn removed for '" + player.getWorld().getName() + "'", NamedTextColor.LIGHT_PURPLE)));
					break;
					
				default:
					sender.sendMessage(chatmsgprefix.append(Component.text(" Unknown command", NamedTextColor.RED)));
					Usage(sender);
					break;
			}
		}
		return true;
	}

    void Usage(CommandSender sender) {
        sender.sendMessage(chatmsgprefix
            .append(Component.text("/obs show", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text(" - Show world spawn points that are set", NamedTextColor.GOLD)));
        sender.sendMessage(chatmsgprefix
            .append(Component.text("/obs setspawn", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text(" - Set override spawn for world player is in", NamedTextColor.GOLD)));
        sender.sendMessage(chatmsgprefix
            .append(Component.text("/obs delspawn", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text(" - Remove spawn point for world player is in", NamedTextColor.GOLD)));
   }
}
