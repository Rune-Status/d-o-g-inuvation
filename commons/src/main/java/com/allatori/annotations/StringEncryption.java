package com.allatori.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface StringEncryption {
    String ENABLE = "enable";
    String DISABLE = "disable";
    String MAXIMUM = "maximum";
    String MAXIMUM_WITH_WARNINGS = "maximum-with-warnings";

    String value();
}
