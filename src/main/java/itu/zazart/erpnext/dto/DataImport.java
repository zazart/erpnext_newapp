package itu.zazart.erpnext.dto;

import itu.zazart.erpnext.model.hr.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.text.spi.CollatorProvider;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DataImport {
    private MultipartFile file1;
    private MultipartFile file2;
    private MultipartFile file3;

    private List<Company> companyList;
    private List<Employee> employeeList;
    private List<SalaryComponent> salaryComponentList;
    private List<SalaryStructure> salaryStructureList;
    private List<SalaryStructureAssignment> salaryStructureAssignmentList;

    // Existing data
    private List<Company> existingCompanies;
    private List<Employee> existingEmployees;
    private List<SalaryComponent> existingSalaryComponents;
    private List<SalaryStructure> existingSalaryStructures;
    private List<SalaryStructureAssignment> existingSalaryStructureAssignments;
    private List<String> companyAbbrList;

    public DataImport() {
        this.employeeList = new ArrayList<>();
        this.companyList = new ArrayList<>();
        this.salaryComponentList = new ArrayList<>();
        this.salaryStructureList = new ArrayList<>();
        this.salaryStructureAssignmentList = new ArrayList<>();
    }

    public void initAbbrList(){
        companyAbbrList = new ArrayList<>();
        for (Company company : existingCompanies) {
            companyAbbrList.add(company.getAbbr());
        }
    }
}
