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
import itu.zazart.erpnext.model.hr.SalaryComponent;
import itu.zazart.erpnext.model.hr.SalarySlip;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private void generateTableCell(String[] fields,String[] values,Table table) throws IOException {
        for (int i = 0; i < fields.length; i++) {
            table.addCell(getLabelCell(fields[i]));
            table.addCell(getLabelCell(":"));
            table.addCell(getValueCell(values[i]));
        }
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

        String[] leftFields = {"Employee Name", "Employee ID", "Department", "Designation"};
        String[] leftValues = {salarySlip.getEmployee_name(),salarySlip.getEmployee(),"",""};
        generateTableCell(leftFields,leftValues,leftInnerTable);

        Cell leftCell = new Cell().add(leftInnerTable)
                .setPadding(0)
                .setBorder(Border.NO_BORDER);

        // -----------------------------------------------------------------------------------------------------------------
        Cell rightCell = new Cell();
        Table rightInnerTable = new Table(1)
                .useAllAvailableWidth();

        Cell netPayCell = new Cell()
                .add(getParagraph( salarySlip.getCurrency()+" "+salarySlip.getNet_pay().toString(),18,"#000000"))
                .add(getParagraph("Employee Net Pay",10,"#69886f"))
                .setBackgroundColor(getColor("#edfcf1"))
                .setBorder(Border.NO_BORDER)
                .setPaddings(15,15,15,15);
        rightInnerTable.addCell(netPayCell);

        Table payDetailsTable = new Table(UnitValue.createPercentArray(new float[]{4f, 0.5f, 2.5f}))
                .useAllAvailableWidth()
                .setMargins(10, 10, 10, 10);

        String[] payDetailsFields = {"Payment Days","Total Working Days"};
        String[] payDetailsValues = {salarySlip.getPayment_days().toString(),salarySlip.getTotal_working_days().toString()};
        generateTableCell(payDetailsFields,payDetailsValues,payDetailsTable);

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

        String[] leftFields = {"Posting Date", "Status", "Salary Structure", "Start Date","End Date"};
        String[] leftValues = {
                salarySlip.getPosting_date().toString(),
                salarySlip.getStatus(),
                salarySlip.getSalary_structure(),
                salarySlip.getStart_date().toString(),
                salarySlip.getEnd_date().toString()
        };
        generateTableCell(leftFields,leftValues,leftInnerTable);

        Cell leftCell = new Cell().add(leftInnerTable)
                .setPadding(0)
                .setBorder(Border.NO_BORDER);

        Cell rightCell = new Cell();

        Table rightInnerTable = new Table(UnitValue.createPercentArray(new float[]{5, 0.7f, 5}))
                .useAllAvailableWidth();
        String[] rightFields = {"Payroll Frenquency","Unmarked Days","Leave withoud Day","Absent Days"};
        String[] rightValues = {
                salarySlip.getPayroll_frequency(),
                salarySlip.getUnmarked_days().toString(),
                salarySlip.getLeave_without_pay().toString(),
                salarySlip.getAbsent_days().toString()
        };
        generateTableCell(rightFields,rightValues,rightInnerTable);

        rightCell.add(rightInnerTable)
                .setBorder(new SolidBorder(getColor("#c1c7cc"), 0.5f))
                .setBorder(Border.NO_BORDER)
                .setPadding(0);

        mainTable.addCell(leftCell);
        mainTable.addCell(rightCell);

        document.add(mainTable);
    }

    private Cell getCellHeaderComponent(String title) throws IOException {
        return new Cell()
                .add(getParagraph(title, 10, "#333333").setBold().setMarginBottom(5))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(getColor("#c1c7cc"), 0.5f))
                .setTextAlignment((title.equals("AMOUNT")) ? TextAlignment.RIGHT : TextAlignment.LEFT);
    }

    private Table createComponentTable(int marginRight, int marginLeft, String componentType) throws IOException {
        return new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                .setMargins(5,marginRight, 5,marginLeft)
                .useAllAvailableWidth()
                .addHeaderCell(getCellHeaderComponent(componentType)) // EARNINGS OR DEDUCTIONS
                .addHeaderCell(getCellHeaderComponent("AMOUNT"));
    }

    private void showSalaryComponent(List<SalaryComponent> salaryComponents,Table table) throws IOException {
        for (int i = 0; i < salaryComponents.size(); i++) {
            Paragraph compName = getComponentPargraph(salaryComponents.get(i).getSalaryComponent());
            Paragraph componentValue = getParagraph(salaryComponents.get(i).getAmount().toString(), 9, "#333333");
            Cell componentNameCell = new Cell().add(compName.setMarginBottom(10)).setBorder(Border.NO_BORDER).setPadding(0);
            Cell componentValueCell = new Cell().add(componentValue).setTextAlignment(TextAlignment.RIGHT).setBold().setBorder(Border.NO_BORDER).setPadding(0);
            if(i==0){
                compName.setMarginTop(10);
                componentValue.setMarginTop(10);
            }
            table.addCell(componentNameCell);
            table.addCell(componentValueCell);
        }
    }

    private void addSalaryComponents(Document document,SalarySlip salarySlip) throws IOException {
        Table container = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(5);

        Table earningsTable = createComponentTable(10, 5,"EARNINGS");
        showSalaryComponent(salarySlip.getEarnings(),earningsTable);
        Table deductionsTable = createComponentTable(5, 10,"DEDUCTIONS");
        showSalaryComponent(salarySlip.getDeductions(),deductionsTable);

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
                .add(getParagraph( salarySlip.getCurrency()+" "+salarySlip.getNet_pay().toString(), 10, "#333333").setBold())
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
