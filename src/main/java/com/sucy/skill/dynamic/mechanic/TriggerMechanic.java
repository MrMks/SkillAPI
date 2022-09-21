package com.sucy.skill.dynamic.mechanic;

import com.google.common.base.Objects;
import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.ComponentRegistry;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.TriggerHandler;
import com.sucy.skill.dynamic.trigger.Trigger;
import com.sucy.skill.dynamic.trigger.TriggerComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.mechanic.TriggerMechanic
 */
public class TriggerMechanic extends MechanicComponent {

    private static final String TRIGGER = "trigger";
    private static final String DURATION = "duration";
    private static final String STACKABLE = "stackable";
    private static final String ONCE = "once";

    private final Map<Integer, List<Context>> CASTER_MAP = new HashMap<Integer, List<Context>>();
    private final Map<Integer, List<StopTask>> CASTER_TASKS = new HashMap<>();
    private TriggerHandler triggerHandler;
    private boolean once;
    private boolean stackable;

    @Override
    public void load(final DynamicSkill skill, final DataSection dataSection) {
        super.load(skill, dataSection);

        final String name = settings.getString(TRIGGER, "DEATH");
        final Trigger trigger = ComponentRegistry.getTrigger(name);
        if (trigger == null) {
            throw new IllegalArgumentException("Skill is using invalid trigger for mechanic: " + name);
        }

        final Receiver receiver = new Receiver();
        triggerHandler = new TriggerHandler(skill, "fake", trigger, receiver);
        triggerHandler.register(JavaPlugin.getPlugin(SkillAPI.class));
        once = settings.getBool(ONCE, true);
        stackable = settings.getBool(STACKABLE, true);
    }

    @Override
    public String getKey() {
        return "trigger";
    }

    @Override
    public boolean execute(
            final LivingEntity caster, final int level, final List<LivingEntity> targets) {

        final int ticks = (int)(20 * parseValues(caster, DURATION, level, 5));

        boolean worked = false;
        for (final LivingEntity target : targets) {
            if (!stackable && CASTER_MAP.containsKey(target.getEntityId()))
                continue;

            if (!CASTER_MAP.containsKey(target.getEntityId())) {
                CASTER_MAP.put(target.getEntityId(), new ArrayList<>());
            }
            if (!CASTER_TASKS.containsKey(caster.getEntityId())) {
                CASTER_TASKS.put(caster.getEntityId(), new ArrayList<>());
            }
            triggerHandler.init(target, level);

            final Context context = new Context(caster, level);
            CASTER_MAP.get(target.getEntityId()).add(context);
            StopTask task;
            SkillAPI.schedule(task = new StopTask(target, context), ticks);
            CASTER_TASKS.get(caster.getEntityId()).add(task);
            worked = true;
        }
        return worked;
    }

    private void remove(final LivingEntity target, final Context context) {
        final List<Context> contexts = CASTER_MAP.get(target.getEntityId());
        if (contexts == null) return;

        contexts.remove(context);
        if (contexts.isEmpty()) {
            CASTER_MAP.remove(target.getEntityId());
            triggerHandler.cleanup(target);
        }
    }

    @Override
    protected void doCleanUp(LivingEntity caster) {
        List<StopTask> tasks = CASTER_TASKS.remove(caster.getEntityId());
        if (tasks == null) return;
        for (StopTask task : tasks) {
            task.run();
            task.cancel();
        }
    }

    private class StopTask extends BukkitRunnable {

        private final LivingEntity target;
        private final Context context;

        public StopTask(final LivingEntity target, final Context context) {
            this.target = target;
            this.context = context;
        }

        @Override
        public void run() {
            remove(target, context);
        }
    }

    private class Receiver extends TriggerComponent {

        private Receiver() {
            final DataSection data = new DataSection();
            TriggerMechanic.this.settings.save(data);
            this.settings.load(data);
        }

        @Override
        public boolean execute(final LivingEntity target, final int level, final List<LivingEntity> targets) {
            if (!CASTER_MAP.containsKey(target.getEntityId())) return false;

            final List<Context> contexts;
            if (once)
                contexts = CASTER_MAP.remove(target.getEntityId());
            else
                contexts = CASTER_MAP.get(target.getEntityId());

            final List<LivingEntity> targetList = new ArrayList<LivingEntity>();
            targetList.add(target);

            contexts.removeIf(context -> !context.caster.isValid());
            for (final Context context : contexts) {
                DynamicSkill.getCastData(context.caster).put("listen-target", targetList);
                TriggerMechanic.this.executeChildren(context.caster, context.level, targets);
            }

            return true;
        }
    }

    private static class Context {
        public final LivingEntity caster;
        public final int level;

        public Context(final LivingEntity caster, final int level) {
            this.caster = caster;
            this.level = level;
        }

        @Override
        public boolean equals(final Object other) {
            if (other == this) return true;
            if (!(other instanceof Context)) return false;
            final Context context = (Context) other;
            return context.caster == caster && context.level == level;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(caster, level);
        }
    }
}
