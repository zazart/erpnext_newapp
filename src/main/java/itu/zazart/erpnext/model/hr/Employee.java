package itu.zazart.erpnext.model.hr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private String name;
    private String owner;
    private Date creation;
    private Date modified;
    private String modifiedBy;
    private int docstatus;
    private int idx;

    private String employee;
    private String namingSeries;
    private String firstName;
    private String middleName;
    private String lastName;
    private String employeeName;
    private String gender;
    private Date dateOfBirth;
    private String salutation;
    private Date dateOfJoining;
    private String image;
    private String status;
    private String userId;
    private int createUserPermission;
    private String company;
    private String department;
    private String employmentType;
    private String employeeNumber;
    private String designation;
    private String reportsTo;
    private String branch;
    private String grade;
    private String jobApplicant;
    private Date scheduledConfirmationDate;
    private Date finalConfirmationDate;
    private Date contractEndDate;
    private int noticeNumberOfDays;
    private Date dateOfRetirement;
    private String cellNumber;
    private String personalEmail;
    private String companyEmail;
    private String preferedContactEmail;
    private String preferedEmail;
    private int unsubscribed;
    private String currentAddress;
    private String currentAccommodationType;
    private String permanentAddress;
    private String permanentAccommodationType;
    private String personToBeContacted;
    private String emergencyPhoneNumber;
    private String relation;
    private String attendanceDeviceId;
    private String holidayList;
    private String defaultShift;
    private String expenseApprover;
    private String leaveApprover;
    private String shiftRequestApprover;
    private BigDecimal ctc;
    private String salaryCurrency;
    private String salaryMode;
    private String payrollCostCenter;
    private String bankName;
    private String bankAcNo;
    private String iban;
    private String maritalStatus;
    private String familyBackground;
    private String bloodGroup;
    private String healthDetails;
    private String healthInsuranceProvider;
    private String healthInsuranceNo;
    private String passportNumber;
    private Date validUpto;
    private Date dateOfIssue;
    private String placeOfIssue;
    private String bio;
    private Date resignationLetterDate;
    private Date relievingDate;
    private Date heldOn;
    private String newWorkplace;
    private String leaveEncashed;
    private Date encashmentDate;
    private String reasonForLeaving;
    private String feedback;
    private int lft;
    private int rgt;
    private String oldParent;
}
