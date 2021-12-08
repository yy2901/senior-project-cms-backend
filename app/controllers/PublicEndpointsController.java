package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dbconnect.APIOperations;
import dbconnect.EntryOperations;
import models.Entry;
import models.TimeOrder;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import scala.concurrent.impl.FutureConvertersImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class PublicEndpointsController extends Controller {
    private HttpExecutionContext _httpExecutionContext;
    private EntryOperations _entryOperations;
    private APIOperations _apiOperations;

    @Inject
    public PublicEndpointsController(HttpExecutionContext httpExecutionContext, EntryOperations entryOperations,
                                     APIOperations apiOperations){
        _entryOperations = entryOperations;
        _httpExecutionContext = httpExecutionContext;
        _apiOperations = apiOperations;
    }

    public Result getEntry(String parent, String name){
        Entry entry = _entryOperations.getEntry("/"+parent,"/"+name);
        ObjectNode result = Json.newObject();
        result.put("id",entry.getRowid());
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

    public Result getRoute(Option<Integer> page, String parent, Option<Integer> optionItems, Option<String> optionOrder) {
        if(page.isEmpty()){
            return ok(_apiOperations.getSingle(parent));
        } else {
            List<ObjectNode> results = _apiOperations.getCollection(page.get(), parent, optionItems, optionOrder);
            return ok(Json.toJson(results));
        }
    }
}
