package org.openobservatory.ooniprobe.dao;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class MeasurementDao {
	public static List<Measurement> queryList(@Nullable Integer resultId, @Nullable Integer measurementId, @Nullable Boolean uploaded, @Nullable Boolean failed) {
		ArrayList<SQLOperator> where = new ArrayList<>();
		if (resultId != null) {
			where.add(Measurement_Table.result_id.eq(resultId));
		}
		if (measurementId != null) {
			where.add(Measurement_Table.id.eq(measurementId));
		}
		if (Boolean.FALSE.equals(uploaded)) {
			where.add(OperatorGroup.clause().or(Measurement_Table.is_uploaded.eq(false)).or(Measurement_Table.report_id.isNull()));
		}
		if (Boolean.TRUE.equals(uploaded)) {
			where.add(Measurement_Table.is_uploaded.eq(true));
			where.add(Measurement_Table.report_id.isNotNull());
		}
		if (failed != null) {
			where.add(Measurement_Table.is_failed.eq(failed));
		}
		return SQLite.select().from(Measurement.class).where(where.toArray(new SQLOperator[0])).queryList();
	}
}
