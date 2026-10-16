package fr.openmc.core.features.reports;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.features.reports.commands.ReportCommand;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Credit(developers = {"ElitGaimix"})
public class ReportsManager extends Feature implements HasCommands, HasDatabase {

    private static Dao<DBReport, String> reportDao;

    public static List<Report> currentReport;

    @Override
    public Set<Object> getCommands() {
        return Set.of(new ReportCommand());
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Report.class);
        reportDao = DaoManager.createDao(connectionSource, DBReport.class);

        List<Report> reports = reportDao.queryForAll().stream().map(DBReport::serialize).toList();
        for (Report report : reports) {
            if (report.state == ReportState.OPEN) {
                currentReport.add(report);
            }else if (report.state == ReportState.CLOSED && !report.isValide())
                reportDao.delete(report.serialize());
        }
    }

}
