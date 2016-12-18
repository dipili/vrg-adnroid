package com.github.diplombmstu.vrg.temp;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * TODO add comment
 */
public class SetImageCommand implements Command
{
    private final byte[] imageBytes;

    public SetImageCommand(String imageFileName) throws IOException
    {
        RandomAccessFile aFile = new RandomAccessFile(imageFileName, "r");
        FileChannel inChannel = aFile.getChannel();
        long fileSize = inChannel.size();
        ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
        inChannel.read(buffer);

        buffer.flip();

        imageBytes = new byte[buffer.remaining()];
        buffer.get(imageBytes);

        inChannel.close();
        aFile.close();
    }

    @Override
    public int getCode()
    {
        return 0; // FIXME: 14.12.16
    }

    @Override
    public byte[] getBody()
    {
        return imageBytes;
    }
}
