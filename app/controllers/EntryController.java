package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import dbconnect.EntryOperations;
import models.Entry;
import models.UpdateEntry;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EntryController extends Controller {
    private final EntryOperations _entryOperations;
    private final HttpExecutionContext _httpExecutionContext;

    @Inject
    public EntryController (EntryOperations entryOperations, HttpExecutionContext httpExecutionContext) {
        _entryOperations = entryOperations;
        _httpExecutionContext = httpExecutionContext;
    }

    public Result getEntries(String parent) {
        ImmutableList<Entry> entries = _entryOperations.getEntries("/"+parent);
        return ok(Json.toJson(entries));
    }

    public Result getEntry(String parent, String child) {
        Entry entry = _entryOperations.getEntry("/"+parent+"/"+child);
        return ok(Json.toJson(entry));
    }

    public Result createEntry(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            Entry entry = mapper.treeToValue(jsonNode, Entry.class);
            String status = _entryOperations.createEntry(entry.getParent(),entry.getName());
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }

    public Result deleteEntry(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            Entry entry = objectMapper.treeToValue(jsonNode, Entry.class);
            String status = _entryOperations.deleteEntry(entry.getRowid());
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError();
        }
    }

    public Result updateEntry(Http.Request request) {
        try {
            JsonNode jsonNode = request.body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateEntry updateEntry = objectMapper.treeToValue(jsonNode,
                    UpdateEntry.class);
            String status = _entryOperations.updateEntry(updateEntry);
            return ok(status);
        } catch (JsonProcessingException e) {
            return internalServerError(e.getMessage());
        }
    }
}
