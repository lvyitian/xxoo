
package awa.gui;

import java.util.Objects;
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
  public final int linenum;
  public final int maxx;
  public final int awanum;

  public AwaGUI(final Player p, final int linenum, final int maxx, final int awanum, final Player target)
  {
    this.p = p;
    this.target = target;
    if (linenum <= 0) {
      throw new IllegalArgumentException("linenum should >0 but equals to " + linenum);
    }
    if ((maxx > 8) || (maxx < 0)) {
      throw new IllegalArgumentException("maxx should <=8 and >=0 but equals to " + maxx);
    }
    if (awanum <= 2) {
      throw new IllegalArgumentException("awanum should >2 but equals to " + awanum);
    }
    if ((linenum * (maxx + 1)) < awanum) {
      throw new IllegalArgumentException(
          "no enough space! linenum: " + linenum + " maxx: " + maxx + " awanum: " + awanum);
    }
    this.linenum = linenum;
    this.maxx = maxx;
    this.awanum = awanum;
    this.pages = new Vector<>();
    this.pages.add(Bukkit.createInventory(p, linenum * 9));
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
            if ((xy[0] >= 0) && (xy[0] <= maxx) && (xy[1] >= 0) && (xy[1] <= (linenum - 1))) {
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
                AwaGUI.this.turn = !AwaGUI.this.turn;
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
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.isSame(x, xy[1], ct)) {
        count++;
      } else {
        break;
      }
    }
    for (int x = xy[0] + 1; x <= this.maxx; x++) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.isSame(x, xy[1], ct)) {
        count++;
      } else {
        break;
      }
    }
    if (count >= (this.awanum - 1)) {
      this.win(ct);
      return;
    }
    count = 0;
    for (int y = xy[1] - 1; y >= 0; y--) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.isSame(xy[0], y, ct)) {
        count++;
      } else {
        break;
      }
    }
    for (int y = xy[1] + 1; y <= (this.linenum - 1); y++) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.isSame(xy[0], y, ct)) {
        count++;
      } else {
        break;
      }
    }
    if (count >= (this.awanum - 1)) {
      this.win(ct);
      return;
    }
    count = 0;
    for (int x = xy[0] - 1, y = xy[1] - 1; (x >= 0) && (y >= 0); x--, y--) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.isSame(x, y, ct)) {
        count++;
      } else {
        break;
      }
    }
    for (int x = xy[0] + 1, y = xy[1] + 1; (x <= this.maxx) && (y <= (this.linenum - 1)); x++, y++) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.isSame(x, y, ct)) {
        count++;
      } else {
        break;
      }
    }
    if (count >= (this.awanum - 1)) {
      this.win(ct);
      return;
    }
    count = 0;
    for (int x = xy[0] - 1, y = xy[1] + 1; (x >= 0) && (y <= (this.linenum - 1)); x--, y++) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.isSame(x, y, ct)) {
        count++;
      } else {
        break;
      }
    }
    for (int x = xy[0] + 1, y = xy[1] - 1; (x <= this.maxx) && (y >= 0); x++, y--) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.isSame(x, y, ct)) {
        count++;
      } else {
        break;
      }
    }
    if (count >= (this.awanum - 1)) {
      this.win(ct);
      return;
    }
    boolean end = true;
    for (int x = 0; x <= this.maxx; x++) {
      for (int y = 0; y <= (this.linenum - 1); y++) {
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
    } else {
      final Vector<Integer[]> emptyslots = new Vector<>();
      for (int x = 0; x <= this.maxx; x++) {
        for (int y = 0; y <= (this.linenum - 1); y++) {
          if (this.getAwaBySlot(this.xyToSlot(x, y)) == null) {
            emptyslots.add(new Integer[] { x, y });
          }
        }
      }
      boolean turnend = true;
      for (final Integer[] i : emptyslots) {
        if (this.hasNext(i, emptyslots, true)) {
          turnend = false;
          break;
        }
      }
      boolean notturnend = true;
      for (final Integer[] i : emptyslots) {
        if (this.hasNext(i, emptyslots, false)) {
          notturnend = false;
          break;
        }
      }
      if (turnend || notturnend) {
        AwaGUI.this.isEnd = true;
        AwaGUI.this.close();
        if (turnend && notturnend) {
          this.p.sendMessage("平局");
          this.target.sendMessage("平局");
        } else {
          this.win(!turnend);
        }
      }
    }
  }

  public boolean cotainsPoint(final int x, final int y, final Vector<Integer[]> all)
  {
    return all.parallelStream().anyMatch(i -> Objects.equals(x, i[0]) && Objects.equals(y, i[1]));
  }

  public boolean hasNext(final Integer[] xy, final Vector<Integer[]> all, final boolean ct)
  {
    int count = 0;
    for (int x = xy[0] - 1; x >= 0; x--) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.cotainsPoint(x, xy[1], all) || this.isSame(x, xy[1], ct)) {
        count++;
      } else {
        break;
      }
    }
    for (int x = xy[0] + 1; x <= this.maxx; x++) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.cotainsPoint(x, xy[1], all) || this.isSame(x, xy[1], ct)) {
        count++;
      } else {
        break;
      }
    }
    if (count >= (this.awanum - 1)) {
      return true;
    }
    count = 0;
    for (int y = xy[1] - 1; y >= 0; y--) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.cotainsPoint(xy[0], y, all) || this.isSame(xy[0], y, ct)) {
        count++;
      } else {
        break;
      }
    }
    for (int y = xy[1] + 1; y <= (this.linenum - 1); y++) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.cotainsPoint(xy[0], y, all) || this.isSame(xy[0], y, ct)) {
        count++;
      } else {
        break;
      }
    }
    if (count >= (this.awanum - 1)) {
      return true;
    }
    count = 0;
    for (int x = xy[0] - 1, y = xy[1] - 1; (x >= 0) && (y >= 0); x--, y--) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.cotainsPoint(x, y, all) || this.isSame(x, y, ct)) {
        count++;
      } else {
        break;
      }
    }
    for (int x = xy[0] + 1, y = xy[1] + 1; (x <= this.maxx) && (y <= (this.linenum - 1)); x++, y++) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.cotainsPoint(x, y, all) || this.isSame(x, y, ct)) {
        count++;
      } else {
        break;
      }
    }
    if (count >= (this.awanum - 1)) {
      return true;
    }
    count = 0;
    for (int x = xy[0] - 1, y = xy[1] + 1; (x >= 0) && (y <= (this.linenum - 1)); x--, y++) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.cotainsPoint(x, y, all) || this.isSame(x, y, ct)) {
        count++;
      } else {
        break;
      }
    }
    for (int x = xy[0] + 1, y = xy[1] - 1; (x <= this.maxx) && (y >= 0); x++, y--) {
      if (count >= (this.awanum - 1)) {
        break;
      }
      if (this.cotainsPoint(x, y, all) || this.isSame(x, y, ct)) {
        count++;
      } else {
        break;
      }
    }
    return (count >= (this.awanum - 1));
  }

  public AwaGUI(final Player p, final int linenum, final int maxx, final int awanum, final Player target,
      final int page)
  {
    this(p, linenum, maxx, awanum, target);
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
