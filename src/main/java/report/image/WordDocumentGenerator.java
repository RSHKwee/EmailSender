package report.image;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class WordDocumentGenerator {

  public static void generateWordDocument(Map<String, List<File>> groupedPhotos, String outputPath) throws Exception {
    XWPFDocument document = new XWPFDocument();

    // Add odd numbers section
    addSection(document, "Oneven Nummers", groupedPhotos.get("ODD"));

    // Add even numbers section
    addSection(document, "Even Nummers", groupedPhotos.get("EVEN"));

    try (FileOutputStream out = new FileOutputStream(outputPath)) {
      document.write(out);
    }
    document.close();
  }

  private static void addSection(XWPFDocument document, String title, List<File> photos) throws Exception {
    if (photos.isEmpty()) {
      return;
    }

    // Add title
    XWPFParagraph titlePara = document.createParagraph();
    XWPFRun titleRun = titlePara.createRun();
    titleRun.setText(title);
    titleRun.setBold(true);
    titleRun.setFontSize(14);

    // Add photos
    XWPFParagraph photoPara = document.createParagraph();

    for (File photo : photos) {
      try (FileInputStream is = new FileInputStream(photo)) {
        XWPFRun photoRun = photoPara.createRun();
        photoRun.addPicture(is, Document.PICTURE_TYPE_JPEG, photo.getName(), Units.toEMU(100), Units.toEMU(100));
        photoRun.addBreak();
        photoRun.setText("Folder: " + photo.getParentFile().getName());
        photoRun.addBreak(BreakType.TEXT_WRAPPING);
      }
    }
  }
}
