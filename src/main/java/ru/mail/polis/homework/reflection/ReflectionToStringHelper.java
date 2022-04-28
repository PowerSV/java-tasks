package ru.mail.polis.homework.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Необходимо реализовать метод reflectiveToString, который для произвольного объекта
 * возвращает его строковое описание в формате:
 *
 * {field_1: value_1, field_2: value_2, ..., field_n: value_n}
 *
 * где field_i - имя поля
 *     value_i - его строковое представление (String.valueOf),
 *               за исключением массивов, для которых value формируется как:
 *               [element_1, element_2, ..., element_m]
 *                 где element_i - строковое представление элемента (String.valueOf)
 *                 элементы должны идти в том же порядке, что и в массиве.
 *
 * Все null'ы следует представлять строкой "null".
 *
 * Порядок полей
 * Сначала следует перечислить в алфавитном порядке поля, объявленные непосредственно в классе объекта,
 * потом в алфавитном порядке поля объявленные в родительском классе и так далее по иерархии наследования.
 * Примеры можно посмотреть в тестах.
 *
 * Какие поля выводить
 * Необходимо включать только нестатические поля. Также нужно пропускать поля, помеченные аннотацией @SkipField
 *
 * Упрощения
 * Чтобы не усложнять задание, предполагаем, что нет циклических ссылок, inner классов, и transient полей
 *
 * Реализация
 * В пакете ru.mail.polis.homework.reflection можно редактировать только этот файл
 * или добавлять новые (не рекомендуется, т.к. решение вполне умещается тут в несколько методов).
 * Редактировать остальные файлы нельзя.
 *
 * Баллы
 * В задании 3 уровня сложности, для каждого свой набор тестов:
 *   Easy - простой класс, нет наследования, массивов, статических полей, аннотации SkipField (4 балла)
 *   Easy + Medium - нет наследования, массивов, но есть статические поля и поля с аннотацией SkipField (6 баллов)
 *   Easy + Medium + Hard - нужно реализовать все требования задания (10 баллов)
 *
 * Итого, по заданию можно набрать 10 баллов
 * Баллы могут снижаться за неэффективный или неаккуратный код
 */
public class ReflectionToStringHelper {

    public static String reflectiveToString(Object object) {
        // TODO: implement
        if (object == null) {
            return "null";
        }
        Class<?> c = object.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(fieldsToString(c, object));

        c = c.getSuperclass();
        while (c != Object.class) {
            sb.append(", ");
            sb.append(fieldsToString(c, object));
            c = c.getSuperclass();
        }
        sb.append("}");

        return sb.toString();
    }

    private static StringBuilder fieldsToString(Class<?> c, Object object) {
        List<Field> fields = Arrays.stream(c.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !field.isAnnotationPresent(SkipField.class))
                .sorted(Comparator.comparing(Field::getName))
                .collect(Collectors.toList());

        if (fields.size() == 0) {
            return new StringBuilder();
        }

        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < fields.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                Field current = fields.get(i);
                sb.append(current.getName()).append(": ");
                current.setAccessible(true);
                if (current.getType().isArray()) {
                    sb.append(arrayToString(current.get(object)));
                } else {
                    sb.append(current.get(object));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return sb;
    }

    private static StringBuilder arrayToString(Object array) {
        if (array == null) {
            return new StringBuilder("null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < Array.getLength(array); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(Array.get(array, i));
        }
        sb.append("]");
        return sb;
    }
}
