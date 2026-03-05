package com.parasol;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parasol.model.Email;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailRouter {

    private static final Logger LOG = Logger.getLogger(EmailRouter.class.getName());

    private static final String DEPT_CLAIMS = "CLAIMS";
    private static final String DEPT_RETENTION = "RETENTION";
    private static final String DEPT_ACQUISITION = "ACQUISITION";

    private static final List<String> EXISTING_CUSTOMERS = List.of(
            "Sarah Mitchell", "Robert Chen", "Emily Watson", "James Rodriguez",
            "Patricia O'Brien", "Michael Turner", "Karen Foster", "David Park");

    private static final List<String> CLAIM_KEYWORDS = List.of(
            "accident", "damage", "claim", "injury", "loss", "flood", "fire",
            "collision", "theft", "broken", "wreck", "incident", "stolen");

    private static final Set<String> VALID_DEPARTMENTS = Set.of(DEPT_CLAIMS, DEPT_RETENTION, DEPT_ACQUISITION);

    private static final Pattern THINK_PATTERN = Pattern.compile("<think>[^<]*+</think>", Pattern.DOTALL);
    private static final Pattern CODE_FENCE_PATTERN = Pattern.compile("```(?:json)?\\s*+(\\{[^}]*+})\\s*+```", Pattern.DOTALL);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AtomicLong idSequence = new AtomicLong(0);
    private final ObjectMapper mapper = new ObjectMapper();
    private final EmailStore store;
    private final EmailClassifier classifier;

    public EmailRouter(EmailStore store, EmailClassifier classifier) {
        this.store = store;
        this.classifier = classifier;
    }

    @Incoming("emails-in")
    public void consume(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            String from = node.get("from").asText();
            String subject = node.get("subject").asText();
            String body = node.get("body").asText();

            String[] classification = classifyEmail(from, subject, body);

            Email email = new Email(
                    idSequence.incrementAndGet(),
                    from, subject, body,
                    LocalDateTime.now().format(FORMATTER),
                    classification[0], classification[1]);

            store.add(email);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to process email", e);
        }
    }

    private String[] classifyEmail(String from, String subject, String body) {
        try {
            String response = classifier.classify(from, subject, body);
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "LLM classification response: {0}", response);
            }

            JsonNode result = parseClassificationResponse(response);
            String dept = result.get("department").asText().toUpperCase();
            String llmReason = result.get("reason").asText();

            if (!VALID_DEPARTMENTS.contains(dept)) {
                throw new InvalidClassificationException("Invalid department from LLM: " + dept);
            }

            return new String[] { dept, llmReason };
        } catch (Exception e) {
            LOG.log(Level.WARNING, "LLM classification failed, falling back to keyword matching", e);
            return classifyByKeywords(from, subject, body);
        }
    }

    private JsonNode parseClassificationResponse(String response) throws IOException {
        // Strip Qwen3 <think>...</think> blocks
        String cleaned = THINK_PATTERN.matcher(response).replaceAll("").trim();

        // Try extracting JSON from markdown code fences
        Matcher fenceMatcher = CODE_FENCE_PATTERN.matcher(cleaned);
        if (fenceMatcher.find()) {
            cleaned = fenceMatcher.group(1);
        }

        // Find the JSON object in the remaining text
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) {
            cleaned = cleaned.substring(start, end + 1);
        }

        return mapper.readTree(cleaned);
    }

    private String[] classifyByKeywords(String from, String subject, String body) {
        String senderName = extractSenderName(from);
        String matchedCustomer = findExistingCustomer(senderName);
        String combinedText = body + " " + subject;
        boolean hasClaim = containsClaimKeyword(combinedText);

        if (matchedCustomer != null && hasClaim) {
            return new String[] { DEPT_CLAIMS, "Existing customer " + matchedCustomer + " \u2014 possible claim" };
        } else if (matchedCustomer != null) {
            return new String[] { DEPT_RETENTION, "Detected existing customer: " + matchedCustomer };
        } else if (hasClaim) {
            return new String[] { DEPT_CLAIMS, "Possible claim from new customer" };
        } else {
            return new String[] { DEPT_ACQUISITION, "Unknown customer \u2014 potential new business" };
        }
    }

    private String extractSenderName(String from) {
        int lt = from.indexOf('<');
        if (lt > 0) {
            return from.substring(0, lt).trim();
        }
        return from.trim();
    }

    private String findExistingCustomer(String senderName) {
        String lower = senderName.toLowerCase();
        for (String customer : EXISTING_CUSTOMERS) {
            if (lower.contains(customer.toLowerCase())) {
                return customer;
            }
        }
        return null;
    }

    private boolean containsClaimKeyword(String text) {
        String lower = text.toLowerCase();
        for (String keyword : CLAIM_KEYWORDS) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static class InvalidClassificationException extends RuntimeException {
        InvalidClassificationException(String message) {
            super(message);
        }
    }
}
