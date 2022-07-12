package com.epam.esm.dao.filter;

import com.epam.esm.entity.Tag;
import java.util.List;

/**
 * The interface provided methods to filter data in database table related with tags.
 */
public interface TagDaoFilter {
    /**
     * Read the most widely used tag of a user with the highest cost of all orders
     *
     * @return list of most widely used tag with the highest cost
     */
    List<Tag> readMostPopularHighestPriceTag();
}
