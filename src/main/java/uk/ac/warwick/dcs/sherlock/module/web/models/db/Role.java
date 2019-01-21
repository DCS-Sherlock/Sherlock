package uk.ac.warwick.dcs.sherlock.module.web.models.db;

import javax.persistence.*;

@Entity
@Table(name="role")
public class Role {
    @Id
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    public Role() {

    }

    public Role(String name, Account account) {
        this.name = name;
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}