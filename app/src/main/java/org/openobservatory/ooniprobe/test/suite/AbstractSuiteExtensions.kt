package org.openobservatory.ooniprobe.test.suite

import org.openobservatory.ooniprobe.common.Application
import org.openobservatory.ooniprobe.common.DefaultDescriptors
import org.openobservatory.ooniprobe.model.database.Url
import org.openobservatory.ooniprobe.test.test.AbstractTest


fun getSuite(
    app: Application,
    tn: String,
    urls: List<String>?,
    origin: String?
): AbstractSuite? {
    for (descriptor in DefaultDescriptors.getAll(app)) {
        for (test in descriptor.nettests) {
            if (test.name == tn) {
                if (urls != null) {
                    for (url in urls) {
                        Url.checkExistingUrl(url)
                    }
                }
                val suite: DynamicTestSuite = descriptor.getTest(app)
                suite.setTestList(
                    AbstractTest.getTestByName(test.name).apply {
                        setOrigin(origin)
                        inputs = urls
                    }
                )
                return suite
            }
        }
    }
    return null
}