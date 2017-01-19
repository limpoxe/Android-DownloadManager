package com.limpoxe.downloads.utils;

public final class IoUtils {
    private IoUtils() {
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if 'closeable' is null.
     */
    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }


}
