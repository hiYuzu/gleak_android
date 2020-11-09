package com.hb712.gleak_android.rtsp.widget;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/9 11:05
 */
public class FileMediaDataSource implements IMediaDataSource {

    private RandomAccessFile mFile;
    private long mFileSize;

    public FileMediaDataSource(File file) throws IOException {
        mFile = new RandomAccessFile(file, "r");
        mFileSize = mFile.length();
    }

    @Override
    public int readAt(long l, byte[] bytes, int i, int i1) throws IOException {
        if (mFile.getFilePointer() != l)
            mFile.seek(l);
        if (i1 == 0)
            return 0;
        return mFile.read(bytes, 0, i1);
    }

    @Override
    public long getSize() throws IOException {
        return mFileSize;
    }

    @Override
    public void close() throws IOException {
        mFileSize = 0;
        mFile.close();
        mFile = null;
    }
}
