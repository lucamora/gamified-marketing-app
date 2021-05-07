package it.polimi.gma.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageReader {
    public static final int MAX_IMAGE_SIZE = 4096; // maximum of 4MB

    public static byte[] read(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[MAX_IMAGE_SIZE];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        return outputStream.toByteArray();
    }
}
