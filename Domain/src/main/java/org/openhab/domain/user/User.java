package org.openhab.domain.user;

/**
 * Created by Tony Alpskog in 2014.
 */
public class User {
    public static final String ARG_USER_ID = "User ID";
    public final String defaultImage = "http://www.icone-png.com/png/47/47162.png";

    public String getImageUrl() {
        return defaultImage;
    }
}
