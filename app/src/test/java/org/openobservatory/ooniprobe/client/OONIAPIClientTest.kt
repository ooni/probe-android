package org.openobservatory.ooniprobe.client

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.openobservatory.ooniprobe.RobolectricAbstractTest

class OONIAPIClientTest : RobolectricAbstractTest() {

    @Test
    fun measurementSuccess() {
        val response = apiClient.getMeasurement(EXISTING_REPORT_ID, null).execute()

        assertTrue(response.isSuccessful)
        response.body().use { body ->
            assertNotNull(body)
            assertNotNull(body!!.string())
        }
    }

    @Test
    fun measurementError() {
        val response = apiClient.getMeasurement(NON_EXISTING_REPORT_ID, null).execute()

        assertFalse(response.isSuccessful)
    }

    @Test
    fun testSelectMeasurementsWithJson() {
        val response = apiClient.checkReportId(EXISTING_REPORT_ID).execute()

        assertTrue(response.isSuccessful)
        with(response.body()) {
            assertNotNull(this)
            assertTrue(this!!.found)
        }
    }

    private val apiClient get() = a.apiClient

    companion object {
        private const val EXISTING_REPORT_ID =
            "20190113T202156Z_AS327931_CgoC3KbgM6zKajvIIt1AxxybJ1HbjwwWJjsJnlxy9rpcGY54VH"
        private const val NON_EXISTING_REPORT_ID = "EMPTY"
    }
}
