package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dbconnect.EntryOperations;
import models.Entry;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PublicEndpointsController extends Controller {
    private HttpExecutionContext _httpExecutionContext;
    private EntryOperations _entryOperations;

    @Inject
    public PublicEndpointsController(HttpExecutionContext httpExecutionContext, EntryOperations entryOperations){
        _entryOperations = entryOperations;
        _httpExecutionContext = httpExecutionContext;
    }

    public Result getEntry(String parent, String name){
        Entry entry = _entryOperations.getEntry("/"+parent+"/"+name);
        ObjectNode result = Json.newObject();
        result.put("title",entry.getTitle());
        result.put("slug",entry.getSlug());
        result.put("time",entry.getTime());
        if(entry.getTeaser()!=null){
            result.set("teaser",entry.getTeaser());
        }
        if(entry.getContent()!=null){
            result.set("content",entry.getContent());
        }
        return ok(result);
    }
}
