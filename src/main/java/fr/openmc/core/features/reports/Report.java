package fr.openmc.core.features.reports;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Report {

    public static int maxDuration = 30; // 30 jours

    private UUID id;

    private Instant createdAt;

    private UUID player;

    private UUID target;

    public String description;

    public UUID moderator;

    public ReportState state;

    public Report(UUID id, Instant createdAt, UUID player, UUID target, String description, UUID moderator, ReportState state) {
        this.id = id;
        this.createdAt = createdAt;
        this.player = player;
        this.target = target;
        this.description = description;
        this.moderator = moderator;
        this.state = state;
    }

    public Report(UUID player, UUID target, String description){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.player = player;
        this.target = target;
        this.description = description;
        this.moderator = null;
        this.state = ReportState.OPEN;
    }

    public DBReport serialize(){
        return new DBReport(this.id,this.createdAt,this.player,this.target,this.description,this.moderator,this.state);
    }

    public boolean isValide(){
        return createdAt.isBefore(Instant.now().minus(maxDuration, ChronoUnit.DAYS));
    }

}
