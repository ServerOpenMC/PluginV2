package fr.openmc.core.features.reports;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.Instant;
import java.util.UUID;

@DatabaseTable(tableName = "reports")
public class DBReport {
    @DatabaseField(id = true,columnName = "report_uuid")
    private UUID id;
    @DatabaseField(canBeNull = false,columnName = "created_at")
    private Instant createdAt;
    @DatabaseField(canBeNull = false)
    private UUID player;
    @DatabaseField(canBeNull = false)
    private UUID target;
    @DatabaseField(canBeNull = false)
    private String description;
    @DatabaseField()
    private UUID moderator;
    @DatabaseField(canBeNull = false)
    private ReportState state;

    public DBReport(UUID id, Instant createdAt, UUID player, UUID target, String description, UUID moderator, ReportState state) {
        this.id = id;
        this.createdAt = createdAt;
        this.player = player;
        this.target = target;
        this.description = description;
        this.moderator = moderator;
        this.state = state;
    }

    public Report serialize(){
        return new Report(this.id,this.createdAt,this.player,this.target,this.description,this.moderator,this.state);
    }
}
