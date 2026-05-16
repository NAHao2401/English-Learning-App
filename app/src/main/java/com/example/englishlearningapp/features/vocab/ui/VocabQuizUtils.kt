package com.example.englishlearningapp.features.vocab.ui

import com.example.englishlearningapp.data.remote.api.response.LearnedVocabItem

data class ReviewQuizQuestion(
    val vocabId: Int,
    val word: String,
    val pronunciation: String?,
    val correctAnswer: String,
    val options: List<String>,
    val correctIndex: Int,
    val masteryLevel: Int
)

fun buildQuizQuestions(
    dueItems: List<LearnedVocabItem>,
    allItems: List<LearnedVocabItem>
): List<ReviewQuizQuestion> {
    return dueItems.map { item ->
        val correctAnswer = item.meaning

        val wrongPool = allItems
            .filter { it.vocabularyId != item.vocabularyId }
            .map { it.meaning }
            .shuffled()
            .distinct()
            .take(3)

        val wrongOptions = wrongPool.toMutableList()
        while (wrongOptions.size < 3) {
            wrongOptions.add("(không có đáp án)")
        }

        val allOptions = (wrongOptions + correctAnswer).shuffled()
        val correctIndex = allOptions.indexOf(correctAnswer)

        ReviewQuizQuestion(
            vocabId = item.vocabularyId,
            word = item.word,
            pronunciation = item.pronunciation,
            correctAnswer = correctAnswer,
            options = allOptions,
            correctIndex = correctIndex,
            masteryLevel = item.masteryLevel
        )
    }.shuffled()
}

// Helper for VocabularyResponse lists (used by SelfPractice flows)
fun buildQuizQuestionsFromVocabularyResponses(
    dueItems: List<com.example.englishlearningapp.data.remote.api.response.VocabularyResponse>,
    allItems: List<com.example.englishlearningapp.data.remote.api.response.VocabularyResponse>
): List<ReviewQuizQuestion> {
    return dueItems.map { item ->
        val correctAnswer = item.meaning

        val wrongPool = allItems
            .filter { it.id != item.id }
            .map { it.meaning }
            .shuffled()
            .distinct()
            .take(3)

        val wrongOptions = wrongPool.toMutableList()
        while (wrongOptions.size < 3) {
            wrongOptions.add("(không có đáp án)")
        }

        val allOptions = (wrongOptions + correctAnswer).shuffled()
        val correctIndex = allOptions.indexOf(correctAnswer)

        ReviewQuizQuestion(
            vocabId = item.id,
            word = item.word,
            pronunciation = item.pronunciation,
            correctAnswer = correctAnswer,
            options = allOptions,
            correctIndex = correctIndex,
            masteryLevel = 0
        )
    }.shuffled()
}
