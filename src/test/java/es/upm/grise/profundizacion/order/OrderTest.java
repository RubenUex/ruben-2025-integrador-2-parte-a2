
package es.upm.grise.profundizacion.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderTest {

	private Order order;

	@BeforeEach
	void setUp() {
		order = new Order();
	}

	@Test
	void testAddValidItem() throws IncorrectItemException {
		Item item = mock(Item.class);
		Product product = mock(Product.class);

		when(item.getProduct()).thenReturn(product);
		when(item.getPrice()).thenReturn(10.0);
		when(item.getQuantity()).thenReturn(2);

		order.addItem(item);

		List<Item> items = order.getItems();
		assertEquals(1, items.size());
		assertEquals(item, items.get(0));
	}

	@Test
	void testAddItemWithNegativePriceThrowsException() {
		Item item = mock(Item.class);
		when(item.getPrice()).thenReturn(-5.0);
		when(item.getQuantity()).thenReturn(1);

		assertThrows(IncorrectItemException.class, () -> order.addItem(item));
	}

	@Test
	void testAddItemWithZeroQuantityThrowsException() {
		Item item = mock(Item.class);
		when(item.getPrice()).thenReturn(5.0);
		when(item.getQuantity()).thenReturn(0);

		assertThrows(IncorrectItemException.class, () -> order.addItem(item));
	}

	@Test
	void testAddSameProductSamePriceIncrementsQuantity() throws IncorrectItemException {
		Product product = mock(Product.class);

		Item item1 = mock(Item.class);
		when(item1.getProduct()).thenReturn(product);
		when(item1.getPrice()).thenReturn(10.0);
		when(item1.getQuantity()).thenReturn(2);

		Item item2 = mock(Item.class);
		when(item2.getProduct()).thenReturn(product);
		when(item2.getPrice()).thenReturn(10.0);
		when(item2.getQuantity()).thenReturn(3);

		order.addItem(item1);
		order.addItem(item2);

		// Verificamos que no se añadió un nuevo item, sino que se incrementó la cantidad
		assertEquals(1, order.getItems().size());
		verify(item1).setQuantity(5); // 2 + 3
	}

	@Test
	void testAddSameProductDifferentPriceAddsNewItem() throws IncorrectItemException {
		Product product = mock(Product.class);

		Item item1 = mock(Item.class);
		when(item1.getProduct()).thenReturn(product);
		when(item1.getPrice()).thenReturn(10.0);
		when(item1.getQuantity()).thenReturn(2);

		Item item2 = mock(Item.class);
		when(item2.getProduct()).thenReturn(product);
		when(item2.getPrice()).thenReturn(15.0);
		when(item2.getQuantity()).thenReturn(1);

		order.addItem(item1);
		order.addItem(item2);

		assertEquals(2, order.getItems().size());
	}
}
