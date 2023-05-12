package mohamed.taha.fakestoretask.ui.cart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mohamed.taha.fakestoretask.data.Product
import mohamed.taha.fakestoretask.data.ProductListCategory
import mohamed.taha.fakestoretask.data.repository.ProductsRepository
import javax.inject.Inject

data class CartScreenState(
    var isLoading: Boolean = true,
    var allProducts: List<Product> = emptyList(),
)

@HiltViewModel
class CartViewModel @Inject constructor(private val repository: ProductsRepository) : ViewModel() {

    var cartScreenState = mutableStateOf(CartScreenState())
        private set

    fun getProducts() {
        cartScreenState.value = cartScreenState.value.copy(isLoading = true)
        repository.getProducts(viewModelScope, ProductListCategory.AllCategories) { products ->
            viewModelScope.launch(Dispatchers.Main) {
                cartScreenState.value =
                    cartScreenState.value.copy(isLoading = false, allProducts = products)
            }
        }
    }

    fun updateProduct(product: Product) {
        repository.updateProduct(viewModelScope, product = product) {
            getProducts()
        }
    }

    init {
        getProducts()
    }
}