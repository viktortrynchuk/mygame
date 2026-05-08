package com.example.mygame.engine_and_helpers.ui

/** Minimal shell: finishes current Activity if possible, otherwise terminates process. */
interface AppShell {
    fun exitApp()
}

//@Singleton
//class AppShellImpl @Inject constructor(
//    private val app: Application
//) : AppShell {
//    override fun exitApp() {
//        // Try to finish the foreground Activity if we have it
//        ActivityHost.get()?.finishAffinity()
//        // Fallback: hard exit
//        try { Process.killProcess(Process.myPid()) } finally { exitProcess(0) }
//    }
//}