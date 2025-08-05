package com.szymonfluder.shop.util;

import java.time.LocalDate;

public class DateProvider {
    public static String getLocalDateAsString() {
        return LocalDate.now().toString();
    }
}
