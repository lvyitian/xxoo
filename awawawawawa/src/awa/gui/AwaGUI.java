
package awa.gui;

import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import awa.Main;

public class AwaGUI implements IGUIWrapper
{
  private final Player p;
  private int currentPage;
  public volatile Vector<Inventory> pages;
  public volatile boolean turn = true;
  public final Player target;
  public volatile boolean isEnd;
  public final Vector<Awa> awas = new Vector<>();

  public AwaGUI(final Player p, final Player target)
  {
    this.p = p;
    this.target = target;
    this.pages = new Vector<>();
    this.pages.add(Bukkit.createInventory(p, 27));
    final AwaGUI instance = this;
    Main.getInstance().registerListener(new Listener()
    {
      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
      public void onPlayerCloseInventory(final InventoryCloseEvent e)
      {
        if (instance.pages.contains(e.getInventory())) {
          if (!AwaGUI.this.isEnd) {
            AwaGUI.this.isEnd = true;
            AwaGUI.this.close();
          }
        }
      }
    });
    Main.getInstance().registerListener(new Listener()
    {
      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
      public void onPlayerClickInventory(final InventoryClickEvent e)
      {
        if (instance.pages.contains(e.getClickedInventory())) {
          e.setCancelled(true);
          if (AwaGUI.this.isEnd) {
            return;
          }
          if (e.getClick() != ClickType.LEFT) {
            return;
          }
          if ((AwaGUI.this.turn && e.getWhoClicked().equals(AwaGUI.this.p))
              || (!AwaGUI.this.turn && e.getWhoClicked().equals(AwaGUI.this.target))) {
            final int[] xy = AwaGUI.this.slotToXY(e.getRawSlot());
            if ((xy[0] >= 0) && (xy[0] <= 2) && (xy[1] >= 0) && (xy[1] <= 2)) {
              if (e.getCurrentItem().getType() == Material.AIR) {
                if (AwaGUI.this.turn) {
                  e.getClickedInventory().setItem(e.getRawSlot(),
                      new ItemBuilder().setType(Material.WOOL).setAmount(1).setDamage((short) 14).create());
                } else {
                  e.getClickedInventory().setItem(e.getRawSlot(),
                      new ItemBuilder().setType(Material.WOOL).setAmount(1).setDamage((short) 15).create());
                }
                AwaGUI.this.awas.add(new Awa(AwaGUI.this.turn, e.getRawSlot()));
                AwaGUI.this.update(e.getRawSlot());
                AwaGUI.this.turn=!AwaGUI.this.turn;
              }
            }
          }
        }
      }
    });
  }

  public Awa getAwaBySlot(final int slot)
  {
    return this.awas.parallelStream().filter(i -> i.index == slot).findFirst().orElse(null);
  }

  public int xyToSlot(final int x, final int y)
  {
    return (y * 9) + x;
  }

  public int[] slotToXY(final int slot)
  {
    final int[] ret = new int[2];
    ret[0] = slot % 9;
    ret[1] = slot / 9;
    return ret;
  }

  public boolean isSame(final int x, final int y, final boolean ori)
  {
    final Awa awa = this.getAwaBySlot(this.xyToSlot(x, y));
    if (awa == null) {
      return false;
    }
    return awa.turn == ori;
  }

  public void win(final boolean turn)
  {
    AwaGUI.this.isEnd = true;
    AwaGUI.this.close();
    if (turn) {
      this.p.sendMessage("你赢了");
      this.target.sendMessage("你输了");
    } else {
      this.p.sendMessage("你输了");
      this.target.sendMessage("你赢了");
    }
  }

  public void update(final int slot)
  {
    final int[] xy = this.slotToXY(slot);
    final boolean ct = this.getAwaBySlot(slot).turn;
    int count = 0;
    for (int x = xy[0] - 1; x >= 0; x--) {
      if (count >= 2) {
        break;
      }
      if (this.isSame(x, xy[1], ct)) {
        count++;
      }
    }
    for (int x = xy[0] + 1; x <= 2; x++) {
      if (count >= 2) {
        break;
      }
      if (this.isSame(x, xy[1], ct)) {
        count++;
      }
    }
    if (count >= 2) {
      this.win(ct);
      return;
    }
    count = 0;
    for (int y = xy[1] - 1; y >= 0; y--) {
      if (count >= 2) {
        break;
      }
      if (this.isSame(xy[0], y, ct)) {
        count++;
      }
    }
    for (int y = xy[1] + 1; y <= 2; y++) {
      if (count >= 2) {
        break;
      }
      if (this.isSame(xy[0], y, ct)) {
        count++;
      }
    }
    if (count >= 2) {
      this.win(ct);
      return;
    }
    count = 0;
    for (int x = xy[0] - 1, y = xy[1] - 1; (x >= 0) && (y >= 0); x--, y--) {
      if (count >= 2) {
        break;
      }
      if (this.isSame(x, y, ct)) {
        count++;
      }
    }
    for (int x = xy[0] + 1, y = xy[1] + 1; (x <= 2) && (y <= 2); x++, y++) {
      if (count >= 2) {
        break;
      }
      if (this.isSame(x, y, ct)) {
        count++;
      }
    }
    if (count >= 2) {
      this.win(ct);
      return;
    }
    count = 0;
    for (int x = xy[0] - 1, y = xy[1] + 1; (x >= 0) && (y >= 0); x--, y++) {
      if (count >= 2) {
        break;
      }
      if (this.isSame(x, y, ct)) {
        count++;
      }
    }
    for (int x = xy[0] + 1, y = xy[1] - 1; (x <= 2) && (y <= 2); x++, y--) {
      if (count >= 2) {
        break;
      }
      if (this.isSame(x, y, ct)) {
        count++;
      }
    }
    if (count >= 2) {
      this.win(ct);
      return;
    }
    boolean end = true;
    for (int x = 0; x <= 2; x++) {
      for (int y = 0; y <= 2; y++) {
        if (this.getAwaBySlot(this.xyToSlot(x, y)) == null) {
          end = false;
          break;
        }
      }
    }
    if (end) {
      AwaGUI.this.isEnd = true;
      AwaGUI.this.close();
      this.p.sendMessage("平局");
      this.target.sendMessage("平局");
    }
  }

  public AwaGUI(final Player p, final Player target, final int page)
  {
    this(p, target);
    this.setPage(page);
  }

  public static class Awa
  {
    public final boolean turn;
    public final int index;

    public Awa(final boolean turn, final int index)
    {
      this.turn = turn;
      this.index = index;
    }
  }

  @Override
  public void open()
  {
    this.p.openInventory(this.getInventory());
    this.target.openInventory(this.getInventory());
  }

  @Override
  public int getPageNum()
  {
    return this.pages.size();
  }

  @Override
  public void close()
  {
    if (this.pages.contains(this.p.getOpenInventory().getTopInventory())) {
      this.p.closeInventory();
    }
    if (this.pages.contains(this.target.getOpenInventory().getTopInventory())) {
      this.target.closeInventory();
    }
  }

  @Override
  public void setPage(final int page)
  {
    int temp = page;
    if (temp >= this.pages.size()) {
      temp = this.pages.size() - 1;
    } else if (temp < 0) {
      temp = 0;
    }
    this.currentPage = temp;
  }

  @Override
  public void refresh()
  {
    final boolean opened = this.pages.contains(this.p.getOpenInventory().getTopInventory());
    this.pages = new Vector<>();
    this.pages.add(Bukkit.createInventory(this.p, 27));
    this.setPage(this.currentPage);
    if (opened) {
      this.open();
    }
  }

  @Override
  public int getPage()
  {
    return this.currentPage;
  }

  @Override
  public Inventory getInventory()
  {
    return this.pages.get(this.currentPage);
  }

  @Override
  public Player getOwner()
  {
    return this.p;
  }
}
