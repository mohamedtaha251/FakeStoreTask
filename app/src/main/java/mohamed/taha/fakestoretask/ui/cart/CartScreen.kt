package mohamed.taha.fakestoretask.ui.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.germainkevin.collapsingtopbar.rememberCollapsingTopBarScrollBehavior
import mohamed.taha.fakestoretask.ui.cart.components.CartTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mohamed.taha.fakestoretask.ui.cart.components.CartProductItem
import mohamed.taha.fakestoretask.ui.drawer.LeftDrawer
import mohamed.taha.fakestoretask.utils.NavActions
import mohamed.taha.fakestoretask.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartScreen(
    currentRoute: String,
    navActions: NavActions,
    coroutineScope: CoroutineScope,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val openLeftDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }
    val closeLeftDrawer: () -> Unit =
        { coroutineScope.launch { scaffoldState.drawerState.close() } }

    val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
        centeredTitleAndSubtitle = false,
        expandedTopBarMaxHeight = 126.dp
    )

    val addedToCartProducts =
        cartViewModel.cartScreenState.value.allProducts.filter { it.addedToCart }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scaffoldState = scaffoldState,
        topBar = {
            CartTopAppBar(
                itemsInCart = addedToCartProducts,
                openLeftDrawer = openLeftDrawer,
                scrollBehavior = scrollBehavior,
            )
        },
        drawerContent = {
            LeftDrawer(
                products = cartViewModel.cartScreenState.value.allProducts,
                currentRoute,
                navActions,
                closeLeftDrawer
            )
        },
        content = { contentPadding ->

            Column() {
                if (addedToCartProducts.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                            .padding(contentPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_cart_products),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .wrapContentHeight()
                            .padding(contentPadding)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(
                            items = addedToCartProducts,
                            key = { it.hashCode() }
                        ) { product ->
                            CartProductItem(
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 12.dp)
                                    .fillMaxWidth()
                                    .animateItemPlacement(),
                                product = product,
                                removeFromCart = {
                                    cartViewModel.updateProduct(it)
                                }
                            )
                        }
                    }
                }

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                        ),                        fontSize = 24.sp,
                        text = "Total cost"
                    )
                    Text(
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                        ),
                        fontSize = 24.sp,
                        text = "100 $"
                    )


                }

            }


        }
    )
}