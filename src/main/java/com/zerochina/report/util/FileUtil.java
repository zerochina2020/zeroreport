package com.zerochina.report.util;

import java.io.*;

/**
 * 文件处理工具类
 * @author jiaquan
 * @date 2020/9/10
 */
public class FileUtil {

    public final static int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String read(InputStream in) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return read(reader);
    }

    public static String readFromResource(String resource) {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if (in == null) {
                in = FileUtil.class.getResourceAsStream(resource);
            }
            if (in == null) {
                return null;
            }
            String text = read(in);
            return text;
        } finally {
            close(in);
        }
    }

    public static byte[] readByteArrayFromResource(String resource) throws IOException {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if (in == null) {
                return null;
            }

            return readByteArray(in);
        } finally {
            close(in);
        }
    }

    public static byte[] readByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        final int EOF = -1;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static String read(Reader reader) {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
            return writer.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("read error", ex);
        }
    }

    public static void close(Closeable x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {}
    }
}
