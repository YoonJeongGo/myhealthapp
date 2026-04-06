pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // ★ 필수
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyApplication1"
include(":app")