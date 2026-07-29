package com.example.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

data class ChartDataPoint(val label: String, val value: Float)

@Composable
fun AreaChart(
    data: List<ChartDataPoint>,
    lineColor: Color,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    title: String = ""
) {
    if (data.isEmpty()) return

    val maxValue = max(data.maxOf { it.value }, 10f)
    val ySteps = 4
    val yStepValue = maxValue / ySteps

    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val padding = 30.dp.toPx()
            val textPaint = Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 10.sp.toPx()
                textAlign = Paint.Align.RIGHT
            }
            val textPaintX = Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 10.sp.toPx()
                textAlign = Paint.Align.CENTER
            }
            
            val chartWidth = size.width - padding
            val chartHeight = size.height - padding
            val xStep = chartWidth / (data.size - 1)
            
            // Draw grid and Y labels
            for (i in 0..ySteps) {
                val y = chartHeight - (i * chartHeight / ySteps)
                val value = i * yStepValue
                
                // Grid line
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(padding, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                
                // Label
                drawContext.canvas.nativeCanvas.drawText(
                    value.toInt().toString(),
                    padding - 10f,
                    y + 10f,
                    textPaint
                )
            }
            
            // Draw X labels
            data.forEachIndexed { index, point ->
                val x = padding + index * xStep
                drawContext.canvas.nativeCanvas.drawText(
                    point.label,
                    x,
                    size.height,
                    textPaintX
                )
            }
            
            // Draw area and line
            val path = Path()
            val fillPath = Path()
            
            var prevX = 0f
            var prevY = 0f
            
            data.forEachIndexed { index, point ->
                val x = padding + index * xStep
                val y = chartHeight - (point.value / maxValue * chartHeight)
                
                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, chartHeight)
                    fillPath.lineTo(x, y)
                } else {
                    // Cubic bezier for smoothness
                    val controlX = (prevX + x) / 2
                    path.cubicTo(controlX, prevY, controlX, y, x, y)
                    fillPath.cubicTo(controlX, prevY, controlX, y, x, y)
                }
                
                prevX = x
                prevY = y
            }
            
            fillPath.lineTo(prevX, chartHeight)
            fillPath.close()
            
            // Draw fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = chartHeight
                )
            )
            
            // Draw line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Draw points
            data.forEachIndexed { index, point ->
                val x = padding + index * xStep
                val y = chartHeight - (point.value / maxValue * chartHeight)
                
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y),
                    style = Fill
                )
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
