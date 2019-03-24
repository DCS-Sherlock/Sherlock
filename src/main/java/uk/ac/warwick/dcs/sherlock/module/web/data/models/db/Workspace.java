package uk.ac.warwick.dcs.sherlock.module.web.data.models.db;

import javax.persistence.*;

@Entity
@Table(name="workspace")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;

    @Column(name="engine_id")
    private long engineId;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    public Workspace() { }

    public Workspace(Account account) {
        this.account = account;
        this.engineId = 0L;
    }

    public Workspace(Account account, long engineId) {
        this.account = account;
        this.engineId = engineId;
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
