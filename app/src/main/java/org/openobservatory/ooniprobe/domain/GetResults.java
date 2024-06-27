package org.openobservatory.ooniprobe.domain;

import androidx.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.domain.models.DatedResults;
import org.openobservatory.ooniprobe.fragment.resultList.ResultListSpinnerItem;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Result_Table;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class GetResults {

    private final Calendar c = Calendar.getInstance();

    @Inject
    GetResults() { }

    public Result get(int id) {
        return SQLite.select().from(Result.class).where(Result_Table.id.eq(id)).querySingle();
    }

    public List<Result> getOrderedByTime(@Nullable String testGroupNameFilter) {
        SQLOperator[] conditions = (testGroupNameFilter != null && !testGroupNameFilter.isEmpty())
                ? new SQLOperator[]{ Result_Table.test_group_name.is(testGroupNameFilter) }
                : new SQLOperator[0];

        return  SQLite.select().from(Result.class)
                .where(conditions)
                .orderBy(Result_Table.start_time, false)
                .queryList();
    }

    public List<Result> getOrderedByTimeWithRunId(String runId) {
        return  SQLite.select().from(Result.class)
                .where(Result_Table.descriptor_runId.eq(Long.valueOf(runId)))
                .orderBy(Result_Table.start_time, false)
                .queryList();
    }

    public List<DatedResults> getGroupedByMonth(ResultListSpinnerItem testGroupNameFilter) {
        List<Result> results = new ArrayList<>();
        switch (testGroupNameFilter.type) {
            case DEFAULT -> {
                results = getOrderedByTime(testGroupNameFilter.id);
            }
            case RUN_V2_ITEM -> {
                results = getOrderedByTimeWithRunId(testGroupNameFilter.id);
            }
        }
        List<DatedResults> datedResults = new ArrayList<>();

        List<Result> sameDateResults = new ArrayList<>();
        int lastDateIdentifier = -1;

        for (Result result: results) {
            int newDateIdentifier = getUniqueIdentifierFromDate(result.start_time);

            if (newDateIdentifier != lastDateIdentifier) {
                lastDateIdentifier = newDateIdentifier;
                sameDateResults = new ArrayList<>();
                datedResults.add(new DatedResults(result.start_time, sameDateResults));
            }

            sameDateResults.add(result);
        }

        return datedResults;
    }

    private Integer getUniqueIdentifierFromDate(Date date) {
        c.setTime(date);
        return c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH);
    }
}
