package org.example.Performance;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


// Measured with JMeter
// With one thread - throughput = 970 req/sec
// With two threads - throughput = 1650 req/sec
// With four threads - throughput = 3121 req/sec

// throughput = number of tasks completed in a given time
// This class will contain examples of multithreading for performance improvement
// Thread pooling - reusing a fixed number of threads to execute multiple tasks
public class ThroughputMultithreading {
    // Path to the source text file
    public static final String SOURCE_CLASSPATH = "XXXXXXXX";

    // Number of threads in the thread pool
    public static final int NUMBER_OF_THREADS = 4;

    public static void main(String[] args) throws IOException {
        // Read the entire text file into a string
        String text = new String(Files.readAllBytes(Paths.get(SOURCE_CLASSPATH)));

        // Start the HTTP server
        startServer(text);
    }

    // Method to start the HTTP server
    public static void startServer(String text) throws IOException {
        // Create an HTTP server listening on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        // Create a context for handling search requests
        server.createContext("/search", new WordCountHandler(text));
        // Create a thread pool executor
        // it allows to handle multiple requests concurrently with a fixed number of threads
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        // Set the executor for the server
        server.setExecutor(executor);
        // Start the server
        server.start();
    }

    // Handler for counting word occurrences
    public static class WordCountHandler implements HttpHandler {
        // The text in which to count word occurrences
        private String text;

        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Extract the query from the request URI
            String query = exchange.getRequestURI().getQuery();
            // Split the query into key and value
            String[] keyValue = query.split("=");
            // Validate the action
            String word = keyValue[1];
            // Validate the action
            String action = keyValue[0];
            // If the action is not "word", return a 400 Bad Request response
            if (!action.equals("word")) {
                exchange.sendResponseHeaders(400, 0);
                return;
            }
            // Count the occurrences of the word in the text
            long count = countWordOccurrences(word, text);

            // Send the response with the count
            byte [] response = Long.toString(count).getBytes();
            exchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        // Method to count occurrences of a word in the text
        public static long countWordOccurrences(String word, String text) {
            // Initialize count and index
            long count = 0;
            int index = 0;

            // Loop to find all occurrences of the word
            while (index >= 0) {
                index = text.indexOf(word, index);
                if (index >= 0) {
                    count++;
                    index++;
                }
            }
            return count;
        }

    }
}
