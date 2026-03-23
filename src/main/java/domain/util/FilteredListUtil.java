package domain.util;

import java.util.List;
import java.util.function.Predicate;

public class FilteredListUtil {

    public static <T> List<T> filter(List<T> items, Predicate<T> predicate) {
        return items.stream().filter(predicate).toList();
    }
}