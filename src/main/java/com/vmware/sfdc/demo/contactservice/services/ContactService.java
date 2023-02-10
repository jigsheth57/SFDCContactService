package com.vmware.sfdc.demo.contactservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.force.api.ApiSession;
import com.force.api.ForceApi;
import com.vmware.sfdc.demo.contactservice.model.Contact;
import com.vmware.sfdc.demo.contactservice.webclients.AuthServiceClient;

import reactor.core.publisher.Mono;

@Component
public class ContactService {
    private final AuthServiceClient authServiceClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactService.class);
    ForceApi api;

    private ReactiveValueOperations<String, Contact> contactValueOps;

    @Autowired
    public ContactService(AuthServiceClient authServiceClient, ReactiveRedisTemplate<String, Contact> contactTemplate) {
        this.authServiceClient = authServiceClient;
        this.contactValueOps = contactTemplate.opsForValue();
    }

    public Mono<Contact> getContactById(String id, HttpHeaders headers) {
        LOGGER.debug("getContactById({})", id);
        return retrieve(id, headers);
    }

    public Mono<Contact> getContactByIdFallback(String id, HttpHeaders headers) {
        LOGGER.debug("getContactByIdFallback({})", id);
        return authServiceClient.getApiSession(headers).map(apiSession -> {
            setApiSession(apiSession);
            Contact contact = api.getSObject("contact", id).as(Contact.class);
            try {
                store(contact.getId(), contact);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            return contact;
        });
    }

    public Mono<Contact> updateContact(Contact contact, HttpHeaders headers) {
        LOGGER.debug("updateContact({})", contact);
        String id = contact.getId();
        contact.setId(null);
        return authServiceClient.getApiSession(headers).map(apiSession -> {
            setApiSession(apiSession);
            api.updateSObject("contact", id, contact);
            Contact mod_contact = api.getSObject("contact", id).as(Contact.class);
            try {
                store(mod_contact.getId(), mod_contact);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            return mod_contact;
        });
    }

    public Mono<String> deleteContact(String id, HttpHeaders headers) {
        LOGGER.debug("deleteContact({})", id);
        return authServiceClient.getApiSession(headers).map(apiSession -> {
            setApiSession(apiSession);
            api.deleteSObject("contact", id);
            try {
                remove(id);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            return String.format("Contact %s deleted!", id);
        });
    }

    public Mono<Contact> createContact(Contact contact, HttpHeaders headers) {
        LOGGER.debug("createContact({})", contact);
        return authServiceClient.getApiSession(headers).map(apiSession -> {
            setApiSession(apiSession);
            String id = api.createSObject("contact", contact);
            contact.setId(id);
            try {
                store(contact.getId(), contact);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            return contact;
        });
    }

    private void setApiSession(ApiSession apiSession) {
        LOGGER.debug("setApiSession: accessToken={}", apiSession.getAccessToken());
        this.api = new ForceApi(apiSession);
    }

    private void store(String key, Contact contact) {
        LOGGER.debug("store({},{})", key, contact);
        if (key != null) {
            contactValueOps.set(key, contact).subscribe(result -> {
                LOGGER.debug("Contact({}) cached? {}", key, result);
            });
        }
    }

    private void remove(String key) {
        LOGGER.debug("remove({})", key);
        if (key != null) {
            contactValueOps.delete(key).subscribe(result -> {
                LOGGER.debug("Contact({}) removed from cached? {}", key, result);
            });
        }
    }

    private Mono<Contact> retrieve(String key, HttpHeaders headers) {
        LOGGER.debug("retrieve({})", key);
        if (key != null) {
            return contactValueOps.get(key)
                    .switchIfEmpty(Mono.defer(() -> getContactByIdFallback(key,
                            headers)))
                    .onErrorResume(err -> {
                        LOGGER.error(err.getMessage());
                        return Mono.empty();
                    });
        }
        return Mono.empty();
    }
    
}
