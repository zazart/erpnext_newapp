package itu.zazart.erpnext.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String username;
    private String userImage;

    public String getInitial() {
        if (firstName != null && !firstName.isEmpty()) {
            return firstName.substring(0, 1).toUpperCase();
        }
        return "?";
    }
}
