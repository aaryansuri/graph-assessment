package org.assessment.graph.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.assessment.graph.pojos.City;

public class UniqueRandomCity {
    private final List<City> elements;
    private Iterator<City> iterator;

    public UniqueRandomCity(List<City> elements) {
        this.elements = new ArrayList<>(elements);
        shuffleElements();
    }

    private void shuffleElements() {
        Collections.shuffle(elements, new Random());
        iterator = elements.iterator();
    }

    public City nextCity() {
        if (!iterator.hasNext()) {
            shuffleElements();
        }
        return iterator.next();
    }
}