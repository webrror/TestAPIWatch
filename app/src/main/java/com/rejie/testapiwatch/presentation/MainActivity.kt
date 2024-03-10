/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.rejie.testapiwatch.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.AppCard
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.rejie.testapiwatch.R
import com.rejie.testapiwatch.domain.data.ProductsRepositoryImpl
import com.rejie.testapiwatch.domain.data.RetrofitInstance
import com.rejie.testapiwatch.domain.models.Product
import com.rejie.testapiwatch.presentation.theme.TestAPIWatchTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ProductsViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductsViewModel(ProductsRepositoryImpl(RetrofitInstance.api))
                        as T
            }
        }
    })

    @OptIn(ExperimentalWearFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        val vignetteState = mutableStateOf(VignettePosition.TopAndBottom)
        setContent {
            TestAPIWatchTheme {
                val listState = rememberScalingLazyListState()
                Scaffold(
                    positionIndicator = {
                        PositionIndicator(scalingLazyListState = listState)
                    },
                    vignette = {

                            Vignette(vignettePosition = vignetteState.value)

                    },
                    timeText = {
                        TimeText()
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        val productsList = viewModel.products.collectAsState().value
                        val context = LocalContext.current
                        LaunchedEffect(key1 = viewModel.showErrorToastChannel) {
                            viewModel.showErrorToastChannel.collectLatest {
                                if (it) {
                                    Toast.makeText(
                                        context, "Error", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        TimeText()
                        if (productsList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            val focusRequester = rememberActiveFocusRequester()
                            val coroutineScope = rememberCoroutineScope()
                            ScalingLazyColumn(

                                modifier = Modifier
                                    .fillMaxSize()
                                    .onRotaryScrollEvent {
                                        coroutineScope.launch {
                                            listState.scrollBy(it.verticalScrollPixels)

                                            listState.animateScrollBy(5f)
                                        }
                                        true
                                    }
                                    .focusRequester(focusRequester)
                                    .focusable(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(
                                    top = 40.dp,
                                    bottom = 40.dp,
                                    start = 15.dp,
                                    end = 15.dp
                                ),
                                state = listState,
                                anchorType = ScalingLazyListAnchorType.ItemStart,
                                flingBehavior = ScalingLazyColumnDefaults.snapFlingBehavior(state =listState)
                            ) {
                                items(productsList.size) {
                                    AppTileCard(product = productsList[it])
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AppTileCard(product: Product){
        val imageState = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current).data(product.thumbnail)
                .size(Size.ORIGINAL)
                .build()
        ).state

        imageState.painter?.let {
            CardDefaults.imageWithScrimBackgroundPainter(
                backgroundImagePainter = it
            )
        }?.let {
//            TitleCard(
//                onClick = {},
//                title = { Text(product.title) },
//
////                backgroundPainter = it,
//
//            ) {
//                Text("$${product.price.toString()}")
//            }
            AppCard(
                onClick = {},
                appName = { Text(
                    product.category.uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                ) },
                title = { Text(product.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ) },
                time = { Text("") },
            ) {
                Text("$${product.price.toString()}")
            }
        }
    }
}