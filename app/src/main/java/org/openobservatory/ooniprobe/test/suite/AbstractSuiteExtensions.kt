package org.openobservatory.ooniprobe.test.suite

import org.openobservatory.ooniprobe.common.Application
import org.openobservatory.ooniprobe.common.ooniDescriptors
import org.openobservatory.ooniprobe.model.database.Url


fun getSuite(
    app: Application,
    tn: String,
    urls: List<String?>?,
    origin: String?
): AbstractSuite? {
    for (suite in ooniDescriptors(app).map { return@map it.getTest(app) }) {
        for (test in suite.getTestList(app.preferenceManager)) {
            if (test.name == tn) {
                if (urls != null) {
                    for (url in urls) {
                        Url.checkExistingUrl(url)
                    }
                }
                test.inputs = urls
                test.setOrigin(origin)
                suite.setTestList(test)
                return suite
            }
        }
    }
    return null
}