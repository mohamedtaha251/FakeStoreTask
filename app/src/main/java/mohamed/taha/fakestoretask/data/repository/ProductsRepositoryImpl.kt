package mohamed.taha.fakestoretask.data.repository

import mohamed.taha.fakestoretask.data.Product
import mohamed.taha.fakestoretask.data.ProductDao
import mohamed.taha.fakestoretask.data.ProductListCategory
import mohamed.taha.fakestoretask.data.api.FakeStoreApi
import mohamed.taha.fakestoretask.utils.getProductsByCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val fakeStoreApi: FakeStoreApi,
    private val productDao: ProductDao,
) : ProductsRepository {

    override fun getProducts(
        coroutineScope: CoroutineScope,
        productListCategory: ProductListCategory,
        result: (List<Product>) -> Unit
    ) {
        coroutineScope.launch((Dispatchers.IO)) {
            productDao.getAllProducts().collectLatest { allProducts ->
                if (allProducts.isEmpty()) {
                    fakeStoreApi.getProducts().enqueue(object : Callback<List<Product>?> {
                        override fun onResponse(
                            call: Call<List<Product>?>,
                            response: Response<List<Product>?>
                        ) {
                            response.body()?.let { products ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    productDao.insertProducts(products)
                                }
                                products.getProductsByCategory(productListCategory) { prods ->
                                    result(prods)
                                }
                            }
                                ?: result(emptyList())
                        }

                        override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                            result(emptyList())
                        }
                    })
                } else {
                    allProducts.getProductsByCategory(productListCategory) { result(it) }
                }
            }
        }
    }

    override fun updateProduct(
        coroutineScope: CoroutineScope,
        product: Product,
        onProductUpdated: () -> Unit
    ) {
        val job = coroutineScope.launch(Dispatchers.IO) {
            productDao.insertProduct(product)
        }
        if (job.isCompleted) onProductUpdated()
    }

    override fun getProductById(
        productId: Int,
        coroutineScope: CoroutineScope,
        result: (Product) -> Unit
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            val product = productDao.getProductById(productId)
            result(product)
        }
    }
}