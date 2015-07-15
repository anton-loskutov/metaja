package org.metaja.common;

public interface IComposite<T> {

    T instance();

    void delegate(T t);
}
