package org.openobservatory.ooniprobe.model.database

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.structure.database.FlowCursor
import org.openobservatory.ooniprobe.common.AppDatabase


class ResultExtensions {

    companion object {

        @JvmStatic
        fun Result.getStatus(): Int {
            val queryById = """
        SELECT
            CASE 
                WHEN COUNT(m.result_id) = 0 THEN -1
                ELSE SUM(CASE WHEN m.is_done = 0 THEN 1 ELSE 0 END)
            END AS in_progress_count
        FROM
            Result r
        LEFT JOIN
            Measurement m ON r.id = m.result_id
        WHERE
            r.id = ?
    """.trimIndent()
            val cursor: FlowCursor = FlowManager.getDatabase(AppDatabase::class.java)
                .writableDatabase
                .rawQuery(queryById, arrayOf(id.toString()))

            var inProgressCount = 0
            if (cursor.moveToFirst()) {
                cursor.getColumnIndex("in_progress_count").let {
                    if (it == -1) {
                        return@let
                    }
                    inProgressCount = cursor.getInt(it)
                }
            }
            cursor.close()
            return inProgressCount
        }
    }
}