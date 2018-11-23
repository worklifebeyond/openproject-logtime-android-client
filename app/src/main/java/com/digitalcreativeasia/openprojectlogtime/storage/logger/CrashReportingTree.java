package com.digitalcreativeasia.openprojectlogtime.storage.logger;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class CrashReportingTree extends Timber.Tree {
    @Override
    protected void log(int priority, @org.jetbrains.annotations.Nullable String tag, @NotNull String message, @org.jetbrains.annotations.Nullable Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        MockCrashReport.log(priority, tag, message);

        if (t != null) {
            if (priority == Log.ERROR) {
                MockCrashReport.logError(t);
            } else if (priority == Log.WARN) {
                MockCrashReport.logWarning(t);
            }
        }
    }
}