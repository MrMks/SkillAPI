package com.sucy.skill.util;

import com.sucy.skill.api.player.PlayerAccounts;

public class CachePlayerAccounts {
    private final PlayerAccounts accounts;
    private final int counts;
    private int left;

    public CachePlayerAccounts(PlayerAccounts accounts, int counts) {
        this.accounts = accounts;
        this.counts = counts;
    }

    public boolean tick() {
        left++;
        return left >= counts;
    }

    public void reset() {
        left = 0;
    }

    public PlayerAccounts getAccounts() {
        return accounts;
    }
}
