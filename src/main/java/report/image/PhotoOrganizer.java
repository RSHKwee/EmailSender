package report.image;

import java.io.File;
import java.util.*;
import java.util.regex.*;

public class PhotoOrganizer {
    
    // Extract house number from folder name like "3871TD15"
    private static Integer extractHouseNumber(String folderName) {
        // Pattern: 4 digits + 2 letters + 1 or more digits (house number)
        Pattern pattern = Pattern.compile("^\\d{4}[A-Z]{2}(\\d+)$");
        Matcher matcher = pattern.matcher(folderName);
        
        if (matcher.matches()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    // Check if number is odd/even
    private static boolean isEven(int number) {
        return number % 2 == 0;  // Standard odd/even check[citation:1]
    }
}