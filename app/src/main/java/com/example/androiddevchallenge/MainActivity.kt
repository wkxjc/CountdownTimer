/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Kevin 2021-03-04
 */
class MainActivity : AppCompatActivity() {
    private val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.valueAnim?.cancel()
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val viewModel: TimerViewModel = viewModel()
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
                }
            )
        },
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CompleteText(viewModel)
            TimeLeftText(viewModel)
            ProgressCircle(viewModel)
            EditText(viewModel)
            Row {
                StartButton(viewModel)
                StopButton(viewModel)
            }
        }
    }
}

@Composable
private fun TimeLeftText(viewModel: TimerViewModel) {
    Text(
        text = viewModel.timeLeftValue(),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun CompleteText(viewModel: TimerViewModel) {
    Text(
        text = viewModel.completeString(),
        color = MaterialTheme.colors.primary
    )
}

@Composable
fun ProgressCircle(viewModel: TimerViewModel) {
    val size = 160.dp
    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(size)
        ) {
            val sweepAngle = viewModel.progressSweepAngle()
            val r = size.toPx() / 2
            val stokeWidth = 12.dp.toPx()
            drawCircle(
                color = Color.LightGray,
                style = Stroke(width = stokeWidth, pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(3.dp.toPx(), 2.dp.toPx())))
            )
            drawArc(
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = stokeWidth),
                color = Color.Cyan,
                alpha = 0.5f
            )
            // Pointer
            val angle = (360 - sweepAngle) / 180 * Math.PI
            val pointTailLength = 8.dp.toPx()
            drawLine(
                color = Color.Red,
                start = Offset(r + pointTailLength * sin(angle).toFloat(), r + pointTailLength * cos(angle).toFloat()),
                end = Offset((r - r * sin(angle) - sin(angle) * stokeWidth / 2).toFloat(), (r - r * cos(angle) - cos(angle) * stokeWidth / 2).toFloat()),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(
                color = Color.Red,
                radius = 5.dp.toPx()
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx()
            )
        }
    }
}

@Composable
private fun EditText(viewModel: TimerViewModel) {
    Box(
        modifier = Modifier
            .size(300.dp, 120.dp),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.showEditText()) {
            TextField(
                modifier = Modifier
                    .padding(16.dp)
                    .size(200.dp, 60.dp),
                value = viewModel.editTextValue(),
                onValueChange = {
                    viewModel.editTextValueChanged(it)
                },
                label = { Text("Countdown Seconds") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
private fun StartButton(viewModel: TimerViewModel) {
    Button(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp),
        enabled = viewModel.startButtonEnabled(),
        onClick = {
            viewModel.clickStartButton()
        }
    ) {
        Text(text = viewModel.startButtonDisplayString())
    }
}

@Composable
private fun StopButton(viewModel: TimerViewModel) {
    Button(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp),
        enabled = viewModel.stopButtonEnabled(),
        onClick = {
            viewModel.clickStopButton()
        }
    ) {
        Text(text = "Stop")
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
