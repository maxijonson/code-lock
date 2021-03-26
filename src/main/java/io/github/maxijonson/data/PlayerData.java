package io.github.maxijonson.data;

import java.io.Serializable;
import java.util.UUID;

public class PlayerData implements Serializable {
    private static transient final long serialVersionUID = 1L;

    private UUID uuid;
    private int defaultCode = -1;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(int defaultCode) {
        this.defaultCode = defaultCode;
    }
}
