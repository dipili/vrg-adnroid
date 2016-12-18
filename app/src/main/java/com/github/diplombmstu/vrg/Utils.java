package com.github.diplombmstu.vrg;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.StatusLine;

import java.util.List;
import java.util.Map;

/**
 * TODO add comment
 */
public class Utils
{
    public static void printOpeningHandshakeException(OpeningHandshakeException e)
    {
        // Status line.
        StatusLine sl = e.getStatusLine();
        System.out.println("=== Status Line ===");
        System.out.format("HTTP Version  = %s\n", sl.getHttpVersion());
        System.out.format("Status Code   = %d\n", sl.getStatusCode());
        System.out.format("Reason Phrase = %s\n", sl.getReasonPhrase());

        // HTTP headers.
        Map<String, List<String>> headers = e.getHeaders();
        System.out.println("=== HTTP Headers ===");
        for (Map.Entry<String, List<String>> entry : headers.entrySet())
        {
            // Header name.
            String name = entry.getKey();

            // Values of the header.
            List<String> values = entry.getValue();

            if (values == null || values.size() == 0)
            {
                // Print the name only.
                System.out.println(name);
                continue;
            }

            for (String value : values)
            {
                // Print the name and the value.
                System.out.format("%s: %s\n", name, value);
            }
        }
    }
}
