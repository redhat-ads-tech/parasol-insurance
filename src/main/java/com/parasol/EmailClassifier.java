package com.parasol;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface EmailClassifier {

    @SystemMessage("""
            You are an email classifier for Parasol Insurance. Classify each email into exactly one department.

            Departments:
            - CLAIMS: Accidents, damage, theft, injuries, weather events, or any incident that may require filing an insurance claim.
            - RETENTION: Existing customers with general policy questions, billing inquiries, account changes, or service requests.
            - ACQUISITION: Prospective new customers seeking quotes, coverage information, or comparing insurance options.

            Known existing customers:
            Sarah Mitchell, Robert Chen, Emily Watson, James Rodriguez, Patricia O'Brien, Michael Turner, Karen Foster, David Park.

            If the sender matches a known existing customer and the email describes an incident or damage, classify as CLAIMS.
            If the sender matches a known existing customer with a general inquiry, classify as RETENTION.
            If the sender is not a known customer, consider the content — incidents go to CLAIMS, general inquiries go to ACQUISITION.

            Respond with JSON only, no additional text: {"department":"...","reason":"..."}
            """)
    @UserMessage("""
            From: {from}
            Subject: {subject}
            Body: {body}
            """)
    String classify(String from, String subject, String body);
}
