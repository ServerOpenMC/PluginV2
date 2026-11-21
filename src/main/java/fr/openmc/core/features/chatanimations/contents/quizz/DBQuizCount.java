package fr.openmc.core.features.chatanimations.contents.quizz;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@DatabaseTable(tableName = "quiz_count")
public class DBQuizCount {

    @DatabaseField(id = true, dataType = DataType.UUID)
    private UUID uuid;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int correctAnswers;

    public DBQuizCount() {
        //
    }

    public DBQuizCount(UUID uuid) {
        this.uuid = uuid;
        this.correctAnswers = 0;
    }
}
