package org.figuramc.fsb.api;

import org.figuramc.fsb.api.utils.Pair;

import java.util.Arrays;
import java.util.List;

import static org.figuramc.fsb.api.FiguraPermissionNodes.*;

public class FiguraPermissions {
    public static final List<Pair<FiguraPermissionNodes, Boolean>> PERMISSIONS_LIST = Arrays.asList(
            Pair.of(FIGURA_AVATARS_IMMORTALIZE, false),
            Pair.of(FIGURA_AVATARS_SET, false)
    );
    public static final List<Pair<FiguraPermissionNodes, String>> OPTIONS_LIST = Arrays.asList(
            Pair.of(FIGURA_PINGS_RATELIMIT, 32 + ""),
            Pair.of(FIGURA_PINGS_SIZELIMIT, 1024 + ""),
            Pair.of(FIGURA_AVATARS_SIZELIMIT, 102400 + ""),
            Pair.of(FIGURA_AVATARS_COUNTLIMIT, 1 + "")
    );
}