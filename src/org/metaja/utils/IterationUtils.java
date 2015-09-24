package org.metaja.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IterationUtils {

    public static class Iteration<T> {

        private T value;
        private int index;
        private boolean last;

        public T it() {
            return value;
        }

        public int ix() {
            return index;
        }

        public boolean last() {
            return last;
        }

        private Iteration() {
        }
    }

    public static <T> Stream<Iteration<T>> stream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<Iteration<T>>() {
            private final Iteration<T> iteration = new Iteration<>();
            private boolean hasNext = iterator.hasNext();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Iteration<T> next() {
                if (!hasNext) {
                    throw new NoSuchElementException();
                }
                iteration.value = iterator.next();
                iteration.index = index++;
                iteration.last = !(hasNext = iterator.hasNext());
                return iteration;
            }
        }, 0), false);
    }

    public static <T> Stream<Iteration<T>> stream(T[] array) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<Iteration<T>>() {
            private final Iteration<T> iteration = new Iteration<>();
            private final int indexLast = array.length - 1;
            private int index = -1;

            @Override
            public boolean hasNext() {
                return index != indexLast;
            }

            @Override
            public Iteration<T> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                iteration.value = array[++index];
                iteration.index = index;
                iteration.last = !hasNext();
                return iteration;
            }
        }, 0), false);
    }

    private IterationUtils() {
    }

    // for org.metaja.template.TemplateOps
    @Deprecated
    public static IterationUtils INSTANCE = new IterationUtils();
}
