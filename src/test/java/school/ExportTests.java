package school;

import java.nio.file.Files;
import school.access.Access;
import school.access.AccessModes;
import school.core.CsvExport;
import school.core.SchoolFacade;
import school.events.EventBus;

final class ExportTests {
    static void run() throws Exception {
        SchoolFacade school = new SchoolFacade(new EventBus());
        DemoData.load(school);
        Access viewer = AccessModes.viewer();
        Checks.equal(true, CsvExport.render(school, viewer).contains("89.20,\"B\""));
        Checks.equal(true, CsvExport.render(school, viewer).contains("Not graded"));
        Checks.equal("\"A,\"\"B\"\"\"", CsvExport.cell("A,\"B\""));
        Checks.equal("\"'=1+1\"", CsvExport.cell("=1+1"));
        var file = Files.createTempFile("school-gradebook", ".csv");
        try {
            CsvExport.write(school, viewer, file);
            Checks.equal(CsvExport.render(school, viewer), Files.readString(file));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
