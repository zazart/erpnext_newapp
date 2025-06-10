package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.dto.DataImport;
import itu.zazart.erpnext.dto.ImportError;
import itu.zazart.erpnext.model.hr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {
    private final CompanyService companyService;
    private final EmployeeService employeeService;
    private final SalaryComponentService salaryComponentService;
    private final SalaryStructureService salaryStructureService;
    private final SalaryStructureAssignmentService salaryStructureAssignmentService;
    private final SalarySlipService salarySlipService;
    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    public ImportService(CompanyService companyService, EmployeeService employeeService, SalaryComponentService salaryComponentService, SalaryStructureService salaryStructureService, SalaryStructureAssignmentService salaryStructureAssignmentService, SalarySlipService salarySlipService) {
        this.companyService = companyService;
        this.employeeService = employeeService;
        this.salaryComponentService = salaryComponentService;
        this.salaryStructureService = salaryStructureService;
        this.salaryStructureAssignmentService = salaryStructureAssignmentService;
        this.salarySlipService = salarySlipService;
    }

    public String checkNaturalCompanyAbbreviation(String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            return "";
        }
        String[] words = companyName.trim().split("\\s+");
        StringBuilder abbreviation = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                abbreviation.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return abbreviation.toString();
    }

    public String generateAbbrIfExist(String newAbbr, String initalAbbr, DataImport dataImport, int alternative) {
        List<String> abbrList = dataImport.getCompanyAbbrList();
        boolean found = false;
        for (String abbr : abbrList) {
            if (abbr.equalsIgnoreCase(newAbbr)) {
                found = true;
                break;
            }
        }
        if (found) {
            return generateAbbrIfExist(initalAbbr+"_"+alternative, initalAbbr, dataImport, alternative+1);
        }
        return newAbbr;
    }


    public Company validateCompany(DataImport dataImport,String companyName) {
        Company company = new Company();
        company.setName(companyName);
        List<Company> existingCompanies = dataImport.getExistingCompanies();
        for (Company existingCompany : existingCompanies) {
            if (existingCompany.getName().equalsIgnoreCase(companyName)) {
                return existingCompany;
            }
        }
        List<Company> companyList = dataImport.getCompanyList();
        for (Company companyItem : companyList) {
            if (companyItem.getName().equalsIgnoreCase(companyName)) {
                return companyItem;
            }
        }
        String initialAbbr = checkNaturalCompanyAbbreviation(companyName);
        company.setDefaultCurrency("USD");
        company.setCountry("Madagascar");
        String abbreviation = generateAbbrIfExist(initialAbbr,initialAbbr, dataImport, 1);
        company.setAbbr(abbreviation);
        companyList.add(company); // add if not exist
        dataImport.getCompanyAbbrList().add(abbreviation);
        return company;
    }


    public void newImportError(List<ImportError> importErrors, ImportError baseError, String messageError) {
        ImportError error = new ImportError();
        error.setFileName(baseError.getFileName());
        error.setLineNumber(baseError.getLineNumber());
        error.setRawData(baseError.getRawData());
        error.setErrorMessage(messageError);
        importErrors.add(error);
    }

    public LocalDate validDate(String dateStr, List<ImportError> importErrors,ImportError baseError) {
        dateStr = dateStr.trim().replaceAll("\u00A0", "");

        String[] formats = {"yyyy-MM-dd", "yyyy/MM/dd", "dd-MM-yyyy", "dd/MM/yyyy"};

        for (String format : formats) {
            try {
                logger.debug("Trying to parse date '{}' with format '{}'", dateStr, format);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
                        .withResolverStyle(ResolverStyle.STRICT);
                TemporalAccessor ta = formatter.parse(dateStr);
                int day = ta.get(ChronoField.DAY_OF_MONTH);
                int month = ta.get(ChronoField.MONTH_OF_YEAR);
                int year = ta.get(ChronoField.YEAR_OF_ERA);
                return LocalDate.of(year, month, day);
            } catch (DateTimeParseException e) {
                logger.debug("Parsing failed with format '{}'", format);
            } catch (Exception e) {
                logger.warn("Unexpected error while parsing date '{}' with format '{}'", dateStr, format, e);
            }
        }
        String errorMessage = "Invalid date format : " + dateStr;
        newImportError(importErrors, baseError,errorMessage);
        return null;
    }

    public LocalDate validDateOfBirth(String dateStr, List<ImportError> importErrors,ImportError baseError) {
        LocalDate dateOfBirth = validDate(dateStr, importErrors, baseError);
        if (dateOfBirth != null) {
            if (dateOfBirth.isAfter(LocalDate.now())) {
                String errorMessage = "Date of birth cannot be in the future : " + dateStr;
                newImportError(importErrors, baseError,errorMessage);
                return null;
            }
        }
        return dateOfBirth;
    }

    public String validGender(String genderStr, List<ImportError> importErrors,ImportError baseError) {
        String[] maleKeywords = {"Male", "Masculin", "Homme"};
        String[] femaleKeywords = {"Female", "Feminin", "Femme"};
        for (String maleKeyword : maleKeywords) {
            if (genderStr.equalsIgnoreCase(maleKeyword)) {
                return "Male";
            }
        }
        for (String femaleKeyword : femaleKeywords) {
            if (genderStr.equalsIgnoreCase(femaleKeyword)) {
                return "Female";
            }
        }
        String errorMessage = "Invalid gender : " + genderStr;
        newImportError(importErrors, baseError,errorMessage);

        return "";
    }

    public void newEmployee(DataImport dataImport, String[] tokens, List<ImportError> importErrors,ImportError baseError) {
        Employee emp = new Employee();
        emp.setRef(tokens[0].trim());
        emp.setLastName(tokens[1].trim());
        emp.setFirstName(tokens[2].trim());
        emp.setGender(validGender(tokens[3].trim(),importErrors,baseError));
        emp.setDateOfJoining(validDate(tokens[4].trim(),importErrors, baseError));
        emp.setDateOfBirth(validDateOfBirth(tokens[5].trim(),importErrors, baseError));
        Company company = validateCompany(dataImport,tokens[6].trim());
        emp.setCompany(company.getName());

        dataImport.getEmployeeList().add(emp);
    }

    public SalaryStructure isExistingSalaryStructure(DataImport dataImport, List<ImportError> importErrors, String salaryStructureName) {
        List<SalaryStructure> salaryStructures = dataImport.getExistingSalaryStructures();
        for (SalaryStructure salaryStructure : salaryStructures) {
            if (salaryStructure.getName().equalsIgnoreCase(salaryStructureName)) {
                return salaryStructure;
            }
        }
        return null;
    }

    public SalaryStructure isSalaryStructureInNewList(DataImport dataImport, String salaryStructureName) {
        List<SalaryStructure> salaryStructures = dataImport.getSalaryStructureList();
        for (SalaryStructure salaryStructure : salaryStructures) {
            if (salaryStructure.getName().equalsIgnoreCase(salaryStructureName)) {
                return salaryStructure;
            }
        }
        return null;
    }

    private static SalaryComponent getSalaryComponentIfExist(SalaryComponent sc, DataImport dataImport, boolean testRedefinition) {
        List<SalaryComponent> existingSalaryComponents = dataImport.getExistingSalaryComponents();
        List<SalaryComponent> salaryComponentList = dataImport.getSalaryComponentList();
        SalaryComponent existing = new  SalaryComponent();
        for (SalaryComponent item : existingSalaryComponents) {
            if (item.getName().equalsIgnoreCase(sc.getName())) {
                if (!testRedefinition) {   // Stop here if you are just checking its existence
                    return item;
                }
                if (!item.getType().equalsIgnoreCase(sc.getType())) { // if he tries to define another type
                    return item;
                }
            }
        }
        for (SalaryComponent item : salaryComponentList) {
            if (item.getName().equalsIgnoreCase(sc.getName())) {
                if (!testRedefinition) {   // Stop here if you are just checking its existence
                    return item;
                }
                if (!item.getType().equalsIgnoreCase(sc.getType())) { // if he tries to define another type
                    return item;
                }
            }
        }
        return null;
    }

    public boolean detectSalaryComponentRedefinition(DataImport dataImport,SalaryComponent sc,List<ImportError> importErrors, ImportError baseError) {
        SalaryComponent existing = getSalaryComponentIfExist(sc, dataImport, true);
        if (existing!=null) {
            String errorMessage = "Cannot redefine Salary Component '"+existing.getName()+"' ";
            errorMessage += "already exists with Type '"+existing.getType()+"' ";
            errorMessage += "and abbreviation '"+existing.getType()+"'";
            newImportError(importErrors, baseError,errorMessage);
            return true;
        }
        return false;
    }

    private static SalaryComponent getSalaryComponent(String[] tokens) {
        SalaryComponent salaryComponent = new SalaryComponent();
        salaryComponent.setName(tokens[1].trim());
        salaryComponent.setSalaryComponent(tokens[1].trim());
        salaryComponent.setSalaryComponentAbbr(tokens[2].trim());
        // Validation Type here
        String formatted = tokens[3].trim().toLowerCase();
        formatted = formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        salaryComponent.setType(formatted);

        salaryComponent.setFormula(tokens[4].trim());
        return salaryComponent;
    }


    public void validateComponent(DataImport dataImport, SalaryStructure ss, String[] tokens,List<ImportError> importErrors,ImportError baseError) {
        SalaryComponent salaryComponent = getSalaryComponent(tokens);
        if (detectSalaryComponentRedefinition(dataImport, salaryComponent, importErrors, baseError)) {
            return;
        }
        SalaryComponent existing = getSalaryComponentIfExist(salaryComponent, dataImport, false);
        if (salaryComponent.getType().equalsIgnoreCase("earning")){
            ss.getEarnings().add(salaryComponent);
        } else {
            ss.getDeductions().add(salaryComponent);
        }

        if (existing == null) {
            dataImport.getSalaryComponentList().add(salaryComponent);
        }
    }

    public void newSalaryStructure(DataImport dataImport, String[] tokens, List<ImportError> importErrors,ImportError baseError) {
        String salaryStructureName = tokens[0].trim();
        SalaryStructure existingSalaryStructure = isExistingSalaryStructure(dataImport,importErrors, salaryStructureName);
        if (existingSalaryStructure != null) {
            String errorMessage = "The salary structure "+salaryStructureName+ " already exists in the database";
            newImportError(importErrors, baseError,errorMessage);
            return;
        }

        SalaryStructure salaryStructure = isSalaryStructureInNewList(dataImport,salaryStructureName);
        if (salaryStructure != null) {
            validateComponent(dataImport, salaryStructure, tokens,importErrors, baseError);
        }
        else {
            Company company = validateCompany(dataImport,tokens[5].trim());

            SalaryStructure newSalaryStructure = new SalaryStructure();
            newSalaryStructure.setName(salaryStructureName);
            newSalaryStructure.setCompany(company.getName());
            newSalaryStructure.setEarnings(new ArrayList<>());
            newSalaryStructure.setDeductions(new ArrayList<>());
            validateComponent(dataImport, newSalaryStructure, tokens,importErrors, baseError);
            dataImport.getSalaryStructureList().add(newSalaryStructure);
        }
    }

    public int isSalaryStructureAssignementInNewList(DataImport dataImport,SalaryStructureAssignment ssa) {
        List<SalaryStructureAssignment> list = dataImport.getSalaryStructureAssignmentList();
        int statusCode = 0;
        for (SalaryStructureAssignment item : list) {
            if (item.getEmployeeObject().getRef().equals(ssa.getEmployeeObject().getRef()) &&
                item.getFromDate().isEqual(ssa.getFromDate())) {
                statusCode = 1; // Redefinition
                if (item.getBase().compareTo(ssa.getBase()) == 0 &&
                    item.getSalaryStructure().equalsIgnoreCase(ssa.getSalaryStructure())) {
                    statusCode = 2; // Duplicate line
                }
            }
        }
        return statusCode;
    }

    public Employee checkEmployeeByRef(DataImport dataImport, String ref) {
        List<Employee> employeeList = dataImport.getEmployeeList();
        for (Employee employee : employeeList) {
            if (employee.getRef().equalsIgnoreCase(ref)) {
                return employee;
            }
        }
        return null;
    }

    public BigDecimal validateBaseAmount(List<ImportError> importErrors, ImportError baseError, String baseAmount) {
        if (baseAmount == null || baseAmount.trim().isEmpty()) {
            String errorMessage = "Base amount is missing or empty.";
            newImportError(importErrors, baseError,errorMessage);
            return null;
        }
        try {
            String normalized = baseAmount.trim().replace(',', '.');
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            String errorMessage = "Invalid base amount: '" + baseAmount + "'. Expected a numeric value.";
            newImportError(importErrors, baseError,errorMessage);
            return null;
        }
    }

    public void newSalaryStructureAssignement(DataImport dataImport, String[] tokens, List<ImportError> importErrors,ImportError baseError) {
        SalaryStructureAssignment ssa = new SalaryStructureAssignment();
        String ref = tokens[1].trim();
        String salary = tokens[3].trim();

        SalaryStructure salaryStructure = isExistingSalaryStructure(dataImport,importErrors,salary);
        if  (salaryStructure == null) {
            salaryStructure = isSalaryStructureInNewList(dataImport,salary);
        }

        Employee employee = checkEmployeeByRef(dataImport, ref);

        ssa.setFromDate(validDate(tokens[0].trim(),importErrors, baseError));
        ssa.setEmployeeObject(employee);
        ssa.setBase(validateBaseAmount(importErrors, baseError, tokens[2]));

        if (salaryStructure != null) {
            ssa.setSalaryStructure(salaryStructure.getName());
            ssa.setCompany(salaryStructure.getCompany());
            ssa.setCurrency(salaryStructure.getCurrency());
        }

        int statusCode = isSalaryStructureAssignementInNewList(dataImport,ssa);
        if (statusCode == 1) {
            String errorMessage = "The Salary Structure Assignment for employee "+ssa.getEmployeeObject().getRef();
            errorMessage += " on " + ssa.getFromDate() + " could not be redefined.";
            newImportError(importErrors, baseError,errorMessage);
        }
        if (statusCode == 2) { // Duplicated row
             return;
        }
        dataImport.getSalaryStructureAssignmentList().add(ssa);
    }

    public void readAndValidateFile(DataImport dataImport, List<ImportError> importErrors, int fileNumber, int numberColumn) throws Exception {
        MultipartFile file = null;
        switch (fileNumber) {
            case 1 -> file = dataImport.getFile1();
            case 2 -> file = dataImport.getFile2();
            case 3 -> file = dataImport.getFile3();
            default -> throw new IllegalArgumentException("Unsupported file number: " + fileNumber);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            ImportError baseError = new ImportError();
            baseError.setFileName("File "+ fileNumber);
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                baseError.setLineNumber(lineNumber);
                baseError.setRawData(line);
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }
                String[] tokens = line.split(",");

                if (tokens.length < numberColumn){
                    String errorMessage = "Invalid row: less than " +numberColumn+ "columns";
                    newImportError(importErrors, baseError,errorMessage);
                    continue;
                }

                switch (fileNumber) {
                    case 1 -> newEmployee(dataImport,tokens,importErrors,baseError);
                    case 2 -> newSalaryStructure(dataImport,tokens,importErrors,baseError);
                    case 3 -> newSalaryStructureAssignement(dataImport,tokens,importErrors,baseError);
                }
            }
        } catch (IOException e) {
            logger.error("Error while reading file {}", fileNumber, e);
            throw new RuntimeException(e);
        }
    }


    public void prepareImportContext(DataImport dataImport,String sid) {
        dataImport.setExistingCompanies(companyService.getAllCompanies(sid));
        dataImport.setExistingEmployees(employeeService.getAllEmployee(sid));
        dataImport.setExistingSalaryComponents(salaryComponentService.getAllSalaryComponent(sid));
        dataImport.setExistingSalaryStructures(salaryStructureService.getAllSalaryStructure(sid));
        dataImport.setExistingSalaryStructureAssignments(salaryStructureAssignmentService.getAllSalaryStructureAssignment(sid));
        dataImport.initAbbrList();
    }

    public void insertAll(DataImport dataImport, String sid) {
        for (Company company : dataImport.getCompanyList()) {// Company
            company.setCompanyName(company.getName());
            companyService.newCompany(sid, company);
        }
        for (Employee employee : dataImport.getEmployeeList()) {    // Employee
            employeeService.newEmployee(sid, employee);
        }
        for (SalaryComponent salaryComponent : dataImport.getSalaryComponentList()) {   // Salary Component
            salaryComponentService.newSalaryComponent(sid, salaryComponent);
        }
        for (SalaryStructure salaryStructure : dataImport.getSalaryStructureList()) {   // Salary Structure
            salaryStructureService.newSalaryStructure(sid, salaryStructure);
        }
        for (SalaryStructureAssignment  ssa : dataImport.getSalaryStructureAssignmentList()) {  // Salary Structure Assignement
            ssa.setEmployee(ssa.getEmployeeObject().getName());
            SalarySlip salarySlip = new SalarySlip();
            salarySlip.setEmployee(ssa.getEmployee());
            salarySlip.setCompany(ssa.getCompany());
            salarySlip.setStart_date(ssa.getFromDate());
            salarySlip.setSalary_structure(ssa.getSalaryStructure());
            salaryStructureAssignmentService.newSalaryStructureAssignment(sid, ssa);
            salarySlipService.newSalarySlip(sid, salarySlip);
        }
    }

    public void importData(DataImport dataImport, List<ImportError> importErrors,String sid) throws Exception {
        prepareImportContext(dataImport,sid);
        readAndValidateFile(dataImport,importErrors,1,7);
        readAndValidateFile(dataImport,importErrors,2,6);
        readAndValidateFile(dataImport,importErrors,3,4);

        if (!importErrors.isEmpty()){
            throw new Exception("Error while importing");
        }
        insertAll(dataImport,sid);
    }

}
