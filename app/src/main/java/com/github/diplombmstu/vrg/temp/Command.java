package com.github.diplombmstu.vrg.temp;

/**
 * TODO add comment
 */
public interface Command<T> extends PacketBody
{
    int getCode();

    T getBody();
}
