package itu.zazart.erpnext.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSearch {
    private String employeeName;
    private String name;
    private String fullName;
    private String firstName;
    private String lastName;
    private String middleName;

    private String department;
    private String designation;

    private String gender;
    private String status;

    private LocalDate dateOfBirthMin;
    private LocalDate dateOfBirthMax;

    private LocalDate dateOfJoiningMin;
    private LocalDate dateOfJoiningMax;
}
