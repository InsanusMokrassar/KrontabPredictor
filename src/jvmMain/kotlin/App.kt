package dev.inmo.krontab.predictor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.inmo.krontab.predictor.ui.main.MainViewModel
import korlibs.time.format

fun main(args: Array<String>) = application {
    val viewModel = MainViewModel()
    Window(onCloseRequest = ::exitApplication, title = "Kotlin Multiplatform Publishing Builder") {
        MaterialTheme(
            Colors(
                primary = Color(0x01, 0x57, 0x9b),
                primaryVariant = Color(0x00, 0x2f, 0x6c),
                secondary = Color(0xb2, 0xeb, 0xf2),
                secondaryVariant = Color(0x81, 0xb9, 0xbf),
                background = Color(0xe1, 0xe2, 0xe1),
                surface = Color(0xf5, 0xf5, 0xf6),
                error = Color(0xb7, 0x1c, 0x1c),
                onPrimary = Color.White,
                onSecondary = Color.Black,
                onBackground = Color.Black,
                onSurface = Color.Black,
                onError = Color.White,
                isLight = MaterialTheme.colors.isLight,
            )
        ) {
            Box(
                Modifier.fillMaxSize()
                    .background(color = Color(245, 245, 245))
            ) {
                val stateVertical = rememberScrollState(0)

                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Row(Modifier.width(450.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.secondsUIState.value,
                            onValueChange = { viewModel.secondsState.value = it },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle.Default.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                        OutlinedTextField(
                            value = viewModel.minutesUIState.value,
                            onValueChange = { viewModel.minutesState.value = it },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle.Default.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                        OutlinedTextField(
                            value = viewModel.hoursUIState.value,
                            onValueChange = { viewModel.hoursState.value = it },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle.Default.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                        OutlinedTextField(
                            value = viewModel.daysUIState.value,
                            onValueChange = { viewModel.daysState.value = it },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle.Default.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                        OutlinedTextField(
                            value = viewModel.monthsUIState.value,
                            onValueChange = { viewModel.monthsState.value = it },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle.Default.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                    LazyVerticalGrid(GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(viewModel.schedule) {
                            Row(
                                Modifier.fillMaxWidth(),
                                Arrangement.Center
                            ) {
                                Text(DateTimeFormatter.default.format(it))
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )
            }
        }
    }
}