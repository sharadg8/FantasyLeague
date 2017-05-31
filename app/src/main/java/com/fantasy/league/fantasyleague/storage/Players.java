package com.fantasy.league.fantasyleague.storage;

/**
 * Created by Sharad on 15-May-17.
 */

public class Players extends FbPlayers {
    public int    score1;
    public int    score2;
    private String name;

    public Players(String userId, String name) {
        super();

        this.userId = userId;
        this.name = name;
        this.player1 = "";
        this.player2 = "";
    }

    public Players(FbPlayers fbPlayer) {
        super();

        this.userId = fbPlayer.userId;
        this.timestamp = fbPlayer.timestamp;
        this.player1 = fbPlayer.player1;
        this.player2 = fbPlayer.player2;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
