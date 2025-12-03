package es.upm.grise.profundizacion.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa un pedido (Order) que contiene una lista de items añadidos por un cliente.
 *
 * Reglas (según especificación):
 * - Al instanciar, la lista de items debe estar vacía pero no nula.
 * - addItem(Item):
 *     * Añade el item a la lista si es válido.
 *     * price >= 0, si no -> IncorrectItemException.
 *     * quantity > 0, si no -> IncorrectItemException.
 *     * Si el mismo product ya existe en la lista:
 *         - Si el price es el mismo -> no se añade un nuevo item; se incrementa la quantity
 *           del item existente.
 *         - Si el price es diferente -> se añade el nuevo item (puede ser otro proveedor).
 */
public class Order {

	private final List<Item> items;

	/**
	 * Crea una Order con la lista de items vacía (no nula).
	 */
	public Order() {
		this.items = new ArrayList<>();
	}

	/**
	 * Devuelve una vista inmodificable de los items del pedido.
	 */
	public List<Item> getItems() {
		return Collections.unmodifiableList(items);
	}

	/**
	 * Añade un item cumpliendo las reglas de validación y combinación.
	 *
	 * @param item Item a añadir.
	 * @throws IncorrectItemException si el precio o la cantidad son inválidos.
	 * @throws NullPointerException si item es null.
	 */
	public void addItem(Item item) throws IncorrectItemException {
		Objects.requireNonNull(item, "item no puede ser null");

		BigDecimal price = BigDecimal.valueOf(item.getPrice());
		int quantity = item.getQuantity();

		// Validaciones según especificación
		if (price.compareTo(BigDecimal.ZERO) < 0) {
			throw new IncorrectItemException();
		}
		if (quantity <= 0) {
			throw new IncorrectItemException();
		}

		// Intentar combinar con un item existente (mismo producto y mismo precio)
		for (Item existing : items) {
			// Se asume que Product implementa equals/hashCode de forma adecuada.
			if (Objects.equals(existing.getProduct(), item.getProduct())) {
				// Si el producto coincide, comprobamos el precio:
				if (equalsPrice(BigDecimal.valueOf(existing.getPrice()), price)) {
					// Misma combinación product+price -> acumulamos cantidad
					int newQuantity = existing.getQuantity() + quantity;
					existing.setQuantity(newQuantity);
					return; // No añadimos un nuevo item
				}
				// Si el precio es distinto, NO combinamos y el nuevo item se añadirá más abajo
			}
		}

		// Producto no estaba (o estaba pero con precio distinto) -> añadimos item tal cual
		items.add(item);
	}

	/**
	 * Compara precios de forma segura con BigDecimal.
	 * Dos precios son "iguales" si compareTo devuelve 0.
	 */
	private static boolean equalsPrice(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) == 0;
	}
}
