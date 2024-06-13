package org.openobservatory.ooniprobe.common

import android.annotation.SuppressLint
import android.content.Context
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.test.suite.DynamicTestSuite

class DefaultDescriptors {
    companion object {

        /**
         * Creates a list of [OONIDescriptor] representing the OONI tests.
         *
         * @return List of [OONIDescriptor] representing the OONI tests.
         */
        @SuppressLint("StringFormatInvalid")
        @JvmStatic
        fun getAll(context: Context): MutableList<OONIDescriptor<BaseNettest>> {
            return mutableListOf(
                    OONITests.WEBSITES.toOONIDescriptor(context),
                    OONITests.INSTANT_MESSAGING.toOONIDescriptor(context),
                    OONITests.CIRCUMVENTION.toOONIDescriptor(context),
                    OONITests.PERFORMANCE.toOONIDescriptor(context),
                    OONITests.EXPERIMENTAL.toOONIDescriptor(context)
            )
        }


        @JvmStatic
        fun autoRunTests(
            context: Context,
            preferenceManager: PreferenceManager,
            testDescriptorManager: TestDescriptorManager
        ): MutableList<DynamicTestSuite> {

            return getAll(context).filter { ooniDescriptor ->
                when (ooniDescriptor.name) {
                    OONITests.EXPERIMENTAL.label -> preferenceManager.resolveStatus(
                            name = ooniDescriptor.name,
                            prefix = ooniDescriptor.preferencePrefix(),
                            autoRun = true
                    )

                    else -> ooniDescriptor.nettests.any { nettest ->
                        preferenceManager.resolveStatus(
                                name = nettest.name,
                                prefix = ooniDescriptor.preferencePrefix(),
                                autoRun = true
                        )
                    }
                }
            }.map { ooniDescriptor ->
                when (ooniDescriptor.name) {
                    OONITests.EXPERIMENTAL.label -> DynamicTestSuite(
                            name = ooniDescriptor.name,
                            title = ooniDescriptor.title,
                            shortDescription = ooniDescriptor.shortDescription,
                            description = ooniDescriptor.description,
                            icon = ooniDescriptor.getDisplayIcon(context),
                            icon_24 = ooniDescriptor.getDisplayIcon(context),
                            color = ooniDescriptor.color,
                            animation = ooniDescriptor.animation,
                            dataUsage = ooniDescriptor.dataUsage,
                            nettest = (ooniDescriptor.nettests).run {
                                this + (ooniDescriptor.longRunningTests?.filter { nettest ->
                                    preferenceManager.resolveStatus(
                                            name = nettest.name,
                                            prefix = ooniDescriptor.preferencePrefix(),
                                            autoRun = true
                                    )
                                } ?: listOf())
                            }
                    ).apply {
                        autoRun = true
                    }

                    else -> DynamicTestSuite(
                            name = ooniDescriptor.name,
                            title = ooniDescriptor.title,
                            shortDescription = ooniDescriptor.shortDescription,
                            description = ooniDescriptor.description,
                            icon = ooniDescriptor.getDisplayIcon(context),
                            icon_24 = ooniDescriptor.getDisplayIcon(context),
                            color = ooniDescriptor.color,
                            animation = ooniDescriptor.animation,
                            dataUsage = ooniDescriptor.dataUsage,
                            nettest = (ooniDescriptor.nettests).filter { nettest ->
                                preferenceManager.resolveStatus(
                                        name = nettest.name,
                                        prefix = ooniDescriptor.preferencePrefix(),
                                        autoRun = true
                                )
                            }.run {
                                this + (ooniDescriptor.longRunningTests?.filter { nettest ->
                                    preferenceManager.resolveStatus(
                                            name = nettest.name,
                                            prefix = ooniDescriptor.preferencePrefix(),
                                            autoRun = true
                                    )
                                } ?: listOf())
                            }
                    ).apply {
                        autoRun = true
                    }
                }
            }.toMutableList().also { descriptors ->
                val runV2AtoRunTests: List<DynamicTestSuite> =
                    testDescriptorManager.getRunV2Descriptors(expired = false, autoRun = true)
                        .map { testDescriptor ->
                            testDescriptor.toDynamicTestSuite(context).apply {
                                autoRun = true
                            }
                        }
                descriptors.addAll(runV2AtoRunTests)
            }
        }

    }
}