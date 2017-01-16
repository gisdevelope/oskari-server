package fi.nls.oskari.control.statistics.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.nls.oskari.control.statistics.data.StatisticalIndicator;
import fi.nls.oskari.util.IOHelper;
import fi.nls.oskari.util.JSONHelper;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by SMAKINEN on 13.1.2017.
 */
public class DataSourceUpdaterTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Test
    public void testReadWorkQueue()
            throws Exception {

        StatisticalIndicator indicator = MAPPER.readValue(getClass().getResourceAsStream("indicator_full.json"), StatisticalIndicator.class);
        assertEquals("Indicator id parsed correctly", "3056", indicator.getId());
    }
    @Test
    public void writeToList()
            throws Exception {

        final String fullIndicator = IOHelper.readString(getClass().getResourceAsStream("indicator_full.json"));
        StatisticalIndicator indicator = MAPPER.readValue(fullIndicator, StatisticalIndicator.class);

        final ObjectMapper listMapper = new ObjectMapper();
        listMapper.addMixIn(StatisticalIndicator.class, JacksonIndicatorListMixin.class);

        final String expectedListItem = IOHelper.readString(getClass().getResourceAsStream("indicator_listitem.json"));
        String result = listMapper.writeValueAsString(indicator);
        assertTrue("List serialization should match", JSONHelper.isEqual(JSONHelper.createJSONObject(result), JSONHelper.createJSONObject(expectedListItem)));

        String full = MAPPER.writeValueAsString(indicator);
        assertTrue("List serialization should match", JSONHelper.isEqual(JSONHelper.createJSONObject(full), JSONHelper.createJSONObject(fullIndicator)));
    }
}