package com.example.englishlearningapp.navigation

sealed class Screen(val route: String) {

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object Home : Screen("home")

    data object Learn : Screen("learn")

    data object Scan : Screen("scan")

    data object Vocab : Screen("vocab")

    data object UserTopics : Screen("user_topics")

    data object SavedVocab : Screen("saved_vocab")

    data object Review : Screen("review")

    data object LearnedWords : Screen("learned_words")

    data object ReviewQuiz : Screen("review_quiz")

    data object ReviewQuizListening : Screen("review_quiz_listening")

    data object ReviewQuizChallenge : Screen("review_quiz_challenge")

    data object TopicDetail : Screen("topic_detail/{topicId}")

    data object CefrDetail : Screen("cefr_detail/{level}") {
        fun createRoute(level: String): String {
            return "cefr_detail/$level"
        }
    }

    data object CefrLevelDetail : Screen("cefr_level_detail/{level}") {
        fun createRoute(level: String): String {
            return "cefr_level_detail/$level"
        }
    }

    data object UserTopicDetail : Screen("user_topic_detail/{userTopicId}")

    data object Flashcard : Screen("flashcard/{topicId}")

    data object StudyFlashcard : Screen("study_flashcard/{topicId}") {
        fun createRoute(topicId: Int): String {
            return "study_flashcard/$topicId"
        }
    }

    data object Profile : Screen("profile")

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
}