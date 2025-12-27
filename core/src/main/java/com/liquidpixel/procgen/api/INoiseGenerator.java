package com.liquidpixel.procgen.api;

public interface INoiseGenerator {

    float getValue(int x, int y);

    float getValue(int x, int y, int width, int height);

}
