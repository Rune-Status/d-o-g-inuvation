package org.rspeer.game.providers;

public interface RSDirectXModel extends RSModel {

    int[] getZVertices();

    short[] getYTriangles();

    short[] getXTriangles();

    int[] getXVertices();

    short[] getZTriangles();

    int[] getYVertices();
}