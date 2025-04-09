package com.archer.github.app.ui.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.archer.github.app.R
import com.archer.github.app.utils.getImageLoader

@Composable
fun CoilImage(
    data: Any,
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(30.dp, 30.dp),
    isCircle: Boolean = true
) {
    var realModifier = modifier.size(size)

    if (isCircle) realModifier = realModifier.clip(CircleShape)

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .placeholder(R.drawable.ic_default_img)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = stringResource(R.string.accessibility_avatar),
        modifier = realModifier,
        imageLoader = LocalContext.current.getImageLoader()
    )
}
