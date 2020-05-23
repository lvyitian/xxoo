
package awa.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

public final class ListUtil
{
  private ListUtil()
  {

  }

  @SafeVarargs
  public static <T> ArrayList<T> toList(final T... o)
  {
    return Lists.newArrayList(o);
  }

  @SafeVarargs
  public static <T> List<T> append(final List<T> l, final T... o)
  {
    final List<T> temp = new ArrayList<>(l);
    temp.addAll(ListUtil.toList(o));
    return temp;
  }

  public static <T> T[] toArray(final List<T> l, final Class<T> clazz)
  {
    @SuppressWarnings("unchecked")
    final T[] ret = (T[]) java.lang.reflect.Array.newInstance(clazz, l.size());
    for (int i = 0; i < l.size(); i++) {
      ret[i] = l.get(i);
    }
    return ret;
  }

  public static List<String> toLowerCaseList(final List<String> l)
  {
    final List<String> ret = new ArrayList<>();
    l.forEach(i -> ret.add(i.toLowerCase(Locale.ENGLISH)));
    return ret;
  }

  public static <T> boolean hasDuplicatedElement(final List<T> l)
  {
    for (int i = 0; i < l.size(); i++) {
      for (int i2 = i + 1; i2 < l.size(); i2++) {
        if ((l.get(i) == null) && (l.get(i2) == null)) {
          return true;
        }
        if (l.get(i) == null) {
          continue;
        }
        if (l.get(i2) == null) {
          continue;
        }
        if (l.get(i).equals(l.get(i2))) {
          return true;
        }
      }
    }
    return false;
  }
}
