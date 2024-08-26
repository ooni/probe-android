package org.openobservatory.ooniprobe.common

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * ProgressTask is an abstract class that represents a task that reports progress.
 * @param P The type of the progress token.
 * @param R The type of the result.
 */
abstract class ProgressTask<P, R> {
    abstract fun runTask(progressToken: OnTaskProgressUpdate<P>): R
}

/**
 * Task is an alias for the java.util.concurrent.Callable interface.
 * @param R The type of the result.
 */
typealias Task<R> = Callable<R>

/**
 * OnTaskProgressUpdate is a typealias for a callback that is invoked when a task reports progress.
 * @param P The type of the progress token.
 */
typealias OnTaskProgressUpdate<P> = (P) -> Unit

/**
 * OnTaskComplete is a typealias for a callback that is invoked when a task is completed.
 * @param R The type of the result.
 */
typealias OnTaskComplete<R> = (R) -> Unit

/**
 * TaskExecutor is a utility class that provides methods to execute tasks in a separate thread and post results on the main thread.
 * It uses a single thread executor to run tasks and a Handler to post results on the main thread.
 *
 * @property executor The executor service that runs tasks in a separate thread.
 * @property handler The handler that posts results on the main thread.
 * @property future The future that represents the result of a task.
 */
class TaskExecutor {
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var future: Future<*>
    /**
     * Executes a task in a separate thread and posts the result on the main thread.
     * @param task The task to be executed.
     * @param onComplete The callback to be invoked when the task is completed.
     */
    fun <R> executeTask(task: Task<R>, onComplete: OnTaskComplete<R>) {
        future = executor.submit {
            val result = task.call()
            if (!future.isCancelled) {
                handler.post {
                    onComplete(result)
                }
            }
        }
    }

    /**
     * Executes a task that reports progress in a separate thread and posts the result and progress updates on the main thread.
     * @param progressTask The task to be executed.
     * @param onProgress The callback to be invoked when the task reports progress.
     * @param onComplete The callback to be invoked when the task is completed.
     */
    fun <P, R> executeProgressTask(
        progressTask: ProgressTask<P, R>,
        onProgress: OnTaskProgressUpdate<P>,
        onComplete: OnTaskComplete<R>
    ) {
        future = executor.submit {
            val result = progressTask.runTask(
                progressToken = { progress ->
                    handler.post {
                        onProgress(progress)
                    }
                }
            )

            handler.post {
                onComplete(result)
            }
        }
    }

    /**
     * Cancels the currently running task.
     */
    fun cancelTask() {
        this.future.cancel(true)
    }
}