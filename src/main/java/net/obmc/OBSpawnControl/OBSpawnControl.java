package net.obmc.OBSpawnControl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class OBSpawnControl extends JavaPlugin
{

	static Logger log = Logger.getLogger("Minecraft");

	public static OBSpawnControl instance;
	private EventListener listener;

	private static ArrayList<String> worldlist = new ArrayList<String>();
	private HashMap<String, Location> spawnpoints = new HashMap<String, Location>();

	private static String plugin = "OBSpawnControl";
	private static String pluginprefix = "[" + plugin + "] ";
	private static String chatmsgprefix = ChatColor.AQUA + "" + ChatColor.BOLD + plugin + ChatColor.DARK_GRAY + ChatColor.BOLD + " » " + ChatColor.LIGHT_PURPLE + "";
	private static String logmsgprefix = pluginprefix + "- ";
	
	public OBSpawnControl() {
		instance = this;
	}

	// Make our (public) main class methods and variables available to other classes
	public static OBSpawnControl getInstance() {
    	return instance;
    }

	// plugin enable
	public void onEnable() {

		refreshWorldList();

		initializeStuff();

		registerStuff();

		log.log(Level.INFO, getLogMsgPrefix() + "Plugin Version " + this.getDescription().getVersion() + " activated!");
	}

	// plugin disable
	public void onDisable() {
		log.log(Level.INFO, getLogMsgPrefix() + "Plugin deactivated!");
	}

	// initialize plugin
	public void initializeStuff() {
		this.saveDefaultConfig();
		Configuration config = this.getConfig();
		
		// load up spawnpoints for worlds in config
		ConfigurationSection cfgspawnlist = config.getConfigurationSection( "spawnpoints" );
		if ( cfgspawnlist != null ) {
			for ( String cfgworld : cfgspawnlist.getKeys(false) ) {
		
			if ( !worldlist.contains( cfgworld ) ) continue;

				log.log( Level.INFO, getLogMsgPrefix() + "Loading " + cfgworld + " spawn point");
				Location spawnpoint = createSpawnPoint( cfgworld, (String) cfgspawnlist.get( cfgworld) );
				if ( spawnpoint == null ) {
					log.log( Level.INFO, getLogMsgPrefix() + "Invalid spawn point provided for " + cfgworld);
				}
				spawnpoints.put( cfgworld, spawnpoint );
			}
		} else {
			log.log( Level.INFO, getLogMsgPrefix() + "No spawn points to load");
		}
	}

	// register our listeners
	public void registerStuff() {
		// event listener
        this.listener = new EventListener();
        this.getServer().getPluginManager().registerEvents((Listener)this.listener, (Plugin)this);
        // command listener
        this.getCommand("obspawn").setExecutor(new CommandListener());
	}

    // see if a world has an active spawn point
	public boolean hasSpawnPoint( String world ) {
		if ( spawnpoints.keySet().contains( world ) ) {
			return true;
		}
		return false;
	}

	// return a spawn point for a world
	public Location getSpawnPoint( String world ) {
		return spawnpoints.get( world );
	}

	// return active spawn list
	public HashMap<String, Location> getSpawnPoints() {
		return this.spawnpoints;
	}

	// add spawn point to active spawn list and config
	public void setSpawnPoint( Location location ) {
		spawnpoints.put( location.getWorld().getName(), location );
		addSpawnConfig( location );
		log.log(Level.INFO,getLogMsgPrefix() + "Created spawn for " + location.getWorld().getName() + " at " + location.toString());
	}

	// add a spawn point to the config
	private void addSpawnConfig( Location location ) {
		this.saveDefaultConfig();
		String locationstr = BigDecimal.valueOf( location.getX() ).setScale( 3, RoundingMode.HALF_UP ).doubleValue() + "," +
				BigDecimal.valueOf( location.getY() ).setScale( 3, RoundingMode.HALF_UP ).doubleValue() + "," +
				BigDecimal.valueOf( location.getZ() ).setScale( 3, RoundingMode.HALF_UP ).doubleValue()+ "," +
				BigDecimal.valueOf( location.getPitch() ).setScale( 3, RoundingMode.HALF_UP ).doubleValue()+ "," +
				BigDecimal.valueOf( location.getYaw() ).setScale( 3, RoundingMode.HALF_UP ).doubleValue();
		Configuration config = this.getConfig();
		ConfigurationSection cfgspawnlist = config.getConfigurationSection( "spawnpoints" );
		if ( cfgspawnlist == null ) {
			config.createSection( "spawnpoints" );
			cfgspawnlist = config.getConfigurationSection( "spawnpoints" );
		}
		cfgspawnlist.set( location.getWorld().getName(), locationstr );
		this.saveConfig();
	}

	// load a spawnpoint for a world if it's in the config
	public void loadSpawnPoint( String world ) {
		Configuration config = this.getConfig();
		ConfigurationSection cfgspawnlist = config.getConfigurationSection( "spawnpoints" );
		if ( cfgspawnlist != null ) {
			if ( cfgspawnlist.contains( world ) ) {
				Location spawnpoint = createSpawnPoint( world, (String) cfgspawnlist.get( world) );
				if ( spawnpoint == null ) {
					log.log( Level.INFO, getLogMsgPrefix() + "Invalid spawn point provided for " + world);
				}
				spawnpoints.put( world, spawnpoint );
			}
		}
	}

	// remove a spawnpoint
	public void delSpawnPoint( Location location ) {
		spawnpoints.remove( location.getWorld().getName() );
		delSpawnConfig( location );
		log.log(Level.INFO,getLogMsgPrefix() + "Removed spawn for " + location.getWorld().getName() + " at " + location.toString());
	}
	
	// remove a spawn from config
	private void delSpawnConfig( Location location) {
		Configuration config = this.getConfig();
		ConfigurationSection cfgspawnlist = config.getConfigurationSection( "spawnpoints" );
		if ( cfgspawnlist != null ) {
			if ( cfgspawnlist.contains( location.getWorld().getName() ) ) {
				cfgspawnlist.set( location.getWorld().getName(), null);
				this.saveConfig();		
			}
		}
	}

	// Consistent messaging
	public String getPluginName() {
		return plugin;
	}
	public String getPluginPrefix() {
		return pluginprefix;
	}
	public String getChatMsgPrefix() {
		return chatmsgprefix;
	}
	public String getLogMsgPrefix() {
		return logmsgprefix;
	}
	
	// build a list of worlds on the server
	public void refreshWorldList() {
		worldlist.clear();
		for ( World world : Bukkit.getWorlds() ) {
			worldlist.add( world.getName() );
		}
	}

	// create a new spawnpoint by validating the coordinates
	private Location createSpawnPoint ( String world, String coordinatestr ) {
		Location spawnpoint = null;
		if ( coordinatestr != null ) {
			coordinatestr.replaceAll( " ", "" );
			String[] spawncoords = coordinatestr.split( "," );
			if ( spawncoords.length < 3 ) {
				log.log( Level.INFO, getLogMsgPrefix() + "Invalid override spawn setting. 3 or more coordinate values (X,Y,Z) needed." );
				return null;
			}
			Double x = null; Double y = null; Double z = null;
			try {
				x = Double.parseDouble( spawncoords[0] );
				y = Double.parseDouble( spawncoords[1] );
				z = Double.parseDouble( spawncoords[2] );
				if ( y > Bukkit.getWorld( world ).getMaxHeight() ) { y = (double) (Bukkit.getWorld( world ).getMaxHeight()-1); }
				if ( y < Bukkit.getWorld( world ).getMinHeight() ) { y = (double) Bukkit.getWorld( world ).getMinHeight(); }
			} catch ( NumberFormatException e ) {
				log.log(Level.INFO, getLogMsgPrefix() + "Invalid override spawn coordinates for '" + world + "'");
				return null;
			}
			// get pitch and yaw
			Float yaw = null; Float pitch = null;
			if ( spawncoords.length > 3 && spawncoords.length < 6 ) {
				try {
					pitch = Float.parseFloat(spawncoords[3]);
					yaw = Float.parseFloat(spawncoords[4]);
					if ( pitch < -180.0f ) { pitch = -180.0f; }
					if ( pitch >  180.0f ) { pitch =  180.0f; }
					if ( yaw < -90.0f ) { yaw = -90.0f; }
					if ( yaw >  90.0f ) { yaw =  90.0f; }
				} catch (NumberFormatException e) {
					pitch = 0.0f;
					yaw = 0.0f;
				}
				spawnpoint = new Location( Bukkit.getWorld( world ), x, y, z, pitch, yaw );
			} else {
				spawnpoint = new Location( Bukkit.getWorld( world ), x, y, z );
			}
		} else {
			log.log(Level.INFO, getLogMsgPrefix() + "Unable to read spawn coordinates from config for '" + world + "'");
			return null;
		}
		return spawnpoint;
	}
}
