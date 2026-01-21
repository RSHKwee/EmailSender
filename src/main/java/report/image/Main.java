package report.image;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    try {
      // 1. Specify photo directory
      File photoDir = new File("path/to/your/photos");

      // 2. Organize photos by street side
      PhotoOrganizer organizer = new PhotoOrganizer();
      Map<String, List<File>> groupedPhotos = organizer.organizePhotos(photoDir);

      // 3. Sort within groups (optional)
      groupedPhotos.get("ODD").sort(Comparator.comparing(File::getParent));
      groupedPhotos.get("EVEN").sort(Comparator.comparing(File::getParent));

      // 4. Generate PDF
      DocumentGenerator.generatePDF(groupedPhotos, "StreetPhotos.pdf");

      // OR generate Word document
      // WordDocumentGenerator.generateWordDocument(groupedPhotos,
      // "StreetPhotos.docx");

      System.out.println("Generated document with:");
      System.out.println("  Odd numbers: " + groupedPhotos.get("ODD").size() + " photos");
      System.out.println("  Even numbers: " + groupedPhotos.get("EVEN").size() + " photos");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
