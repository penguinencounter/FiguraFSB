package org.figuramc.fsb.api.json;

import com.google.gson.annotations.SerializedName;
import org.figuramc.fsb.api.avatars.EHashPair;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.HashMap;

public class FiguraUserStruct {
    @SerializedName("badges")
    public BitSet prideBadges = new BitSet();

    @SerializedName("avatar")
    public @Nullable String equippedAvatar;

    @SerializedName("avatarHash")
    public @Nullable EHashPair avatarHash;

    @SerializedName("owned")
    public HashMap<String, EHashPair> ownedAvatars = new HashMap<>();
}
