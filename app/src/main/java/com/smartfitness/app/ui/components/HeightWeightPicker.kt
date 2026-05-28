package com.smartfitness.app.ui.components


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ITEM_HEIGHT = 44.dp
private const val VISIBLE_ITEMS = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeightWeightPicker(
    title: String,
    unit: String,
    range: IntRange,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val list = remember { range.toList() }
    val initialIndex = list.size / 2

    val itemHeightPx = with(LocalDensity.current) { ITEM_HEIGHT.toPx() }
    // contentPadding = (visibleItems / 2) * itemHeight  so item[0] can reach center
    val contentPaddingDp = ITEM_HEIGHT * (VISIBLE_ITEMS / 2)

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )

    // centeredIndex: which real-list item sits inside the highlight box
    // firstVisibleItemIndex is the first item AFTER the top contentPadding is scrolled past,
    // so index 0 == list[0]. The centered slot is always at visibleItems/2 offset.
    val centeredIndex by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            val extra = if (offset > itemHeightPx / 2f) 1 else 0
            (listState.firstVisibleItemIndex + extra)
                .coerceIn(list.indices)
        }
    }

    var selectedIndex by remember { mutableIntStateOf(initialIndex) }

    LaunchedEffect(listState) {
        snapshotFlow { centeredIndex }.collect { idx ->
            selectedIndex = idx
        }
    }

    // Snap to center after fling/drag ends
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrolling ->
            if (!scrolling) {
                listState.animateScrollToItem(selectedIndex)
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Title row with Done button on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = {
                    onSelect(list[selectedIndex.coerceIn(list.indices)])
                    onDismiss()
                }) {
                    Text(
                        text = "Done",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF113DFA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Outer box is exactly ITEM_HEIGHT * VISIBLE_ITEMS tall
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(ITEM_HEIGHT * VISIBLE_ITEMS)
            ) {

                LazyColumn(
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    // contentPadding pushes first & last items to be reachable at center
                    contentPadding = PaddingValues(vertical = contentPaddingDp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(list) { index, value ->
                        val isCenter = index == selectedIndex
                        Text(
                            text = "$value",
                            fontSize = if (isCenter) 26.sp else 18.sp,
                            fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCenter) Color(0xFF113DFA) else Color.LightGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ITEM_HEIGHT)
                                .padding(vertical = 10.dp)
                        )
                    }
                }

                // Center highlight box — sits exactly over the middle slot
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                        .border(
                            1.5.dp,
                            Color(0xFF113DFA).copy(alpha = 0.4f),
                            RoundedCornerShape(12.dp)
                        )
                )

                // Unit label
                Text(
                    text = unit,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF113DFA),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}