package com.innowise.orderservice.utils;

public class GlobalConstants {

    private GlobalConstants() {}

    public static final String FROM_DATE = "from_date";
    public static final String TO_DATE = "to_date";
    public static final String STATUS = "status";
    public static final String DATE_FORMAT =
            "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]):" +
                    "\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
}
