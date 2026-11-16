package school.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import school.access.Access;
import school.grading.GradeResult;

public final class CsvExport {
    private CsvExport() {
    }

    public static String cell(String value) {
        String stripped = value.stripLeading();
        if (!stripped.isEmpty() && "=+-@".indexOf(stripped.charAt(0)) >= 0) {
            value = "'" + value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    public static String render(SchoolFacade school, Access access) {
        StringBuilder out = new StringBuilder("Student ID,Student,Course,Assessments,Score,Result\r\n");
        for (Enrollment enrollment : school.enrollments(access)) {
            Optional<GradeResult> result = school.result(access, enrollment);
            String score = result.map(r -> String.format(Locale.ROOT, "%.2f", r.score())).orElse("");
            String label = result.map(GradeResult::label).orElse("Not graded");
            out.append(cell(enrollment.studentId())).append(',');
            out.append(cell(school.personName(access, enrollment.studentId()))).append(',');
            out.append(cell(enrollment.courseCode())).append(',');
            out.append(enrollment.assessments().size()).append(',');
            out.append(score).append(',');
            out.append(cell(label)).append("\r\n");
        }
        return out.toString();
    }

    public static void write(SchoolFacade school, Access access, Path path) throws IOException {
        Files.writeString(path, render(school, access), StandardCharsets.UTF_8);
    }
}
