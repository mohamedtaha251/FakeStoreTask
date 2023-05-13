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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.germainkevin.collapsingtopbar.rememberCollapsingTopBarScrollBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mohamed.taha.fakestoretask.R
import mohamed.taha.fakestoretask.ui.cart.components.CartProductItem
import mohamed.taha.fakestoretask.ui.cart.components.CartTopAppBar
import mohamed.taha.fakestoretask.ui.drawer.LeftDrawer
import mohamed.taha.fakestoretask.utils.NavActions

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

            ConstraintLayout(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
                val (list, total) = createRefs()

                Column(modifier = Modifier.constrainAs(list) {
                    top.linkTo(parent.top)
                    bottom.linkTo(total.top, margin = 20.dp)
                }) {
                    if (addedToCartProducts.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .wrapContentWidth()
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
                                        .padding(
                                            start = 4.dp,
                                            end = 4.dp,
                                            top = 4.dp,
                                            bottom = 12.dp
                                        )
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
                }



                if (addedToCartProducts.isNotEmpty())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .constrainAs(total) {
                            bottom.linkTo(parent.bottom, margin = 25.dp)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        fontSize = 24.sp,
                        text = "Total cost",
                        fontWeight = FontWeight.Bold

                    )
                    Text(
                        modifier = Modifier.padding(10.dp),
                        fontSize = 24.sp,
                        text = "100 $",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}