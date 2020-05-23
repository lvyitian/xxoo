
package awa.gui;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class ItemUtil
{
  private ItemUtil() {}
  public static void setDisplayName(final ItemStack is, final String name)
  {
    final ItemMeta im = is.getItemMeta();
    im.setDisplayName(name);
    is.setItemMeta(im);
  }

  public static void addLore(final ItemStack is, final String... lore)
  {
    ItemUtil.setLore(is, ListUtil.toArray(ListUtil.append(is.getItemMeta().getLore(), lore), String.class));
  }

  public static void setLore(final ItemStack is, final String... lore)
  {
    final ItemMeta im = is.getItemMeta();
    im.setLore(ListUtil.toList(lore));
    is.setItemMeta(im);
  }

  public static boolean setSkullOwner(final ItemStack s, final OfflinePlayer p)
  {
    final ItemMeta im = s.getItemMeta();
    if (!(im instanceof SkullMeta)) {
      return false;
    }
    final SkullMeta sm = (SkullMeta) im;
    sm.setOwningPlayer(p);
    s.setItemMeta(sm);
    return true;
  }

  public static String getDisplayName(final ItemStack is)
  {
    return is.getItemMeta().getDisplayName();
  }
}
