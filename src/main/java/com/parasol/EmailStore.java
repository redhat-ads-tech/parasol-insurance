package com.parasol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.parasol.model.Email;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailStore {

    private final CopyOnWriteArrayList<Email> emails = new CopyOnWriteArrayList<>();

    public void add(Email email) {
        emails.add(email);
    }

    public List<Email> all() {
        List<Email> result = new ArrayList<>(emails);
        Collections.reverse(result);
        return result;
    }

    public List<Email> after(long id) {
        List<Email> result = new ArrayList<>();
        for (Email e : emails) {
            if (e.getId() > id) {
                result.add(e);
            }
        }
        Collections.reverse(result);
        return result;
    }
}
