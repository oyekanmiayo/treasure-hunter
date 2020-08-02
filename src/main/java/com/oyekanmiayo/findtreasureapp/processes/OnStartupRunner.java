package com.oyekanmiayo.findtreasureapp.processes;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnStartupRunner implements CommandLineRunner {

    private final Hunter hunter;

    //Executes on application startup because it implements CommandLineRunner
    @Override
    public void run(String... args) throws Exception {
        hunter.findTreasure();
    }
}
