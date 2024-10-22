package io.github.potterplus.magicscan.rule;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.MagicScanPlugin;
import io.github.potterplus.magicscan.scan.Violation;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;

/**
 * Represents a rule to hold a Magic type to.
 */
public abstract class Rule<T> {

    @Getter
    private MagicScanPlugin plugin;

    @Getter
    private MagicScanController controller;

    public Rule(MagicScanController controller) {
        this.plugin = controller.getPlugin();
        this.controller = controller;
    }

    public abstract RuleType getRuleType();
    public abstract String getKey();
    public abstract Collection<Violation> validate(T type);

    public ConfigurationSection getConfigurationSection() {
        return getController().getRulesFile().getFileConfiguration().getConfigurationSection(getRuleType().getKey() + ".rules." + getKey());
    }

    public boolean isEnabled() {
        ConfigurationSection section = getConfigurationSection();

        if (section == null) {
            return true;
        }

        return section.getBoolean("enabled", true);
    }

    public boolean shouldValidateRule() {
        ConfigurationSection section = getConfigurationSection();

        if (section == null) return true;

        // TODO Additional validations

        return this.isEnabled();
    }

    public String getViolationMessageKey() {
        return "violations." + getRuleType().getKey() + "." + getKey();
    }

    public String getViolationMessage() {
        return getController().getMessage(getViolationMessageKey());
    }
}
