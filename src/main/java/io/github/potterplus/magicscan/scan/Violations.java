package io.github.potterplus.magicscan.scan;

import io.github.potterplus.magicscan.rule.Rule;

import java.util.*;

/**
 * Generic utilities for creating collections of violations for rule validation.
 */
public class Violations {

    /**
     * Creates a collection of Violations.
     * @param violations The Violations to collect.
     * @return The Collection of Violations.
     */
    public static Collection<Violation> of(Violation... violations) {
        return new ArrayList<>(Arrays.asList(violations));
    }

    /**
     * Creates a collection of guaranteed non-null Violations.
     * @param violations The Violations to collect.
     * @return The Collection of Violations.
     */
    public static Collection<Violation> ofNonNull(Violation... violations) {
        List<Violation> list = new ArrayList<>();

        for (Violation v : violations) {
            if (v != null) {
                list.add(v);
            }
        }

        if (list.size() > 0) {
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Creates a singleton Collection of Violations for the provided Rule with the provided String replacements applied.
     * @param rule The Rule to create the Violation from.
     * @param replacements The String replacements to perform.
     * @return The Collection of Violations.
     */
    public static Collection<Violation> forRule(Rule<?> rule, Map<String, String> replacements) {
        return Collections.singletonList(
                Violation.builder(rule)
                        .message(rule.getViolationMessageKey())
                        .replace(replacements)
                        .build()
        );
    }

    /**
     * Creates a singleton Collection of Violations for the provided Rule.
     * @param rule The Rule to create the Violation from.
     * @return The Collection of Violations.
     */
    public static Collection<Violation> forRule(Rule<?> rule) {
        return Collections.singletonList(
                Violation.builder(rule)
                        .message(rule.getViolationMessageKey())
                        .build()
        );
    }

    /**
     * Creates an empty Collection of Violations.
     * @return An empty Collection.
     */
    public static Collection<Violation> none() {
        return Collections.emptyList();
    }
}
