
package awa.gui;

import java.lang.reflect.Constructor;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder
{
  private ItemStack is;

  public ItemBuilder()
  {
    try {
      final Constructor<ItemStack> con = ItemStack.class.getDeclaredConstructor();
      con.setAccessible(true);
      this.is = con.newInstance();
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public ItemBuilder(final ItemStack is)
  {
    this.is = is;
  }

  public ItemBuilder setType(final Material m)
  {
    this.is.setType(m);
    return this;
  }

  public ItemBuilder setAmount(final int m)
  {
    this.is.setAmount(m);
    return this;
  }

  public ItemBuilder setDamage(final short d)
  {
    this.is.setDurability(d);
    return this;
  }

  public ItemBuilder setItemMeta(final ItemMeta i)
  {
    this.is.setItemMeta(i);
    return this;
  }

  public ItemBuilder setDisplayName(final String name)
  {
    ItemUtil.setDisplayName(this.is, name);
    return this;
  }

  public ItemBuilder setSkullOwner(final OfflinePlayer p)
  {
    ItemUtil.setSkullOwner(this.is, p);
    return this;
  }

  public ItemBuilder addLore(final String... l)
  {
    ItemUtil.addLore(this.is, l);
    return this;
  }

  public ItemBuilder setLore(final String... l)
  {
    ItemUtil.setLore(this.is, l);
    return this;
  }

  public ItemStack create()
  {
    return this.is;
  }
}
