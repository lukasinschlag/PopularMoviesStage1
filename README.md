# PopularMoviesStage1

Note: in the activity MovieListActivity the api key for the TMDb has to be set

Due to an error with Android Studio 3 (java.lang.NoClassDefFoundError: Landroid/arch/lifecycle/LifecycleRegistry)
the following have been added to the build.gradle:

    compile "android.arch.lifecycle:runtime:1.1.0"
    compile "android.arch.lifecycle:extensions:1.1.0"
    annotationProcessor "android.arch.lifecycle:compiler:1.1.0"
