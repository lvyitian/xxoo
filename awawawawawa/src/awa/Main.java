
package awa;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import awa.gui.AwaGUI;

public class Main extends JavaPlugin
{
  private static Main instance;
  public static Main getInstance()
  {
    return Main.instance;
  }
  {
    Main.instance=this;
  }
  public void registerListener(Listener lis)
  {
    Bukkit.getPluginManager().registerEvents(lis, this);
  }
  public AwaGUI open(String p,String target)
  {
    AwaGUI gui=new AwaGUI(Bukkit.getPlayer(UUID.fromString(p)),Bukkit.getPlayer(UUID.fromString(target)));
    gui.open();
    return gui;
  }
}
