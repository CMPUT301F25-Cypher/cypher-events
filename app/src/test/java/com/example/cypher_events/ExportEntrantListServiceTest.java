package com.example.cypher_events;

import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.model.Event;
import com.example.cypher_events.data.repository.fake.FakeEventRepository;
import com.example.cypher_events.domain.service.ExportEntrantListService;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ExportEntrantListServiceTest {

    @Test
    public void testExportAsCSV() throws Exception {
        FakeEventRepository repo = new FakeEventRepository();
        Event event = repo.getEventById("e1").data;

        // add entrants so CSV isn’t empty
        event.getEvent_selectedEntrants().add(new Entrant("Alice", "alice@example.com", "111"));
        event.getEvent_selectedEntrants().add(new Entrant("Bob", "bob@example.com", "222"));

        ExportEntrantListService service = new ExportEntrantListService(repo);
        String csv = service.exportAsCSV("e1");

        System.out.println("CSV OUTPUT:\n" + csv); //  Debug output

        assertNotNull(csv);
        assertTrue(csv.contains("Alice"));
        assertTrue(csv.contains("bob@example.com"));
    }
}