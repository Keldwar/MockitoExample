package shopping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import product.Product;
import product.ProductDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование класса {@link ShoppingServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ShoppingServiceImplTest {
    @Mock
    private ProductDao productDaoMock;

    @InjectMocks
    private ShoppingServiceImpl shoppingService;

    /**
     * Тестирование получение всех продуктов.
     */
    @Test
    void getAllProductsTest() {
        List<Product> products = List.of(new Product(), new Product());
        when(productDaoMock.getAll()).thenReturn(products);

        List<Product> actualProduct = shoppingService.getAllProducts();

        assertEquals(products, actualProduct);
    }

    /**
     * Тестирование получение продукта по его имени.
     */
    @Test
    void getProductByNameTest() {
        String productName = "Product";
        Product product = new Product();
        product.setName(productName);
        when(productDaoMock.getByName(productName)).thenReturn(product);

        Product actualProduct = shoppingService.getProductByName(productName);

        assertEquals(product, actualProduct);
        verify(productDaoMock).getByName(productName);
    }

    /**
     * Тестирование возможности покупки, когда корзина пуста.
     */
    @Test
    void buy_whenCartIsEmpty() throws BuyException {
        Cart cart = mock(Cart.class);
        when(cart.getProducts()).thenReturn(new HashMap<>());

        assertFalse(shoppingService.buy(cart));

        verify(cart).getProducts();
    }

    /**
     * Тестирование невозможности покупки, когда количество товаров в корзине больше, чем доступно.
     */
    @Test
    void buy_whenProductCountIsNotValid() {
        Map<Product, Integer> products = new HashMap<>();
        Product product = new Product();
        int initAmountOfProduct = 5;
        product.addCount(initAmountOfProduct);
        products.put(product, 6);

        Cart cart = Mockito.mock(Cart.class);
        when(cart.getProducts()).thenReturn(products);

        assertThrows(BuyException.class, () -> shoppingService.buy(cart));
        assertEquals(initAmountOfProduct, product.getCount());
    }

    /**
     * Тестирование возможности покупки, когда количество товаров в корзине меньше, чем доступно.
     */
    @Test
    public void buy_whenProductCountIsValid() throws BuyException {
        Map<Product, Integer> products = new HashMap<>();
        Product product = new Product();
        int initCountOfProduct = 10;
        product.addCount(initCountOfProduct);
        products.put(product, 1);

        Cart cart = Mockito.mock(Cart.class);
        when(cart.getProducts()).thenReturn(products);

        assertTrue(shoppingService.buy(cart));
        assertEquals(products.get(product), initCountOfProduct - product.getCount());
    }
}