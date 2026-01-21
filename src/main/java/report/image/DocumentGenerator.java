package report.image;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class DocumentGenerator {
    
    public static void generatePDF(Map<String, List<File>> groupedPhotos, 
                                  String outputPath) throws Exception {
        PDDocument document = new PDDocument();
        
        // Create sections for odd and even
        addPhotoSection(document, "Oneven Nummers (Odd Numbers)", 
                       groupedPhotos.get("ODD"));
        addPhotoSection(document, "Even Nummers (Even Numbers)", 
                       groupedPhotos.get("EVEN"));
        
        document.save(outputPath);
        document.close();
    }
    
    private static void addPhotoSection(PDDocument document, 
                                       String title, 
                                       List<File> photos) throws Exception {
        if (photos.isEmpty()) return;
        
        PDPage page = new PDPage();
        document.addPage(page);
        
        try (PDPageContentStream contentStream = 
             new PDPageContentStream(document, page)) {
            
            // Add title
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText(title);
            contentStream.endText();
            
            // Add photos (simplified layout)
            float y = 700;
            float x = 50;
            
            for (File photo : photos) {
                if (y < 100) { // New page if needed
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    y = 700;
                    x = 50;
                }
                
                BufferedImage image = ImageIO.read(photo);
                PDImageXObject pdImage = 
                    LosslessFactory.createFromImage(document, image);
                
                contentStream.drawImage(pdImage, x, y - 100, 100, 100);
                
                // Add filename below image
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.newLineAtOffset(x, y - 110);
                contentStream.showText(photo.getName());
                contentStream.endText();
                
                x += 120;
                if (x > 500) {
                    x = 50;
                    y -= 150;
                }
            }
        }
    }
}
