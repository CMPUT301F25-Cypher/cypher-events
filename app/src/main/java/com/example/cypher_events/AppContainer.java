package com.example.cypher_events;

import com.example.cypher_events.data.repository.EntrantRepository;
import com.example.cypher_events.data.repository.EventRepository;
import com.example.cypher_events.data.repository.fake.FakeEntrantRepository;
import com.example.cypher_events.data.repository.fake.FakeEventRepository;
import com.example.cypher_events.domain.service.DefaultLotteryService;
import com.example.cypher_events.domain.service.LotteryService;

public final class AppContainer {

    private static AppContainer INSTANCE;

    public final EventRepository   eventRepository;
    public final EntrantRepository entrantRepository;
    public final LotteryService    lotteryService;

    private AppContainer() {
        this.eventRepository   = new FakeEventRepository();
        this.entrantRepository = new FakeEntrantRepository();
        this.lotteryService    = new DefaultLotteryService();
    }

    public static AppContainer get() {
        if (INSTANCE == null) {
            INSTANCE = new AppContainer();
        }
        return INSTANCE;
    }
}
