package source.overload;

import java.util.List;

public class LocalVariablesNameCollision
{
    public void foo()
    {
    }

    public void foo(List<Item> items)
    {
        for (Item item : items)
        {
        }

        Item item = items.get(0);
    }
}

class Item {}