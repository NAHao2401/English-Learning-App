package com.example.englishlearningapp.features.vocab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.englishlearningapp.features.vocab.viewmodel.VocabViewModel
import androidx.hilt.navigation.compose.hiltViewModel

// ── CheckState enum ──
enum class CheckState { IDLE, CORRECT, WRONG }

@Composable
fun ReviewQuizChallengeScreen(
    navController: NavController,
    viewModel: VocabViewModel
) {
    val learnedData by viewModel.learnedVocabs.collectAsState()
    val allItems = learnedData?.items ?: emptyList()
    val dueItems = allItems.filter { it.isDue }
    val questions = remember(dueItems) {
        if (dueItems.isEmpty()) emptyList()
        else buildQuizQuestions(dueItems, allItems)
    }

    // Load data on screen entry
    LaunchedEffect(Unit) {
        viewModel.loadLearnedVocabs()
    }

    // ── Quiz state ──
    var currentIndex by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var checkState by remember { mutableStateOf<CheckState>(CheckState.IDLE) }
    var correctCount by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    val question = questions.getOrNull(currentIndex)
    val correctWord = question?.word ?: ""
    val isLastQuestion = currentIndex == questions.size - 1

    // Hint state — reset when question changes
    val maxHints = remember(correctWord) {
        when {
            correctWord.length >= 5 -> 2
            correctWord.length >= 3 -> 1
            else -> 0
        }
    }
    var hintsUsed by remember(currentIndex) { mutableIntStateOf(0) }
    val hintsLeft = maxHints - hintsUsed

    // Reset per question
    LaunchedEffect(currentIndex) {
        userInput = ""
        checkState = CheckState.IDLE
        hintsUsed = 0
    }

    // Keyboard controller
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-dismiss keyboard when result panel appears
    LaunchedEffect(checkState) {
        if (checkState != CheckState.IDLE) {
            keyboardController?.hide()
        }
    }

    // Auto-focus input on question change
    LaunchedEffect(currentIndex) {
        focusRequester.requestFocus()
    }

    // ── Hint logic ──
    fun applyHint() {
        if (hintsLeft <= 0 || checkState != CheckState.IDLE) return
        val target = correctWord.lowercase()
        val current = userInput.lowercase()

        // Find length of correct prefix
        var matchLen = 0
        for (i in target.indices) {
            if (i < current.length && current[i] == target[i]) {
                matchLen = i + 1
            } else {
                break
            }
        }
        // matchLen chars match from start; add next correct char
        val nextChar = target.getOrNull(matchLen)?.toString() ?: return
        userInput = target.substring(0, matchLen) + nextChar
        hintsUsed++
    }

    // ── Check answer ──
    fun checkAnswer() {
        if (userInput.isBlank() || checkState != CheckState.IDLE) return
        val isCorrect = userInput.trim().equals(correctWord.trim(), ignoreCase = true)
        if (isCorrect) {
            correctCount++
            checkState = CheckState.CORRECT
            viewModel.rateQuizAnswer(
                vocabularyId = question?.vocabId ?: 0,
                currentMastery = question?.masteryLevel ?: 1,
                isCorrect = true
            )
        } else {
            checkState = CheckState.WRONG
            viewModel.rateQuizAnswer(
                vocabularyId = question?.vocabId ?: 0,
                currentMastery = question?.masteryLevel ?: 1,
                isCorrect = false
            )
        }
    }

    // ── Advance to next question ──
    fun goNext() {
        if (isLastQuestion) {
            showResult = true
        } else {
            currentIndex++
        }
    }

    // ── Guard screens ──
    if (showResult) {
        QuizResultScreen(correctCount, questions.size) {
            viewModel.loadLearnedVocabs()
            navController.navigateUp()
        }
        return
    }
    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có từ nào cần ôn tập!", color = Color.White)
        }
        return
    }

    // ── UI colors based on checkState ──
    val inputBorderColor = when (checkState) {
        CheckState.CORRECT -> Color(0xFF4CAF50)
        CheckState.WRONG -> Color(0xFFF44336)
        CheckState.IDLE -> Color(0xFF1565C0)
    }
    val inputBgColor = when (checkState) {
        CheckState.CORRECT -> Color(0xFF1B5E20)
        CheckState.WRONG -> Color(0xFF7F0000)
        CheckState.IDLE -> Color(0xFF2A2A2A)
    }

    // ── Scaffold ──
    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp,
                            end = 16.dp,
                            bottom = 8.dp, // Chỉ padding phía dưới
                            top = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(16.dp))
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1f) / questions.size },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFF3A3A3A)
                    )
                    Text(
                        "${currentIndex + 1}/${questions.size}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Main content (top area) ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // Leave bottom space for result panel
                    .padding(
                        bottom = if (checkState != CheckState.IDLE) 220.dp else 0.dp
                    )
                    .padding(horizontal = 16.dp)
                    .imePadding()
            ) {
                Spacer(Modifier.height(12.dp))

                // Label row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (checkState == CheckState.WRONG) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚡", fontSize = 16.sp)
                                Spacer(Modifier.size(4.dp))
                                Text(
                                    "Từ hay sai",
                                    color = Color(0xFFFF8C00),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                        }
                        Text(
                            text = when (checkState) {
                                CheckState.CORRECT -> "Chính xác!"
                                else -> "Nhập từ"
                            },
                            color = when (checkState) {
                                CheckState.CORRECT -> Color(0xFF4CAF50)
                                else -> Color.White
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    // Seed icon
                    val mastery = allItems.find {
                        it.vocabularyId == question?.vocabId
                    }?.masteryLevel ?: 0
                    LearnedSeedIcon(masteryLevel = mastery)
                }

                Spacer(Modifier.height(20.dp))

                // Meaning (question prompt)
                Text(
                    text = question?.correctAnswer ?: "",
                    color = Color(0xFFCCCCCC),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(16.dp))

                // Text input field
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { if (checkState == CheckState.IDLE) userInput = it },
                    enabled = checkState == CheckState.IDLE,
                    readOnly = checkState != CheckState.IDLE,
                    singleLine = true,
                    trailingIcon = {
                        // Show ✗ icon when wrong
                        if (checkState == CheckState.WRONG) {
                            Icon(Icons.Default.Close, tint = Color(0xFFF44336), contentDescription = "Error")
                        }
                        // Show ✓ icon when correct
                        if (checkState == CheckState.CORRECT) {
                            Icon(Icons.Default.Check, tint = Color(0xFF4CAF50), contentDescription = "Correct")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        disabledBorderColor = inputBorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White,
                        cursorColor = Color(0xFF1565C0),
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        disabledContainerColor = inputBgColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            when (checkState) {
                                CheckState.IDLE -> checkAnswer()
                                else -> goNext()
                            }
                        }
                    )
                )

                Spacer(Modifier.height(12.dp))

                // "Đáp án đúng: {word}" — show when WRONG, centered
                AnimatedVisibility(visible = checkState == CheckState.WRONG) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.White, fontSize = 16.sp)) {
                                    append("Đáp án đúng: ")
                                }
                                withStyle(
                                    SpanStyle(
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                ) {
                                    append(correctWord)
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Hint + Kiểm tra row — only visible when IDLE
                AnimatedVisibility(visible = checkState == CheckState.IDLE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hint button
                        if (maxHints > 0) {
                            OutlinedButton(
                                onClick = { applyHint() },
                                enabled = hintsLeft > 0,
                                modifier = Modifier.height(50.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (hintsLeft > 0) Color(0xFF4CAF50) else Color(0xFF3A3A3A)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent
                                )
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = "Hint",
                                    tint = if (hintsLeft > 0) Color(0xFF4CAF50)
                                    else Color(0xFF4A4A4A),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.size(6.dp))
                                Text(
                                    "Gợi ý ($hintsLeft)",
                                    color = if (hintsLeft > 0) Color(0xFF4CAF50)
                                    else Color(0xFF4A4A4A),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Kiểm tra button
                        Button(
                            onClick = { checkAnswer() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Kiểm tra",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // ── Bottom Result Panel (slides up from bottom) ──
            AnimatedVisibility(
                visible = checkState != CheckState.IDLE,
                enter = slideInVertically(initialOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                val isCorrect = checkState == CheckState.CORRECT
                val panelBg = if (isCorrect) Color(0xFF1A2A1A) else Color(0xFF2A1A1A)
                val accentColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = panelBg,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .navigationBarsPadding()
                    ) {
                        // Panel header: "Chính xác" / "Không chính xác"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isCorrect) "Chính xác" else "Không chính xác",
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                // Correct word (underlined, colored)
                                Text(
                                    text = correctWord,
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                            // "Báo lỗi" button
                            TextButton(onClick = { /* report error */ }) {
                                Icon(
                                    Icons.Default.Info,
                                    tint = Color.Gray,
                                    contentDescription = "Report",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.size(4.dp))
                                Text("Báo lỗi", color = Color.Gray, fontSize = 13.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Meaning line
                        Text(
                            text = question?.correctAnswer ?: "",
                            color = Color(0xFFCCCCCC),
                            fontSize = 14.sp
                        )

                        // Optional: image from image_url (when available in VocabularyResponse)
                        // TODO: Add AsyncImage when image_url field is added to VocabularyResponse

                        Spacer(Modifier.height(16.dp))

                        // "Tiếp tục" button
                        Button(
                            onClick = { goNext() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isLastQuestion) "Hoàn thành bài học"
                                else "Tiếp tục",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
