package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.dto.DataImport;
import itu.zazart.erpnext.dto.ImportError;
import itu.zazart.erpnext.model.hr.*;
import itu.zazart.erpnext.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
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
        ImportError error = new ImportError();
        error.setFileName(baseError.getFileName());
        error.setLineNumber(baseError.getLineNumber());
        error.setRawData(baseError.getRawData());
        error.setErrorMessage("Invalid date format : " + dateStr);
        importErrors.add(error);
        return null;
    }

    public LocalDate validDateOfBirth(String dateStr, List<ImportError> importErrors,ImportError baseError) {
        LocalDate dateOfBirth = validDate(dateStr, importErrors, baseError);
        if (dateOfBirth != null) {
            if (dateOfBirth.isAfter(LocalDate.now())) {
                ImportError error = new ImportError();
                error.setFileName(baseError.getFileName());
                error.setLineNumber(baseError.getLineNumber());
                error.setRawData(baseError.getRawData());
                error.setErrorMessage("Date of birth cannot be in the future : " + dateStr);
                importErrors.add(error);
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
        ImportError error = new ImportError();
        error.setFileName(baseError.getFileName());
        error.setLineNumber(baseError.getLineNumber());
        error.setRawData(baseError.getRawData());
        error.setErrorMessage("Invalid gender : " + genderStr);
        importErrors.add(error);
        return "";
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
                    ImportError error = new ImportError();
                    error.setFileName(baseError.getFileName());
                    error.setLineNumber(lineNumber);
                    error.setRawData(line);
                    error.setErrorMessage("Invalid row: less than 7 columns");
                    importErrors.add(error);
                    continue;
                }

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
        } catch (IOException e) {
            logger.error("Error while reading file", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isExistingSalaryStructure(DataImport dataImport, List<ImportError> importErrors, String salaryStructureName, ImportError baseError) {
        List<SalaryStructure> salaryStructures = dataImport.getExistingSalaryStructures();
        for (SalaryStructure salaryStructure : salaryStructures) {
            if (salaryStructure.getName().equalsIgnoreCase(salaryStructureName)) {
                ImportError error = new ImportError();
                error.setFileName(baseError.getFileName());
                error.setLineNumber(baseError.getLineNumber());
                error.setRawData(baseError.getRawData());
                error.setErrorMessage("The salary structure "+salaryStructureName+ " already exists in the database");
                importErrors.add(error);
                return true;
            }
        }
        return false;
    }

    public boolean isSalaryStructureInNewList(DataImport dataImport, String salaryStructureName) {
        List<SalaryStructure> salaryStructures = dataImport.getSalaryStructureList();
        for (SalaryStructure salaryStructure : salaryStructures) {
            if (salaryStructure.getName().equalsIgnoreCase(salaryStructureName)) {
                return true;
            }
        }
        return false;
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
                    ImportError error = new ImportError();
                    error.setFileName(baseError.getFileName());
                    error.setLineNumber(lineNumber);
                    error.setRawData(line);
                    error.setErrorMessage("Invalid row: less than 6 columns");
                    importErrors.add(error);
                    continue;
                }
                String salaryStructureName = tokens[0].trim();

                if (isExistingSalaryStructure(dataImport,importErrors, salaryStructureName, baseError)) {
                    continue;
                }

                if (isSalaryStructureInNewList(dataImport, salaryStructureName)){

                }
                else {

                }
            }
        } catch (IOException e) {
            logger.error("Error while reading file", e);
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
