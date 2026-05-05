package com.example.englishlearningapp.features.lesson.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.data.remote.api.response.QuestionResponse

@Composable
fun LessonDetailScreen(
    questions: List<QuestionResponse>,
    selectedAnswers: Map<Int, String>,
    isLoading: Boolean,
    errorMessage: String?,
    onSelectAnswer: (Int, String) -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextButton(onClick = onBackClick) {
            Text("← Back")
        }

        Text(
            text = "Lesson Detail",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            questions.forEachIndexed { index, question ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Question ${index + 1}",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = question.question_text,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        question.answer_options.forEach { option ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedAnswers[question.id] == option.option_text,
                                    onClick = {
                                        onSelectAnswer(question.id, option.option_text)
                                    }
                                )

                                Text(
                                    text = option.option_text,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        val canSubmit = questions.isNotEmpty() &&
                selectedAnswers.size == questions.size &&
                !isLoading

        Button(
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = canSubmit
        ) {
            Text(
                text = if (selectedAnswers.size < questions.size) {
                    "Answer all questions"
                } else {
                    "Check"
                }
            )
        }
    }
}