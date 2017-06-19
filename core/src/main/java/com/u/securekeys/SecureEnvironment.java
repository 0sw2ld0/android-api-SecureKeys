package com.u.securekeys;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Bridge between native and java for accessing secure keys.
 *
 * Created by saguilera on 3/3/17.
 */
public final class SecureEnvironment {

    private static final String ENV_LIBRARY_NAME = "secure-keys";
    static final String ENV_PROCESSED_MAP_NAME = "com.u.securekeys.ProcessedMap";
    static final String ENV_PROCESSED_MAP_METHOD = "retrieve";

    private static final long NAN_LONG = -1;
    private static final String NAN_STRING = "";

    private static boolean initialized;

    static {
        System.loadLibrary(ENV_LIBRARY_NAME);

        try {
            tryNativeInit();
            initialized = true;
        } catch (Exception e) {
            initialized = false;
        }
    }

    private SecureEnvironment() throws IllegalAccessException {
        throw new IllegalAccessException("This object cant be instantiated");
    }

    @SuppressWarnings("unchecked")
    @Keep
    private static void tryNativeInit() throws Exception {
        Class<?> clazz = Class.forName(ENV_PROCESSED_MAP_NAME);
        Method method = clazz.getDeclaredMethod(ENV_PROCESSED_MAP_METHOD);
        method.setAccessible(true);

        HashMap<String, String> entries = (HashMap<String, String>) method.invoke(null);

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            _putEntry(entry.getKey(), entry.getValue());
        }
    }

    public static @NonNull String getString(@NonNull String key) {
        if (!initialized || key.isEmpty()) {
            return NAN_STRING;
        }

        return _getString(key);
    }

    public static long getLong(@NonNull String key) {
        String value = getString(key);

        if (!initialized || value.isEmpty()) {
            return NAN_LONG;
        }

        return Long.valueOf(value);
    }

    public static double getDouble(@NonNull String key) {
        String value = getString(key);

        if (!initialized || value.isEmpty()) {
            return NAN_LONG;
        }

        return Double.valueOf(value);
    }

    @Keep
    private static native String _getString(String key);
    @Keep
    private static native void _putEntry(String key, String value);

}
