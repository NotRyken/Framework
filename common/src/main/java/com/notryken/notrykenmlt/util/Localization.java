package com.notryken.notrykenmlt.util;

import com.notryken.notrykenmlt.NotRykenMLT;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class Localization {
    public static String translationKey(String domain, String path) {
        return domain + "." + NotRykenMLT.MOD_ID + "." + path;
    }

    public static MutableComponent localized(String domain, String path, Object... args) {
        return Component.translatable(translationKey(domain, path), args);
    }
}
