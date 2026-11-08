package com.example.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.AttributeProgress
import com.example.domain.model.AttributeType
import com.example.domain.model.HunterRank
import com.example.presentation.SystemBannerEvent
import com.example.ui.theme.LocalReducedAnimations
import com.example.ui.theme.SystemBorderDim
import com.example.ui.theme.SystemCharcoal
import com.example.ui.theme.SystemCyan
import com.example.ui.theme.SystemGold
import com.example.ui.theme.SystemObsidian
import com.example.ui.theme.SystemSurface
import com.example.ui.theme.SystemSurfaceElevated
import com.example.ui.theme.SystemTextMuted
import com.example.ui.theme.SystemTextPrimary
import com.example.ui.theme.SystemTextSecondary
import com.example.ui.theme.SystemViolet
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HolographicCard(
    modifier: Modifier = Modifier,
    accentColor: Color = SystemCyan,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp, topEnd = 4.dp, bottomStart = 4.dp)
    val clickMod = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Card(
        modifier = modifier
            .clip(shape)
            .then(clickMod),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = SystemSurface.copy(alpha = 0.92f)),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.65f),
                    SystemBorderDim.copy(alpha = 0.4f),
                    accentColor.copy(alpha = 0.35f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun RankBadgeChip(
    rank: HunterRank,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
        color = rank.color.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, rank.color.copy(alpha = 0.85f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(rank.color, CutCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = rank.fullLabel.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = rank.color
            )
        }
    }
}

@Composable
fun SystemProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = SystemCyan,
    trackColor: Color = SystemObsidian,
    height: Dp = 8.dp
) {
    val reducedAnimations = LocalReducedAnimations.current
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (reducedAnimations) tween(0) else tween(500, easing = FastOutSlowInEasing),
        label = "system_progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(trackColor)
            .border(0.5.dp, color.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(height)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(color.copy(alpha = 0.65f), color)
                    )
                )
        )
    }
}

@Composable
fun SystemNotificationBanner(
    event: SystemBannerEvent?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = event != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        event?.let { banner ->
            val accent = when {
                banner.isRankUp -> SystemGold
                banner.isLevelUp -> SystemCyan
                else -> SystemViolet
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                color = SystemSurfaceElevated,
                border = BorderStroke(1.5.dp, accent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "System notification",
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "[SYSTEM NOTICE] ${banner.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = accent
                        )
                        Text(
                            text = banner.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss notification",
                            tint = SystemTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PentagonAttributeRadar(
    attributes: Map<AttributeType, AttributeProgress>,
    modifier: Modifier = Modifier
) {
    val orderedTypes = listOf(
        AttributeType.STRENGTH,
        AttributeType.ENDURANCE,
        AttributeType.AGILITY,
        AttributeType.FLEXIBILITY,
        AttributeType.DISCIPLINE
    )
    val maxScaleLevel = (attributes.values.maxOfOrNull { it.level } ?: 5).coerceAtLeast(5)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(134.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = size.minDimension / 2.2f

                // Concentric pentagon rings
                for (ring in 1..3) {
                    val r = radius * (ring / 3f)
                    val ringPath = Path()
                    for (i in 0 until 5) {
                        val angleRad = Math.toRadians((-90.0 + i * 72.0))
                        val x = cx + (r * cos(angleRad)).toFloat()
                        val y = cy + (r * sin(angleRad)).toFloat()
                        if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
                    }
                    ringPath.close()
                    drawPath(
                        path = ringPath,
                        color = SystemBorderDim.copy(alpha = 0.6f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Axis spokes
                for (i in 0 until 5) {
                    val angleRad = Math.toRadians((-90.0 + i * 72.0))
                    val x = cx + (radius * cos(angleRad)).toFloat()
                    val y = cy + (radius * sin(angleRad)).toFloat()
                    drawLine(
                        color = SystemBorderDim.copy(alpha = 0.5f),
                        start = Offset(cx, cy),
                        end = Offset(x, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Player attribute polygon
                val valuePath = Path()
                val vertices = mutableListOf<Offset>()
                orderedTypes.forEachIndexed { i, type ->
                    val progress = attributes[type]
                    val levelScore = ((progress?.level ?: 1) + (progress?.progressFraction ?: 0f))
                    val normalized = (levelScore / (maxScaleLevel + 1f)).coerceIn(0.22f, 1f)
                    val angleRad = Math.toRadians((-90.0 + i * 72.0))
                    val x = cx + (radius * normalized * cos(angleRad)).toFloat()
                    val y = cy + (radius * normalized * sin(angleRad)).toFloat()
                    vertices.add(Offset(x, y))
                    if (i == 0) valuePath.moveTo(x, y) else valuePath.lineTo(x, y)
                }
                valuePath.close()

                drawPath(
                    path = valuePath,
                    color = SystemCyan.copy(alpha = 0.22f)
                )
                drawPath(
                    path = valuePath,
                    color = SystemCyan,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                vertices.forEachIndexed { idx, pt ->
                    drawCircle(
                        color = orderedTypes[idx].color,
                        radius = 4.dp.toPx(),
                        center = pt
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            orderedTypes.forEach { attr ->
                val prog = attributes[attr]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(attr.color, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${attr.shortCode} • ${attr.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SystemTextSecondary
                        )
                    }
                    Text(
                        text = "LV.${prog?.level ?: 1}",
                        style = MaterialTheme.typography.labelLarge,
                        color = attr.color,
                        fontWeight = FontWeight.Bold
                    )
                }
                SystemProgressBar(
                    progress = prog?.progressFraction ?: 0.2f,
                    color = attr.color,
                    height = 4.dp
                )
            }
        }
    }
}
