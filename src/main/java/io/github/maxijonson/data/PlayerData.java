package io.github.maxijonson.data;

import java.util.UUID;

/**
 * Relevant player data used throughout the plugin. Not to be confused with the
 * Player object which PlayerData only uses its UUID to associate the Player
 * with the PlayerData
 */
public class PlayerData extends DataEntity<PlayerData> {
    private static transient final long serialVersionUID = 1L;

    /** Unique identifier */
    private UUID uuid;

    /** The default code to use when placing a code lock. null for none. */
    private String defaultCode = null;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(String defaultCode) {
        this.defaultCode = defaultCode;
    }

    @Override
    public void sync(PlayerData entity) {
        if (defaultCode != entity.defaultCode) {
            defaultCode = entity.defaultCode;
        }
    }
}
