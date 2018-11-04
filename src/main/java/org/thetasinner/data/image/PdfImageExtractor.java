package org.thetasinner.data.image;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*

 images.forEach(im -> {
            try {
                ImageIO.write(im, "PNG", new File("out.png"));
            } catch (IOException e) {
                System.out.println("Problem writing image to file " + e.getMessage());
            }
        });

 */

public class PdfImageExtractor {
    static ArrayList<RenderedImage> extract(File source, ExtractionProperties extractionProperties) throws IOException {
        var doc = PDDocument.load(source);

        var images = new ArrayList<RenderedImage>();
        doc.getPages().forEach(page -> images.addAll(fromResources(page.getResources(), extractionProperties)));

        return images;
    }

    private static ArrayList<RenderedImage> fromResources(PDResources resources, ExtractionProperties extractionProperties) {
        var images = new ArrayList<RenderedImage>();

        resources.getXObjectNames().forEach(xObjectName -> {
            try {
                var xObject = resources.getXObject(xObjectName);
                if (xObject instanceof PDFormXObject) {
                    images.addAll(fromResources(((PDFormXObject) xObject).getResources(), extractionProperties));
                }
                else if (xObject instanceof PDImageXObject) {
                    images.add(((PDImageXObject) xObject).getImage());
                }
            } catch (IOException e) {
                System.out.println("Error extracting resource " + e.getMessage());
            }
        });

        return images;
    }
}
