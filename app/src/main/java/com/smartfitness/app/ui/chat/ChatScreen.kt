package com.smartfitness.app.ui.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import dev.jeziellago.compose.markdowntext.MarkdownText
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.smartfitness.app.core.theme.Background
import com.smartfitness.app.core.theme.BrandColor
import com.smartfitness.app.core.theme.PrimaryBlue
import com.smartfitness.app.core.theme.PrimaryBlueLight
import com.smartfitness.app.core.theme.TextPrimary
import com.smartfitness.app.core.utilities.HelperFunctions.EMOJIS
import com.smartfitness.app.core.utilities.HelperFunctions.dateLabelFor
import com.smartfitness.app.core.utilities.HelperFunctions.isSameDay
import com.smartfitness.app.core.utilities.HelperFunctions.quickActions
import com.smartfitness.app.domain.model.ChatMessage
import com.smartfitness.app.domain.model.MessageStatus
import com.smartfitness.app.domain.model.QuickAction
import com.smartfitness.app.ui.components.AppDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


// ── ChatScreen ────────────────────────────────────────────────────
@Composable
fun ChatScreen(navHostController: NavHostController)
{
    val viewModel: ChatViewModel = hiltViewModel()
    val aiMessages by viewModel.messages.collectAsState()
    val isTyping   by viewModel.isTyping.collectAsState()
    var selectedMessages by remember { mutableStateOf(setOf<ChatMessage>()) }
    val isSelectionMode = selectedMessages.isNotEmpty()
    var messageText      by remember { mutableStateOf("") }
    var showEmojiPicker  by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val listState = rememberLazyListState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
    val messages = remember {
        mutableStateListOf(
            ChatMessage("Hi 👋 How can I help you today?", false, yesterday),
            ChatMessage("Suggest me a workout plan", true, yesterday),
            ChatMessage("Sure! Try a full body workout 3x/week 💪", false, System.currentTimeMillis())
        )
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(aiMessages.size) {
        aiMessages.lastOrNull()?.takeIf { it.role == "assistant" }?.let {
            messages.add(
                ChatMessage(
                    text = it.content,
                    false, System.currentTimeMillis(),
                    status = MessageStatus.DELIVERED
                )
            )
        }

      /*  val aiMessageIndex = messages.lastIndex   // ✅ ALWAYS SAFE
        scope.launch {
            viewModel.streamResponse(messageText).collect { chunk ->

                // ✅ Update same message using copy
                messages[aiMessageIndex] =
                    messages[aiMessageIndex].copy(text = chunk)
            }
        }*/
    }

    LaunchedEffect(messages.lastOrNull()?.text) {
        listState.animateScrollToItem(messages.lastIndex)
    }

    LaunchedEffect(messages.size, isTyping) {
        val target = messages.size + (if (isTyping) 1 else 0)
        if (target > 0) listState.animateScrollToItem(target - 1)
    }

    // Outer Column — TopBar + content
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            ChatTopBar(
             navController = navHostController,
            selectedCount = selectedMessages.size,
            onClearSelection = {
                selectedMessages = emptySet()
            },
            onDelete = {
                showDeleteDialog = true
            }
            ) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(paddingValues)
                .background(Background)
        ) {


            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    itemsIndexed(messages) { index, message ->
                        val showDivider = index == 0 || !isSameDay(
                            Calendar.getInstance()
                                .apply { timeInMillis = messages[index - 1].timestamp },
                            Calendar.getInstance().apply { timeInMillis = message.timestamp })
                        if (showDivider) DateDivider(label = dateLabelFor(message.timestamp))
                        ChatBubble(message,
                            isSelected = selectedMessages.contains(message),
                            onLongPress = {
                                selectedMessages = selectedMessages + message
                            },
                            onClick = {
                                if (isSelectionMode){
                                    selectedMessages = if (selectedMessages.contains(message))
                                        selectedMessages - message else selectedMessages + message
                                }
                            })
                    }
                    if (isTyping) {
                        item { TypingIndicator() }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom
                ) {

                    QuickActionChips(
                        actions = quickActions,
                        onClick = { action ->
                            // Option 1: Direct send
                            val now = System.currentTimeMillis()
                            messages.add(ChatMessage(action.prompt, true, now))
                            viewModel.sendMessage(action.prompt)

                            // Option 2 (alternative): just fill input
                            // messageText = action.prompt
                        }
                    )
                }
                ChatInput(
                    text = messageText,
                    showEmojiPicker = showEmojiPicker,
                    selectedImageUri = selectedImageUri,
                    onEmojiToggle = { showEmojiPicker = !showEmojiPicker },
                    onEmojiSelected = { emoji -> messageText += emoji },
                    onTextChange = { messageText = it },
                    onImageSelected = { uri ->
                        selectedImageUri = if (uri == Uri.EMPTY) null else uri
                    },
                    onSend = {
                        val now = System.currentTimeMillis()
                        if (selectedImageUri != null) {
                            messages.add(
                                ChatMessage(
                                    text = messageText, isUser = true,
                                    timestamp = now, imageUri = selectedImageUri
                                )
                            )
                            viewModel.sendMessage(
                                if (messageText.isNotBlank()) messageText
                                else "I sent an image. Give fitness advice."
                            )
                            selectedImageUri = null; messageText = ""; showEmojiPicker = false
                        } else if (messageText.isNotBlank()) {
                            messages.add(ChatMessage(messageText, true, now, status = MessageStatus.SENDING))
                            viewModel.sendMessage(messageText)
                            messageText = ""; showEmojiPicker = false
                        }
                    },
                    modifier = Modifier
                     .imePadding()
                )
            }
        }
    }
    if (showDeleteDialog){
        AppDialog(
            title = "Delete Messages",
            message = "Are you sure you want to delete the selected messages?",
            confirmText = "Delete",
            onConfirm = {
                messages.removeAll(selectedMessages)
                selectedMessages = emptySet()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

// ── ChatTopBar ────────────────────────────────────────────────────
@Composable
fun ChatTopBar(navController: NavController,
               selectedCount: Int,
               onClearSelection: () -> Unit,
               onDelete: () -> Unit)
{

    val isSelectionMode = selectedCount > 0

    Box(modifier = Modifier
        .fillMaxWidth()
        .shadow(4.dp)
        .background(Brush.horizontalGradient(listOf(PrimaryBlue, BrandColor, PrimaryBlueLight)))
        .statusBarsPadding()
        .padding(horizontal = 4.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {

            if (isSelectionMode){
                IconButton(onClick = onClearSelection) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "$selectedCount selected",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color.White)
                }
            }else{
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                            )
                        ),
                        contentAlignment = Alignment.Center
                    ) { Text("🤖", fontSize = 22.sp) }
                    Box(modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI Fitness Coach", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = Color.White)
                    Text("● Online · Always ready", fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f))
                }
                SettingIcon(
                    onClearChat = { onDelete() },
                    onExportChat = { /* TODO */ },
                    onSettingsClick = { /* TODO */ }
                )
            }
        }
    }


}




@Composable
fun SettingIcon(
    onClearChat: () -> Unit,
    onExportChat: () -> Unit,
    onSettingsClick: () -> Unit
){
    var showDropMenu by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { showDropMenu = true }) {
            Icon(Icons.Default.MoreVert, "Options", tint = Color.White)
        }

        DropdownMenu(
            expanded = showDropMenu,
            onDismissRequest = { showDropMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Clear chat") },
                onClick = {
                    showDropMenu = false
                    onClearChat()
                }
            )

            DropdownMenuItem(
                text = { Text("Export chat") },
                onClick = {
                    showDropMenu = false
                    onExportChat()
                }
            )

            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = {
                    showDropMenu = false
                    onSettingsClick()
                }
            )
        }
    }
}



// ── DateDivider ───────────────────────────────────────────────────
@Composable
fun DateDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f),
            color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
        Text(text = label, fontSize = 12.sp, color = Color.Gray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE8EAFF))
                .padding(horizontal = 12.dp, vertical = 4.dp))
        HorizontalDivider(modifier = Modifier.weight(1f),
            color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
    }
}

// ── TypingIndicator ───────────────────────────────────────────────
@Composable
fun TypingIndicator() {
    val offsets = remember { List(3) { Animatable(0f) } }
    offsets.forEachIndexed { i, anim ->
        LaunchedEffect(anim) {
            kotlinx.coroutines.delay(i * 150L)
            anim.animateTo(1f, animationSpec = infiniteRepeatable(
                animation = tween(400, easing = LinearEasing), repeatMode = RepeatMode.Reverse))
        }
    }
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomEnd = 16.dp, bottomStart = 4.dp
                )
            )
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        offsets.forEach { anim ->
            Box(modifier = Modifier
                .size(8.dp)
                .offset(y = (-8 * anim.value).dp)
                .clip(CircleShape)
                .background(BrandColor))
        }
    }
}

// ── ChatBubble ────────────────────────────────────────────────────
@Composable
fun ChatBubble(message: ChatMessage,
               isSelected: Boolean,
               onLongPress: () -> Unit,
               onClick: () -> Unit) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bgColor   = if (message.isUser) BrandColor else Color.White
    val textColor = if (message.isUser) Color.White else TextPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
    ) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .combinedClickable(
            onClick = onClick,
            onLongClick = onLongPress
        ),
        horizontalAlignment = alignment) {
        Box(modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                )
            )
            .background(bgColor)
            .widthIn(max = 260.dp)
        ) {
            Column(modifier = Modifier.padding(
                horizontal = if (message.imageUri != null) 4.dp else 14.dp,
                vertical   = if (message.imageUri != null) 4.dp else 10.dp)) {
                if (message.imageUri != null) {
                    AsyncImage(model = message.imageUri, contentDescription = "Sent image",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp)))
                }
                if (message.text.isNotBlank()) {
                    if (message.imageUri != null) Spacer(Modifier.height(6.dp))
                    MarkdownText(
                        markdown = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textColor,
                            fontSize = 15.sp
                        ),
                        modifier = if (message.imageUri != null)
                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp) else Modifier
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(Date(message.timestamp)),
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.6f)
            )

                Spacer(modifier = Modifier.width(4.dp))
            if (message.isUser) {
                MessageStatusIcon(message.status)
            }

        }
    }
    if (isSelected)
         Box(modifier = Modifier
             .matchParentSize()
             .clip(RoundedCornerShape(16.dp))
             .background(
                 MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
             ))
     }


}

@Composable
fun MessageStatusIcon(status: MessageStatus) {
    val color = when (status) {
        MessageStatus.SENT -> Color.Gray
        MessageStatus.DELIVERED -> Color.Gray
        MessageStatus.SEEN -> Color(0xFF4FC3F7) // blue
        else -> Color.Gray
    }

    val icon = when (status) {
        MessageStatus.SENT -> Icons.Default.Done
        MessageStatus.DELIVERED -> Icons.Default.DoneAll
        MessageStatus.SEEN -> Icons.Default.AirplaneTicket
        else ->  Icons.Default.Done
    }

    Icon(
        imageVector = icon,
        contentDescription = "status",
        tint = color,
    )
}
// ── EmojiPalette ─────────────────────────────────────────────────
@Composable
fun EmojiPalette(onEmojiSelected: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(horizontal = 8.dp, vertical = 6.dp)) {
        EMOJIS.chunked(8).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { emoji ->
                    Text(emoji, fontSize = 26.sp,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onEmojiSelected(emoji) })
                }
                repeat(8 - row.size) { Spacer(Modifier.size(34.dp)) }
            }
        }
    }
}


@Composable
fun QuickActionChips(
    actions: List<QuickAction>,
    onClick: (QuickAction) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(actions.size) { index ->
            val action = actions[index]

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onClick(action) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = action.title,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
// ── ChatInput ─────────────────────────────────────────────────────
@Composable
fun ChatInput(
    text: String,
    showEmojiPicker: Boolean,
    selectedImageUri: Uri?,
    onEmojiToggle: () -> Unit,
    onEmojiSelected: (String) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
)
{
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onImageSelected(it) } }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.let { onTextChange(it) }
        }
    }

    val iconTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val inputBg  = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)) {

        // Image preview
        if (selectedImageUri != null) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                AsyncImage(model = selectedImageUri, contentDescription = "Preview",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp)))
                Box(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { onImageSelected(Uri.EMPTY) },
                    contentAlignment = Alignment.Center
                ) { Text("✕", color = Color.White, fontSize = 10.sp) }
            }
        }

        // Emoji palette
        if (showEmojiPicker) EmojiPalette(onEmojiSelected = onEmojiSelected)

        // Input row
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {

            TextField(
                value = text, onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask something...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                leadingIcon = {
                    Box(modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onEmojiToggle() },
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.EmojiEmotions, "Emoji",
                            tint = if (showEmojiPicker) BrandColor
                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(22.dp))
                    }
                },
                shape = RoundedCornerShape(24.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = MaterialTheme.colorScheme.onSurface),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = inputBg, unfocusedContainerColor = inputBg,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                    cursorColor             = BrandColor)
            )

            // Mic
            Box(modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable {
                    speechLauncher.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                    })
                }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Mic, "Speak", tint = iconTint, modifier = Modifier.size(22.dp))
            }

            // Attach
            Box(modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AttachFile, "Attach", tint = iconTint,
                    modifier = Modifier.size(22.dp))
            }

            // Send
            Box(modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(BrandColor)
                .clickable { onSend() },
                contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.Send, "Send",
                    tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}


