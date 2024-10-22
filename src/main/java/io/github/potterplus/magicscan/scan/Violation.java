package io.github.potterplus.magicscan.scan;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.rule.Rule;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a violation of a rule's conditions.
 */
@Data
public class Violation {

    public static Builder builder(Rule<?> rule) {
        return new Builder(rule);
    }

    public static class Builder {

        private Violation violation;

        public Builder(Rule<?> rule) {
            this.violation = new Violation(rule);

            violation.setMessageKey(rule.getViolationMessageKey());
        }

        public Builder message(String messageKey) {
            violation.setMessageKey(messageKey);

            return this;
        }

        public Builder replace(String replace, String with) {
            Map<String, String> replacements = violation.getReplacements();

            replacements.put(replace, with);

            violation.setReplacements(replacements);

            return this;
        }

        public Builder replace(Map<String, String> replacements) {
            violation.setReplacements(replacements);

            return this;
        }

        public Builder warning(boolean warning) {
            violation.setWarning(warning);

            return this;
        }

        public Violation build() {
            return this.violation;
        }
    }

    @NonNull
    private MagicScanController controller;

    @NonNull
    private Rule<?> rule;

    private String messageKey;

    private Map<String, String> replacements;

    private boolean warning;

    public Violation(Rule<?> rule) {
        this.controller = rule.getController();
        this.rule = rule;
        this.replacements = new HashMap<>();
    }

    @Override
    public String toString() {
        ChatColor primaryColor = warning ? ChatColor.YELLOW : ChatColor.RED;
        ChatColor secondaryColor = warning ? ChatColor.GOLD : ChatColor.DARK_RED;

        String path = "violations." + rule.getRuleType().getKey() + ".";

        if (!messageKey.startsWith(path)) {
            messageKey = path + messageKey;
        }

        String message = controller.getMessage(messageKey);

        if (replacements != null) {
            for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                message = message.replace(replacement.getKey(), secondaryColor + replacement.getValue() + primaryColor);
            }
        }

        return primaryColor + message + " (" + secondaryColor + "Rule: " + rule.getKey() + primaryColor + ")";
    }
}
