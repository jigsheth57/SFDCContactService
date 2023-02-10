package com.vmware.sfdc.demo.contactservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.sfdc.demo.contactservice.model.Contact;
import com.vmware.sfdc.demo.contactservice.services.ContactService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api")
public class ContactServiceController {
    private final ContactService contactService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceController.class);

    public ContactServiceController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Returns result of an contact by Id from cache
     * 
     * @return Contact
     */
    @GetMapping("/contact/{contactId}")
    @Operation(summary = "Returns result of a contact by Id from cache", description = "Returns result of a contact by Id from cache", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class)))
    })
    public Mono<Contact> getContactById(@PathVariable String contactId, @RequestHeader HttpHeaders headers) {
        LOGGER.debug("getContactById({})", contactId);
        return contactService.getContactById(contactId, headers);
    }

    /**
     * Returns result of an contact by Id from SalesForce.com
     * 
     * @return Contact
     */
    @GetMapping("/contact-fallback/{contactId}")
    @Operation(summary = "Returns result of a contact by Id from SalesForce.com", description = "Returns result of a contact by Id from SalesForce.com", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class)))
    })
    public Mono<Contact> getContactByIdFallback(@PathVariable String contactId, @RequestHeader HttpHeaders headers) {
        LOGGER.debug("getContactByIdFallback({})", contactId);
        return contactService.getContactByIdFallback(contactId, headers);
    }

    /**
     * Update a contact in SalesForce.com
     * 
     * @return Contact
     */
    @PutMapping("/contact")
    @Operation(summary = "Update a contact in SalesForce.com", description = "Update a contact in SalesForce.com", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class)))
    })
    public Mono<Contact> updateContact(@RequestBody Contact contact, @RequestHeader HttpHeaders headers) {
        LOGGER.debug("updateContact({})", contact);
        return contactService.updateContact(contact, headers);
    }

    /**
     * Delete a contact in SalesForce.com
     * 
     * @return String
     */
    @DeleteMapping("/contact/{contactId}")
    @Operation(summary = "Delete a contact in SalesForce.com", description = "Delete a contact in SalesForce.com", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    public Mono<String> deleteContact(@PathVariable String contactId, @RequestHeader HttpHeaders headers) {
        LOGGER.debug("deleteContact({})", contactId);
        return contactService.deleteContact(contactId, headers);
    }

    /**
     * Create a contact in SalesForce.com
     * 
     * @return Contact
     */
    @PostMapping("/contact")
    @Operation(summary = "Create a contact in SalesForce.com", description = "Create a contact in SalesForce.com", responses = {
            @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class)))
    })
    public Mono<Contact> createContact(@RequestBody Contact contact, @RequestHeader HttpHeaders headers) {
        LOGGER.debug("createContact({})", contact);
        return contactService.createContact(contact, headers);
    }

}
