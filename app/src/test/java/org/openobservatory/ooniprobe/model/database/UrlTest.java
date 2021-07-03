package org.openobservatory.ooniprobe.model.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.Test;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.RobolectricAbstractTest;
import org.openobservatory.ooniprobe.factory.UrlFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UrlTest extends RobolectricAbstractTest {

    static final String EXAMPLE_URL = "https://www.example.org";

    @Test
    public void getUrlTest() {
        // Arrange
        Url url = UrlFactory.build();
        url.save();

        // Act
        Url result = Url.getUrl(url.url);

        // Assert
        assertEquals(url.url, result.url);
        assertEquals(url.id, result.id);
        assertEquals(url.category_code, result.category_code);
        assertEquals(url.country_code, result.country_code);
    }

    @Test
    public void addNewUrlWhenCheckingTest() {
        // Act
        Url result = Url.checkExistingUrl("https://www.example.org");
        Url dataBaseUrl = SQLite.select().from(Url.class).where(Url_Table.url.eq(EXAMPLE_URL)).querySingle();

        // Assert
        assertNotNull(dataBaseUrl);
        assertEquals(result.url, EXAMPLE_URL);
        assertEquals(dataBaseUrl.url, EXAMPLE_URL);
        assertEquals(dataBaseUrl.category_code, "MISC");
        assertEquals(dataBaseUrl.country_code, "XX");
    }

    @Test
    public void updateCategoryCodeAndCountryCodeWhenCheckingTest() {
        // Arrange
        Url url = new Url();
        url.url = EXAMPLE_URL;
        url.category_code = "ALDR";
        url.country_code = "PT";
        url.save();

        String newCategory = "REL";
        String newCountry = "US";

        // Act
        Url updatedUrl = Url.checkExistingUrl(EXAMPLE_URL, newCategory, newCountry);
        Url dataBaseUrl = SQLite.select().from(Url.class).where(Url_Table.url.eq(EXAMPLE_URL)).querySingle();

        // Assert
        assertNotNull(dataBaseUrl);
        assertEquals(updatedUrl.url, EXAMPLE_URL);
        assertEquals(updatedUrl.category_code, newCategory);
        assertEquals(updatedUrl.country_code, newCountry);
        assertEquals(dataBaseUrl.url, EXAMPLE_URL);
        assertEquals(dataBaseUrl.category_code, newCategory);
        assertEquals(dataBaseUrl.country_code, newCountry);
    }

    @Test
    public void getIconTest() {
        // Arrange
        Url url = new Url();
        url.url = EXAMPLE_URL;
        url.category_code = "ALDR";
        url.country_code = "PT";
        url.save();

        int resourceId = c.getResources().obtainTypedArray(R.array.CategoryIcons).getResourceId(0, -1);

        // Act
        int value = url.getCategoryIcon(c);

        // Assert
        assertEquals(resourceId, value);
    }

    @Test
    public void toStringTest() {
        // Arrange
        Url url = UrlFactory.build();

        // Act
        String value = url.toString();

        // Assert
        assertEquals(url.url,value);
    }
}