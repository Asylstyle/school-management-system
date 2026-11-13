package school;

import java.nio.file.Files;

final class ExportTests {
    static void run() throws Exception {
        SchoolFacade school = new SchoolFacade(new EventBus());
        DemoData.load(school);
        Checks.equal(true, CsvExport.render(school).contains("89.20,\"B\""));
        Checks.equal(true, CsvExport.render(school).contains("Not graded"));
        Checks.equal("\"A,\"\"B\"\"\"", CsvExport.cell("A,\"B\""));
        Checks.equal("\"'=1+1\"", CsvExport.cell("=1+1"));
        var file = Files.createTempFile("school-gradebook", ".csv");
        try {
            CsvExport.write(school, file);
            Checks.equal(CsvExport.render(school), Files.readString(file));
        } finally { Files.deleteIfExists(file); }
    }
}
