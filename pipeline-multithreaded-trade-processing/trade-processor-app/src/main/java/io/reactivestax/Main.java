package io.reactivestax;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        new TradeCsvChunkGenerator().generateChunk("/Users/Suraj.Adhikari/downloads/trades.csv");
        ExecutorService chunkProcessorThreadPool = Executors.newFixedThreadPool(10);
        TradeCsvChunkProcessor tradeCsvChunkProcessor = new TradeCsvChunkProcessor(chunkProcessorThreadPool, 10);
        tradeCsvChunkProcessor.processChunks();

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        tradeCsvChunkProcessor.startMultiThreadsForReadingFromQueue(executorService);

    }
}



