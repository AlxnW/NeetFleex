package com.example.neetflex.patterns.prototype;

public interface IPrototype<T> {

    T shallowClone();
    T deepClone();
}
