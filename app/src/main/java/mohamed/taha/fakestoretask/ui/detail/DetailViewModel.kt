package mohamed.taha.fakestoretask.ui.detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mohamed.taha.fakestoretask.data.Product
import mohamed.taha.fakestoretask.data.repository.ProductsRepository
import javax.inject.Inject


data class DetailScreenState(var product: Product? = null)

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: ProductsRepository) :
    ViewModel() {

    var detailScreenState = mutableStateOf(DetailScreenState())
        private set

    fun getProductById(productId: Int) = repository.getProductById(productId, viewModelScope) {
        viewModelScope.launch(Dispatchers.Main) {
            detailScreenState.value = detailScreenState.value.copy(product = it)
        }
    }

    fun updateProduct(product: Product) {
        repository.updateProduct(viewModelScope, product) {
            getProductById(product.id)
        }
    }
}