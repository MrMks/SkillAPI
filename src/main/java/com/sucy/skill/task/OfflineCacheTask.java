package com.sucy.skill.task;

import com.sucy.skill.util.CachePlayerAccounts;

import java.util.Map;

public class OfflineCacheTask implements Runnable {

    Map<String, CachePlayerAccounts> map;

    public OfflineCacheTask(Map<String, CachePlayerAccounts> map) {
        this.map = map;
    }

    @Override
    public void run() {
        map.values().removeIf(CachePlayerAccounts::tick);
    }
}
