
package awa.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface IGUIWrapper
{
  void open();

  void close();

  int getPageNum();

  int getPage();

  void setPage(int page);

  void refresh();

  Inventory getInventory();

  Player getOwner();
}
