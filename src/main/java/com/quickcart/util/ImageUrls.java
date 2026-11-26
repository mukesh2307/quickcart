package com.quickcart.util;

public final class ImageUrls {
    private static final String PEXELS_PHOTO_PREFIX = "https://images.pexels.com/photos/";

    private ImageUrls() {
    }

    public static String thumbnail(String imageUrl, int size) {
        if (imageUrl == null
                || !imageUrl.startsWith(PEXELS_PHOTO_PREFIX)
                || imageUrl.indexOf('?') >= 0
                || imageUrl.indexOf('#') >= 0) {
            return imageUrl;
        }

        return imageUrl + "?auto=compress&cs=tinysrgb&w=" + size
                + "&h=" + size + "&fit=crop";
    }
}
