package com.monem.ktai.presentation.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monem.ktai.presentation.common.components.KtAIButton
import com.monem.ktai.presentation.common.components.KtAIOutlinedButton
import com.monem.ktai.presentation.common.components.KtAITopBar
import com.monem.ktai.presentation.common.theme.AccentGreen
import com.monem.ktai.presentation.common.theme.CardBackground
import com.monem.ktai.presentation.common.theme.CardBorder
import com.monem.ktai.presentation.common.theme.FreePlanColor
import com.monem.ktai.presentation.common.theme.MaxPlanColor
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.ProPlanColor
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

private data class PlanInfo(
    val name: String,
    val price: String,
    val color: Color,
    val features: List<String>,
    val isCurrent: Boolean = false,
    val isRecommended: Boolean = false,
)

private val plans = listOf(
    PlanInfo(
        name = "Free",
        price = "$0/mo",
        color = FreePlanColor,
        features = listOf(
            "20 AI messages/month",
            "1 workspace",
            "Basic chat",
            "Small file context (2K tokens)",
        ),
        isCurrent = true,
    ),
    PlanInfo(
        name = "Pro",
        price = "$9.99/mo",
        color = ProPlanColor,
        features = listOf(
            "200 AI messages/month",
            "5 workspaces",
            "Multi-file edits",
            "Longer context (8K tokens)",
            "Priority support",
        ),
        isRecommended = true,
    ),
    PlanInfo(
        name = "Max",
        price = "$24.99/mo",
        color = MaxPlanColor,
        features = listOf(
            "Unlimited AI messages",
            "Unlimited workspaces",
            "Agent mode",
            "Project memory",
            "Priority model access",
            "Large context (32K tokens)",
        ),
    ),
)

@Composable
fun SubscriptionScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
    ) {
        KtAITopBar(title = "Subscription Plans", onBack = onNavigateBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Choose Your Plan",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            plans.forEach { plan ->
                PlanCard(plan = plan)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlanCard(plan: PlanInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(
            width = if (plan.isRecommended) 2.dp else 1.dp,
            color = if (plan.isRecommended) plan.color else CardBorder,
        ),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (plan.isRecommended) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = plan.color, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                }
                Text(plan.name, style = MaterialTheme.typography.titleLarge, color = plan.color)
                if (plan.isCurrent) {
                    Spacer(Modifier.width(8.dp))
                    Text("Current", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(plan.price, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)

            Spacer(Modifier.height(16.dp))

            plan.features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(feature, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (plan.isCurrent) {
                KtAIOutlinedButton(text = "Current Plan", onClick = {})
            } else {
                KtAIButton(text = "Upgrade to ${plan.name}", onClick = { /* TODO: Payment flow */ })
            }
        }
    }
}
