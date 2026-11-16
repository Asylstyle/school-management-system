package school.grading;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GradingPolicies {
    private static final Map<String, GradingStrategy> POLICIES = new LinkedHashMap<>();

    static {
        POLICIES.put("letters", new WeightedGrading());
        POLICIES.put("pass", new PassFailGrading());
    }

    private GradingPolicies() {
    }

    public static GradingStrategy byName(String name) {
        GradingStrategy policy = POLICIES.get(name);
        if (policy == null) {
            throw new IllegalArgumentException("Unknown grading policy. Choose: " + names());
        }
        return policy;
    }

    public static String names() {
        return String.join("/", POLICIES.keySet());
    }
}
