package org.medianik.lendyou.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

public interface Jsonable {
    @NonNull
    @Override
    String toString();

    JsonElement toJson();
}
