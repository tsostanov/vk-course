package com.example.vkcourse.applist.testutil

internal suspend inline fun <reified T : Throwable> assertSuspendThrows(
    action: suspend () -> Unit,
): T {
    try {
        action()
    } catch (error: Throwable) {
        if (error is T) return error
        throw AssertionError("Expected ${T::class.java.simpleName}, got ${error.javaClass.simpleName}", error)
    }
    throw AssertionError("Expected ${T::class.java.simpleName}, but the call completed successfully")
}
