package itu.zazart.erpnext.service.hr;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

@Service
public class ExportPdfService {

    public byte[] generateTodoListPdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Titre
        document.add(new Paragraph("Ma To-Do List").setBold().setFontSize(20));

        // Création de la liste
        List list = new List();

        list.add(new ListItem("Apprendre iText"));
        list.add(new ListItem("Tester la génération de PDF"));
        list.add(new ListItem("Créer un export PDF personnalisé"));
        list.add(new ListItem("Intégrer à mon application Spring Boot"));

        document.add(list);

        document.close();

        return baos.toByteArray();
    }

    public byte[] generateModernPdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Marges
        document.setMargins(20, 20, 20, 20);

        // Titre principal
        Paragraph title = new Paragraph("Ma To-Do List")
                .setFontSize(24)
                .setBold()
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Tableau moderne
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 5}))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setBackgroundColor(new DeviceRgb(245, 245, 245));

        // En-têtes
        table.addHeaderCell(new Cell().add(new Paragraph("#").setBold()).setBackgroundColor(new DeviceRgb(230, 230, 250)));
        table.addHeaderCell(new Cell().add(new Paragraph("Tâche")).setBold().setBackgroundColor(new DeviceRgb(230, 230, 250)));

        // Contenu dynamique simulé
        String[] tasks = {
                "Préparer l’environnement GCP",
                "Déployer une base de données PostgreSQL",
                "Configurer la CI/CD",
                "Créer le design du Salary Slip PDF",
                "Finaliser la connexion ERPNext"
        };

        for (int i = 0; i < tasks.length; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))).setPadding(5));
            table.addCell(new Cell().add(new Paragraph(tasks[i])).setPadding(5));
        }

        // Ajout du tableau
        document.add(table);

        // Signature
        Paragraph footer = new Paragraph("Généré automatiquement — " + LocalDate.now())
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(30);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }


}
