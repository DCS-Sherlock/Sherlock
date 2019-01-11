package uk.ac.warwick.dcs.sherlock.module.web.models.db;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="workspace")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;

    @NotNull()
    @Size(min=1, max=64)
    @Column(name="name", nullable = false)
    public String name;

    @Column(name="engine_id")
    private long engineId;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    public Workspace() { }

    public Workspace(String name, Account account) {
        this.name = name;
        this.account = account;
        this.engineId = 0L;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEngineId() {
        return engineId;
    }

    public void setEngineId(long sherlock_id) {
        this.engineId = sherlock_id;
    }
}
