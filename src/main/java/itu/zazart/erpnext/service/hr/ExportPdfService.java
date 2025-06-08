package itu.zazart.erpnext.service.hr;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import itu.zazart.erpnext.model.hr.SalarySlip;
import org.springframework.stereotype.Service;

@Service
public class ExportPdfService {

    public DeviceRgb getColor(String hex){
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        return new DeviceRgb(r, g, b);
    }

    private void removeBordersFromTable(Table table) {
        for (int i = 0; i < table.getNumberOfRows(); i++) {
            for (int j = 0; j < table.getNumberOfColumns(); j++) {
                Cell cell = table.getCell(i, j);
                if (cell != null) {
                    cell.setBorder(Border.NO_BORDER);
                }
            }
        }
    }

    private Paragraph getComponentPargraph(String text) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        return new Paragraph(text)
                .setFont(font)
                .setFontSize(9)
                .setFontColor(getColor("#000000"));
    }

    private Paragraph getParagraph(String text,int fontSize,String hex) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        return new Paragraph(text)
                .setFont(font)
                .setFontSize(fontSize)
                .setFontColor(getColor(hex));
    }

    private Paragraph getParaField(String text) throws IOException {
        return getParagraph(text,10,"#746f6f");
    }

    private Cell getLabelCell(String text,Paragraph paragraph) throws IOException {
        return new Cell()
                .add(paragraph)
                .setBorder(Border.NO_BORDER)
                .setPadding(2);
    }

    private Cell getValueCell(String text, Paragraph paragraph) throws IOException {
        return new Cell()
                .add(paragraph)
                .setBorder(Border.NO_BORDER)
                .setPadding(2);
    }



    private Cell getLabelCell(String text) throws IOException {
        return new Cell()
                .add(getParaField(text))
                .setBorder(Border.NO_BORDER)
                .setPadding(2);
    }

    private Cell getValueCell(String text) throws IOException {
        return new Cell()
                .add(getParaField(text).setFontColor(getColor("#0f0f0f")))
                .setBorder(Border.NO_BORDER)
                .setPadding(2);
    }


    private void addHeader(Document document,SalarySlip salarySlip) throws IOException {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(10);

        Paragraph leftTitle = getParagraph(salarySlip.getName(),16,"#333333").setBold();
        Paragraph rightTitle = getParaField("Payslip For the Month");
        Paragraph rightSubTitle = getParagraph("June 2023",12,"#000000");

        Cell leftCell = new Cell().add(leftTitle).setVerticalAlignment(VerticalAlignment.MIDDLE);
        Cell rightCell = new Cell().setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.MIDDLE);
        rightCell.add(rightTitle);
        rightCell.add(rightSubTitle);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        removeBordersFromTable(headerTable);
        document.add(headerTable);

        SolidLine line = new SolidLine(1f);
        line.setColor(getColor("#e1e5e9"));
        LineSeparator ls = new LineSeparator(line);
        ls.setMarginBottom(15);
        document.add(ls);
    }


    private void addEmployeeSummary(Document document,SalarySlip salarySlip) throws IOException {
        Table mainTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);
        Table leftInnerTable = new Table(UnitValue.createPercentArray(new float[]{4, 0.6f, 5}))
                .useAllAvailableWidth();
        Cell employeeSummaryCell = new Cell(1, 3)
                .add(getParaField("EMPLOYEE SUMMARY").setBold().setFontColor(getColor("#4f4f4f")))
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(5);
        leftInnerTable.addHeaderCell(employeeSummaryCell);

        leftInnerTable.addCell(getLabelCell("Employee Name"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(salarySlip.getEmployee_name()));

        leftInnerTable.addCell(getLabelCell("Employee ID"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(salarySlip.getEmployee()));

        leftInnerTable.addCell(getLabelCell("Department"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(""));

        leftInnerTable.addCell(getLabelCell("Designation"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(""));

        Cell leftCell = new Cell().add(leftInnerTable)
                .setPadding(0)
                .setBorder(Border.NO_BORDER);

        // ---------------------------------------------------------------------------------------
        Cell rightCell = new Cell();
        Table rightInnerTable = new Table(1)
                .useAllAvailableWidth();

        Cell netPayCell = new Cell()
                .add(getParagraph(salarySlip.getNet_pay().toString(),18,"#000000"))
                .add(getParagraph("Employee Net Pay",10,"#69886f"))
                .setBackgroundColor(getColor("#edfcf1"))
                .setBorder(Border.NO_BORDER)
                .setPaddings(15,15,15,15);
        rightInnerTable.addCell(netPayCell);

        Table payDetailsTable = new Table(UnitValue.createPercentArray(new float[]{4f, 0.5f, 2.5f}))
                .useAllAvailableWidth()
                .setMargins(10, 10, 10, 10);
        payDetailsTable.addCell(getLabelCell("Payment Days").setBorder(Border.NO_BORDER));
        payDetailsTable.addCell(getLabelCell(":").setBorder(Border.NO_BORDER));
        payDetailsTable.addCell(getValueCell(salarySlip.getPayment_days().toString()).setBorder(Border.NO_BORDER));

        payDetailsTable.addCell(getLabelCell("Total Working Days").setBorder(Border.NO_BORDER));
        payDetailsTable.addCell(getLabelCell(":").setBorder(Border.NO_BORDER));
        payDetailsTable.addCell(getValueCell(salarySlip.getTotal_working_days().toString()).setBorder(Border.NO_BORDER));

        Cell detailsCell = new Cell()
                .add(payDetailsTable)
                .setBorder(Border.NO_BORDER);
        rightInnerTable.addCell(detailsCell);

        rightCell.add(rightInnerTable)
                .setBorder(new SolidBorder(getColor("#c1c7cc"), 0.5f))
                .setPadding(0);

        mainTable.addCell(leftCell);
        mainTable.addCell(rightCell);

        document.add(mainTable);
        DottedLine dottedLine = new DottedLine(1.5f);
        dottedLine.setColor(getColor("#c1c7cc"));
        LineSeparator ls = new LineSeparator(dottedLine);
        ls.setMarginBottom(15);
        document.add(ls);
    }



    private void addSalarySlipInformation(Document document,SalarySlip salarySlip) throws IOException {
        Table mainTable = new Table(UnitValue.createPercentArray(new float[]{4, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(20);
        Table leftInnerTable = new Table(UnitValue.createPercentArray(new float[]{4.3f, 0.6f, 5}))
                .useAllAvailableWidth();

        leftInnerTable.addCell(getLabelCell("Posting Date"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(salarySlip.getPosting_date().toString()));

        leftInnerTable.addCell(getLabelCell("Status"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(salarySlip.getStatus()));

        leftInnerTable.addCell(getLabelCell("Salary Structure"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(salarySlip.getSalary_structure()));

        leftInnerTable.addCell(getLabelCell("Start Date"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(salarySlip.getStart_date().toString()));

        leftInnerTable.addCell(getLabelCell("End Date"));
        leftInnerTable.addCell(getLabelCell(":"));
        leftInnerTable.addCell(getValueCell(salarySlip.getEnd_date().toString()));

        Cell leftCell = new Cell().add(leftInnerTable)
                .setPadding(0)
                .setBorder(Border.NO_BORDER);

        Cell rightCell = new Cell();

        Table rightInnerTable = new Table(UnitValue.createPercentArray(new float[]{5, 0.7f, 5}))
                .useAllAvailableWidth();

        rightInnerTable.addCell(getLabelCell("Payroll Frenquency"));
        rightInnerTable.addCell(getLabelCell(":"));
        rightInnerTable.addCell(getValueCell(salarySlip.getPayroll_frequency()));

        rightInnerTable.addCell(getLabelCell("Unmarked Days"));
        rightInnerTable.addCell(getLabelCell(":"));
        rightInnerTable.addCell(getValueCell(salarySlip.getUnmarked_days().toString()));

        rightInnerTable.addCell(getLabelCell("Leave withoud Day"));
        rightInnerTable.addCell(getLabelCell(":"));
        rightInnerTable.addCell(getValueCell(salarySlip.getLeave_without_pay().toString()));

        rightInnerTable.addCell(getLabelCell("Absent Days"));
        rightInnerTable.addCell(getLabelCell(":"));
        rightInnerTable.addCell(getValueCell(salarySlip.getAbsent_days().toString()));

        rightCell.add(rightInnerTable)
                .setBorder(new SolidBorder(getColor("#c1c7cc"), 0.5f))
                .setBorder(Border.NO_BORDER)
                .setPadding(0);

        mainTable.addCell(leftCell);
        mainTable.addCell(rightCell);

        document.add(mainTable);
    }

    private void addSalaryComponents(Document document,SalarySlip salarySlip) throws IOException {
        Table container = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(5);

        Table earningsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                .setMargins(5,10,5,5)
                .useAllAvailableWidth();
        earningsTable.addHeaderCell(
                new Cell()
                        .add(getParagraph("EARNINGS", 10, "#333333").setBold().setMarginBottom(5))
                        .setBorder(Border.NO_BORDER)
                        .setBorderBottom(new SolidBorder(getColor("#c1c7cc"), 0.5f))
        );

        earningsTable.addHeaderCell(
                new Cell()
                        .add(getParagraph("AMOUNT", 10, "#333333").setBold().setMarginBottom(5))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(Border.NO_BORDER)
                        .setBorderBottom(new SolidBorder(getColor("#c1c7cc"), 0.5f))

        );

//        for (int i = 0; i < salarySlip.getEarnings().size(); i++) {
//            Cell componentNameCell = new Cell();
//            Cell componentValueCell = new Cell();
//            earningsTable.addCell(new Cell().add(getComponentPargraph("Allowance").setMarginBottom(10)).setBorder(Border.NO_BORDER).setPadding(0));
//            earningsTable.addCell(new Cell().add(getParagraph("3,000.00",9,"#333333")).setTextAlignment(TextAlignment.RIGHT).setBold().setBorder(Border.NO_BORDER).setPadding(0));
//        }
//
        earningsTable.addCell(new Cell().add(getComponentPargraph("Basic Salary").setMarginBottom(10).setMarginTop(10)).setBorder(Border.NO_BORDER).setPadding(0));
        earningsTable.addCell(new Cell().add(getParagraph("20,000.00",9,"#333333").setMarginTop(10)).setTextAlignment(TextAlignment.RIGHT).setBold().setBorder(Border.NO_BORDER).setPadding(0));
        earningsTable.addCell(new Cell().add(getComponentPargraph("Bonus").setMarginBottom(10)).setBorder(Border.NO_BORDER).setPadding(0));
        earningsTable.addCell(new Cell().add(getParagraph("5,000.00",9,"#333333")).setTextAlignment(TextAlignment.RIGHT).setBold().setBorder(Border.NO_BORDER).setPadding(0));
        earningsTable.addCell(new Cell().add(getComponentPargraph("Allowance").setMarginBottom(10)).setBorder(Border.NO_BORDER).setPadding(0));
        earningsTable.addCell(new Cell().add(getParagraph("3,000.00",9,"#333333")).setTextAlignment(TextAlignment.RIGHT).setBold().setBorder(Border.NO_BORDER).setPadding(0));

        // Deductions Table
        Table deductionsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                .setMargins(5,5,5,10)
                .useAllAvailableWidth();

        deductionsTable.addHeaderCell(
                new Cell()
                        .add(getParagraph("DEDUCTIONS", 10, "#333333").setBold().setMarginBottom(5))
                        .setBorder(Border.NO_BORDER)
                        .setBorderBottom(new SolidBorder(getColor("#c1c7cc"), 0.5f))
        );

        deductionsTable.addHeaderCell(
                new Cell()
                        .add(getParagraph("AMOUNT", 10, "#333333").setBold().setMarginBottom(5))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(Border.NO_BORDER)
                        .setBorderBottom(new SolidBorder(getColor("#c1c7cc"), 0.5f))

        );
        deductionsTable.addCell(new Cell().add(getComponentPargraph("Tax").setMarginTop(10).setMarginBottom(10)).setBorder(Border.NO_BORDER).setPadding(0));
        deductionsTable.addCell(new Cell().add(getParagraph("2,000.00",9,"#333333").setMarginTop(10)).setTextAlignment(TextAlignment.RIGHT).setBold().setBorder(Border.NO_BORDER).setPadding(0));
        deductionsTable.addCell(new Cell().add(getComponentPargraph("Insurance").setMarginBottom(10)).setBorder(Border.NO_BORDER).setPadding(0));
        deductionsTable.addCell(new Cell().add(getParagraph("1,000.00",9,"#333333")).setTextAlignment(TextAlignment.RIGHT).setBold().setBorder(Border.NO_BORDER).setPadding(0));

        container.addCell(new Cell().add(earningsTable).setBorder(Border.NO_BORDER).setPadding(0));
        container.addCell(new Cell().add(deductionsTable).setBorder(Border.NO_BORDER).setPadding(0));


        Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 3, 1}))
                .setMargin(-10)
                .useAllAvailableWidth();

        totalsTable.addCell(new Cell().add(getComponentPargraph("Gross Pay").setFontColor(getColor("#333333")).setMargin(10).setBold()).setBorder(Border.NO_BORDER).setBackgroundColor(getColor("#f8f8fb")));
        totalsTable.addCell(new Cell().add(getParagraph(salarySlip.getGross_pay().toString(), 9, "#333333").setMargin(10).setBold()).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(getColor("#f8f8fb")));
        totalsTable.addCell(new Cell().add(getComponentPargraph("Total Deductions").setFontColor(getColor("#333333")).setMargin(10).setBold()).setBorder(Border.NO_BORDER).setBackgroundColor(getColor("#f8f8fb")));
        totalsTable.addCell(new Cell().add(getParagraph(salarySlip.getTotal_deduction().toString(), 9, "#333333").setMargin(10).setBold()).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setBackgroundColor(getColor("#f8f8fb")));

        Table wrapper = new Table(1).useAllAvailableWidth();
        Cell wrapperCell = new Cell()
                .setBorder(new SolidBorder(getColor("#c1c7cc"), 1f))
                .setMarginTop(20)
                .setPadding(10);

        wrapperCell.add(container);
        wrapperCell.add(totalsTable);

        wrapper.addCell(wrapperCell);

        document.add(wrapper);
    }



    private void addNetPay(Document document,SalarySlip salarySlip) throws IOException {
        Table mainTable = new Table(1)
                .useAllAvailableWidth()
                .setMarginTop(20);

        Cell rightCell = new Cell();
        Table rightInnerTable = new Table(UnitValue.createPercentArray(new float[]{5,2}))
                .useAllAvailableWidth();

        Cell netPayCell = new Cell()
                .add(getParagraph("TOTAL NET PAYABLE",9,"#333333").setBold().setMarginBottom(3))
                .add(getParagraph("Gross Earnings - Total Deductions",9,"#746f6f"))
                .setBorder(Border.NO_BORDER)
                .setPadding(10);
        rightInnerTable.addCell(netPayCell);


        Cell detailsCell = new Cell()
                .add(getParagraph("USD 30 000.00", 10, "#333333").setBold())
                .setBackgroundColor(getColor("#edfcf1"))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(Border.NO_BORDER);
        rightInnerTable.addCell(detailsCell);

        rightCell.add(rightInnerTable)
                .setBorder(new SolidBorder(getColor("#c1c7cc"), 1f))
                .setPadding(0);

        mainTable.addCell(rightCell);

        document.add(mainTable);

        Paragraph line = new Paragraph()
                .add(new Text("Amount In Words: ").setFontSize(9).setFontColor(getColor("#746f6f")))
                .add(new Text("Indian Rupee Thirty Thousand Only").setFontSize(9).setFontColor(getColor("#000000")))
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20)
                .setMarginBottom(20);

        document.add(line);


    }




    public byte[] generateSalarySlipPdf(SalarySlip salarySlip) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        document.setMargins(30, 30, 30, 30);

        addHeader(document,salarySlip);
        addEmployeeSummary(document,salarySlip);
        addSalarySlipInformation(document,salarySlip);
        addSalaryComponents(document,salarySlip);
        addNetPay(document,salarySlip);


        document.close();
        return baos.toByteArray();
    }




}
