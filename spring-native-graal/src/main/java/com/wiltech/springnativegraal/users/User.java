package com.wiltech.springnativegraal.users;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private Boolean active;

    public void update(String firstName) {
        this.username = firstName;
    }

//    public User(Long id, String firstName, String secondName, LocalDate dateOfBirth) {
//        this.id = id;
//        this.firstName = firstName;
//        this.secondName = secondName;
//        this.dateOfBirth = dateOfBirth;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public String getSecondName() {
//        return secondName;
//    }
//
//    public LocalDate getDateOfBirth() {
//        return dateOfBirth;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        User user = (User) o;
//
//        if (!Objects.equals(id, user.id)) return false;
//        if (!Objects.equals(firstName, user.firstName)) return false;
//        if (!Objects.equals(secondName, user.secondName)) return false;
//        return Objects.equals(dateOfBirth, user.dateOfBirth);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = id != null ? id.hashCode() : 0;
//        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
//        result = 31 * result + (secondName != null ? secondName.hashCode() : 0);
//        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
//        return result;
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", firstName='" + firstName + '\'' +
//                ", secondName='" + secondName + '\'' +
//                ", dateOfBirth=" + dateOfBirth +
//                '}';
//    }
}
