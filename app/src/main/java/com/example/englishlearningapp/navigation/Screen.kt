package com.example.englishlearningapp.navigation

sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object Home : Screen("home")

    data object TopicList : Screen("topics")

    data object LessonList : Screen("lessons/{topicId}") {
        fun createRoute(topicId: Int): String {
            return "lessons/$topicId"
        }
    }

    data object LessonDetail : Screen("lesson-detail/{lessonId}") {
        fun createRoute(lessonId: Int): String {
            return "lesson-detail/$lessonId"
        }
    }

    data object LessonResult : Screen("lesson-result")

    data object Progress : Screen("progress")

    data object Speaking : Screen("speaking")
}
