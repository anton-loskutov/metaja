package org.metaja.template.ops;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Collections {

    Collections(){}

    public static <T, U> void iterate(Collection<T> collection, U u, BiConsumer<T, U> consumer, Runnable between) {
        Iterator<T> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return;
        }
        consumer.accept(iterator.next(), u);
        while (iterator.hasNext()) {
            between.run();
            consumer.accept(iterator.next(), u);
        }
    }

    public static <T> void iterate(Collection<T> collection, Consumer<T> consumer, Runnable between) {
        iterate(collection, null, (t,u) -> consumer.accept(t), between);
    }

    public static <T, U> void iterate(Collection<T> collection, U u, BiConsumer<T, U> consumer) {
        iterate(collection, u, consumer, ()->{});
    }

    public static <T> void iterate(Collection<T> collection, Consumer<T> consumer) {
        iterate(collection, null, (t,u) -> consumer.accept(t), ()->{});
    }
}
