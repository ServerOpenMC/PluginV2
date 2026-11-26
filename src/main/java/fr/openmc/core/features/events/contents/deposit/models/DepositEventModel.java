package fr.openmc.core.features.events.contents.deposit.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.utils.text.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@DatabaseTable(tableName = "event_deposit_info")
public class DepositEventModel {
    @DatabaseField(id = true, columnName = "event_id")
    private String eventId;

    @Setter
    @DatabaseField(canBeNull = false, columnName = "enabled")
    private boolean enabled;

    @DatabaseField(canBeNull = false, columnName = "ended")
    private boolean ended;

    @DatabaseField(canBeNull = false, columnName = "start_at")
    private String startAt;

    @DatabaseField(canBeNull = false, columnName = "end_at")
    private String endAt;

    DepositEventModel() {
        // Necessary for OrmLite
    }

    public DepositEventModel(String eventId, LocalDateTime startAt, LocalDateTime endAt) {
        this.eventId = eventId;
        this.enabled = true;
        this.ended = false;
        this.startAt = startAt.toString();
        this.endAt = endAt.toString();
    }

    public LocalDateTime getStartDate() {
        return LocalDateTime.parse(startAt);
    }

    public LocalDateTime getEndDate() {
        return LocalDateTime.parse(endAt);
    }

    public boolean isActive() {
        return isActive(DateUtils.getLocalDateTime());
    }

    public boolean isActive(LocalDateTime now) {
        return enabled && !ended && !now.isBefore(getStartDate()) && now.isBefore(getEndDate());
    }

    public boolean shouldEnd(LocalDateTime now) {
        return !ended && !now.isBefore(getEndDate());
    }

    public void markEnded() {
        this.ended = true;
        this.enabled = false;
    }

    public void reopen() {
        this.ended = false;
        this.enabled = true;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt.toString();
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt.toString();
    }
}