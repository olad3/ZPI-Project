package com.zpi.amoz.models;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID employeeId;

    @OneToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "contactPersonID", nullable = false)
    private ContactPerson contactPerson;

    @ManyToOne
    @JoinColumn(name = "companyID")
    private Company company;

    @Enumerated(EnumType.STRING)
    private RoleInCompany roleInCompany;

    private LocalDate employmentDate;

    @OneToOne
    @JoinColumn(name = "personID", nullable = false, unique = true)
    private Person person;

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ContactPerson getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(ContactPerson contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public RoleInCompany getRoleInCompany() {
        return roleInCompany;
    }

    public void setRoleInCompany(RoleInCompany roleInCompany) {
        this.roleInCompany = roleInCompany;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(LocalDate employmentDate) {
        this.employmentDate = employmentDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public enum RoleInCompany {
        OWNER, REGULAR
    }
}