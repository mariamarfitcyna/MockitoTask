
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingServiceImpl;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ShoppingServiceImplTest {

    private final ProductDao productDaoMock = Mockito.mock(ProductDao.class);

    private final ShoppingServiceImpl shoppingService = new ShoppingServiceImpl(productDaoMock);


    @Test
    public void getProductByNameTest(){
        Product product = new Product(2);
        product.setName("cone jam");

        when(productDaoMock.getByName(anyString()))
                .thenReturn(product);

        Assertions.assertEquals(shoppingService.getProductByName("cone jam"), product);

    }


    /**
     * Тестирование возможности совершения покупки при пустой корзине
     */
    @Test
    public void EmptyCartTest(){
        Cart cart = Mockito.mock(Cart.class);

        when(cart.getProducts())
                .thenReturn(new HashMap<>());

        try {
            Assertions.assertFalse(shoppingService.buy(cart));
        }catch (BuyException e){
            e.printStackTrace();
        }
    }
    /**
     * Тестирование возможности совершения покупки при нормальных условиях - заполненной корзине
     */
    @Test
    public void NotEmptyCartTest(){
        Cart cart = Mockito.mock(Cart.class);
        Map<Product, Integer> products = new HashMap<>();

        Product product = new Product(2);
        products.put(product, 2);

        when(cart.getProducts())
                .thenReturn(products);

        try {
            Assertions.assertTrue(shoppingService.buy(cart));
        }catch (BuyException e){
            e.printStackTrace();
        }

        verify(productDaoMock, times(1))
                .save(product);

    }

    /**
     * Тестирование исключения BuyException, если продуктов в наличии меньше, чем требуется
     */

    @Test
    public void validateCanBuyTest(){
        Product apple = new Product(3);

        Product heavyCream = new Product(2);

        Map<Product, Integer> products = new HashMap<>();
        products.put(apple, 3);
        products.put(heavyCream, 3);

        Cart cart = Mockito.mock(Cart.class);
        when(cart.getProducts())
                .thenReturn(products);

        Assertions.assertThrows(BuyException.class, () -> {
            shoppingService.buy(cart);
        });

    }

    /**
     * Тестирование получения всей корзины продуктов
     */

    @Test
    public void getAllProductsTest(){

        shoppingService.getAllProducts();

        verify(productDaoMock, times(1))
                .getAll();
    }

    /**
     * Тестирование уменьшения количества продуктов при покупке
     */

    @Test
    public void subtractCountTest(){

        Product cider = new Product(3);

        Map<Product, Integer> products = new HashMap<>();
        products.put(cider, 2);

        Cart cart = Mockito.mock(Cart.class);
        when(cart.getProducts())
                .thenReturn(products);
        try {
            shoppingService.buy(cart);
        }catch (BuyException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(1, cider.getCount());
    }

}
