package school.access;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AccessModes {
    private static final Map<String, Access> MODES = new LinkedHashMap<>();

    static {
        MODES.put("viewer", viewer());
        MODES.put("registrar", registrar());
        MODES.put("grading", grading());
        MODES.put("admin", admin());
    }

    private AccessModes() {
    }

    public static Access viewer() {
        return new ReadOnlyAccess();
    }

    public static Access registrar() {
        return new RegistrarAccess(viewer());
    }

    public static Access grading() {
        return new GradingAccess(viewer());
    }

    public static Access admin() {
        return new GradingAccess(new RegistrarAccess(viewer()));
    }

    public static Access byName(String name) {
        Access access = MODES.get(name);
        if (access == null) {
            throw new IllegalArgumentException("Unknown access mode. Choose: " + names());
        }
        return access;
    }

    public static String names() {
        return String.join("/", MODES.keySet());
    }
}
