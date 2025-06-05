package itu.zazart.erpnext;

import itu.zazart.erpnext.model.hr.Company;
import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.service.hr.CompanyService;
import itu.zazart.erpnext.service.hr.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@SpringBootTest
class ErpnextApplicationTests {
	private final CompanyService companyService;
	private final EmployeeService employeeService;
	private final String testSid = "2aa682df2dec52345a6e3f1cd651b7f5738ed7b4c813d0d26279b458";

	@Autowired
    ErpnextApplicationTests(CompanyService companyService, EmployeeService employeeService) {
        this.companyService = companyService;
        this.employeeService = employeeService;
    }

    @Test
	void getCompany() {
		List<Company> companies = companyService.getAllCompanies(testSid);
		System.out.println("done");
	}

	@Test
	void getEmployees() {
		List<Employee> employees = employeeService.getAllEmployee(testSid);
		System.out.println("done");
	}

	@Test
	void parse(){
		String dateStr = "03/04/2024";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
				.withResolverStyle(ResolverStyle.STRICT);

		try {
			TemporalAccessor ta = formatter.parse(dateStr);

			int day = ta.get(ChronoField.DAY_OF_MONTH);
			int month = ta.get(ChronoField.MONTH_OF_YEAR);
			// Attention ici, YEAR_OF_ERA au lieu de YEAR
			int year = ta.get(ChronoField.YEAR_OF_ERA);

			LocalDate date = LocalDate.of(year, month, day);

			System.out.println(date);  // 2024-04-03
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void checkAbbr(){
		String companyName = "My Company Coco";

		String[] words = companyName.trim().split("\\s+");
		StringBuilder abbreviation = new StringBuilder();
		for (String word : words) {
			if (!word.isEmpty()) {
				abbreviation.append(Character.toUpperCase(word.charAt(0)));
			}
		}
		System.out.println(abbreviation);
	}


}
