package school;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
public final class CsvExport {
    private CsvExport() {}
    static String cell(String value) {
        String stripped = value.stripLeading();
        if (!stripped.isEmpty() && "=+-@".indexOf(stripped.charAt(0)) >= 0) value = "'" + value;
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    public static String render(SchoolFacade school) {
        StringBuilder out = new StringBuilder("Student ID,Student,Course,Assessments,Score,Result\r\n");
        for (Enrollment e : school.enrollments()) {
            var result = school.result(e);
            out.append(cell(e.studentId())).append(',').append(cell(school.personName(e.studentId()))).append(',')
                .append(cell(e.courseCode())).append(',').append(e.assessments().size()).append(',')
                .append(result.map(r -> String.format(Locale.ROOT, "%.2f", r.score())).orElse("")).append(',')
                .append(cell(result.map(GradeResult::label).orElse("Not graded"))).append("\r\n");
        }
        return out.toString();
    }
    public static void write(SchoolFacade school, Path path) throws IOException {
        Files.writeString(path, render(school), StandardCharsets.UTF_8);
    }
}
