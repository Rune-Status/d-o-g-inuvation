package org.rspeer.game.providers;

import java.io.RandomAccessFile;

public interface RSFileOnDisk extends RSProvider {
    RandomAccessFile getRandomAccessFile();
}