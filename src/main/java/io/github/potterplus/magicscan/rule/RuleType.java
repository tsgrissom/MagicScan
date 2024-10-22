package io.github.potterplus.magicscan.rule;

import lombok.*;

import java.util.function.Function;

/**
 * The types of rules.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum RuleType {

    SPELL("spells"),

    WAND("wands");

    public static class StringToRuleTypeFunction implements Function<String, RuleType> {

        @Override
        public RuleType apply(String ruleType) {
            for (RuleType type : RuleType.values()) {
                if (type.getKey().equalsIgnoreCase(ruleType)) return type;
            }

            return RuleType.valueOf(ruleType);
        }
    }

    /**
     * Resolves a RuleType from a String. First attempts to search by key and if that fails utilizes RuleType#valueOf.
     * @param ruleType The String to resolve.
     * @return The resolved RuleType.
     */
    public static RuleType resolve(String ruleType) {
        return new StringToRuleTypeFunction().apply(ruleType);
    }

    @Getter @NonNull
    private String key;
}
