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
    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
                        .withResolverStyle(ResolverStyle.STRICT);
                TemporalAccessor ta = formatter.parse(dateStr);
                int day = ta.get(ChronoField.DAY_OF_MONTH);
                int month = ta.get(ChronoField.MONTH_OF_YEAR);
                int year = ta.get(ChronoField.YEAR_OF_ERA);
                return LocalDate.of(year, month, day);
            } catch (DateTimeParseException e) {
                logger.error("Invalid date format: {}", dateStr, e);
            } catch (Exception e) {
                logger.error("Error parsing date: {}", dateStr, e);
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

    public void readAndValidateFile1(DataImport dataImport, List<ImportError> importErrors) {
        dataImport.setEmployeeList(new ArrayList<>());
        dataImport.setCompanyList(new ArrayList<>());
        MultipartFile file = dataImport.getFile1();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            ImportError baseError = new ImportError();
            baseError.setFileName("File 1");
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                baseError.setLineNumber(lineNumber);
                baseError.setRawData(line);
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }
                String[] tokens = line.split(",");
                if (tokens.length < 7){
                    String errorMessage = "Invalid row: less than 7 columns";
                    newImportError(importErrors, baseError,errorMessage);
                    continue;
                }

                newEmployee(dataImport,tokens,importErrors,baseError);  // New Employee
            }
        } catch (IOException e) {
            logger.error("Error while reading file 1", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isExistingSalaryStructure(DataImport dataImport, List<ImportError> importErrors, String salaryStructureName, ImportError baseError) {
        List<SalaryStructure> salaryStructures = dataImport.getExistingSalaryStructures();
        for (SalaryStructure salaryStructure : salaryStructures) {
            if (salaryStructure.getName().equalsIgnoreCase(salaryStructureName)) {
                String errorMessage = "The salary structure "+salaryStructureName+ " already exists in the database";
                newImportError(importErrors, baseError,errorMessage);
                return true;
            }
        }
        return false;
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
            if (item.getName().equalsIgnoreCase(sc.getName()) &&
                    item.getType().equalsIgnoreCase(sc.getType())) {
                return item;
            }
        }
        return null;
    }

    public boolean detectRedefinition(DataImport dataImport,SalaryComponent sc,List<ImportError> importErrors, ImportError baseError) {
        SalaryComponent existing = getSalaryComponentIfExist(sc, dataImport, true);
        if (existing!=null) {
            String errorMessage = "Cannot redefine Salary Component '"+existing.getName()+"'";
            errorMessage += "already exists with Type '"+existing.getType()+"' ";
            errorMessage += "and abbreviation '"+existing.getType()+"'";
            newImportError(importErrors, baseError,errorMessage);
            return true;
        }
        return false;
    }



    public void validateComponent(DataImport dataImport, SalaryStructure ss, String[] tokens,List<ImportError> importErrors,ImportError baseError) {
        SalaryComponent salaryComponent = new SalaryComponent();
        salaryComponent.setName(tokens[1].trim());
        salaryComponent.setSalaryComponent(tokens[1].trim());
        salaryComponent.setSalaryComponentAbbr(tokens[2].trim());
        // Validation Type here
        salaryComponent.setType(tokens[3].trim());
        salaryComponent.setFormula(tokens[4].trim());
        if (detectRedefinition(dataImport, salaryComponent, importErrors, baseError)) {
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
        if (isExistingSalaryStructure(dataImport,importErrors, salaryStructureName, baseError)) {
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

    public void readAndValidateFile2(DataImport dataImport, List<ImportError> importErrors) {
        dataImport.setSalaryComponentList(new ArrayList<>());
        dataImport.setSalaryStructureList(new ArrayList<>());
        MultipartFile file = dataImport.getFile2();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            ImportError baseError = new ImportError();
            baseError.setFileName("File 2");
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                baseError.setLineNumber(lineNumber);
                baseError.setRawData(line);
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }
                String[] tokens = line.split(",");

                if (tokens.length < 6){
                    String errorMessage = "Invalid row: less than 6 columns";
                    newImportError(importErrors, baseError,errorMessage);
                    continue;
                }

                newSalaryStructure(dataImport,tokens,importErrors,baseError);
            }
        } catch (IOException e) {
            logger.error("Error while reading file 2", e);
            throw new RuntimeException(e);
        }
    }

    public void readAndValidateFile3(DataImport dataImport, List<ImportError> importErrors) {

    }


    public void importData(DataImport dataImport, List<ImportError> importErrors) {
        readAndValidateFile1(dataImport, importErrors);
        readAndValidateFile2(dataImport, importErrors);
        readAndValidateFile3(dataImport, importErrors);

    }

}
