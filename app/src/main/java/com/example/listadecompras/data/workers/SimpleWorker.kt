package com.example.listadecompras.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.listadecompras.data.SampleDependency
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class SimpleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    sampleDependency: SampleDependency
) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        while (true) {
            Log.d("SimpleWorker", "while (true)")
            delay(500)


        }
    }
    companion object{

        const val WORK_NAME = "SimpleWorker"

        fun makeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SimpleWorker>().build()
        }
    }
}