package mohamed.taha.fakestoretask.data.api

import mohamed.taha.fakestoretask.data.Product
import retrofit2.Call
import retrofit2.http.GET

interface FakeStoreApi {

    companion object {
        const val BASE_URL = "https://fakestoreapi.com/"
    }

    @GET("products")
    fun getProducts(): Call<List<Product>?>

}