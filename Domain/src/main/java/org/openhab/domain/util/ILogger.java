package org.openhab.domain.util;

public interface ILogger {
    void i(String tag, String message);
    void v(String tag, String message);
    void d(String tag, String message);
    void e(String tag, String message);
    void e(String tag, String message, Throwable e);
    void w(String tag, String message);
    void w(String tag, String message, Throwable e);
}
