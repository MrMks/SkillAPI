/**
 * SkillAPI
 * com.sucy.skill.data.PlayerStats
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.data;

import com.rit.sucy.config.FilterType;
import com.rit.sucy.scoreboard.StatHolder;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stat provider for the MCCore stat scoreboard
 */
public class PlayerStats implements StatHolder
{
    private PlayerClass player;

    /**
     * Constructor
     *
     * @param player player to show stats for
     */
    public PlayerStats(PlayerClass player)
    {
        this.player = player;
    }

    /**
     * @return map of stats for the scoreboard
     */
    @Override
    public List<String> getNames()
    {
        List<String> stats = new ArrayList<String>();
        Settings settings = SkillAPI.getSettings();

        if (settings.isStatsShowHealth())
            stats.add(statMap.get(HEALTH));
        if (settings.isManaEnabled() && settings.isStatsShowMana())
            stats.add(player.getData().getManaName());
        if (settings.isStatsShowPoints())
            stats.add(statMap.get(POINTS));
        if (settings.isStatsShowLevel())
            stats.add(statMap.get(LEVEL));
        if (settings.isStatsShowExp())
            stats.add(statMap.get(EXP));
        if (settings.isStatsShowExpLeft())
            stats.add(statMap.get(EXP_LEFT));
        if (settings.isAttributesEnabled() && settings.isStatsShowAttrib())
            stats.add(statMap.get(ATTRIB));

        return stats;
    }

    /**
     * @return populated list of values
     */
    @Override
    public ArrayList<Integer> getValues()
    {
        ArrayList<Integer> values = new ArrayList<Integer>();

        Settings settings = SkillAPI.getSettings();
        if (settings.isStatsShowHealth())
            values.add((int) Math.ceil(player.getPlayerData().getPlayer().getHealth()));
        if (settings.isManaEnabled() && settings.isStatsShowMana())
            values.add((int) player.getPlayerData().getMana());
        if (settings.isStatsShowPoints())
            values.add(player.getPoints());
        if (settings.isStatsShowLevel())
            values.add(player.getLevel());
        if (settings.isStatsShowExp())
            values.add((int) player.getExp());
        if (settings.isStatsShowExpLeft())
            values.add(player.getRequiredExp() - (int) player.getExp());
        if (settings.isAttributesEnabled() && settings.isStatsShowAttrib())
            values.add(player.getPlayerData().getAttributePoints());

        return values;
    }

    private static final String BASE        = "Stats.";
    private static final String EXP         = "exp";
    private static final String EXP_LEFT    = "exp-left";
    private static final String HEALTH      = "health";
    private static final String LEVEL       = "level";
    private static final String POINTS      = "points";
    private static final String ATTRIB      = "attrib";

    private static final HashMap<String, String> statMap = new HashMap<String, String>();

    /**
     * Initializes the offline players used by the scoreboard. This is done
     * by the API on startup so do not call this method.
     */
    public static void init()
    {
        if (statMap.size() == 0)
        {
            load(EXP, BASE + EXP);
            load(EXP_LEFT, BASE + EXP_LEFT);
            load(HEALTH, BASE + HEALTH);
            load(LEVEL, BASE + LEVEL);
            load(POINTS, BASE + POINTS);
            load(ATTRIB, BASE + ATTRIB);
        }
    }

    /**
     * Clears the initialized offline players. This is done by the API
     * upon disable so do not call this method.
     */
    public static void clear()
    {
        statMap.clear();
    }

    private static void load(String key, String node)
    {
        statMap.put(key, SkillAPI.getLanguage().getMessage(node, true, FilterType.COLOR).get(0));
    }
}
