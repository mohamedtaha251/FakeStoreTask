package mohamed.taha.fakestoretask

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mohamed.taha.fakestoretask.data.MyShopDatabase
import mohamed.taha.fakestoretask.data.Product
import mohamed.taha.fakestoretask.data.ProductDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class ProductDaoTest {

    private val testDispatcher = StandardTestDispatcher()


    private lateinit var database: MyShopDatabase
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MyShopDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        productDao = database.productDao
    }

    @After
    @Throws(IOException::class)
    fun cleanup() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun testGetProductById() = runTest {
        val product = Product(1, "Test Product")
        productDao.insertProduct(product)

        val retrievedProduct = productDao.getProductById(1)
        assertEquals(product, retrievedProduct)
    }

    @Test
    fun testGetAllProducts() = runTest {
        val products = listOf(
            Product(1, "Product 1"),
            Product(2, "Product 2"),
            Product(3, "Product 3")
        )
        productDao.insertProducts(products)

        val collectedProducts = productDao.getAllProducts().take(1).toList().first()
        assertEquals(products, collectedProducts)
    }

    @Test
    fun testInsertProduct() = runTest {
        val product = Product(1, "New Product")
        productDao.insertProduct(product)

        val retrievedProduct = productDao.getProductById(1)
        assertEquals(product, retrievedProduct)
    }

    @Test
    fun testInsertProducts() = runTest {
        val products = listOf(
            Product(1, "Product 1"),
            Product(2, "Product 2"),
            Product(3, "Product 3")
        )
        productDao.insertProducts(products)

        val collectedProducts = productDao.getAllProducts().take(1).toList().first()
        assertEquals(products, collectedProducts)
    }

    @Test
    fun testDeleteProduct() = runTest {
        val product = Product(1, "Test Product")
        productDao.insertProduct(product)

        productDao.deleteProduct(product)

        val retrievedProduct = productDao.getProductById(1)
        assertNull(retrievedProduct)
    }
}
