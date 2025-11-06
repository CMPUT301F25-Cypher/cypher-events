package com.example.cypher_events;

import com.example.cypher_events.data.repository.*;
import com.example.cypher_events.data.repository.fake.*;
import com.example.cypher_events.domain.model.Entrant;
import com.example.cypher_events.domain.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        if (INSTANCE == null) INSTANCE = new AppContainer();
        return INSTANCE;
    }




}