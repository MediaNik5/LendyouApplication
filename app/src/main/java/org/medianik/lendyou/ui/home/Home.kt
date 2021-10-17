package org.medianik.lendyou.ui.home

import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.insets.navigationBarsPadding
import org.medianik.lendyou.R
import org.medianik.lendyou.ui.component.LendyouSurface
import org.medianik.lendyou.ui.theme.LendyouTheme

fun NavGraphBuilder.addHomeGraph(
    onSnackSelected: (Long, NavBackStackEntry) -> Unit,
    modifier: Modifier = Modifier
) {
//    composable(HomeSections.FEED.route) { from ->
//        Prototype(featureName = R.string.home_feed)
////        Feed(onSnackClick = { id -> onSnackSelected(id, from) }, modifier)
//    }
    composable(HomeSections.DEBTORS.route) { from ->
        Prototype(featureName = R.string.home_debtors)
//        Search(onSnackClick = { id -> onSnackSelected(id, from) }, modifier)
    }
    composable(HomeSections.LENDERS.route) { from ->
        Prototype(featureName = R.string.home_lenders)
//        Cart(onSnackClick = { id -> onSnackSelected(id, from) }, modifier)
    }
    composable(HomeSections.DEBTS.route) { from ->
        Prototype(featureName = R.string.home_debts)
//        Cart(onSnackClick = { id -> onSnackSelected(id, from) }, modifier)
    }
    composable(HomeSections.PROFILE.route) {
        Prototype(featureName = R.string.home_profile)
//        Profile(modifier)
    }
}

enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
//    FEED(R.string.home_feed, Icons.Outlined.Home, "home/feed"),
    DEBTS(R.string.home_debts, Icons.Outlined.Payments, "home/debts"),
    DEBTORS(R.string.home_debtors, Icons.Outlined.CallReceived, "home/debtors"),
    LENDERS(R.string.home_lenders, Icons.Outlined.CallMade, "home/lenders"),
    PROFILE(R.string.home_profile, Icons.Outlined.AccountCircle, "home/profile")
}

@Composable
fun LendyouBottomBar(
    tabs: Array<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    color: Color = LendyouTheme.colors.iconPrimary,
    contentColor: Color = LendyouTheme.colors.iconInteractive
) {
    val routes = remember { tabs.map { it.route } }
    val currentSelection = tabs.first { it.route == currentRoute }

    LendyouSurface(
        color = color,
        contentColor = contentColor,
    ) {
        val springSpec = SpringSpec<Float>(
            // Determined experimentally
            stiffness = 800f,
            dampingRatio = 0.8f
        )

        LendyouBottomNavLayout(
            selectedIndex = currentSelection.ordinal,
            itemCount = routes.size,
            indicator = { LendyouBottomNavIndicator() },
            animSpec = springSpec,
            modifier = Modifier.navigationBarsPadding(start = false, end = false)
        ) {
            tabs.forEach { section ->
                DefaultLendyouBottomNavigationItem(
                    section,
                    currentSelection,
                    navigateToRoute,
                    springSpec
                )
            }
        }
    }
}

@Composable
private fun DefaultLendyouBottomNavigationItem(
    section: HomeSections,
    currentSelection: HomeSections,
    navigateToRoute: (String) -> Unit,
    springSpec: SpringSpec<Float>
) {
    val selected = section == currentSelection
    val tint by animateColorAsState(
        if (selected) {
            LendyouTheme.colors.iconInteractive
        } else {
            LendyouTheme.colors.iconInteractiveInactive
        }
    )
    LendyouBottomNavigationItem(
        icon = {
            Icon(
                imageVector = section.icon,
                tint = tint,
                contentDescription = null
            )
        },
        text = {
            Text(
                text = stringResource(section.title)/*.uppercase(
                    ConfigurationCompat.getLocales(
                        LocalConfiguration.current
                    ).get(0)
                )*/,
                color = tint,
                style = MaterialTheme.typography.button,
                maxLines = 1
            )
        },
        selected = selected,
        onSelected = { navigateToRoute(section.route) },
        animSpec = springSpec,
        modifier = BottomNavigationItemPadding
            .clip(BottomNavIndicatorShape)
    )
}

private val TextIconSpacing = 2.dp
private val BottomNavHeight = 56.dp
private val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)

@Composable
private fun LendyouBottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val selectionFractions = selectionAnimationForThisFrame(itemCount, selectedIndex, animSpec)
    val indicatorIndex = indicatorAnimationForThisFrame(selectedIndex, animSpec)

    Layout(
        modifier = modifier.height(BottomNavHeight),
        content = {
            content()
            Box(Modifier.layoutId("indicator"), content = indicator)
        }
    ) { measurables, constraints ->
        check(itemCount == (measurables.size - 1))  // account for indicator

        measureAndPlaceMeasurables(
            constraints,
            itemCount,
            measurables,
            selectionFractions,
            indicatorIndex
        )
    }
}

private fun MeasureScope.measureAndPlaceMeasurables(
    constraints: Constraints,
    itemCount: Int,
    measurables: List<Measurable>,
    selectionFractions: List<Animatable<Float, AnimationVector1D>>,
    indicatorIndex: Animatable<Float, AnimationVector1D>
): MeasureResult {
    // Divide the width into n+1 slots and give the selected item 2 slots
    val unselectedWidth = constraints.maxWidth / (itemCount + 1)
    val selectedWidth = 2 * unselectedWidth
    val indicatorMeasurable = measurables.first { it.layoutId == "indicator" }

    val itemPlaceables = measureItems(
        measurables,
        selectionFractions,
        indicatorMeasurable,
        constraints,
        unselectedWidth,
        selectedWidth
    )

    val indicatorPlaceable = measure(indicatorMeasurable, constraints, selectedWidth)

    return place(
        indicatorIndex,
        itemPlaceables,
        indicatorPlaceable,
        constraints,
        unselectedWidth
    )
}

private fun measureItems(
    measurables: List<Measurable>,
    selectionFractions: List<Animatable<Float, AnimationVector1D>>,
    indicatorMeasurable: Measurable,
    constraints: Constraints,
    unselectedWidth: Int,
    selectedWidth: Int
): List<Placeable> {
    val itemPlaceables = measurables
        .filterNot { it == indicatorMeasurable }
        .mapIndexed { index, measurable ->
            // Animate item's width based upon the selection amount
            val width = lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
            measure(measurable, constraints, width)
        }
    return itemPlaceables
}

private fun measure(
    measurable: Measurable,
    constraints: Constraints,
    width: Int
): Placeable {
    return measurable.measure(
        constraints.copy(
            minWidth = width,
            maxWidth = width
        )
    )
}

private fun MeasureScope.place(
    indicatorIndex: Animatable<Float, AnimationVector1D>,
    itemPlaceables: List<Placeable>,
    indicatorPlaceable: Placeable,
    constraints: Constraints,
    unselectedWidth: Int
) = layout(
    width = constraints.maxWidth,
    height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
) {
    val indicatorLeft = indicatorIndex.value * unselectedWidth
    indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
    var x = 0
    itemPlaceables.forEach { placeable ->
        placeable.placeRelative(x = x, y = 0)
        x += placeable.width
    }
}

@Composable
private fun selectionAnimationForThisFrame(
    itemCount: Int,
    selectedIndex: Int,
    animSpec: AnimationSpec<Float>
): List<Animatable<Float, AnimationVector1D>> {
    val transitionFractions = remember(itemCount) {
        List(itemCount) { i ->
            Animatable(if (i == selectedIndex) 1f else 0f)
        }
    }
    transitionFractions.forEachIndexed { index, selectionFraction ->
        val target = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(target, animSpec) {
            selectionFraction.animateTo(target, animSpec)
        }
    }
    return transitionFractions
}


@Composable
private fun indicatorAnimationForThisFrame(
    selectedIndex: Int,
    animSpec: AnimationSpec<Float>
): Animatable<Float, AnimationVector1D> {
    val indicatorIndex = remember { Animatable(0f) }
    val targetIndicatorIndex = selectedIndex.toFloat()
    LaunchedEffect(key1 = targetIndicatorIndex) {
        indicatorIndex.animateTo(targetIndicatorIndex, animSpec)
    }
    return indicatorIndex
}

@Composable
private fun LendyouBottomNavIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = LendyouTheme.colors.iconInteractive,
    shape: Shape = BottomNavIndicatorShape
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(BottomNavigationItemPadding)
            .border(strokeWidth, color, shape)
    )
}

@Composable
fun LendyouBottomNavigationItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.selectable(selected = selected, onClick = onSelected),
        contentAlignment = Alignment.Center
    ) {
        // Animate the icon/text positions within the item based on selection
        val animationProgress by animateFloatAsState(if (selected) 1f else 0f, animSpec)
        LendyouBottomNavItemLayout(
            icon = icon,
            text = text,
            animationProgress = animationProgress
        )
    }
}

@Composable
private fun LendyouBottomNavItemLayout(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
) {
    Layout(
        content = {
            ContentIcon(icon)
            val scale = lerp(0.6f, 1f, animationProgress)
            ContentText(animationProgress, scale, text)
        }
    ) { measurables, constraints ->
        val iconPlaceable = measurables.first { it.layoutId == "icon" }.measure(constraints)
        val textPlaceable = measurables.first { it.layoutId == "text" }.measure(constraints)

        placeTextAndIcon(
            textPlaceable,
            iconPlaceable,
            constraints.maxWidth,
            constraints.maxHeight,
            animationProgress
        )
    }
}

@Composable
private fun ContentIcon(icon: @Composable (BoxScope.() -> Unit)) {
    Box(
        modifier = Modifier
            .layoutId("icon")
            .padding(horizontal = TextIconSpacing),
        content = icon
    )
}

@Composable
private fun ContentText(
    animationProgress: Float,
    scale: Float,
    text: @Composable() (BoxScope.() -> Unit)
) {
    Box(
        modifier = Modifier
            .layoutId("text")
            .padding(horizontal = TextIconSpacing)
            .graphicsLayer {
                alpha = animationProgress
                scaleX = scale
                scaleY = scale
                transformOrigin = BottomNavLabelTransformOrigin
            },
        content = text
    )
}

private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY = (height - iconPlaceable.height) / 2
    val textY = (height - textPlaceable.height) / 2

    val textWidth = textPlaceable.width * animationProgress
    val iconX = (width - textWidth - iconPlaceable.width) / 2
    val textX = iconX + iconPlaceable.width

    return layout(width, height) {
        iconPlaceable.placeRelative(iconX.toInt(), iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(textX.toInt(), textY)
        }
    }
}

@Composable
private fun JetsnackBottomNavPreview() {
    LendyouTheme {
        LendyouBottomBar(
            tabs = HomeSections.values(),
            currentRoute = "home/feed",
            navigateToRoute = { }
        )
    }
}





















