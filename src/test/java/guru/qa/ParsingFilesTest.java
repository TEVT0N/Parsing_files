package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static com.codeborne.pdftest.assertj.Assertions.assertThat;

public class ParsingFilesTest {

    ClassLoader cl = getClass().getClassLoader();

    @Test
    void zipParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("archive.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("Resume.pdf")) {
                    PDF pdf = new PDF(zis);
                    assertThat(pdf.numberOfPages).isEqualTo(1);
                } else if (entry.getName().equals("Business_trip.xlsx")) {
                    XLS xls = new XLS(zis);
                    String stringCellValue = xls.excel.getSheetAt(1).getRow(12).getCell(1).getStringCellValue();
                    assertThat(stringCellValue).contains("DELL");
                } else if (entry.getName().equals("profile.csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> content = reader.readAll();
                    assertThat(content).contains(new String[]{"Sergey;Burmistrov"});
                } else if (entry.getName().equals("profile.json")) {
                    Gson gson = new Gson();
                    String json = new String(zis.readAllBytes());
                    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                    assertThat(jsonObject.get("surname").getAsString()).isEqualTo("Burmistrov");
                }
            }
        }
    }
}
