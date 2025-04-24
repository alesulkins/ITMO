package org.example.product.comparator;

import org.example.product.Person;

import java.util.Comparator;

/**
 * Компаратор для сравнения объектов Person по росту.
 * Обрабатывает null-значения, считая их меньшими любых не-null значений.
 */
public class ComparatorByOwnersHeight implements Comparator<Person> {
    /**
     * Сравнивает два объекта Person по значению роста.
     *
     * @param p1 Первый объект для сравнения
     * @param p2 Второй объект для сравнения
     * @return Результат сравнения (-1, 0 или 1)
     */
    @Override
    public int compare(Person p1, Person p2) {
        Integer h1 = p1.getHeight();
        Integer h2 = p2.getHeight();

        if (h1 == null && h2 == null) return 0;
        if (h1 == null) return -1;  // null the smallest
        if (h2 == null) return 1;

        return h1.compareTo(h2);
    }
}
