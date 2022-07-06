package io.github.potterplus.api.misc;

import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * TODO Write docs
 */
public class PluginLogger {

    public static class Instance {

        public Instance(Level level) {
            this.level = level;
        }

        public Instance() {
            this(Level.INFO);
        }

        private Level level;
        private String message;

        public Instance at(Level level) {
            this.level = level;

            return this;
        }

        public Instance with(String message) {
            this.message = message;

            return this;
        }

        public Instance with(String format, Object... objects) {
            this.message = String.format(format, objects);

            return this;
        }

        public void print() {
            Bukkit.getLogger().log(level, message);
        }
    }

    public static Instance start() {
        return new Instance();
    }

    public static Instance at(Level level) {
        return new Instance(level);
    }

    public static Instance atInfo() {
        return new Instance(Level.INFO);
    }

    public static void atInfo(String s) {
        atInfo().with(s).print();
    }

    public static Instance atWarn() {
        return new Instance(Level.WARNING);
    }

    public static void atWarn(String s) {
        atWarn().with(s).print();
    }

    public static Instance atSevere() {
        return new Instance(Level.SEVERE);
    }

    public static void atSevere(String s) {
        atSevere().with(s).print();
    }
}
