package com.example.ebook.ui.screens.reader

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ebook.data.model.Book
import com.example.ebook.data.model.Bookmark
import com.example.ebook.data.model.Highlight
import com.example.ebook.data.model.ReadingProgress
import com.example.ebook.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

enum class ReaderTheme { DARK, LIGHT, SEPIA, OLED }

data class ChatMessage(val isUser: Boolean, val text: String)

data class ReaderUiState(
    val timeToReadChapter: Int = 5,
    val showSummaryOverlay: Boolean = false,
    val isChatOpen: Boolean = false,
    val chatMessages: List<ChatMessage> = emptyList(),
    val isChatLoading: Boolean = false,
    val aiSummaryConfig: String? = null,
    val isVoiceCommandActive: Boolean = false,
    val isEndReviewVisible: Boolean = false,
    val customFontName: String? = null,
    val ttsDownloadProgress: Float? = null,
    val book: Book? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val fontSize: Int = 18,
    val readerTheme: ReaderTheme = ReaderTheme.DARK,
    val isNightMode: Boolean = true,
    val isBookmarked: Boolean = false,
    val showControls: Boolean = true,
    val showAudioPlayer: Boolean = false,
    val showToc: Boolean = false,
    val showHighlightMenu: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val isTtsReady: Boolean = false,
    val audioProgress: Float = 0f,
    val audioSpeed: Float = 1.0f,
    val highlights: List<Highlight> = emptyList(),
    val selectedHighlightColor: Long = 0xFFFFEB3B,
    val autoScrollEnabled: Boolean = false,
    val autoScrollSpeed: Float = 1.0f,
    val showQuoteShare: Boolean = false,
    val selectedQuoteText: String = ""
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle,
    private val llmEngine: com.example.ebook.data.local.ai.LocalLlmEngine,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val bookId: Int = savedStateHandle.get<Int>("bookId") ?: 1

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null
    private var autoScrollJob: Job? = null

    init {
        loadBook()
        observeBookmark()
        observeHighlights()
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("fa")
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        _uiState.update { it.copy(isAudioPlaying = false) }
                    }
                    override fun onError(utteranceId: String?) {
                        _uiState.update { it.copy(isAudioPlaying = false) }
                    }
                })
                _uiState.update { it.copy(isTtsReady = true) }
            }
        }
    }

    private fun loadBook() {
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookById(bookId)
            if (book != null) {
                _uiState.update { it.copy(book = book, totalPages = book.pages.size) }
            }
            repository.getReadingProgress(bookId).collect { progress ->
                if (progress != null) {
                    _uiState.update {
                        it.copy(currentPage = progress.currentPage.coerceIn(0, it.totalPages - 1))
                    }
                }
            }
        }
    }

    private fun observeBookmark() {
        viewModelScope.launch {
            _uiState.collectLatest { state ->
                repository.isBookmarked(bookId, state.currentPage).collect { isBookmarked ->
                    _uiState.update { it.copy(isBookmarked = isBookmarked) }
                }
            }
        }
    }

    private fun observeHighlights() {
        viewModelScope.launch {
            _uiState.map { it.currentPage }.distinctUntilChanged().collect { page ->
                repository.getHighlights(bookId, page).collect { highlights ->
                    _uiState.update { it.copy(highlights = highlights) }
                }
            }
        }
    }

    fun goToPage(page: Int) {
        val totalPages = _uiState.value.totalPages
        if (page in 0 until totalPages) {
            _uiState.update { it.copy(currentPage = page) }
            saveProgress()
        }
    }

    fun nextPage() = goToPage(_uiState.value.currentPage + 1)
    fun previousPage() = goToPage(_uiState.value.currentPage - 1)

    fun increaseFontSize() = _uiState.update { it.copy(fontSize = (it.fontSize + 2).coerceAtMost(32)) }
    fun decreaseFontSize() = _uiState.update { it.copy(fontSize = (it.fontSize - 2).coerceAtLeast(12)) }

    fun toggleNightMode() {
        val next = when (_uiState.value.readerTheme) {
            ReaderTheme.DARK -> ReaderTheme.LIGHT
            ReaderTheme.LIGHT -> ReaderTheme.SEPIA
            ReaderTheme.SEPIA -> ReaderTheme.OLED
            ReaderTheme.OLED -> ReaderTheme.DARK
        }
        _uiState.update { it.copy(readerTheme = next, isNightMode = next == ReaderTheme.DARK || next == ReaderTheme.OLED) }
    }

    fun setReaderTheme(theme: ReaderTheme) {
        _uiState.update { it.copy(readerTheme = theme, isNightMode = theme == ReaderTheme.DARK || theme == ReaderTheme.OLED) }
    }

    fun toggleControls() = _uiState.update { it.copy(showControls = !it.showControls) }

    fun toggleVoiceCommand() {
        _uiState.update { it.copy(isVoiceCommandActive = !it.isVoiceCommandActive) }
    }

    fun generateAiSummary() {
        _uiState.update { it.copy(showSummaryOverlay = true, aiSummaryConfig = "در حال پردازش هوش مصنوعی...") }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            _uiState.update { it.copy(aiSummaryConfig = "- معرفی شخصیت اصلی\n- توصیف فضای شهر\n- اتفاقات مرموز نیمه شب") }
        }
    }

    fun closeSummary() {
        _uiState.update { it.copy(showSummaryOverlay = false) }
    }

    fun importCustomFont() {
        _uiState.update { it.copy(customFontName = "Vazirmatn.ttf") }
    }

    fun downloadTtsAudio() {
        viewModelScope.launch {
            _uiState.update { it.copy(ttsDownloadProgress = 0f) }
            for (i in 1..10) {
                kotlinx.coroutines.delay(100)
                _uiState.update { it.copy(ttsDownloadProgress = i / 10f) }
            }
            _uiState.update { it.copy(ttsDownloadProgress = null) }
        }
    }
    
    fun dismissEndReview() {
        _uiState.update { it.copy(isEndReviewVisible = false) }
    }
    fun toggleAudioPlayer() {
        _uiState.update { it.copy(showAudioPlayer = !it.showAudioPlayer) }
    }

    fun toggleToc() = _uiState.update { it.copy(showToc = !it.showToc) }

    fun toggleAudioPlayback() {
        val state = _uiState.value
        if (state.isAudioPlaying) {
            tts?.stop()
            _uiState.update { it.copy(isAudioPlaying = false) }
        } else {
            val pageText = state.book?.pages?.getOrNull(state.currentPage) ?: return
            tts?.setSpeechRate(state.audioSpeed)
            tts?.speak(pageText, TextToSpeech.QUEUE_FLUSH, null, "page_${state.currentPage}")
            _uiState.update { it.copy(isAudioPlaying = true) }
        }
    }

    fun setAudioSpeed(speed: Float) {
        tts?.setSpeechRate(speed)
        _uiState.update { it.copy(audioSpeed = speed) }
    }

    fun setAudioProgress(progress: Float) = _uiState.update { it.copy(audioProgress = progress) }

    fun setHighlightColor(colorHex: Long) = _uiState.update { it.copy(selectedHighlightColor = colorHex) }

    fun addHighlight(startIndex: Int, endIndex: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            repository.addHighlight(
                Highlight(
                    bookId = bookId,
                    page = state.currentPage,
                    startIndex = startIndex,
                    endIndex = endIndex,
                    colorHex = state.selectedHighlightColor
                )
            )
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.isBookmarked) {
                repository.getBookmarks(bookId).first().find { it.page == state.currentPage }
                    ?.let { repository.removeBookmark(it) }
            } else {
                repository.addBookmark(Bookmark(bookId = bookId, page = state.currentPage))
            }
        }
    }

    fun setAutoScroll(enabled: Boolean) {
        _uiState.update { it.copy(autoScrollEnabled = enabled) }
        autoScrollJob?.cancel()
        if (enabled) {
            autoScrollJob = viewModelScope.launch {
                while (true) {
                    val delayMs = (10000L / _uiState.value.autoScrollSpeed).toLong()
                    delay(delayMs)
                    val state = _uiState.value
                    if (state.autoScrollEnabled && state.currentPage < state.totalPages - 1) {
                        nextPage()
                    } else {
                        _uiState.update { it.copy(autoScrollEnabled = false) }
                        break
                    }
                }
            }
        }
    }

    fun setAutoScrollSpeed(speed: Float) = _uiState.update { it.copy(autoScrollSpeed = speed) }

    fun setQuoteText(text: String) = _uiState.update { it.copy(selectedQuoteText = text, showQuoteShare = text.isNotBlank()) }

    fun dismissQuoteShare() = _uiState.update { it.copy(showQuoteShare = false, selectedQuoteText = "") }

    private fun saveProgress() {
        viewModelScope.launch {
            val state = _uiState.value
            repository.saveReadingProgress(
                ReadingProgress(
                    bookId = bookId,
                    currentPage = state.currentPage,
                    totalPages = state.totalPages,
                    chapter = "فصل ${state.currentPage + 1}",
                    lastReadTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
        autoScrollJob?.cancel()
    }


    fun toggleChat() {
        _uiState.update { it.copy(isChatOpen = !it.isChatOpen) }
    }

    fun sendChatMessage(message: String) {
        val userMsg = ChatMessage(isUser = true, text = message)
        _uiState.update { it.copy(
            chatMessages = it.chatMessages + userMsg,
            isChatLoading = true
        ) }

        viewModelScope.launch {
            // Give time to prepare
            kotlinx.coroutines.delay(500)
            
            // Empty placeholder for AI message
            val aiMsgIndex = _uiState.value.chatMessages.size
            _uiState.update { it.copy(chatMessages = it.chatMessages + ChatMessage(isUser = false, text = "")) }
            
            var aiText = ""
            // Mocking stream
            llmEngine.generateChatResponseStream(message, "context...").collect { token ->
                aiText += token
                val updatedMessages = _uiState.value.chatMessages.toMutableList()
                if (aiMsgIndex < updatedMessages.size) {
                    updatedMessages[aiMsgIndex] = ChatMessage(isUser = false, text = aiText)
                    _uiState.update { it.copy(chatMessages = updatedMessages, isChatLoading = false) }
                }
            }
        }
    }
}
