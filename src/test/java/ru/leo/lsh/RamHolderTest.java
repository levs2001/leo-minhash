package ru.leo.lsh;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RamHolderTest {
    @Test
    public void testFindSame() {
        IHolder<Long> holder = new RamHolder<>(5, 8, 2);
        holder.add(1L, "Mama myla ramu");
        holder.add(2L, "Mama myla ramu");

        List<Long> nearest = holder.findNearest("Mama myla ramu");
        assertEquals(2, nearest.size());
        assertTrue(nearest.contains(1L));
        assertTrue(nearest. contains(2L));
    }

    @Test
    public void testFindNearest() {
        IHolder<Long> holder = new RamHolder<>(2, 2, 10);
        holder.add(1L, "Mama myla ramu, a papa ne mil");
        holder.add(2L, "I am a crocodile, but you can be monkey");

        List<Long> nearest = holder.findNearest("Mama myla rami, a papa ne mil");
        assertEquals(1, nearest.size());
        assertTrue(nearest.contains(1L));

        nearest = holder.findNearest("I am a crocodile, but we can be monkeys");
        assertEquals(1, nearest.size());
        assertTrue(nearest.contains(2L));
    }
}