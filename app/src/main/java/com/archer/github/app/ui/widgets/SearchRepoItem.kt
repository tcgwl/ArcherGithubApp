package com.archer.github.app.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.archer.github.app.R
import com.archer.github.app.logic.model.Repository
import com.archer.github.app.ui.nav.Route
import com.archer.github.app.utils.DateUtil
import com.archer.github.app.utils.LocalNavHostController
import java.net.URLEncoder

@Composable
fun SearchRepoItem(
    repo: Repository,
    isMine: Boolean = false
) {
    val navController = LocalNavHostController.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable {
                navController.navigate("${Route.WEB.route}/${URLEncoder.encode(repo.fullName, "UTF-8")}")
            },
        colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isMine) {
                    repo.owner?.avatarUrl?.let { avatarUrl ->
                        CoilImage(
                            data = avatarUrl
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Text(
                    text = if (isMine) { repo.name } else { repo.fullName },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalContentColor.current.copy(alpha = 0.9f)
                )
            }
            repo.description?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = LocalContentColor.current.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repo.language?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = LocalContentColor.current.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                if (!isMine) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.accessibility_star_count)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${repo.stargazersCount}",
                        fontSize = 12.sp,
                        color = LocalContentColor.current.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = stringResource(
                        R.string.mine_repo_update_at_time,
                        DateUtil.dateFormat(repo.updatedAt)
                    ),
                    fontSize = 12.sp,
                    color = LocalContentColor.current.copy(alpha = 0.8f)
                )
            }
        }
    }
}
