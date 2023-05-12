package mohamed.taha.fakestoretask.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mohamed.taha.fakestoretask.data.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM product Where id=:productId")
    suspend fun getProductById(productId: Int): Product

    @Query("SELECT * FROM product")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Delete
    suspend fun deleteProduct(product: Product)

}